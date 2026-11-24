# Integração contínua e implantação

Como o Crivo é testado no GitHub e publicado numa **instalação única**: uma VPS, um endereço, `https://crivo.psigama.com` (SPEC 0.29, seção 1.3). Nele, o Caddy entrega do disco as páginas editoriais — o site estático — e manda para a aplicação todo o resto. As regras que valem acima deste roteiro estão na [Constitution](CONSTITUTION.md), em *Implantação por fase*.

## Visão geral

| Peça | Onde | O que faz |
|---|---|---|
| [`.github/workflows/ci.yml`](../.github/workflows/ci.yml), job `test` | GitHub Actions | A cada push e a cada *pull request*, compila e roda a suíte completa, inclusive a matriz de cegamento e a integridade da trilha, com o PostgreSQL embarcado, nos JDKs 21 e 25 |
| [`.github/workflows/ci.yml`](../.github/workflows/ci.yml), job `deploy` | GitHub Actions, em todo push na `main` que passa na suíte | Compila o JAR, gera o site, entra na VPS por SSH com chave, troca a versão da aplicação ([`deploy/trocar-versao.sh`](../deploy/trocar-versao.sh)) e confere a saúde — se ela não voltar, recoloca a versão anterior — e, depois, espelha o site estático |
| VPS de produção | VPS KVM da Hostinger no data center de São Paulo (Brasil): Ubuntu 26.04 LTS x86_64, 1 vCPU, 4 GB de RAM | Linux, Java 25, PostgreSQL do sistema, a aplicação como serviço `systemd` (`airlab`), o site estático em `/var/www/crivo` e o Caddy na borda |

A implantação é **automática** (Constitution v1.8.0, regra de implantação 8). A regra continua só permitindo atualizar a aplicação entre sessões de coleta, e a trava deixou de ser uma confirmação no formulário do workflow: passou a ser o **ramo**. O trabalho fica em branch, o merge na `main` é que implanta, e quem faz o merge é quem garante que nenhum estudo está em andamento. O site editorial não reinicia a aplicação — é arquivo em disco —, e a regra 8 não se aplica a ele (regra 9).

Onde é a VPS **não está no repositório** (regra 7): endereço, usuário, porta, caminhos, nome do serviço e endereço de saúde vêm dos *Secrets* e *Variables* do ambiente `production` (seção 3). Os arquivos que moram na VPS têm o seu modelo em [`deploy/`](../deploy/): a unidade `systemd`, o `Caddyfile`, o `airlab.env`, o script da troca de versão e o [`preparar-vps.sh`](../deploy/preparar-vps.sh), que prepara a máquina para esta arquitetura (seção 6, passo 1).

## 1. Preparar a VPS

Uma vez, na VPS. A instalação atual, de 15/11/2026, é uma VPS KVM da Hostinger no data center de São Paulo, com Ubuntu 26.04 LTS (x86_64). Os dados ficam no Brasil, como prefere a regra de implantação 1.

1. **Acesso**: só por chave SSH. A do administrador (`crivo-admin`) é cadastrada no painel da Hostinger. Desligue o login por senha num arquivo lido antes dos outros em `/etc/ssh/sshd_config.d/` (no `sshd`, vale o primeiro valor lido, e a imagem traz um `50-cloud-init.conf` que liga a senha), por exemplo `00-psigama.conf`:

   ```text
   PasswordAuthentication no
   KbdInteractiveAuthentication no
   PermitRootLogin prohibit-password
   ```

2. **Firewall** (`ufw`): `default deny incoming`, `limit 22/tcp`, `allow 80/tcp`, `allow 443/tcp` e `enable`. O `limit` recusa um endereço que abra mais de seis conexões em 30 segundos, o que freia as tentativas em massa sem atrapalhar o workflow, que abre três.

   A regra de implantação 3 prefere o SSH acessível só por uma rede privada (Tailscale). A instalação atual deixa a porta 22 aberta para a internet, só com chave e com atualizações de segurança automáticas: para um servidor administrado por uma pessoa, a rede privada traria mais peças (conta, política, cliente OAuth, segredos) do que proteção. Se um dia isso mudar, a VPS entra na rede Tailscale, o workflow ganha o passo [`tailscale/github-action`](https://github.com/tailscale/github-action) e a porta 22 sai da internet.
3. **Pacotes**: `openjdk-25-jre-headless`, `postgresql` e `rsync` do Ubuntu; o Caddy do repositório oficial ([Caddy](https://caddyserver.com/docs/install#debian-ubuntu-raspbian)). O `rsync` precisa existir nas duas pontas: é com ele que o site vai para a VPS. Versões instaladas (regra de implantação 8):

   | Pacote | Versão |
   |---|---|
   | OpenJDK | 25.0.5.1 (`openjdk-25-jre-headless` 25.0.5.1+2-1~26.04.1) |
   | PostgreSQL | 18.6 (`postgresql-18` 18.6-0ubuntu0.26.04.1) |
   | Caddy | 2.11.9 |

   O PostgreSQL embarcado dos testes tem a mesma versão principal (`embedded-postgres-binaries.version` no `pom.xml`, regra de implantação 4). Ao atualizar um, atualize o outro.

   O Java do workflow acompanha o da VPS: o job `deploy` constrói o JAR com o Temurin da mesma versão principal, e o job `test` roda a suíte nela e no Java 21, o mais antigo que o projeto aceita (`java.version` no `pom.xml`). A VPS tem um JDK só; em 23/11/2026, o 25 substituiu o 21. A troca de versão principal reinicia a aplicação, e por isso só se faz entre sessões de coleta:

   1. instale o pacote novo e confira que `/usr/bin/java` aponta para ele, com `update-alternatives --display java`. Se a alternativa estiver em modo manual, presa no antigo, `sudo update-alternatives --auto java` a solta;
   2. reinicie o serviço (`sudo systemctl restart airlab`) e espere `/actuator/health` responder `UP`;
   3. só então remova o pacote antigo, registre a versão na tabela acima e troque a versão no `ci.yml` (dois lugares: a matriz do `test` e o JDK do `deploy`).

   Antes do passo 3, `sudo update-alternatives --set java` com o caminho do Java antigo, seguido de outro restart, desfaz a troca.

4. **Aplicação**:
   - usuário de serviço: `sudo useradd --system --home /opt/airlab --shell /usr/sbin/nologin airlab`;
   - diretórios: `sudo install -d -o airlab -g airlab -m 0755 /opt/airlab` e `sudo install -d -o airlab -g airlab -m 0700 /opt/airlab/data`. O `/opt/airlab` precisa ser visível ao usuário `deploy` (seção 2): é por ele que o workflow vê o JAR atual e guarda a cópia `.previous` antes de trocar. Com a pasta fechada, essa cópia nunca é feita e não há versão para onde voltar. Os dados ficam em `data/`, que só o `airlab` lê;
   - banco: o PostgreSQL escuta só em `127.0.0.1`, com `listen_addresses = '127.0.0.1'` em `/etc/postgresql/18/main/conf.d/crivo.conf` (regra de implantação 4). São dois papéis: `airlab_owner`, dono do banco, com o qual o Flyway roda as migrações; e `airlab_app`, com o qual a aplicação conecta e que a migração `V10` reduz ao privilégio mínimo (na trilha, só `SELECT` e `INSERT`). A `V10` só cria o `airlab_app` se ele não existir, sem login; por isso ele é criado antes, já com login. Com `sudo -u postgres psql`, e senhas geradas por `openssl rand -hex 32`:

     ```sql
     CREATE ROLE airlab_owner LOGIN PASSWORD '...';
     CREATE ROLE airlab_app LOGIN PASSWORD '...';
     CREATE DATABASE airlab OWNER airlab_owner ENCODING 'UTF8' TEMPLATE template0;
     REVOKE ALL ON DATABASE airlab FROM PUBLIC;
     GRANT CONNECT ON DATABASE airlab TO airlab_app;
     ```

   - configuração e segredos: `/etc/airlab/airlab.env`, com dono `root` e permissão `600`, a partir de [`deploy/airlab.env.example`](../deploy/airlab.env.example) (regra de implantação 7). Nele vão `AIRLAB_BASE_URL=https://crivo.psigama.com`, o endereço público desta instalação, usado nos links dos e-mails e na `og:image`; **`AIRLAB_SITE_URL` vazia**, porque o site mora aqui mesmo — definida com este endereço, a aplicação responderia às rotas editoriais com 301 para si mesma; `AIRLAB_DB_USER=airlab_app` com a senha desse papel; `SPRING_FLYWAY_USER=airlab_owner` e `SPRING_FLYWAY_PASSWORD` com a do dono, que o Spring Boot usa só nas migrações; e a `AIRLAB_IDENTITY_KEY`, gerada com `openssl rand -base64 32`. **Guarde uma cópia da chave fora da VPS**: sem ela, os dados cifrados do esquema `identity` não se recuperam. O `AIRLAB_RESEARCHER_PASSWORD_HASH` tem de estar preenchido antes da primeira implantação. Sem SMTP, o `SPRING_MAIL_HOST` fica vazio ou comentado: o Crivo trata as duas formas como sem e-mail e mantém a inscrição do P1 fechada, e o e-mail fica fora do `/actuator/health`, que o deploy confere;
   - serviço: copie [`deploy/airlab.service`](../deploy/airlab.service) para `/etc/systemd/system/airlab.service` e rode `sudo systemctl daemon-reload && sudo systemctl enable airlab`. O serviço só sobe quando houver um JAR em `/opt/airlab/airlab.jar`, o que a primeira implantação resolve.
5. **Site estático**: `sudo install -d -o deploy -g deploy -m 0755 /var/www/crivo`. É pasta só da implantação — o workflow a sincroniza com `rsync --delete`, e nada ali se escreve à mão. O dono é o `deploy`, que escreve; o Caddy só lê.
6. **Caddy**: a configuração do Crivo é [`deploy/Caddyfile.example`](../deploy/Caddyfile.example) e mora em `/etc/caddy/conf.d/crivo.caddyfile`, importada pelo `Caddyfile` principal com `import conf.d/*.caddyfile`. Ela faz HTTPS automático, serve o site do disco, manda para a aplicação em `127.0.0.1:8080` todo o resto, recusa o `/actuator` de fora e responde 301 no endereço antigo (`app.crivo.psigama.com`).

   **Um arquivo por projeto, porque a VPS não é só do Crivo**: hoje ela serve também o ReMoto (`remoto.psigama.com`), no mesmo Caddy. Reescrever o `Caddyfile` principal tiraria o outro site do ar; cada projeto tem o seu arquivo em `conf.d/`. Dois blocos para o mesmo endereço — um no principal, outro em `conf.d/` — fazem o Caddy recusar a configuração inteira, então o bloco antigo do Crivo sai do principal quando o de `conf.d/` entra. Depois de editar:

   ```bash
   caddy validate --config /etc/caddy/Caddyfile && sudo systemctl reload caddy
   ```

7. **DNS**: um registro `A` de `crivo.psigama.com` para o IP público da VPS, e outro de `app.crivo.psigama.com`, que só redireciona. Enquanto um nome não apontar para a VPS, o Caddy não obtém o certificado dele e tenta de novo sozinho. A troca do `crivo.psigama.com`, que até a SPEC 0.28 apontava para o GitHub Pages, está na seção 6.

## 2. Usuário de implantação

O workflow entra na VPS como um usuário próprio, `deploy`, que só pode trocar o JAR, reiniciar o serviço e escrever na pasta do site.

1. Gere um par de chaves só para isso, fora da VPS e sem senha, porque o workflow não tem como digitá-la: `ssh-keygen -t ed25519 -N '' -f crivo-deploy -C github-actions-crivo`. No Windows, deixe a chave privada legível só pelo seu usuário (o SSH do Windows recusa chave que outra conta lê).
2. Na VPS: `sudo adduser --disabled-password --gecos "" deploy` e coloque `restrict` seguido do conteúdo de `crivo-deploy.pub` em `/home/deploy/.ssh/authorized_keys`, com permissão `600`. O `restrict` corta encaminhamento de porta, de agente e X11 e o terminal; o `scp`, o `rsync` e o `bash -s` do workflow continuam funcionando.
3. Crie `/etc/sudoers.d/airlab-deploy` com `sudo visudo -f /etc/sudoers.d/airlab-deploy`:

   ```text
   deploy ALL=(root) NOPASSWD: /usr/bin/install -o airlab -g airlab -m 0644 /opt/airlab/airlab.jar /opt/airlab/airlab.jar.previous, \
     /usr/bin/install -o airlab -g airlab -m 0644 /home/deploy/airlab.jar.new /opt/airlab/airlab.jar, \
     /usr/bin/install -o airlab -g airlab -m 0644 /opt/airlab/airlab.jar.previous /opt/airlab/airlab.jar, \
     /usr/bin/systemctl restart airlab
   ```

   Os comandos e os caminhos são exatamente os que [`deploy/trocar-versao.sh`](../deploy/trocar-versao.sh) usa, com os valores das *Variables* da seção 3. Mudar `DEPLOY_JAR_PATH`, `DEPLOY_SERVICE` ou o nome do usuário pede mudar a linha correspondente aqui — fora dela, o `sudo` recusa. O site não aparece nesta lista: o `rsync` escreve em `/var/www/crivo` como o próprio `deploy`, sem `sudo`, e não tem como tocar a aplicação.

## 3. GitHub

**Ambiente `production`** (repositório → *Settings* → *Environments*): crie o ambiente, restrito à branch `main`. Nele ficam os segredos — o que, vazado, dá acesso à VPS — e as variáveis que dizem onde ela é.

| Segredo | Valor |
|---|---|
| `DEPLOY_HOST` | IP público da VPS. É segredo, e não variável, para ficar mascarado no log das execuções, que num repositório público qualquer pessoa lê |
| `DEPLOY_SSH_KEY` | Conteúdo da chave privada `crivo-deploy`, inteira e **sem senha** |
| `DEPLOY_KNOWN_HOSTS` | A linha `ed25519` que `ssh-keyscan -t ed25519 <DEPLOY_HOST>` devolve, conferida contra a impressão digital que a própria VPS mostra (`ssh-keygen -lf /etc/ssh/ssh_host_ed25519_key.pub`). Fixa a identidade da VPS: se ela mudar, a implantação para |

| Variável | Valor na instalação atual |
|---|---|
| `DEPLOY_USER` | `deploy` — é variável, e não segredo: sendo uma palavra comum, mascará-la esconderia "deploy" de todo o log, inclusive de nomes de passo e de mensagens de erro |
| `DEPLOY_PORT` | `22` |
| `DEPLOY_SITE_DIR` | `/var/www/crivo` |
| `DEPLOY_JAR_PATH` | `/opt/airlab/airlab.jar` |
| `DEPLOY_SERVICE` | `airlab` |
| `DEPLOY_HEALTH_URL` | `http://127.0.0.1:8080/actuator/health` — em loopback, dentro da VPS; se a aplicação não escutar na 8080 (`AIRLAB_PORT`), mude aqui também |

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

**Voltar à versão anterior à mão**, na VPS:

```bash
sudo install -o airlab -g airlab -m 0644 /opt/airlab/airlab.jar.previous /opt/airlab/airlab.jar
sudo systemctl restart airlab
```

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

## 6. A virada: `crivo.psigama.com` passa a ser a VPS

Uma vez, **entre sessões de coleta**, para sair do GitHub Pages (SPEC 0.28) e chegar à instalação única (SPEC 0.29). Até a v0.28, `crivo.psigama.com` era um `CNAME` para `rogeriocisi.github.io` e a aplicação morava em `app.crivo.psigama.com`.

0. Na véspera, baixe o TTL do registro de `crivo.psigama.com` para 300 segundos, para que a troca do passo 4 se espalhe em minutos.
1. Na VPS: a pasta do site e o `rsync` (seção 1, passos 3 e 5) e o `Caddyfile` novo (passo 6). O [`deploy/preparar-vps.sh`](../deploy/preparar-vps.sh) faz essa parte mecânica, e pode rodar de novo quantas vezes quiser:

   ```bash
   scp -r deploy <seu-usuário>@<vps>:~/
   sudo bash deploy/preparar-vps.sh
   ```

   Assim ele confere tudo e não mexe no que está no ar: cria a pasta e instala o `rsync`, escreve no `airlab.env` o endereço público com `AIRLAB_SITE_URL` vazia (guardando cópia do anterior, e a aplicação só lê isso ao reiniciar), deixa o `Caddyfile` novo validado ao lado do atual e lista o que falta. Com `--aplicar` ele troca a borda — com cópia do anterior e volta atrás se o Caddy recusar — e reinicia a aplicação; é opt-in porque reiniciar só se faz entre sessões de coleta (regra de implantação 8). O Caddy vai tentar o certificado de `crivo.psigama.com` e falhar enquanto o DNS não apontar para cá; ele repete sozinho. Nada do que está no ar muda sem o `--aplicar`.
2. No GitHub: os segredos e as variáveis da seção 3, com `APP_URL=https://crivo.psigama.com`. O `DEPLOY_SSH_KEY` pode ficar para o passo 6, e aí ele é o interruptor: sem ele, nenhum push chega à VPS.
3. **Reinicie a aplicação**, para o `airlab.env` do passo 1 valer: `sudo systemctl restart airlab`, ou o script com `--aplicar`. **Antes do DNS**: com `AIRLAB_SITE_URL` ainda apontando para `crivo.psigama.com`, assim que o nome chegasse à VPS a aplicação responderia às rotas editoriais com 301 para si mesma, num laço. Reiniciada, ela serve essas páginas ela mesma, e `app.crivo.psigama.com` continua respondendo como antes.
4. DNS: apague o `CNAME` de `crivo.psigama.com` e crie um `A` para o IP público da VPS. Um nome com `CNAME` não pode ter outro registro, então a ordem é apagar e depois criar.
5. **A borda, junto com o DNS**: tire do `Caddyfile` principal o bloco de `app.crivo.psigama.com` (a configuração do Crivo agora é `conf.d/crivo.caddyfile`, seção 1, passo 6) e recarregue — `caddy validate --config /etc/caddy/Caddyfile && sudo systemctl reload caddy`, ou o script com `--aplicar`, que confere o DNS antes de recarregar. **Não faça isto antes do passo 4**: o bloco novo responde 301 de `app.crivo.psigama.com` para `crivo.psigama.com`, e enquanto esse nome não apontar para a VPS o 301 levaria quem acessa a um endereço que não responde. Depois dele, `crivo.psigama.com` é servido pela VPS; o site estático ainda não está lá, então quem responde as páginas editoriais é a aplicação — não há janela sem site.
6. Cadastre o `DEPLOY_SSH_KEY` e faça o merge na `main`. O job `deploy` troca a aplicação e espelha o site, que passa a responder antes dela. Confira `https://crivo.psigama.com/`, `https://crivo.psigama.com/garantias` (do disco), `https://crivo.psigama.com/protocolos` (da aplicação) e o 301 de `https://app.crivo.psigama.com/protocolos`.
7. No GitHub, desligue o Pages: *Settings* → *Pages* → *Source* → **None**; o ambiente `github-pages` pode ser apagado. O arquivo `CNAME` saiu do repositório, porque era só do Pages. Em *Verified domains* da conta, o `psigama.com` verificado pode ficar: ele impede que outra conta publique em `crivo.psigama.com`.
8. No repositório `psigama`, os links para as páginas da aplicação (`/protocolos`, `/protocolo/P1` a `/protocolo/P4`, as rotas `/rodar`) passam a ser `crivo.psigama.com/...`, sem o `app.`. O 301 cobre os antigos, mas o link direto evita o salto.

## Fora destes workflows

- **Backups** (regra de implantação 5): dump diário criptografado para fora da VPS e teste mensal de restauração. Precisam de um roteiro próprio.

## Dependências do GitHub Actions

Só o workflow as usa; nenhuma entra na aplicação (P8). As versões principais ficam fixadas no arquivo.

| Ação | Mantenedor | Para quê |
|---|---|---|
| `actions/checkout`, `actions/setup-java`, `actions/upload-artifact` | GitHub | Baixar o código, instalar o JDK e guardar o JAR e os relatórios de teste |

O resto é `ssh`, `scp`, `rsync` e `curl`, que já vêm no runner do Ubuntu.
