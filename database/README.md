# Base de datos del Proyecto II

Esta carpeta queda preparada para el esquema MySQL coordinado con el compañero A. Todavía no contiene una conexión JDBC ni un esquema SQL.

Workbench 8.0 CE se usará para administrar MySQL Server y ejecutar el script. Workbench no reemplaza a MySQL Server. Solo backend deberá incorporar el conector JDBC y la configuración de conexión; las credenciales reales no se deben incluir en el repositorio.

El servidor de sockets sigue utilizando los XML de data/. El cliente ya no accede directamente a esos archivos. ServiciosServidor concentra la construcción de los accesos a datos.

Antes de sustituir XML, se debe acordar el esquema, las claves y las relaciones; después incorporar el conector y las transacciones para evitar reservas simultáneas del mismo recurso.
