#!/usr/bin/env bash
# Prepara a VPS para a instalação única do Crivo: a pasta do site, o rsync, o endereço público na
# configuração da aplicação e o Caddy na borda (SPEC 0.29, seção 1.3; docs/DEPLOY.md, seção 6).
#
# Roda NA VPS, como root, e pode rodar de novo quantas vezes quiser: cada passo confere antes de
# mexer e não refaz o que já está feito. Ele não toca em DNS nem em segredo do GitHub, que são de
# fora, e não cria o usuário de serviço, o banco nem o serviço systemd, que são a instalação
# primeira (docs/DEPLOY.md, seção 1).
#
# Copie a pasta deploy/ para a VPS e rode de lá, porque o Caddyfile sai do exemplo que está nela:
#
#   scp -r deploy <seu-usuário>@<vps>:~/
#   sudo bash deploy/preparar-vps.sh              confere e prepara, sem mexer no que está no ar
#   sudo bash deploy/preparar-vps.sh --aplicar     troca o Caddyfile e reinicia a aplicação
#
# Sem --aplicar, nada do que está servindo muda: o script cria o que falta, escreve o Caddyfile
# novo ao lado do atual, valida-o e diz o que fazer. Com --aplicar, ele troca a borda (com cópia do
# anterior e volta atrás se o Caddy recusar) e reinicia a aplicação — e por isso é opt-in: reiniciar
# a aplicação só se faz entre sessões de coleta (Constitution, regra de implantação 8).
#
# Os nomes e caminhos vêm de variável de ambiente, e os padrões são os da instalação atual. Eles
# têm de bater com as Variables do ambiente production no GitHub (docs/DEPLOY.md, seção 3):
#
#   DOMINIO=crivo.psigama.com         DOMINIO_ANTIGO=app.crivo.psigama.com
#   SITE_DIR=/var/www/crivo           DEPLOY_USER=deploy        APP_USER=airlab
#   JAR_PATH=/opt/airlab/airlab.jar   SERVICE=airlab            ENV_FILE=/etc/airlab/airlab.env
#   HEALTH_URL=http://127.0.0.1:8080/actuator/health

set -euo pipefail

DOMINIO=${DOMINIO:-crivo.psigama.com}
DOMINIO_ANTIGO=${DOMINIO_ANTIGO:-app.crivo.psigama.com}
SITE_DIR=${SITE_DIR:-/var/www/crivo}
DEPLOY_USER=${DEPLOY_USER:-deploy}
APP_USER=${APP_USER:-airlab}
JAR_PATH=${JAR_PATH:-/opt/airlab/airlab.jar}
SERVICE=${SERVICE:-airlab}
ENV_FILE=${ENV_FILE:-/etc/airlab/airlab.env}
HEALTH_URL=${HEALTH_URL:-http://127.0.0.1:8080/actuator/health}
CADDYFILE=${CADDYFILE:-/etc/caddy/Caddyfile}

APLICAR=nao
case "${1:-}" in
  --aplicar) APLICAR=sim ;;
  "") ;;
  *) echo "uso: $0 [--aplicar]" >&2; exit 2 ;;
esac

AQUI=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
EXEMPLO_CADDY="$AQUI/Caddyfile.example"
PENDENCIAS=()

passo() { printf '\n== %s\n' "$1"; }
ok()    { printf '   ok    %s\n' "$1"; }
feito() { printf '   feito %s\n' "$1"; }
aviso() { printf '   ATENÇÃO %s\n' "$1"; PENDENCIAS+=("$1"); }
erro()  { printf 'erro: %s\n' "$1" >&2; exit 1; }

[ "$(id -u)" = "0" ] || erro "rode como root (sudo bash $0)"
command -v systemctl >/dev/null || erro "sem systemd: este script é para a VPS Linux da produção"
id "$DEPLOY_USER" >/dev/null 2>&1 || erro "o usuário de implantação '$DEPLOY_USER' não existe (docs/DEPLOY.md, seção 2)"
id "$APP_USER" >/dev/null 2>&1 || erro "o usuário de serviço '$APP_USER' não existe (docs/DEPLOY.md, seção 1)"

passo "rsync, que a implantação precisa nas duas pontas"
if command -v rsync >/dev/null; then
  ok "já instalado ($(rsync --version | head -1 | cut -d' ' -f1-3))"
else
  apt-get update -qq && apt-get install -y -qq rsync
  feito "instalado"
fi

passo "pasta do site: $SITE_DIR"
if [ -d "$SITE_DIR" ]; then
  ok "já existe"
else
  install -d -o "$DEPLOY_USER" -g "$DEPLOY_USER" -m 0755 "$SITE_DIR"
  feito "criada, dona $DEPLOY_USER"
fi
# Quem escreve é o usuário de implantação, pelo rsync; o Caddy só lê.
if sudo -u "$DEPLOY_USER" test -w "$SITE_DIR"; then
  ok "$DEPLOY_USER escreve nela"
else
  chown -R "$DEPLOY_USER:$DEPLOY_USER" "$SITE_DIR"
  chmod 0755 "$SITE_DIR"
  feito "dono corrigido para $DEPLOY_USER"
fi

passo "o JAR atual visível ao $DEPLOY_USER, para a volta atrás"
diretorio_jar=$(dirname "$JAR_PATH")
if sudo -u "$DEPLOY_USER" test -x "$diretorio_jar"; then
  ok "$diretorio_jar é atravessável"
else
  aviso "o $DEPLOY_USER não entra em $diretorio_jar: a cópia .previous nunca é feita e não há versão para onde voltar (docs/DEPLOY.md, seção 1, passo 4)"
fi

passo "sudoers do $DEPLOY_USER: os comandos exatos da troca de versão"
arquivo_sudoers=/etc/sudoers.d/${SERVICE}-deploy
if [ -f "$arquivo_sudoers" ]; then
  for comando in "$JAR_PATH $JAR_PATH.previous" "$JAR_PATH.previous $JAR_PATH" "systemctl restart $SERVICE"; do
    if grep -qF -- "$comando" "$arquivo_sudoers"; then
      ok "autoriza: $comando"
    else
      aviso "falta no $arquivo_sudoers: $comando"
    fi
  done
else
  aviso "não existe $arquivo_sudoers (docs/DEPLOY.md, seção 2, passo 3)"
fi

passo "endereço público na configuração da aplicação: $ENV_FILE"
definir_chave() {
  local arquivo=$1 chave=$2 valor=$3
  if grep -qE "^[#[:space:]]*${chave}=" "$arquivo"; then
    sed -i -E "s|^[#[:space:]]*${chave}=.*|${chave}=${valor}|" "$arquivo"
  else
    printf '%s=%s\n' "$chave" "$valor" >> "$arquivo"
  fi
}
if [ -f "$ENV_FILE" ]; then
  esperado_base="https://$DOMINIO"
  atual_base=$(sed -nE 's|^AIRLAB_BASE_URL=(.*)$|\1|p' "$ENV_FILE" | tail -1)
  atual_site=$(sed -nE 's|^AIRLAB_SITE_URL=(.*)$|\1|p' "$ENV_FILE" | tail -1)
  if [ "$atual_base" = "$esperado_base" ] && [ -z "$atual_site" ]; then
    ok "já está assim (AIRLAB_BASE_URL=$esperado_base, AIRLAB_SITE_URL vazia)"
  else
    copia="$ENV_FILE.bak-$(date +%Y%m%d%H%M%S)"
    cp -p "$ENV_FILE" "$copia"
    # AIRLAB_SITE_URL fica VAZIA: o site mora aqui mesmo, e apontada para este endereço a
    # aplicação responderia às rotas editoriais com 301 para si mesma (SPEC 1.3, regra 6).
    definir_chave "$ENV_FILE" AIRLAB_BASE_URL "$esperado_base"
    definir_chave "$ENV_FILE" AIRLAB_SITE_URL ""
    chown root:root "$ENV_FILE"; chmod 600 "$ENV_FILE"
    feito "escrito (cópia do anterior em $copia)"
    grep -E '^(AIRLAB_BASE_URL|AIRLAB_SITE_URL)=' "$ENV_FILE" | sed 's/^/         /'
    [ "$APLICAR" = sim ] || aviso "a aplicação só lê isso ao reiniciar: rode de novo com --aplicar, ou systemctl restart $SERVICE"
  fi
else
  aviso "não existe $ENV_FILE (docs/DEPLOY.md, seção 1, passo 4)"
fi

passo "Caddy na borda: $DOMINIO servindo o site e a aplicação"
if [ ! -f "$EXEMPLO_CADDY" ]; then
  aviso "não achei o $EXEMPLO_CADDY ao lado deste script: copie a pasta deploy/ inteira para a VPS"
elif ! command -v caddy >/dev/null; then
  aviso "o Caddy não está instalado (docs/DEPLOY.md, seção 1, passo 3)"
else
  novo="$CADDYFILE.novo"
  sed -e "s|crivo\.psigama\.com|$DOMINIO|g" -e "s|app\.$DOMINIO|$DOMINIO_ANTIGO|g" \
      -e "s|/var/www/crivo|$SITE_DIR|g" "$EXEMPLO_CADDY" > "$novo"
  if caddy validate --config "$novo" >/dev/null 2>&1; then
    ok "configuração nova válida ($novo)"
  else
    caddy validate --config "$novo" 2>&1 | sed 's/^/         /' || true
    erro "o Caddy recusou a configuração nova; nada foi trocado"
  fi
  if [ -f "$CADDYFILE" ] && cmp -s "$novo" "$CADDYFILE"; then
    ok "o Caddyfile no ar já é este"
    rm -f "$novo"
  elif [ "$APLICAR" = sim ]; then
    copia=""
    if [ -f "$CADDYFILE" ]; then
      copia="$CADDYFILE.bak-$(date +%Y%m%d%H%M%S)"
      cp -p "$CADDYFILE" "$copia"
      feito "cópia do anterior em $copia"
    fi
    mv "$novo" "$CADDYFILE"
    if systemctl reload caddy; then
      feito "borda trocada e recarregada"
    else
      if [ -n "$copia" ]; then
        cp -p "$copia" "$CADDYFILE"
        systemctl reload caddy || true
      fi
      erro "o reload falhou; o Caddyfile anterior foi recolocado"
    fi
  else
    aviso "o Caddyfile novo está em $novo, validado, mas NÃO foi aplicado: rode com --aplicar (ou mova à mão e recarregue)"
  fi
fi

if [ "$APLICAR" = sim ]; then
  passo "reiniciar a aplicação e esperar a saúde"
  systemctl restart "$SERVICE"
  saudavel=nao
  for _ in $(seq 1 40); do
    if curl -fsS --max-time 10 "$HEALTH_URL" | grep -q '"status":"UP"'; then saudavel=sim; break; fi
    sleep 3
  done
  if [ "$saudavel" = sim ]; then
    feito "$SERVICE no ar e saudável"
  else
    aviso "o $SERVICE não respondeu UP em $HEALTH_URL (journalctl -u $SERVICE -n 50)"
  fi
fi

printf '\n== o que falta, e não é daqui\n'
printf '   1. DNS: apagar o CNAME de %s e criar um A para o IP desta VPS\n' "$DOMINIO"
printf '   2. GitHub: o segredo DEPLOY_SSH_KEY no ambiente production (é o interruptor da implantação)\n'
printf '   3. o primeiro push na main, que troca o JAR e espelha o site em %s\n' "$SITE_DIR"
if [ ${#PENDENCIAS[@]} -gt 0 ]; then
  printf '\n== pendências nesta VPS\n'
  for pendencia in "${PENDENCIAS[@]}"; do printf '   - %s\n' "$pendencia"; done
  exit 1
fi
printf '\n== a VPS está pronta para a instalação única\n'
