param(
    [switch]$Generate
)

$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$reportPath = Join-Path $projectRoot 'target\site\jacoco\index.html'

if ($Generate -or -not (Test-Path $reportPath)) {
    Push-Location $projectRoot
    try {
        .\mvnw.cmd clean verify
    }
    finally {
        Pop-Location
    }
}

if (-not (Test-Path $reportPath)) {
    throw 'Relatorio de cobertura nao encontrado em target\site\jacoco\index.html'
}

Start-Process $reportPath
