$ErrorActionPreference = 'Stop'

$serviceName = 'PropertyRepairMySQL'
$mysqld = 'C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe'
$config = Join-Path $PSScriptRoot 'property-repair-mysql.ini'

if (-not (Test-Path $mysqld)) {
    throw "MySQL server executable was not found: $mysqld"
}
if (-not (Test-Path $config)) {
    throw "MySQL service configuration was not found: $config"
}

$existing = Get-Service -Name $serviceName -ErrorAction SilentlyContinue
if ($existing) {
    if ($existing.Status -ne 'Stopped') {
        Stop-Service -Name $serviceName -Force
    }
    & $mysqld --remove $serviceName 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "Could not remove the existing $serviceName service."
    }
}

& $mysqld --install $serviceName "--defaults-file=$config" 2>&1
if ($LASTEXITCODE -ne 0) {
    throw "Could not install the $serviceName service."
}

Set-Service -Name $serviceName -StartupType Automatic
Start-Service -Name $serviceName
Start-Sleep -Seconds 5

$service = Get-Service -Name $serviceName
if ($service.Status -ne 'Running') {
    throw "$serviceName was installed but is not running. Current status: $($service.Status)"
}

Write-Output "$serviceName installed and running as an automatic Windows service."
