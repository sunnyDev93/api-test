# Find Java processes running your app JAR and stop them gracefully

# Customize this if your JAR file name differs
$jarName = "cityinfo-0.0.1-SNAPSHOT.jar"

# Get java processes with command line containing the jar name
$processes = Get-CimInstance Win32_Process | Where-Object {
    $_.CommandLine -like "*$jarName*"
}

if ($processes.Count -eq 0) {
    Write-Host "No running CityInfo API processes found."
} else {
    foreach ($proc in $processes) {
        Write-Host "Stopping process ID $($proc.ProcessId) running $jarName"
        Stop-Process -Id $proc.ProcessId -Force
    }
    Write-Host "All matching processes stopped."
}
