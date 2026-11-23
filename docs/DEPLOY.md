# Integração contínua e implantação

Como o Crivo é testado no GitHub e publicado em dois lugares (SPEC, seção 1.3): a **aplicação**, na VM de produção, em `https://app.crivo.psigama.com`, e o **site** com as páginas editoriais, no GitHub Pages, em `https://crivo.psigama.com`. As regras que valem acima deste roteiro estão na [Constitution](CONSTITUTION.md), em *Implantação por fase*.

## Visão geral

| Peça | Onde | O que faz |
|---|---|---|
| [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) | GitHub Actions | A cada push na `main` e a cada *pull request*, compila e roda a suíte completa, inclusive a matriz de cegamento e a integridade da trilha, com o PostgreSQL embarcado |
| [`.github/workflows/deploy.yml`](../.github/workflows/deploy.yml) | GitHub Actions, acionado à mão | Constrói e testa o JAR, entra na VM por SSH com chave, envia o JAR, troca a versão e confere a saúde; se a aplicação não subir, volta para a versão anterior |
| [`.github/workflows/pages.yml`](../.github/workflows/pages.yml) | GitHub Actions, a cada push na `main` | Gera o site estático com `airlab site` e o publica no GitHub Pages. Não tem segredo de SSH nem usa o ambiente `production`: não tem como chegar à VM (seção 5) |
| VM de produção | VPS KVM da Hostinger no data center de São Paulo (Brasil): Ubuntu 26.04 LTS x86_64, 1 vCPU, 4 GB de RAM | Linux, Java 21, PostgreSQL do sistema, a aplicação como serviço `systemd` (`airlab`) e o Caddy na borda |
| GitHub Pages | Repositório `crivo` → *Settings* → *Pages* | O site em `crivo.psigama.com`: as páginas editoriais, o CSS e a página 404 que encaminha para a aplicação |

A implantação da aplicação **nunca é automática**. A regra de implantação 8 só permite atualizá-la entre sessões de coleta, e o formulário do workflow exige que quem o aciona confirme isso. O site é outra coisa: é publicado a cada push na `main` (regra de implantação 9), porque nada nele vem do código dos protocolos ou do banco.

## 1. Preparar a VM

Uma vez, na VM. A instalação atual, de 15/11/2026, é uma VPS KVM da Hostinger no data center de São Paulo, com Ubuntu 26.04 LTS (x86_64). Os dados ficam no Brasil, como prefere a regra de implantação 1.

1. **Acesso**: só por chave SSH. A do administrador (`crivo-admin`) é cadastrada no painel da Hostinger. Desligue o login por senha num arquivo lido antes dos outros em `/etc/ssh/sshd_config.d/` (no `sshd`, vale o primeiro valor lido, e a imagem traz um `50-cloud-init.conf` que liga a senha), por exemplo `00-psigama.conf`:

   ```text
   PasswordAuthentication no
   KbdInteractiveAuthentication no
   PermitRootLogin prohibit-password
   ```

2. **Firewall** (`ufw`): `default deny incoming`, `limit 22/tcp`, `allow 80/tcp`, `allow 443/tcp` e `enable`. O `limit` recusa um endereço que abra mais de seis conexões em 30 segundos, o que freia as tentativas em massa sem atrapalhar o workflow, que abre duas.

   A regra de implantação 3 prefere o SSH acessível só por uma rede privada (Tailscale). A instalação atual deixa a porta 22 aberta para a internet, só com chave e com atualizações de segurança automáticas: para um servidor administrado por uma pessoa, a rede privada traria mais peças (conta, política, cliente OAuth, segredos) do que proteção. Se um dia isso mudar, a VM entra na rede Tailscale, o workflow ganha o passo [`tailscale/github-action`](https://github.com/tailscale/github-action) e a porta 22 sai da internet.
3. **Pacotes**: `openjdk-21-jre-headless` e `postgresql` do Ubuntu; o Caddy do repositório oficial ([Caddy](https://caddyserver.com/docs/install#debian-ubuntu-raspbian)). Versões instaladas (regra de implantação 8):

   | Pacote | Versão |
   |---|---|
   | OpenJDK | 21.0.13 (`openjdk-21-jre-headless` 21.0.13.0.3+1-1~26.04.1) |
   | PostgreSQL | 18.6 (`postgresql-18` 18.6-0ubuntu0.26.04.1) |
   | Caddy | 2.11.9 |

   O PostgreSQL embarcado dos testes tem a mesma versão principal (`embedded-postgres-binaries.version` no `pom.xml`, regra de implantação 4). Ao atualizar um, atualize o outro.

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

   - segredos: `/etc/airlab/airlab.env`, com dono `root` e permissão `600`, a partir do [`.env.example`](../.env.example) (regra de implantação 7). Nele vão `AIRLAB_BASE_URL=https://app.crivo.psigama.com`, o endereço da aplicação, usado nos links dos e-mails; `AIRLAB_SITE_URL=https://crivo.psigama.com`, o endereço do site, que faz a aplicação apontar para ele e responder às rotas do site com 301 — só depois da virada (seção 6); até lá, deixe a variável vazia ou comentada, e a aplicação continua servindo as páginas do site; `AIRLAB_DB_USER=airlab_app` com a senha desse papel; `SPRING_FLYWAY_USER=airlab_owner` e `SPRING_FLYWAY_PASSWORD` com a do dono, que o Spring Boot usa só nas migrações; e a `AIRLAB_IDENTITY_KEY`, gerada com `openssl rand -base64 32`. **Guarde uma cópia da chave fora da VM**: sem ela, os dados cifrados do esquema `identity` não se recuperam. O `AIRLAB_RESEARCHER_PASSWORD_HASH` tem de estar preenchido antes da primeira implantação. Sem SMTP, o `SPRING_MAIL_HOST` fica vazio ou comentado: o Crivo trata as duas formas como sem e-mail e mantém a inscrição do P1 fechada, e o e-mail fica fora do `/actuator/health`, que o deploy confere;
   - serviço: copie [`scripts/airlab.service.example`](../scripts/airlab.service.example) para `/etc/systemd/system/airlab.service` e rode `sudo systemctl daemon-reload && sudo systemctl enable airlab`. O serviço só sobe quando houver um JAR em `/opt/airlab/airlab.jar`, o que a primeira implantação resolve.
5. **Caddy** (`/etc/caddy/Caddyfile`), com HTTPS automático e só as rotas da aplicação expostas:

   ```caddyfile
   app.crivo.psigama.com {
   	@interno path /actuator /actuator/*
   	respond @interno 404
   	reverse_proxy 127.0.0.1:8080
   }
   ```

   Até a virada (seção 6), o mesmo bloco atende também `crivo.psigama.com`: escreva `app.crivo.psigama.com, crivo.psigama.com {` na primeira linha.

6. **DNS**: um registro `A` de `app.crivo.psigama.com` para o IP público da VM. Enquanto ele não existir, o Caddy não obtém o certificado e tenta de novo sozinho. O `crivo.psigama.com` aponta para o GitHub Pages depois da virada (seção 6).

## 2. Usuário de implantação

O workflow entra na VM como um usuário próprio, `deploy`, que só pode trocar o JAR e reiniciar o serviço.

1. Gere um par de chaves só para isso, fora da VM e sem senha, porque o workflow não tem como digitá-la: `ssh-keygen -t ed25519 -N '' -f crivo-deploy -C github-actions-crivo`. No Windows, deixe a chave privada legível só pelo seu usuário (o SSH do Windows recusa chave que outra conta lê).
2. Na VM: `sudo adduser --disabled-password --gecos "" deploy` e coloque `restrict` seguido do conteúdo de `crivo-deploy.pub` em `/home/deploy/.ssh/authorized_keys`, com permissão `600`. O `restrict` corta encaminhamento de porta, de agente e X11 e o terminal; o `scp` e o `bash -s` do workflow continuam funcionando.
3. Crie `/etc/sudoers.d/airlab-deploy` com `sudo visudo -f /etc/sudoers.d/airlab-deploy`:

   ```text
   deploy ALL=(root) NOPASSWD: /usr/bin/install -o airlab -g airlab -m 0644 /opt/airlab/airlab.jar /opt/airlab/airlab.jar.previous, \
     /usr/bin/install -o airlab -g airlab -m 0644 /home/deploy/airlab.jar.new /opt/airlab/airlab.jar, \
     /usr/bin/install -o airlab -g airlab -m 0644 /opt/airlab/airlab.jar.previous /opt/airlab/airlab.jar, \
     /usr/bin/systemctl restart airlab
   ```

   Os comandos e os caminhos são exatamente os que o workflow usa. Se mudar o nome do usuário, ajuste o caminho `/home/deploy`.

## 3. GitHub

**Ambiente `production`** (repositório → *Settings* → *Environments*): crie o ambiente e, se quiser uma segunda trava, marque *Required reviewers*. Cadastre nele os segredos:

| Segredo | Valor |
|---|---|
| `DEPLOY_HOST` | IP público da VM |
| `DEPLOY_USER` | `deploy` |
| `DEPLOY_SSH_KEY` | Conteúdo da chave privada `crivo-deploy` |
| `DEPLOY_KNOWN_HOSTS` | A linha `ed25519` que `ssh-keyscan -t ed25519 <DEPLOY_HOST>` devolve, conferida contra a impressão digital que a própria VM mostra (`ssh-keygen -lf /etc/ssh/ssh_host_ed25519_key.pub`). Fixa a identidade da VM: se ela mudar, a implantação para |

Nenhum desses valores vai para o repositório (regra de implantação 7).

**Variável do repositório** (*Settings* → *Secrets and variables* → *Actions* → *Variables*): `APP_URL`, com o endereço da aplicação, `https://app.crivo.psigama.com`. Não é segredo. O `pages.yml` a usa nos links do site para a aplicação e não publica nada enquanto ela não existir; o `deploy.yml` a mostra como endereço do ambiente `production`.

## 4. Implantar

1. Confirme que nenhuma sessão de coleta está em andamento. Se houver estudo correndo, a atualização precisa de registro na trilha de auditoria (regra de implantação 8).
2. No GitHub: *Actions* → **Deploy** → *Run workflow*, escolha o ramo (normalmente `main`) e marque a confirmação.
3. O workflow roda a suíte de novo, guarda o JAR com o seu SHA-256 como artefato da execução, envia, troca e espera `/actuator/health` responder `UP` em até dois minutos. Se a aplicação não ficar saudável, ele recoloca a versão anterior e falha.

**Voltar à versão anterior à mão**, na VM:

```bash
sudo install -o airlab -g airlab -m 0644 /opt/airlab/airlab.jar.previous /opt/airlab/airlab.jar
sudo systemctl restart airlab
```

## 5. Site no GitHub Pages

O `pages.yml` roda **apenas** a cada push na `main`, inclusive os que só mexem em documentação, porque publicar de novo o mesmo site não muda nada. Ele compila o JAR sem a suíte, que o CI roda no mesmo push. Depois, gera o site e o publica:

```bash
java -jar target/airlab.jar site target/site --aplicacao=https://app.crivo.psigama.com
```

O mesmo comando roda na sua máquina, pelos scripts da CLI, para ver o site antes de publicar:

```bash
./scripts/airlab.sh site target/site --aplicacao=http://127.0.0.1:8080
```

```powershell
.\scripts\airlab.ps1 site target\site --aplicacao=http://127.0.0.1:8080
```

O gerador não sobe o Spring nem o banco. Ele só escreve as páginas listadas em `web.SitePages`, o CSS, o favicon, o `js/encaminhar.js` e o `404.html`, que leva qualquer endereço desconhecido para a aplicação com o mesmo caminho e a mesma consulta.

Desde a SPEC 0.28 (seção 1.5) ele escreve cada página **duas vezes**: em português no endereço de sempre e em inglês sob `en/` — `garantias.html` e `en/garantias.html`. Vai junto o `js/idioma.js`, que leva ao inglês quem chega pela primeira vez com o navegador noutra língua, porque o GitHub Pages não negocia idioma. Uma página que ainda não tem tradução revisada sai em português com um aviso de uma linha, e não falta no site. O `404.html` é um só, e o script dele nunca encaminha para a aplicação um endereço do próprio site, em nenhum dos dois idiomas.

Uma vez, no GitHub:

1. *Settings* → *Pages* → *Build and deployment* → *Source*: **GitHub Actions**.
2. *Settings* → *Pages* → *Custom domain*: `crivo.psigama.com`. Com publicação por Actions, o arquivo `CNAME` não é usado nem precisa existir. Marque **Enforce HTTPS** quando o certificado ficar pronto.
3. Nas configurações da sua conta, *Pages* → *Verified domains*: verifique `psigama.com`, com o registro `TXT` que o GitHub mostra (nome `_github-pages-challenge-rogeriocisi.psigama.com`). Isso impede que outra conta publique em `crivo.psigama.com` se um dia o Pages deste repositório for desligado.
4. *Settings* → *Environments* → `github-pages`: confira que só a `main` pode publicar.
5. Crie a variável `APP_URL` (seção 3). Sem ela, o `pages.yml` termina sem publicar e avisa no resumo da execução.

## 6. A virada: `crivo.psigama.com` passa a ser o site

Uma vez, **entre sessões de coleta**. Testes de ESP e entradas em andamento na hora da virada se perdem, porque o cookie de sessão é do endereço antigo.

0. Na véspera, baixe o TTL do registro de `crivo.psigama.com` para 300 segundos, para que a troca do passo 5 se espalhe em minutos.
1. Implante a versão com a SPEC 0.21 pelo `deploy.yml`. Sem `AIRLAB_SITE_URL`, ela se comporta como antes.
2. DNS: crie o `A` de `app.crivo.psigama.com` para o IP da VM. No Caddy, o bloco atende os dois nomes (seção 1, passo 5). Confira `https://app.crivo.psigama.com/protocolos`.
3. Em `/etc/airlab/airlab.env`: `AIRLAB_BASE_URL=https://app.crivo.psigama.com`, **ainda sem** `AIRLAB_SITE_URL`. Depois, `sudo systemctl restart airlab`.
4. Configure o Pages e a variável `APP_URL` (seção 5) e faça um push na `main` para publicar o site.
5. DNS: troque o `A` de `crivo.psigama.com` por um `CNAME` para `rogeriocisi.github.io`. Um nome com `CNAME` não pode ter outro registro: apague antes o `A` (e o `AAAA`, se houver). Enquanto o GitHub emite o certificado, o endereço pode ficar algum tempo sem HTTPS.
6. Espere a troca se espalhar e o site responder com HTTPS em `https://crivo.psigama.com/participar`. Só então defina `AIRLAB_SITE_URL=https://crivo.psigama.com` e reinicie a aplicação. **A ordem importa**: enquanto `crivo.psigama.com` ainda chega à VM, o Caddy o entrega à própria aplicação, e com `AIRLAB_SITE_URL` definida ela responderia com 301 para si mesma, num laço.
7. Tire `crivo.psigama.com` do bloco do Caddy e recarregue (`sudo systemctl reload caddy`).
8. No repositório `psigama`, troque os links para as páginas da aplicação (`/protocolos`, `/protocolo/P1` a `/protocolo/P4` e as rotas `/rodar`) pelo endereço novo. O `404.html` do site já os encaminha, mas o link direto evita o salto.

## Fora destes workflows

- **Backups** (regra de implantação 5): dump diário criptografado para fora da VM e teste mensal de restauração. Precisam de um roteiro próprio.

## Dependências do GitHub Actions

Só os workflows as usam; nenhuma entra na aplicação (P8). As versões principais ficam fixadas nos arquivos.

| Ação | Mantenedor | Para quê |
|---|---|---|
| `actions/checkout`, `actions/setup-java`, `actions/upload-artifact`, `actions/download-artifact` | GitHub | Baixar o código, instalar o JDK e guardar o JAR entre os jobs |
| `actions/configure-pages`, `actions/upload-pages-artifact`, `actions/deploy-pages` | GitHub | Publicar o site estático no GitHub Pages (`pages.yml`) |
