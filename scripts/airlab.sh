#!/usr/bin/env bash
#
# Crivo (AIR Lab) — CLI de verificacao, Linux e macOS.
#
# Roda o mesmo artefato da aplicacao, sem servidor web, para conferir:
#   - a cadeia de hashes da trilha de auditoria (P4);
#   - o hash de cada versao do pre-registro (P2);
#   - a reproducao do sorteio a partir da semente revelada (P5).
#
# Uso:
#   ./scripts/airlab.sh verify
#   ./scripts/airlab.sh verify --study=STD-7Q4K2M9B
#   AIRLAB_PROFILE=prod ./scripts/airlab.sh verify
#
# Sai com codigo 0 se tudo confere e 1 caso contrario.
#
# O mesmo artefato tambem gera o site estatico com as paginas editoriais (SPEC 0.21, secao 1.3),
# sem banco e sem Spring, numa pasta vazia ou inexistente:
#   ./scripts/airlab.sh site target/site --aplicacao=http://127.0.0.1:8080
#
# E confere o pacote de verificacao de um estudo P1 (SPEC 0.22, E9) so a partir do arquivo, sem
# banco e sem servidor, baixado de /estudos/{codigo}/pacote.json:
#   ./scripts/airlab.sh verificar-pacote pacote-STD-7Q4K2M9B.json
#
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_DIR"

PROFILE="${AIRLAB_PROFILE:-local}"
JAR="target/airlab.jar"

if [[ -f .env ]]; then
  set -a
  # shellcheck disable=SC1091
  source .env
  set +a
fi

if [[ ! -f "$JAR" ]]; then
  echo "==> Construindo o JAR" >&2
  ./mvnw -B -q package -DskipTests
fi

JAVA="${JAVA_HOME:+$JAVA_HOME/bin/}java"

exec "$JAVA" -jar "$JAR" \
  "${@:-verify}" \
  --spring.profiles.active="$PROFILE" \
  --spring.main.web-application-type=none \
  --spring.main.banner-mode=off \
  --logging.level.root=WARN \
  --logging.level.com.psigama.airlab=WARN
