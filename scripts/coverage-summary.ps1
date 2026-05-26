param(
    [switch]$Generate
)

$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$csvPath = Join-Path $projectRoot 'target\site\jacoco\jacoco.csv'

if ($Generate -or -not (Test-Path $csvPath)) {
    Push-Location $projectRoot
    try {
        .\mvnw.cmd clean verify
    }
    finally {
        Pop-Location
    }
}

if (-not (Test-Path $csvPath)) {
    throw 'Relatorio de cobertura nao encontrado em target\site\jacoco\jacoco.csv'
}

$rows = Import-Csv $csvPath -Header GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED,COMPLEXITY_MISSED,COMPLEXITY_COVERED,METHOD_MISSED,METHOD_COVERED |
    Select-Object -Skip 1

$lineMissed = ($rows | Measure-Object -Property LINE_MISSED -Sum).Sum
$lineCovered = ($rows | Measure-Object -Property LINE_COVERED -Sum).Sum
$branchMissed = ($rows | Measure-Object -Property BRANCH_MISSED -Sum).Sum
$branchCovered = ($rows | Measure-Object -Property BRANCH_COVERED -Sum).Sum

$linePct = [math]::Round((100.0 * $lineCovered / ($lineCovered + $lineMissed)), 2)
$branchPct = [math]::Round((100.0 * $branchCovered / ($branchCovered + $branchMissed)), 2)

Write-Output ("Line coverage: {0}% ({1}/{2})" -f $linePct, $lineCovered, ($lineCovered + $lineMissed))
Write-Output ("Branch coverage: {0}% ({1}/{2})" -f $branchPct, $branchCovered, ($branchCovered + $branchMissed))
