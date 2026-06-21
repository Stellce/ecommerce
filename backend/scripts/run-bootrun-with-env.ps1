$envFile = "..\.env"

if (-not (Test-Path $envFile)) {
    Write-Error ".env file not found: $envFile"
    exit 1
}

Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()

    if ($line -eq "" -or $line.StartsWith("#")) {
        return
    }

    $name, $value = $line -split "=", 2

    if (-not $name -or $null -eq $value) {
        return
    }

    $name = $name.Trim()
    $value = $value.Trim().Trim('"').Trim("'")

    [Environment]::SetEnvironmentVariable($name, $value, "Process")
}

.\gradlew.bat bootRun