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

# JDK, pela mesma regra do scripts/start.sh, secao 1: o JAVA_HOME herdado do ambiente pode
# apontar para um JDK que nao existe mais, e o wrapper do Maven recusa o build nesse caso.
# O JAVA_HOME do build sai do proprio java encontrado, nao do que veio do ambiente.
if [[ -n "${JAVA_HOME:-}" && -x "${JAVA_HOME}/bin/java" ]]; then
  JAVA="${JAVA_HOME}/bin/java"
elif command -v java >/dev/null 2>&1; then
  JAVA="$(command -v java)"
else
  echo "erro: nenhum JDK encontrado. Instale o JDK 25 ou defina JAVA_HOME." >&2
  exit 1
fi
JDK_HOME="$("$JAVA" -XshowSettings:properties -version 2>&1 |
  sed -nE 's/^[[:space:]]*java\.home[[:space:]]*=[[:space:]]*(.+)$/\1/p' | head -n 1)"
if [[ -z "$JDK_HOME" || ! -x "$JDK_HOME/bin/java" ]]; then
  JDK_HOME="$(cd "$(dirname "$JAVA")/.." 2>/dev/null && pwd)"
fi
if [[ -z "$JDK_HOME" || ! -x "$JDK_HOME/bin/java" ]]; then
  echo "erro: nao foi possivel descobrir o JDK de '$JAVA'. Defina JAVA_HOME apontando para o JDK 25." >&2
  exit 1
fi
export JAVA_HOME="$JDK_HOME"
JAVA="$JAVA_HOME/bin/java"

if [[ ! -f "$JAR" ]]; then
  echo "==> Construindo o JAR" >&2
  ./mvnw -B -q package -DskipTests
fi

exec "$JAVA" -jar "$JAR" \
  "${@:-verify}" \
  --spring.profiles.active="$PROFILE" \
  --spring.main.web-application-type=none \
  --spring.main.banner-mode=off \
  --logging.level.root=WARN \
  --logging.level.com.psigama.airlab=WARN
