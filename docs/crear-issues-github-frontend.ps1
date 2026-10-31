<#
.SYNOPSIS
    Script para crear automáticamente las issues Frontend #8 a #67 en el repositorio de GitHub de Frontend.
.DESCRIPTION
    Toma un GitHub Personal Access Token (PAT) o lo extrae de Git Credential Manager y publica las issues en MiguelHerrera-13/Proyecto-frontend-Lumina.
.EXAMPLE
    .\crear-issues-github-frontend.ps1 -Desde 8 -Hasta 27
#>

param(
    [Parameter(Mandatory = $false)]
    [string]$GitHubToken = $env:GITHUB_TOKEN,

    [Parameter(Mandatory = $false)]
    [int]$Desde = 8,

    [Parameter(Mandatory = $false)]
    [int]$Hasta = 67,

    [string]$RepoOwner = "MiguelHerrera-13",
    [string]$RepoName = "Proyecto-frontend-Lumina"
)

if ([string]::IsNullOrWhiteSpace($GitHubToken)) {
    try {
        $cred = @"
protocol=https
host=github.com
"@ | git credential fill
        $GitHubToken = ($cred -split "`n" | Where-Object { $_ -match "^password=" }) -replace "^password=", ""
    } catch {}
}

if ([string]::IsNullOrWhiteSpace($GitHubToken)) {
    Write-Warning "No se pudo detectar el token de GitHub. Ejecute:"
    Write-Warning ".\crear-issues-github-frontend.ps1 -GitHubToken 'su_token_personal_aqui'"
    return
}

$planFile = Join-Path $PSScriptRoot "plan-60-issues-frontend.md"
if (-not (Test-Path $planFile)) {
    Write-Error "No se encontró el archivo $planFile"
    return
}

$content = [System.IO.File]::ReadAllText($planFile, [System.Text.Encoding]::UTF8)
$pattern = '(?ms)^### (Issue Frontend #(\d+): [^\r\n]+)\r?\n(.*?)(?=(?:^### Issue Frontend #\d+: )|\Z)'
$matches = [regex]::Matches($content, $pattern)

Write-Host "Se encontraron $($matches.Count) issues en el archivo de plan de frontend." -ForegroundColor Cyan

$headers = @{
    "Authorization" = "Bearer $GitHubToken"
    "Accept"        = "application/vnd.github+json"
    "User-Agent"    = "Lumina-Frontend-Script"
}

foreach ($m in $matches) {
    $fullTitle = $m.Groups[1].Value.Trim()
    $num = [int]$m.Groups[2].Value
    $body = $m.Groups[3].Value.Trim()

    if ($num -ge $Desde -and $num -le $Hasta) {
        Write-Host "Creando en GitHub Frontend: $fullTitle..." -NoNewline
        $payload = @{
            title = $fullTitle
            body  = $body
        } | ConvertTo-Json -Depth 5

        $bodyBytes = [System.Text.Encoding]::UTF8.GetBytes($payload)

        try {
            $resp = Invoke-RestMethod -Uri "https://api.github.com/repos/$RepoOwner/$RepoName/issues" `
                -Method Post `
                -Headers $headers `
                -Body $bodyBytes `
                -ContentType "application/json; charset=utf-8"
            Write-Host " [OK -> Issue #$($resp.number)]" -ForegroundColor Green
            Start-Sleep -Milliseconds 800 # Evitar rate limit de GitHub
        } catch {
            Write-Host " [ERROR]" -ForegroundColor Red
            Write-Error $_
            break
        }
    }
}
