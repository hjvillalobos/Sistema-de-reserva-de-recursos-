# Base de datos del Proyecto II

El backend cuenta con Connector/J y ConexionBD. ServidorMain utiliza MySQL para login y cambio de clave. Los demás servicios todavía utilizan XML. Esta carpeta aún no contiene el esquema SQL creado en Workbench.

Workbench 8.0 CE se usará para administrar MySQL Server y ejecutar el script. Workbench no reemplaza a MySQL Server. Solo backend deberá incorporar el conector JDBC y la configuración de conexión; las credenciales reales no se deben incluir en el repositorio.

El servidor de sockets sigue utilizando los XML de data/. El cliente ya no accede directamente a esos archivos. ServiciosServidor concentra la construcción de los accesos a datos.

Antes de sustituir XML, se debe acordar el esquema, las claves y las relaciones; después incorporar el conector y las transacciones para evitar reservas simultáneas del mismo recurso.

## Probar la conexión desde IntelliJ

1. Recargar el proyecto Maven con JDK 21 o posterior.
2. Abrir la clase `una.eif206.reservas.configuracion.PruebaConexionBD` del backend.
3. Crear una configuración de ejecución de tipo Application para su método main,
   usando el módulo backend. En Run > Edit Configurations, agregar el campo
   Environment variables (puede estar dentro de Modify options).
4. Agregar `RESERVAS_DB_CLAVE` con la contraseña de reservas_app. No compartir
   esta configuración como archivo del proyecto ni incluir la contraseña en Git.
5. Ejecutar PruebaConexionBD. Debe terminar con:
   `Conexión correcta. Las seis tablas están disponibles.`

La URL predeterminada es `jdbc:mysql://localhost:3306/sistema_reservas` y el
usuario es `reservas_app`. Se pueden cambiar mediante `RESERVAS_DB_URL` y
`RESERVAS_DB_USUARIO`. La contraseña no tiene valor predeterminado.

La prueba consulta la base seleccionada, la versión y los nombres de las tablas;
no inserta, modifica ni elimina datos y no comprueba aún las columnas o relaciones.
Las conexiones y los resultados se cierran automáticamente, incluso si ocurre
un error. Los límites de espera evitan esperar indefinidamente por el servidor.

Si falla, revisar el mensaje: contraseña ausente requiere configurar la variable;
Access denied requiere revisar cuenta y contraseña; Communications link failure
requiere comprobar que MySQL Server esté iniciado y que host y puerto coincidan.

## Crear la cuenta de prueba y ejecutar el login MySQL

1. En IntelliJ crear una configuración Application para
   `una.eif206.reservas.configuracion.CrearAdministradorBD`, módulo backend.
2. Agregar a sus variables de entorno:
   - `RESERVAS_DB_CLAVE`: contraseña de la cuenta MySQL reservas_app.
   - `RESERVAS_ADMIN_ID`: identificación elegida para entrar a la aplicación.
   - `RESERVAS_ADMIN_CLAVE`: contraseña elegida para ese administrador.
3. Ejecutar una vez. Debe indicar que el administrador fue creado. Si ya existe,
   no lo sobrescribe ni cambia su contraseña. No hay contraseñas predeterminadas.
4. Ejecutar `una.eif206.reservas.servidor.ServidorMain` con módulo backend, la raíz
   del repositorio como directorio de trabajo y RESERVAS_DB_CLAVE configurada.
   Las variables de PruebaConexionBD no pasan automáticamente a esta configuración.
5. Ejecutar el Launcher del frontend. Ingresar con RESERVAS_ADMIN_ID y la clave
   de la aplicación, no con la contraseña de reservas_app.
6. Probar el cambio de clave con la clave actual, cerrar sesión y verificar que
   la nueva funciona y la anterior es rechazada.

Si se utilizan los scripts PowerShell, compilar primero con `mvn verify` y
configurar RESERVAS_DB_CLAVE en el entorno de la terminal del servidor. También
se admiten RESERVAS_DB_URL y RESERVAS_DB_USUARIO en cada configuración Java.
No compartir configuraciones que contengan secretos ni guardarlas en Git.

La clave se guarda como PBKDF2-HMAC-SHA256 con 600000 iteraciones y sal aleatoria;
no se acepta texto plano en clave_hash. Las consultas usan parámetros. El cambio
de clave comprueba la clave actual y actualiza solo si el hash no cambió entre
la lectura y la escritura. El cliente recibe identificación y rol, nunca el hash.

Esta etapa no importa usuarios XML ni modifica la gestión de funcionarios:
crear o eliminar funcionarios desde esa pantalla aún afecta solamente XML y no
crea/elimina cuentas MySQL. Para probar este bloque usar el administrador creado
con la herramienta. Antes de probar reservas con funcionarios habrá que migrar
su gestión y mantener sus identificaciones coherentes con los datos existentes.
Los constructores XML se conservan para pruebas anteriores; ServidorMain no
recurre a XML cuando falla MySQL.
