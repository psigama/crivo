<#
.SYNOPSIS
    Crivo (AIR Lab) — script unico de inicializacao, Windows.

.DESCRIPTION
    Sobe todos os componentes necessarios:
      - PostgreSQL: no perfil local, embarcado pela propria aplicacao (binarios reais do
        PostgreSQL, sem Docker); no perfil prod, o PostgreSQL do sistema operacional;
      - migracoes Flyway dos esquemas research, identity e audit;
      - a aplicacao Spring Boot, com servidor web embutido.

    Pre-requisito unico: o JDK 25, o mesmo da producao. O Maven vem pelo wrapper do repositorio.

    Ctrl+C encerra a aplicacao e, com ela, o PostgreSQL embarcado.

.EXAMPLE
    .\scripts\start.ps1
.EXAMPLE
    .\scripts\start.ps1 -AppProfile prod
.EXAMPLE
    .\scripts\start.ps1 -Port 9090 -Rebuild
#>
[CmdletBinding()]
param(
    [ValidateSet('local', 'prod')]
    [string] $AppProfile = 'local',

    [int] $Port = 0,

    [switch] $SkipBuild,

    [switch] $Rebuild,

    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]] $ExtraArgs
)

$ErrorActionPreference = 'Stop'

$projectDir = Split-Path -Parent $PSScriptRoot
Set-Location $projectDir
$jar = Join-Path $projectDir 'target\airlab.jar'

function Write-Step([string] $message) { Write-Host "==> $message" -ForegroundColor Green }
function Write-Note([string] $message) { Write-Host "==> $message" -ForegroundColor Yellow }
function Stop-WithError([string] $message) { Write-Host "erro: $message" -ForegroundColor Red; exit 1 }

# 1. JDK ----------------------------------------------------------------------
# O wrapper do Maven recusa o build sem um JAVA_HOME valido ("The JAVA_HOME environment
# variable is not defined correctly"), e o JAVA_HOME herdado do ambiente pode apontar para
# um JDK que nao existe mais: sobra de desinstalacao, ou terminal aberto antes de a variavel
# mudar. Dai o script escolher o java, descobrir o JAVA_HOME real desse java e exportar esse
# valor para o build, em vez de confiar no que veio do ambiente.
function Get-JdkHome([string] $javaExe) {
    # O caminho do JDK sai na propriedade "java.home", que o "-XshowSettings:properties"
    # escreve na saida de erro. Quem redireciona e o cmd.exe; o PowerShell nunca redireciona
    # a saida de erro de executavel nativo (ver a nota sobre NativeCommandError mais abaixo).
    foreach ($linha in (cmd /c "`"$javaExe`" -XshowSettings:properties -version 2>&1")) {
        if ($linha -match '^\s*java\.home\s*=\s*(\S.*?)\s*$') { return $Matches[1] }
    }
    # Sem a propriedade, o diretorio acima de bin serve -- mas so quando o java esta dentro do
    # proprio JDK, nunca quando vem de um atalho, como o javapath da Oracle no PATH do Windows.
    $acima = Split-Path -Parent (Split-Path -Parent $javaExe)
    if ($acima -and (Test-Path (Join-Path $acima 'bin\java.exe'))) { return $acima }
    return $null
}

$java = $null
$javaHomeInvalido = $null
if ($env:JAVA_HOME) {
    if (Test-Path (Join-Path $env:JAVA_HOME 'bin\java.exe')) {
        $java = Join-Path $env:JAVA_HOME 'bin\java.exe'
    }
    else {
        $javaHomeInvalido = $env:JAVA_HOME
    }
}
if (-not $java) {
    $command = Get-Command java -ErrorAction SilentlyContinue
    if ($command) { $java = $command.Source }
}
if (-not $java) {
    if ($javaHomeInvalido) {
        Stop-WithError "JAVA_HOME aponta para '$javaHomeInvalido', que nao tem bin\java.exe, e nao ha java no PATH. Instale o JDK 25 ou corrija JAVA_HOME."
    }
    Stop-WithError 'nenhum JDK encontrado. Instale o JDK 25 ou defina JAVA_HOME.'
}
if ($javaHomeInvalido) {
    Write-Note "JAVA_HOME aponta para '$javaHomeInvalido', que nao tem bin\java.exe; ignorando esse valor."
}

$javaHome = Get-JdkHome $java
if (-not $javaHome) {
    Stop-WithError "nao foi possivel descobrir o JDK de '$java'. Defina JAVA_HOME apontando para o JDK 25."
}
$env:JAVA_HOME = $javaHome
$java = Join-Path $javaHome 'bin\java.exe'

# "java --version" escreve na saida padrao; o antigo "java -version" escreve na saida de erro,
# e redirecionar a saida de erro de um executavel nativo faz o PowerShell embrulhar cada linha
# num ErrorRecord (NativeCommandError). Com $ErrorActionPreference = 'Stop', isso aborta o
# script ao detectar um JDK perfeitamente valido. Dai a forma com dois hifens, sem redirecao.
$versionLine = (& $java --version | Select-Object -First 1)
$major = 0
if ($versionLine -match '(\d+)') { $major = [int]$Matches[1] }
if ($major -lt 25) {
    $encontrado = if ($versionLine) { $versionLine } else { 'versao nao reconhecida' }
    Stop-WithError "e necessario o JDK 25 ou mais novo (encontrado: $encontrado); a producao roda o 25."
}
Write-Step "JDK $major em $javaHome"

# 2. Segredos e pre-condicoes do perfil ---------------------------------------
if (Test-Path '.env') {
    Write-Step 'Lendo variaveis de .env'
    foreach ($line in Get-Content '.env') {
        $trimmed = $line.Trim()
        if ($trimmed -and -not $trimmed.StartsWith('#') -and $trimmed.Contains('=')) {
            $name, $value = $trimmed.Split('=', 2)
            Set-Item -Path ("Env:" + $name.Trim()) -Value $value.Trim('"').Trim()
        }
    }
}

if ($AppProfile -eq 'prod') {
    foreach ($required in @('AIRLAB_DB_URL', 'AIRLAB_DB_USER', 'AIRLAB_DB_PASSWORD',
                            'AIRLAB_IDENTITY_KEY', 'AIRLAB_RESEARCHER_PASSWORD_HASH')) {
        if (-not (Get-Item -Path ("Env:" + $required) -ErrorAction SilentlyContinue)) {
            Stop-WithError "$required nao definida. No perfil prod os segredos vem do ambiente ou do .env; a aplicacao nao inicia sem eles."
        }
    }
    Write-Step 'Perfil prod: PostgreSQL do sistema operacional'
}
else {
    New-Item -ItemType Directory -Force -Path 'data' | Out-Null
    $dbDir = if ($env:AIRLAB_LOCAL_DB_DIR) { $env:AIRLAB_LOCAL_DB_DIR } else { Join-Path $HOME ".airlab\pgdata" }
    Write-Step "Perfil $AppProfile`: PostgreSQL embarcado em $dbDir (somente dados ficticios)"
}

# 3. Build --------------------------------------------------------------------
$mvnw = Join-Path $projectDir 'mvnw.cmd'

if ($Rebuild) {
    Write-Step 'Construindo o JAR com os testes'
    & $mvnw -B clean package
    if ($LASTEXITCODE -ne 0) { Stop-WithError 'o build falhou.' }
}
elseif ($SkipBuild) {
    if (-not (Test-Path $jar)) { Stop-WithError "$jar nao existe; rode sem -SkipBuild." }
    Write-Step 'Usando o JAR existente'
}
else {
    $needsBuild = $true
    if (Test-Path $jar) {
        $jarTime = (Get-Item $jar).LastWriteTimeUtc
        $newest = Get-ChildItem -Path 'src', 'pom.xml' -Recurse -File |
            Sort-Object LastWriteTimeUtc -Descending | Select-Object -First 1
        if ($newest -and $newest.LastWriteTimeUtc -le $jarTime) { $needsBuild = $false }
    }
    if ($needsBuild) {
        Write-Step 'Construindo o JAR (sem testes; use -Rebuild para rodar a suite)'
        & $mvnw -B -q package -DskipTests
        if ($LASTEXITCODE -ne 0) { Stop-WithError 'o build falhou.' }
    }
    else {
        Write-Step 'JAR atualizado'
    }
}

# 4. Aplicacao ----------------------------------------------------------------
$appArgs = @("--spring.profiles.active=$AppProfile")
if ($Port -gt 0) { $appArgs += "--server.port=$Port" }
if ($ExtraArgs) { $appArgs += $ExtraArgs }

Write-Step "Iniciando o Crivo (perfil $AppProfile). Ctrl+C encerra tudo."
& $java '-jar' $jar @appArgs
exit $LASTEXITCODE
