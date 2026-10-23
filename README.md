# EIF206 - Sistema de Reserva de Recursos

Universidad Nacional · Programación 3 · 2026-II

## Estado actual

Cliente JavaFX y servidor se ejecutan por separado y se comunican por sockets.
ServidorMain utiliza MySQL para login, cambio de contraseña, recursos y reservas.
Las consultas de categorías y funcionarios también leen MySQL para que los
selectores e informes utilicen las mismas identificaciones. Su gestión sigue
pendiente de integrar con el compañero responsable; las escrituras de esas dos
pantallas muestran un mensaje explícito en lugar de modificar archivos XML.

- compartido: DTO, interfaces y protocolo de red.
- backend: servicios, lógica, accesos MySQL y clases XML conservadas para pruebas anteriores.
- frontend: JavaFX, proxies y conexión al servidor.
- database: esquema SQL e instrucciones de pruebas y configuración.
- data: XML anteriores; no se importan automáticamente ni se utilizan en ServidorMain.
- legacy: código anterior fuera de la compilación.

El frontend solo distribuye sus bibliotecas y compartido. Su dependencia de
backend tiene alcance de pruebas para verificar conexiones reales.

## Compilar y ejecutar

Requiere JDK 21 o posterior, Maven y MySQL 8.0 con las seis tablas InnoDB descritas
en database/esquema.sql. Desde la raíz ejecutar `mvn verify`.

Configure RESERVAS_DB_CLAVE con la contraseña de reservas_app en el entorno del
servidor. Las configuraciones de IntelliJ tienen variables independientes.
La URL predeterminada es jdbc:mysql://localhost:3306/sistema_reservas; se puede
cambiar con RESERVAS_DB_URL y el usuario con RESERVAS_DB_USUARIO.

En una terminal ejecutar `./iniciar-servidor.ps1` y en otra
`./iniciar-cliente.ps1`. También se pueden ejecutar ServidorMain y Launcher
respectivamente desde IntelliJ. Reinicie el servidor después de compilar cambios.

Por defecto se usa 127.0.0.1:5050. Para otros equipos, iniciar el servidor con
`-Direccion 0.0.0.0` y el cliente con `-Direccion IP_DEL_SERVIDOR`. El transporte
actual no utiliza TLS; estas pruebas deben realizarse en una red de confianza.
El parámetro histórico -Datos ya no determina la persistencia de ServidorMain.

## Recursos y reservas

- Solo el administrador modifica recursos; la categoría debe existir.
- Una reserva pertenece a un funcionario existente en MySQL.
- La asignación obtiene un recurso por categoría y valida los horarios.
- Una transacción READ COMMITTED bloquea los recursos candidatos con FOR UPDATE
  antes de consultar disponibilidad. Los bloqueos permanecen hasta commit o rollback.
- Si falta disponibilidad o falla un detalle, se revierte toda la reserva.
- La cancelación conserva el historial y libera disponibilidad.
- Los horarios contiguos son válidos; los solapamientos activos no lo son.
- Las claves foráneas impiden eliminar recursos utilizados, incluso por reservas canceladas.
- Los nuevos IDs de reservas usan RES- seguido de un UUID, evitando contadores
  basados en cantidad de filas que podrían repetirse entre servidores.
- Las consultas de calendarización, programación y estadísticas reciben los datos
  MySQL a través de los adaptadores, sin rediseñar sus funciones.

## Pruebas

`mvn verify` ejecuta las pruebas habituales sin necesitar credenciales MySQL.
Las pruebas MySQL requieren habilitación explícita y una base exclusiva cuyo
nombre termine en _pruebas. Ver database/README.md. Incluyen conexiones JDBC
independientes, fallo parcial, clientes por sockets y persistencia tras reiniciar
el servidor. No sustituyen la revisión manual completa de las ventanas.

## Alertas

Una reserva confirmada genera EventoReserva para los otros usuarios conectados
al mismo servidor. El consumidor está disponible en
ProveedorServiciosRemoto.conexion().eventos(). La presentación corresponde al
compañero encargado; la cola no es un historial persistente. No hay distribución
de eventos entre instancias distintas del servidor.
