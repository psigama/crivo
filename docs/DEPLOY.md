# Integração contínua e implantação

Como o Crivo é testado no GitHub e implantado na VM de produção, em `https://crivo.psigama.com`. As regras que valem acima deste roteiro estão na [Constitution](CONSTITUTION.md), em *Implantação por fase*.

## Visão geral

| Peça | Onde | O que faz |
|---|---|---|
| [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) | GitHub Actions | A cada push na `main` e a cada *pull request*, compila e roda a suíte completa, inclusive a matriz de cegamento e a integridade da trilha, com o PostgreSQL embarcado |
| [`.github/workflows/deploy.yml`](../.github/workflows/deploy.yml) | GitHub Actions, acionado à mão | Constrói e testa o JAR, entra na rede Tailscale, envia o JAR para a VM, troca a versão e confere a saúde; se a aplicação não subir, volta para a versão anterior |
| VM de produção | VPS KVM da Hostinger no data center de São Paulo (Brasil): Ubuntu 26.04 LTS x86_64, 1 vCPU, 4 GB de RAM | Linux, Java 21, PostgreSQL do sistema, a aplicação como serviço `systemd` (`airlab`) e o Caddy na borda |

A implantação **nunca é automática**. A regra de implantação 8 só permite atualizar a aplicação entre sessões de coleta, e o formulário do workflow exige que quem o aciona confirme isso.

## 1. Preparar a VM

Uma vez, na VM. A instalação atual, de 15/11/2026, é uma VPS KVM da Hostinger no data center de São Paulo, com Ubuntu 26.04 LTS (x86_64). Os dados ficam no Brasil, como prefere a regra de implantação 1.

1. **Acesso**: só por chave SSH. A do administrador (`crivo-admin`) é cadastrada no painel da Hostinger. Desligue o login por senha num arquivo lido antes dos outros em `/etc/ssh/sshd_config.d/` (no `sshd`, vale o primeiro valor lido, e a imagem traz um `50-cloud-init.conf` que liga a senha), por exemplo `00-psigama.conf`:

   ```text
   PasswordAuthentication no
   KbdInteractiveAuthentication no
   PermitRootLogin prohibit-password
   ```

2. **Firewall** (`ufw`): `default deny incoming`, `allow 22/tcp`, `allow 80/tcp`, `allow 443/tcp` e `enable`. Quando o Tailscale estiver no ar, tire a porta 22 da internet (`ufw allow in on tailscale0 to any port 22 proto tcp` e depois `ufw delete allow 22/tcp`), como pede a regra de implantação 3.
3. **Pacotes**: `openjdk-21-jre-headless` e `postgresql` do Ubuntu; o Caddy e o Tailscale dos repositórios oficiais ([Caddy](https://caddyserver.com/docs/install#debian-ubuntu-raspbian), [Tailscale](https://tailscale.com/download/linux/ubuntu-2404)). Versões instaladas (regra de implantação 8):

   | Pacote | Versão |
   |---|---|
   | OpenJDK | 21.0.13 (`openjdk-21-jre-headless` 21.0.13.0.3+1-1~26.04.1) |
   | PostgreSQL | 18.6 (`postgresql-18` 18.6-0ubuntu0.26.04.1) |
   | Caddy | 2.11.9 |
   | Tailscale | 1.104.7 |

   O PostgreSQL embarcado dos testes tem a mesma versão principal (`embedded-postgres-binaries.version` no `pom.xml`, regra de implantação 4). Ao atualizar um, atualize o outro.

4. **Tailscale**: `sudo tailscale up --advertise-tags=tag:crivo`, depois de declarar o tag na política (passo 3, item 1). O nome da VM na rede Tailscale (ou o IP `100.x.y.z`) é o `DEPLOY_HOST` do passo 3.
5. **Aplicação**:
   - usuário de serviço: `sudo useradd --system --home /opt/airlab --shell /usr/sbin/nologin airlab`;
   - diretórios: `sudo install -d -o airlab -g airlab -m 0750 /opt/airlab` e `sudo install -d -o airlab -g airlab -m 0700 /opt/airlab/data`;
   - banco: o PostgreSQL escuta só em `127.0.0.1`, com `listen_addresses = '127.0.0.1'` em `/etc/postgresql/18/main/conf.d/crivo.conf` (regra de implantação 4). São dois papéis: `airlab_owner`, dono do banco, com o qual o Flyway roda as migrações; e `airlab_app`, com o qual a aplicação conecta e que a migração `V10` reduz ao privilégio mínimo (na trilha, só `SELECT` e `INSERT`). A `V10` só cria o `airlab_app` se ele não existir, sem login; por isso ele é criado antes, já com login. Com `sudo -u postgres psql`, e senhas geradas por `openssl rand -hex 32`:

     ```sql
     CREATE ROLE airlab_owner LOGIN PASSWORD '...';
     CREATE ROLE airlab_app LOGIN PASSWORD '...';
     CREATE DATABASE airlab OWNER airlab_owner ENCODING 'UTF8' TEMPLATE template0;
     REVOKE ALL ON DATABASE airlab FROM PUBLIC;
     GRANT CONNECT ON DATABASE airlab TO airlab_app;
     ```

   - segredos: `/etc/airlab/airlab.env`, com dono `root` e permissão `600`, a partir do [`.env.example`](../.env.example) (regra de implantação 7). Nele vão `AIRLAB_BASE_URL=https://crivo.psigama.com`; `AIRLAB_DB_USER=airlab_app` com a senha desse papel; `SPRING_FLYWAY_USER=airlab_owner` e `SPRING_FLYWAY_PASSWORD` com a do dono, que o Spring Boot usa só nas migrações; e a `AIRLAB_IDENTITY_KEY`, gerada com `openssl rand -base64 32`. **Guarde uma cópia da chave fora da VM**: sem ela, os dados cifrados do esquema `identity` não se recuperam. O `AIRLAB_RESEARCHER_PASSWORD_HASH` tem de estar preenchido antes da primeira implantação;
   - serviço: copie [`scripts/airlab.service.example`](../scripts/airlab.service.example) para `/etc/systemd/system/airlab.service` e rode `sudo systemctl daemon-reload && sudo systemctl enable airlab`. O serviço só sobe quando houver um JAR em `/opt/airlab/airlab.jar`, o que a primeira implantação resolve.
6. **Caddy** (`/etc/caddy/Caddyfile`), com HTTPS automático e só as rotas da aplicação expostas:

   ```caddyfile
   crivo.psigama.com {
   	@interno path /actuator /actuator/*
   	respond @interno 404
   	reverse_proxy 127.0.0.1:8080
   }
   ```

7. **DNS**: um registro `A` de `crivo.psigama.com` para o IP público da VM. Enquanto ele não existir, o Caddy não obtém o certificado e tenta de novo sozinho.

## 2. Usuário de implantação

O workflow entra na VM como um usuário próprio, `deploy`, que só pode trocar o JAR e reiniciar o serviço.

1. Gere um par de chaves só para isso, fora da VM: `ssh-keygen -t ed25519 -f crivo-deploy -C github-actions-crivo`.
2. Na VM: `sudo adduser --disabled-password --gecos "" deploy` e coloque o conteúdo de `crivo-deploy.pub` em `/home/deploy/.ssh/authorized_keys`, com permissão `600`.
3. Crie `/etc/sudoers.d/airlab-deploy` com `sudo visudo -f /etc/sudoers.d/airlab-deploy`:

   ```text
   deploy ALL=(root) NOPASSWD: /usr/bin/install -o airlab -g airlab -m 0644 /opt/airlab/airlab.jar /opt/airlab/airlab.jar.previous, \
     /usr/bin/install -o airlab -g airlab -m 0644 /home/deploy/airlab.jar.new /opt/airlab/airlab.jar, \
     /usr/bin/install -o airlab -g airlab -m 0644 /opt/airlab/airlab.jar.previous /opt/airlab/airlab.jar, \
     /usr/bin/systemctl restart airlab
   ```

   Os comandos e os caminhos são exatamente os que o workflow usa. Se mudar o nome do usuário, ajuste o caminho `/home/deploy`.

## 3. Tailscale e GitHub

1. **Política do Tailscale** (painel de administração → *Access controls*): declare os dois tags e libere só o SSH de `tag:ci` para `tag:crivo`.

   ```json
   "tagOwners": {
     "tag:ci": ["autogroup:admin"],
     "tag:crivo": ["autogroup:admin"]
   },
   "grants": [
     { "src": ["tag:ci"], "dst": ["tag:crivo"], "ip": ["tcp:22"] }
   ]
   ```

2. **Cliente OAuth do Tailscale** (painel → *Settings* → *OAuth clients*): com o escopo de escrita de chaves de autenticação (`auth_keys`) e o tag `tag:ci`. Cada execução do workflow entra na rede como um nó efêmero, que some ao terminar.
3. **Ambiente `production` no GitHub** (repositório → *Settings* → *Environments*): crie o ambiente e, se quiser uma segunda trava, marque *Required reviewers*. Cadastre nele os segredos:

| Segredo | Valor |
|---|---|
| `DEPLOY_HOST` | Nome da VM na rede Tailscale ou o IP `100.x.y.z` |
| `DEPLOY_USER` | `deploy` |
| `DEPLOY_SSH_KEY` | Conteúdo da chave privada `crivo-deploy` |
| `DEPLOY_KNOWN_HOSTS` | Saída de `ssh-keyscan -t ed25519 <DEPLOY_HOST>`, rodado de uma máquina da rede Tailscale. Fixa a identidade da VM: se ela mudar, a implantação para |
| `TS_OAUTH_CLIENT_ID` | ID do cliente OAuth do Tailscale |
| `TS_OAUTH_SECRET` | Segredo do cliente OAuth do Tailscale |

Nenhum desses valores vai para o repositório (regra de implantação 7).

## 4. Implantar

1. Confirme que nenhuma sessão de coleta está em andamento. Se houver estudo correndo, a atualização precisa de registro na trilha de auditoria (regra de implantação 8).
2. No GitHub: *Actions* → **Deploy** → *Run workflow*, escolha o ramo (normalmente `main`) e marque a confirmação.
3. O workflow roda a suíte de novo, guarda o JAR com o seu SHA-256 como artefato da execução, envia, troca e espera `/actuator/health` responder `UP` em até dois minutos. Se a aplicação não ficar saudável, ele recoloca a versão anterior e falha.

**Voltar à versão anterior à mão**, na VM:

```bash
sudo install -o airlab -g airlab -m 0644 /opt/airlab/airlab.jar.previous /opt/airlab/airlab.jar
sudo systemctl restart airlab
```

## Fora destes workflows

- **Backups** (regra de implantação 5): dump diário criptografado para fora da VM e teste mensal de restauração. Precisam de um roteiro próprio.

## Dependências do GitHub Actions

Só os workflows as usam; nenhuma entra na aplicação (P8). As versões principais ficam fixadas nos arquivos.

| Ação | Mantenedor | Para quê |
|---|---|---|
| `actions/checkout`, `actions/setup-java`, `actions/upload-artifact`, `actions/download-artifact` | GitHub | Baixar o código, instalar o JDK e guardar o JAR entre os jobs |
| `tailscale/github-action` | Tailscale | Entrar na rede Tailscale para chegar ao SSH da VM sem abri-lo para a internet |
