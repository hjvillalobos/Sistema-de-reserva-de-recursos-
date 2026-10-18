# Código de referencia del Proyecto I

`proyecto1/controlador` conserva los controladores del paquete singular
`una.eif206.reservas.controlador`. Ninguno de los FXML actuales los referencia:
todos utilizan el paquete `controladores`, ahora en `frontend`.

Estos archivos se conservan para revisar y recuperar comportamientos durante la
migración. No se compilan en el reactor Maven. El modelo, los servicios y la
persistencia antiguos permanecen en `backend` junto con sus pruebas; su
consolidación funcional se realizará por separado.

No copie estos controladores al frontend: dependen directamente de los servicios
y modelos antiguos del backend. Las pantallas activas usan contratos de `compartido`.
