# Integração contínua e implantação

Como o Crivo é testado no GitHub e implantado na VM de produção, em `https://crivo.psigama.tec.br`. As regras que valem acima deste roteiro estão na [Constitution](CONSTITUTION.md), em *Implantação por fase*.

## Visão geral

| Peça | Onde | O que faz |
|---|---|---|
| [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) | GitHub Actions | A cada push na `main` e a cada *pull request*, compila e roda a suíte completa, inclusive a matriz de cegamento e a integridade da trilha, com o PostgreSQL embarcado |
| [`.github/workflows/deploy.yml`](../.github/workflows/deploy.yml) | GitHub Actions, acionado à mão | Constrói e testa o JAR, entra na rede Tailscale, envia o JAR para a VM, troca a versão e confere a saúde; se a aplicação não subir, volta para a versão anterior |
| VM de produção | Oracle Cloud (Always Free), região `us-phoenix-1` (Phoenix, EUA), nas condições da regra de implantação 1 | Linux, Java 21, PostgreSQL do sistema, a aplicação como serviço `systemd` (`airlab`) e o Caddy na borda |

A implantação **nunca é automática**. A regra de implantação 8 só permite atualizar a aplicação entre sessões de coleta, e o formulário do workflow exige que quem o aciona confirme isso.

## 1. Preparar a VM

Uma vez, na VM (Ubuntu 24.04; na Oracle Cloud, a forma Arm Ampere A1 do Always Free basta). A conta do projeto tem região inicial em Phoenix (EUA) e, no Always Free, só pode usar essa região. A Constitution 1.4.0 admite isso (regra de implantação 1) com uma condição: **nenhum dado pessoal real entra na VM antes de documentada a base legal da transferência internacional** (LGPD, art. 33), no termo de consentimento, na submissão ao CEP e no pré-registro. Até lá, não configure o envio de e-mail (`SPRING_MAIL_*`) no `airlab.env`: sem ele, a inscrição do P4 fica fechada em produção, e no ar ficam só os testes de PES, anônimos.

1. **Pacotes**: `openjdk-21-jre-headless`, `postgresql`, `curl`, o [Caddy](https://caddyserver.com/docs/install#debian-ubuntu-raspbian) e o [Tailscale](https://tailscale.com/download/linux). Registre as versões instaladas (regra de implantação 8).
2. **Firewall**: abra só as portas 80 e 443 para a internet, na lista de segurança da Oracle e no firewall do sistema. O SSH fica acessível apenas pela rede Tailscale (regra de implantação 3).
3. **Tailscale**: `sudo tailscale up --advertise-tags=tag:crivo`. O nome da VM na rede Tailscale (ou o IP `100.x.y.z`) é o `DEPLOY_HOST` do passo 3.
4. **Aplicação**:
   - usuário de serviço: `sudo useradd --system --home /opt/airlab --shell /usr/sbin/nologin airlab`;
   - diretórios: `sudo install -d -o airlab -g airlab /opt/airlab /opt/airlab/data`;
   - segredos: `/etc/airlab/airlab.env`, com dono `root` e permissão `600`, a partir do [`.env.example`](../.env.example), com `AIRLAB_BASE_URL=https://crivo.psigama.tec.br` (regra de implantação 7);
   - banco: PostgreSQL escutando só em `127.0.0.1`, com o banco e o usuário do `AIRLAB_DB_URL` e do `AIRLAB_DB_USER` (regra de implantação 4);
   - serviço: copie [`scripts/airlab.service.example`](../scripts/airlab.service.example) para `/etc/systemd/system/airlab.service` e rode `sudo systemctl daemon-reload && sudo systemctl enable airlab`. O serviço só sobe quando houver um JAR em `/opt/airlab/airlab.jar`, o que a primeira implantação resolve.
5. **Caddy** (`/etc/caddy/Caddyfile`), com HTTPS automático e só as rotas da aplicação expostas:

   ```caddyfile
   crivo.psigama.tec.br {
   	@interno path /actuator /actuator/*
   	respond @interno 404
   	reverse_proxy 127.0.0.1:8080
   }
   ```

6. **DNS**: um registro `A` de `crivo.psigama.tec.br` para o IP público da VM.

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
- **Criação do banco em produção**: o repositório ainda não documenta como o banco e os papéis (`airlab_app`, migração `V10`) são criados na primeira instalação.

## Dependências do GitHub Actions

Só os workflows as usam; nenhuma entra na aplicação (P8). As versões principais ficam fixadas nos arquivos.

| Ação | Mantenedor | Para quê |
|---|---|---|
| `actions/checkout`, `actions/setup-java`, `actions/upload-artifact`, `actions/download-artifact` | GitHub | Baixar o código, instalar o JDK e guardar o JAR entre os jobs |
| `tailscale/github-action` | Tailscale | Entrar na rede Tailscale para chegar ao SSH da VM sem abri-lo para a internet |
