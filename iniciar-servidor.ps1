param(
    [string]$Direccion = '127.0.0.1',
    [int]$Puerto = 5050,
    [string]$Datos = (Join-Path $PSScriptRoot 'data')
)
$ErrorActionPreference = 'Stop'
$clases = Join-Path $PSScriptRoot 'backend/target/classes'
$bibliotecas = Join-Path $PSScriptRoot 'backend/target/dependency/*'
if (-not (Test-Path (Join-Path $clases 'una/eif206/reservas/servidor/ServidorMain.class'))) {
    throw 'Primero ejecute mvn verify desde la raíz del proyecto.'
}
$javaReservas = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin/java.exe' } else { 'java' }
& $javaReservas "-Dreservas.bind=$Direccion" "-Dreservas.puerto=$Puerto" "-Dreservas.datos=$Datos" `
    -cp "$clases;$bibliotecas" una.eif206.reservas.servidor.ServidorMain
exit $LASTEXITCODE
