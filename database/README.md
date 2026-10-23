# MySQL: configuración y pruebas

## Esquema y conexión

ServidorMain usa MySQL para acceso, recursos y reservas. Las clases XML quedan
para compatibilidad y pruebas anteriores, sin fallback automático.

Las seis tablas esperadas se describen en esquema.sql. Si ya fueron creadas en
Workbench no hay que recrearlas. Para una base vacía, seleccionar primero la base
con USE y ejecutar ese archivo. Todas deben usar InnoDB y conservar las claves
foráneas. El script no borra datos ni actualiza definiciones existentes.

Variables locales de cada configuración Java:

- RESERVAS_DB_CLAVE: contraseña de la cuenta MySQL reservas_app; obligatoria.
- RESERVAS_DB_URL: opcional; jdbc:mysql://localhost:3306/sistema_reservas por defecto.
- RESERVAS_DB_USUARIO: opcional; reservas_app por defecto.

reservas_app necesita SELECT, INSERT, UPDATE y DELETE sobre sistema_reservas.
No guardar contraseñas ni configuraciones con secretos en Git. Workbench administra
MySQL Server; no lo reemplaza. PruebaConexionBD comprueba conexión y nombres de
tablas; no modifica datos ni valida por sí sola todas sus relaciones.

## Administrador

CrearAdministradorBD necesita RESERVAS_DB_CLAVE, RESERVAS_ADMIN_ID y
RESERVAS_ADMIN_CLAVE. Crea un administrador nuevo sin sobrescribir usuarios.
La contraseña se almacena con PBKDF2-HMAC-SHA256, 600000 iteraciones y sal aleatoria.
No volver a ejecutar esta herramienta para cambiar una contraseña: usar la aplicación.

## Preparar una prueba manual de recursos y reservas

1. Compilar los cambios y detener el ServidorMain anterior.
2. Crear una configuración Application para
   una.eif206.reservas.configuracion.PrepararPruebaReservasBD, módulo backend.
3. Configurar RESERVAS_DB_CLAVE y RESERVAS_PRUEBA_CLAVE. La segunda es una contraseña
   que se elige para las dos cuentas de funcionario, distinta de la cuenta MySQL.
4. Ejecutar una vez. La herramienta crea en una transacción dos funcionarios con
   sus usuarios, una categoría y un recurso. Todos llevan un prefijo PRUEBA- único.
   Imprime sus identificadores, pero no la contraseña. No modifica datos previos.
5. Iniciar ServidorMain con sus propias variables de conexión y dos Launcher.
6. Ingresar con un funcionario distinto en cada cliente y la contraseña elegida.
7. Seleccionar la categoría impresa, la misma fecha y horario en ambos clientes.
   Registrar las dos reservas: una debe funcionar y la otra informar falta de disponibilidad.
8. Cancelar desde el cliente que obtuvo la reserva. El otro debe poder reservar
   el mismo recurso y horario. La reserva cancelada permanece en el historial.
9. Con el administrador, crear y editar otro recurso usando esa categoría; borrar
   un recurso sin reservas debe funcionar, borrar uno con reservas debe rechazarse.
10. Reiniciar el servidor y confirmar que las reservas y recursos siguen presentes.

Puede comprobar el resultado en Workbench con SELECT sobre recursos, reservas y
reserva_recursos. El frontend no necesita la contraseña de la conexión MySQL.
Los datos PRUEBA- permanecen para la comprobación manual; no se borran automáticamente.

## Límites de la división del trabajo

La gestión de categorías y funcionarios sigue a cargo del compañero responsable.
Se agregaron adaptadores de lectura para que recursos, reservas e informes utilicen
sus tablas MySQL. Mientras se integra su gestión, crear/modificar/eliminar desde
esas dos pantallas informa que esa función está pendiente; no escribe en XML.
La herramienta de prueba crea únicamente datos de demostración. No se importaron
los XML históricos ni se cambiaron las reglas de los módulos de los compañeros.

## Transacciones y concurrencia

ReservaMySqlLogica valida la entrada. ReservaMySqlDatos abre una transacción
READ COMMITTED, verifica al funcionario y las categorías, bloquea candidatos
con SELECT FOR UPDATE, comprueba cruces de horario e inserta cabecera y detalles.
Si algo falla, hace rollback. Los bloqueos son de MySQL, no un candado global Java:
funcionan con conexiones independientes e instancias distintas del backend.
La cancelación comprueba propietario y cambia el estado en una transacción.
Los IDs nuevos RES-UUID no dependen de contar reservas existentes.

Los SQL usan parámetros. RecursoMySqlDatos diferencia INSERT y UPDATE para que
una creación concurrente no sobrescriba un recurso. Las claves foráneas protegen
las relaciones frente a eliminaciones simultáneas. El bloqueo dura hasta commit
según https://dev.mysql.com/doc/refman/8.0/en/innodb-locking-reads.html.

## Pruebas automáticas con MySQL real

Las clases ReservasMySqlIT y FlujoMySqlIT se habilitan únicamente con
RESERVAS_PRUEBAS_MYSQL=true. Configurar RESERVAS_DB_URL a una base vacía EXCLUSIVA de
pruebas cuyo nombre termine en _pruebas, creada con esquema.sql, y sus credenciales.
Nunca utilizar sistema_reservas. Ejecutar mvn verify desde la raíz.

La cuenta de pruebas necesita DDL y TRIGGER sobre esa base, además de DML. La prueba
de rollback instala y elimina un trigger temporal que provoca un fallo en el
segundo detalle. Usar una instancia aislada sin binlog o con
log_bin_trust_function_creators habilitado solo en esa instancia de pruebas.
No habilitar estos permisos ni cambiar opciones globales en la instancia habitual.

Se verifica CRUD de recursos, claves foráneas, reservas por propietario, horarios
contiguos, cancelación, categorías repetidas, ausencia de disponibilidad, dos
solicitudes con uno o dos recursos, rollback de cabecera y detalles, permisos por
sockets, eventos y persistencia tras reiniciar el backend. Cada prueba elimina
solo sus registros identificados por prefijos aleatorios. No ejecutar estas
pruebas en paralelo sobre la misma base por el trigger temporal.
