param(
    [string]$Direccion = '127.0.0.1',
    [int]$Puerto = 5050
)
$ErrorActionPreference = 'Stop'
$clases = Join-Path $PSScriptRoot 'frontend/target/classes'
$bibliotecas = Join-Path $PSScriptRoot 'frontend/target/dependency/*'
if (-not (Test-Path (Join-Path $clases 'una/eif206/reservas/vista/Launcher.class'))) {
    throw 'Primero ejecute mvn verify desde la raíz del proyecto.'
}
$javaReservas = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin/java.exe' } else { 'java' }
& $javaReservas "-Dreservas.host=$Direccion" "-Dreservas.puerto=$Puerto" `
    -cp "$clases;$bibliotecas" una.eif206.reservas.vista.Launcher
exit $LASTEXITCODE
