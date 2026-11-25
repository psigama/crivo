<#
.SYNOPSIS
    Crivo (AIR Lab) — CLI de verificacao, Windows.

.DESCRIPTION
    Roda o mesmo artefato da aplicacao, sem servidor web, para conferir:
      - a cadeia de hashes da trilha de auditoria (P4);
      - o hash de cada versao do pre-registro (P2);
      - a reproducao do sorteio a partir da semente revelada (P5).

    Sai com codigo 0 se tudo confere e 1 caso contrario.

.EXAMPLE
    .\scripts\airlab.ps1 verify
.EXAMPLE
    .\scripts\airlab.ps1 verify --study=STD-7Q4K2M9B
.EXAMPLE
    .\scripts\airlab.ps1 site target\site --aplicacao=http://127.0.0.1:8080

    Gera o site estatico com as paginas editoriais (SPEC 0.21, secao 1.3), sem banco e sem Spring,
    numa pasta vazia ou inexistente.
.EXAMPLE
    .\scripts\airlab.ps1 verificar-pacote pacote-STD-7Q4K2M9B.json

    Confere o pacote de verificacao de um estudo P1 (SPEC 0.22, E9) so a partir do arquivo, sem
    banco e sem servidor, baixado de /estudos/{codigo}/pacote.json.
#>
[CmdletBinding()]
param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]] $CliArgs = @('verify')
)

$ErrorActionPreference = 'Stop'

$projectDir = Split-Path -Parent $PSScriptRoot
Set-Location $projectDir
$jar = Join-Path $projectDir 'target\airlab.jar'

if (Test-Path '.env') {
    foreach ($line in Get-Content '.env') {
        $trimmed = $line.Trim()
        if ($trimmed -and -not $trimmed.StartsWith('#') -and $trimmed.Contains('=')) {
            $name, $value = $trimmed.Split('=', 2)
            Set-Item -Path ("Env:" + $name.Trim()) -Value $value.Trim('"').Trim()
        }
    }
}

# JDK, pela mesma regra do scripts\start.ps1, secao 1: o JAVA_HOME herdado do ambiente pode
# apontar para um JDK que nao existe mais, e o wrapper do Maven recusa o build nesse caso.
# O JAVA_HOME do build sai do proprio java encontrado, nao do que veio do ambiente.
$java = $null
if ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME 'bin\java.exe'))) {
    $java = Join-Path $env:JAVA_HOME 'bin\java.exe'
}
else {
    $command = Get-Command java -ErrorAction SilentlyContinue
    if ($command) { $java = $command.Source }
}
if (-not $java) {
    Write-Host 'erro: nenhum JDK encontrado. Instale o JDK 25 ou defina JAVA_HOME.' -ForegroundColor Red
    exit 1
}
# "java.home" sai em "-XshowSettings:properties", que escreve na saida de erro; quem redireciona
# e o cmd.exe, nunca o PowerShell (ver a nota sobre NativeCommandError no scripts\start.ps1).
$javaHome = $null
foreach ($linha in (cmd /c "`"$java`" -XshowSettings:properties -version 2>&1")) {
    if ($linha -match '^\s*java\.home\s*=\s*(\S.*?)\s*$') { $javaHome = $Matches[1]; break }
}
if (-not $javaHome -or -not (Test-Path (Join-Path $javaHome 'bin\java.exe'))) {
    $acima = Split-Path -Parent (Split-Path -Parent $java)
    $javaHome = if ($acima -and (Test-Path (Join-Path $acima 'bin\java.exe'))) { $acima } else { $null }
}
if (-not $javaHome) {
    Write-Host "erro: nao foi possivel descobrir o JDK de '$java'. Defina JAVA_HOME apontando para o JDK 25." -ForegroundColor Red
    exit 1
}
$env:JAVA_HOME = $javaHome
$java = Join-Path $javaHome 'bin\java.exe'

if (-not (Test-Path $jar)) {
    Write-Host '==> Construindo o JAR' -ForegroundColor Green
    & (Join-Path $projectDir 'mvnw.cmd') -B -q package -DskipTests
    if ($LASTEXITCODE -ne 0) { exit 1 }
}
$profileName = if ($env:AIRLAB_PROFILE) { $env:AIRLAB_PROFILE } else { 'local' }

& $java '-jar' $jar @CliArgs `
    "--spring.profiles.active=$profileName" `
    '--spring.main.web-application-type=none' `
    '--spring.main.banner-mode=off' `
    '--logging.level.root=WARN' `
    '--logging.level.com.psigama.airlab=WARN'

exit $LASTEXITCODE
