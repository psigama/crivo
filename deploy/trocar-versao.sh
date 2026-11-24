#!/usr/bin/env bash
# Troca a versão da aplicação na VPS, reinicia o serviço e confere a saúde; se a versão nova não
# ficar saudável, recoloca a anterior e falha.
#
# Roda na VPS, não na máquina de quem desenvolve: o job de implantação de
# .github/workflows/ci.yml o entrega ao `bash -s` da VPS pelo SSH, com três variáveis, que vêm das
# Variables do ambiente `production` (docs/DEPLOY.md, seção 3):
#
#   JAR_PATH     o JAR da aplicação na VPS                  (DEPLOY_JAR_PATH)
#   SERVICE      o serviço systemd                          (DEPLOY_SERVICE)
#   HEALTH_URL   o /actuator/health, em 127.0.0.1            (DEPLOY_HEALTH_URL)
#
# O JAR novo já está em ~/airlab.jar.new, enviado pelo passo anterior do workflow. Os comandos com
# sudo são exatamente as linhas de /etc/sudoers.d/airlab-deploy (docs/DEPLOY.md, seção 2): fora
# delas, o usuário de implantação não faz nada como root. Por isso o dono do arquivo (`airlab`) e o
# modo (`0644`) estão escritos aqui, e não em variável: eles fazem parte do comando autorizado.

set -euo pipefail

: "${JAR_PATH:?falta a variável JAR_PATH (DEPLOY_JAR_PATH)}"
: "${SERVICE:?falta a variável SERVICE (DEPLOY_SERVICE)}"
: "${HEALTH_URL:?falta a variável HEALTH_URL (DEPLOY_HEALTH_URL)}"

novo="$HOME/airlab.jar.new"
anterior="$JAR_PATH.previous"

if [ ! -f "$novo" ]; then
  echo "o JAR novo não chegou em $novo" >&2
  exit 1
fi

# A cópia da versão que está no ar, para a volta atrás. Sem ela — primeira implantação, ou
# /opt/airlab fechado ao usuário de implantação — não há versão para onde voltar.
if [ -f "$JAR_PATH" ]; then
  sudo /usr/bin/install -o airlab -g airlab -m 0644 "$JAR_PATH" "$anterior"
fi

sudo /usr/bin/install -o airlab -g airlab -m 0644 "$novo" "$JAR_PATH"
sudo /usr/bin/systemctl restart "$SERVICE"

# Exige o UP no corpo, e não só um curl que não falhou: um 3xx — a tela de outra aplicação na mesma
# máquina, por exemplo — não é erro para o curl e passaria por saúde. Até dois minutos.
saudavel() {
  for _ in $(seq 1 40); do
    if curl -fsS --max-time 10 "$HEALTH_URL" | grep -q '"status":"UP"'; then
      return 0
    fi
    sleep 3
  done
  return 1
}

if saudavel; then
  rm -f "$novo"
  echo "Nova versão no ar e saudável."
  exit 0
fi

echo "A nova versão não ficou saudável; voltando para a anterior." >&2
if [ -f "$anterior" ]; then
  sudo /usr/bin/install -o airlab -g airlab -m 0644 "$anterior" "$JAR_PATH"
  sudo /usr/bin/systemctl restart "$SERVICE"
fi
exit 1
