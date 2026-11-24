# Integração contínua e implantação

Como o Crivo é testado no GitHub e publicado numa **instalação única**: uma VPS, um endereço, `https://crivo.psigama.com` (SPEC 0.29, seção 1.3). Nele, o Caddy entrega do disco as páginas editoriais — o site estático — e manda para a aplicação todo o resto. As regras que valem acima deste roteiro estão na [Constitution](CONSTITUTION.md), em *Implantação por fase*.

Este documento descreve a **arquitetura** da implantação: o que o workflow faz, em que ordem, com que garantias, e o que a máquina precisa oferecer. Ser auditável é parte do que o Crivo se propõe, e isto fica aberto.

O que **não** está aqui é o inventário da instalação em vigor — provedor, versões exatas dos pacotes, nomes de usuário, caminhos, a linha do `sudoers`, os valores atuais dos *Secrets* e *Variables*. Nada disso é segredo, mas junto, num repositório público, dá o mapa de reconhecimento de uma máquina que o próprio endereço identifica. Mora no repositório privado `psigama/crivo-aux`, em `vps/README.md`.

## Visão geral

| Peça | Onde | O que faz |
|---|---|---|
| [`.github/workflows/ci.yml`](../.github/workflows/ci.yml), job `test` | GitHub Actions | A cada push e a cada *pull request*, compila e roda a suíte completa, inclusive a matriz de cegamento e a integridade da trilha, com o PostgreSQL embarcado, no JDK 25 |
| [`.github/workflows/ci.yml`](../.github/workflows/ci.yml), job `deploy` | GitHub Actions, em todo push na `main` que passa na suíte | Compila o JAR, gera o site, entra na VPS por SSH com chave, troca a versão da aplicação ([`deploy/trocar-versao.sh`](../deploy/trocar-versao.sh)) e confere a saúde — se ela não voltar, recoloca a versão anterior — e, depois, espelha o site estático |
| VPS de produção | Uma máquina Linux com dados no Brasil, como prefere a regra de implantação 1 | Linux, Java 25, PostgreSQL do sistema, a aplicação como serviço `systemd`, o site estático em disco e o Caddy na borda |

A implantação é **automática** (Constitution v1.8.0, regra de implantação 8). A regra continua só permitindo atualizar a aplicação entre sessões de coleta, e a trava deixou de ser uma confirmação no formulário do workflow: passou a ser o **ramo**. O trabalho fica em branch, o merge na `main` é que implanta, e quem faz o merge é quem garante que nenhum estudo está em andamento. O site editorial não reinicia a aplicação — é arquivo em disco —, e a regra 8 não se aplica a ele (regra 9).

Onde é a VPS **não está no repositório** (regra 7): endereço, usuário, porta, caminhos, nome do serviço e endereço de saúde vêm dos *Secrets* e *Variables* do ambiente `production` (seção 3). Os arquivos que moram na VPS têm o seu modelo em [`deploy/`](../deploy/): a unidade `systemd`, o `Caddyfile`, o `airlab.env`, o script da troca de versão e o [`preparar-vps.sh`](../deploy/preparar-vps.sh), que prepara a máquina para esta arquitetura.

## 1. O que a máquina precisa oferecer

O roteiro da instalação em vigor está em `crivo-aux`, em `vps/README.md`. O que esta arquitetura **exige** de qualquer máquina que vá servir o Crivo, e que vale como requisito:

1. **Acesso só por chave SSH**, com o login por senha desligado (`PasswordAuthentication no`, `KbdInteractiveAuthentication no`, `PermitRootLogin prohibit-password`).
2. **Firewall** que recusa tudo na entrada e abre só o SSH, o 80 e o 443, com limite de taxa no SSH. A regra de implantação 3 prefere o SSH acessível só por uma rede privada (Tailscale); se uma instalação escolher a porta aberta, ela fica com chave e com atualizações de segurança automáticas, e a escolha se registra no roteiro privado.
3. **Java 25**, a mesma versão principal declarada no `java.version` do `pom.xml`, nos dois jobs do workflow e na checagem de [`scripts/start.sh`](../scripts/start.sh) e [`scripts/start.ps1`](../scripts/start.ps1) — são os quatro lugares que a declaram, e a troca de versão principal reinicia a aplicação, logo só se faz entre sessões de coleta. Mais `rsync` nas duas pontas, porque é com ele que o site vai para a VPS, e o Caddy na borda.
4. **PostgreSQL do sistema**, da mesma versão principal do embarcado dos testes (`embedded-postgres-binaries.version` no `pom.xml`, regra de implantação 4) e **escutando só em `127.0.0.1`**. Dois papéis: `airlab_owner`, dono do banco, com o qual o Flyway roda as migrações; e `airlab_app`, com o qual a aplicação conecta e que a migração [`V10`](../src/main/resources/db/migration/V10__app_role_grants.sql) reduz ao privilégio mínimo (na trilha, só `SELECT` e `INSERT`). A `V10` só cria o `airlab_app` se ele não existir, sem login; por isso ele é criado antes, já com login. As senhas se geram com `openssl rand -hex 32`:

   ```sql
   CREATE ROLE airlab_owner LOGIN PASSWORD '...';
   CREATE ROLE airlab_app LOGIN PASSWORD '...';
   CREATE DATABASE airlab OWNER airlab_owner ENCODING 'UTF8' TEMPLATE template0;
   REVOKE ALL ON DATABASE airlab FROM PUBLIC;
   GRANT CONNECT ON DATABASE airlab TO airlab_app;
   ```

5. **A aplicação como serviço `systemd`**, sob um usuário de sistema próprio e sem shell, a partir de [`deploy/airlab.service`](../deploy/airlab.service). Os dados e a mídia ficam numa pasta que só esse usuário lê (modo `0700`); a pasta do JAR precisa ser visível ao usuário de implantação, que é quem guarda a cópia da versão anterior antes de trocar — fechada, essa cópia nunca é feita e não há versão para onde voltar.
6. **Configuração e segredos** num arquivo de ambiente com dono `root` e permissão `600`, a partir de [`deploy/airlab.env.example`](../deploy/airlab.env.example) (regra de implantação 7). Nele, `AIRLAB_BASE_URL` com o endereço público, usado nos links dos e-mails e na `og:image`; **`AIRLAB_SITE_URL` vazia** na instalação única, porque o site mora na mesma máquina — definida com o próprio endereço, a aplicação responderia às rotas editoriais com 301 para si mesma; as senhas dos dois papéis do banco, a do dono em `SPRING_FLYWAY_PASSWORD`, que o Spring Boot usa só nas migrações; e a `AIRLAB_IDENTITY_KEY`, gerada com `openssl rand -base64 32`. **Guarde uma cópia da chave fora da VPS**: sem ela, os dados cifrados do esquema `identity` não se recuperam. O `AIRLAB_RESEARCHER_PASSWORD_HASH` tem de estar preenchido antes da primeira implantação. Sem SMTP, o `SPRING_MAIL_HOST` fica vazio ou comentado: o Crivo trata as duas formas como sem e-mail e mantém a inscrição do P1 fechada, e o e-mail fica fora do `/actuator/health`, que o deploy confere.
7. **Site estático** numa pasta só da implantação, de que o usuário de implantação é dono e o Caddy só lê. O workflow a sincroniza com `rsync --delete`, e nada ali se escreve à mão.
8. **Caddy** a partir de [`deploy/Caddyfile.example`](../deploy/Caddyfile.example), num arquivo próprio importado pelo `Caddyfile` principal com `import conf.d/*.caddyfile`. Ele faz HTTPS automático, serve o site do disco, manda para a aplicação em `127.0.0.1:8080` todo o resto, recusa o `/actuator` de fora e responde 301 no endereço antigo. **Um arquivo por projeto**: a máquina pode servir mais de um site, e reescrever o `Caddyfile` principal tiraria os outros do ar. Dois blocos para o mesmo endereço — um no principal, outro em `conf.d/` — fazem o Caddy recusar a configuração inteira. Depois de editar:

   ```bash
   caddy validate --config /etc/caddy/Caddyfile && sudo systemctl reload caddy
   ```

9. **DNS**: um registro `A` do endereço público para a máquina, e outro do endereço antigo, que só redireciona. Enquanto um nome não apontar para lá, o Caddy não obtém o certificado dele e tenta de novo sozinho.

O [`deploy/preparar-vps.sh`](../deploy/preparar-vps.sh) faz a parte mecânica disto, confere o resto e não mexe no que está no ar sem `--aplicar`.

## 2. Usuário de implantação

O workflow entra na VPS como um usuário próprio, que **só** pode trocar o JAR, reiniciar o serviço e escrever na pasta do site — nada mais.

1. Gere um par de chaves só para isso, fora da VPS e sem senha, porque o workflow não tem como digitá-la: `ssh-keygen -t ed25519 -N '' -f crivo-deploy -C github-actions-crivo`. No Windows, deixe a chave privada legível só pelo seu usuário (o SSH do Windows recusa chave que outra conta lê).
2. Crie o usuário sem senha e coloque `restrict` seguido do conteúdo de `crivo-deploy.pub` no `authorized_keys` dele, com permissão `600`. O `restrict` corta encaminhamento de porta, de agente e X11 e o terminal; o `scp`, o `rsync` e o `bash -s` do workflow continuam funcionando.
3. Dê a ele, num arquivo em `/etc/sudoers.d/`, `NOPASSWD` para **exatamente** os comandos que [`deploy/trocar-versao.sh`](../deploy/trocar-versao.sh) executa: os três `install` que movem o JAR (guardar a cópia anterior, pôr a nova, e desfazer) e o `systemctl restart` do serviço. Nada além disso — fora dessa lista, o `sudo` recusa. Mudar `DEPLOY_JAR_PATH`, `DEPLOY_SERVICE` ou o nome do usuário pede mudar a linha correspondente. O site não entra nessa lista: o `rsync` escreve na pasta do site como o próprio usuário de implantação, sem `sudo`, e não tem como tocar a aplicação.

A linha em vigor, com os caminhos desta instalação, está em `crivo-aux`.

## 3. GitHub

**Ambiente `production`** (repositório → *Settings* → *Environments*): crie o ambiente, restrito à branch `main`. Nele ficam os segredos — o que, vazado, dá acesso à VPS — e as variáveis que dizem onde ela é. Os valores em vigor estão em `crivo-aux`.

| Segredo | O que é |
|---|---|
| `DEPLOY_HOST` | Endereço da VPS. É segredo, e não variável, para ficar mascarado no log das execuções, que num repositório público qualquer pessoa lê |
| `DEPLOY_SSH_KEY` | Conteúdo da chave privada `crivo-deploy`, inteira e **sem senha** |
| `DEPLOY_KNOWN_HOSTS` | A linha `ed25519` que `ssh-keyscan -t ed25519 <DEPLOY_HOST>` devolve, conferida contra a impressão digital que a própria VPS mostra (`ssh-keygen -lf /etc/ssh/ssh_host_ed25519_key.pub`). Fixa a identidade da VPS: se ela mudar, a implantação para |

| Variável | O que é |
|---|---|
| `DEPLOY_USER` | O usuário de implantação (seção 2). É variável, e não segredo: sendo um nome comum, mascará-lo esconderia a palavra de todo o log, inclusive de nomes de passo e de mensagens de erro |
| `DEPLOY_PORT` | A porta do SSH |
| `DEPLOY_SITE_DIR` | A pasta do site estático na VPS |
| `DEPLOY_JAR_PATH` | O caminho do JAR na VPS |
| `DEPLOY_SERVICE` | O nome da unidade `systemd` |
| `DEPLOY_HEALTH_URL` | O `/actuator/health`, em loopback, dentro da VPS; se a aplicação não escutar na porta padrão (`AIRLAB_PORT`), mude aqui também |

**Variável do repositório** (*Settings* → *Secrets and variables* → *Actions* → *Variables*): `APP_URL`, com o endereço público da instalação, `https://crivo.psigama.com`. Não é segredo: está em toda página publicada. O job `deploy` a usa no `--aplicacao` do gerador do site, como endereço do ambiente `production` e na conferência final.

Nenhum desses valores vai para o repositório (regra de implantação 7). Sem nenhum deles, o job de implantação avisa e não envia nada, e o push não falha — é o que mantém clone e fork compilando. Com alguns e não todos, ele falha dizendo o que falta.

## 4. Implantar

Um push na `main` que passa na suíte implanta. Antes do merge, confirme que nenhuma sessão de coleta está em andamento; se houver estudo correndo, a atualização precisa de registro na trilha de auditoria (regra de implantação 8).

A ordem dos passos, no job `deploy`:

1. compila o JAR com o JDK da VPS e gera o site com `airlab site`, sem banco e sem Spring;
2. guarda o JAR e o seu SHA-256 como artefato da execução, para que uma volta atrás não precise de nova compilação;
3. envia o JAR, troca a versão, reinicia o serviço e espera `/actuator/health` responder `UP` em até dois minutos. Se a aplicação não ficar saudável, recoloca a versão anterior, reinicia e falha — e o site não muda;
4. espelha o site em `DEPLOY_SITE_DIR` com `rsync --delete`: o que saiu do repositório sai da VPS;
5. confere que o endereço público responde.

A aplicação troca **antes** do site porque uma página editorial pode apontar para uma rota que esta versão cria (SPEC 1.3, regra 4); o contrário quebraria o link durante a janela.

Para repetir uma implantação sem um commit novo: *Actions* → **CI** → *Run workflow*, com a branch `main`.

Para **voltar à versão anterior à mão**, a cópia `.previous` que o passo 3 guarda volta ao lugar do JAR e o serviço reinicia; os dois comandos, com os caminhos desta instalação, estão em `crivo-aux`.

## 5. O site estático na VPS

O site é gerado pelo mesmo JAR, sem subir o Spring nem o banco:

```bash
java -jar target/airlab.jar site target/site --aplicacao=https://crivo.psigama.com
```

O mesmo comando roda na sua máquina, pelos scripts da CLI, para ver o site antes de publicar:

```bash
./scripts/airlab.sh site target/site --aplicacao=http://127.0.0.1:8080
```

```powershell
.\scripts\airlab.ps1 site target\site --aplicacao=http://127.0.0.1:8080
```

Ele escreve as páginas listadas em `web.SitePages`, o CSS, o favicon, a imagem de compartilhamento, o `js/idioma.js`, o `js/encaminhar.js` e o `404.html`. Desde a SPEC 0.28 (seção 1.5) cada página sai **duas vezes**: em português no endereço de sempre e em inglês sob `en/` — `garantias.html` e `en/garantias.html`. Uma página sem tradução revisada sai em português com um aviso de uma linha, e não falta no site. O `js/idioma.js` leva ao inglês quem chega pela primeira vez com o navegador noutra língua, porque quem serve arquivo não negocia idioma.

Na instalação única, quem decide o que responde é o Caddy: o arquivo, quando existe, e a aplicação em todo o resto (`handle_errors 404`). Por isso o `404.html` do site **não é usado aqui** — quem responde endereço desconhecido é a aplicação, com a sua própria página de erro. Ele continua sendo gerado para o caso previsto na regra de implantação 9: uma cópia final do site, servida sozinha, depois de a VPS ser arquivada.

Como o site e a aplicação compartilham origem, os arquivos estáticos da pasta do site respondem antes dos da aplicação. São o mesmo arquivo, vindos do mesmo commit, porque o JAR e o site saem da mesma execução do workflow.

## 6. A virada: `crivo.psigama.com` passou a ser a VPS

Feita uma vez, entre sessões de coleta, para sair do GitHub Pages (SPEC 0.28) e chegar à instalação única (SPEC 0.29). Até a v0.28, `crivo.psigama.com` era um `CNAME` para o GitHub Pages e a aplicação morava em `app.crivo.psigama.com`.

O que ficou do desenho, e que vale para qualquer troca parecida: a aplicação reinicia com `AIRLAB_SITE_URL` vazia **antes** de o DNS mudar, senão, assim que o nome chegasse à VPS, ela responderia às rotas editoriais com 301 para si mesma, num laço; o bloco de 301 do endereço antigo só entra na borda **junto com** o DNS, porque antes dele levaria quem acessa a um endereço que não responde; e, entre o DNS e a primeira implantação, quem serve as páginas editoriais é a própria aplicação, então não há janela sem site.

O roteiro passo a passo, com os valores desta instalação, está em `crivo-aux`.

## Fora destes workflows

- **Backups** (regra de implantação 5): dump diário criptografado para fora da VPS e teste mensal de restauração. Precisam de um roteiro próprio.

## Dependências do GitHub Actions

Só o workflow as usa; nenhuma entra na aplicação (P8). As versões principais ficam fixadas no arquivo.

| Ação | Mantenedor | Para quê |
|---|---|---|
| `actions/checkout`, `actions/setup-java`, `actions/upload-artifact` | GitHub | Baixar o código, instalar o JDK e guardar o JAR e os relatórios de teste |

O resto é `ssh`, `scp`, `rsync` e `curl`, que já vêm no runner do Ubuntu.
