# EIF206 - Sistema de Reserva de Recursos

Universidad Nacional · Programación 3 · 2026-II

## Estado actual

Cliente JavaFX y servidor se ejecutan en procesos separados y se comunican por sockets. Login, cambio de contraseña, recursos y reservas utilizan servicios remotos. **La persistencia todavía es XML; MySQL es el siguiente paso.**

- `compartido`: DTO, interfaces y mensajes de red.
- `backend`: servidor, sesiones, permisos, lógica y almacenamiento XML.
- `frontend`: JavaFX, proxies y conexión al servidor.
- `database`: lugar para el esquema MySQL.
- `data`: XML existentes, usados solamente por el servidor.
- `legacy`: código anterior fuera de la compilación.

El frontend depende de compartido. Su dependencia de backend tiene alcance de pruebas: permite probar contra un servidor real y no se distribuye con la aplicación. El antiguo proveedor local permanece como compatibilidad interna, pero el frontend ya no lo carga ni utiliza el perfil `local`.

## Compilar y ejecutar

Requiere JDK 21 o posterior y Maven. Configure `JAVA_HOME` con el JDK elegido. Desde la raíz:

```powershell
mvn verify
```

La compilación prepara las bibliotecas de cada aplicación en `target/dependency`. Las pruebas usan carpetas temporales y conexiones locales; no modifican los XML de `data/`. Java debe poder abrir sockets de loopback.

En una terminal:

```powershell
.\iniciar-servidor.ps1
```

En otra terminal:

```powershell
.\iniciar-cliente.ps1
```

Se pueden abrir varios clientes. Por defecto se usa `127.0.0.1:5050` y el servidor lee `data/` del repositorio. Las acciones realizadas en la aplicación sí guardan cambios en esos XML. Ejecute un solo servidor por carpeta de datos.

Para una prueba entre equipos en una red de confianza:

```powershell
.\iniciar-servidor.ps1 -Direccion 0.0.0.0 -Puerto 5050
.\iniciar-cliente.ps1 -Direccion IP_DEL_SERVIDOR -Puerto 5050
```

Permita ese puerto en el firewall del servidor. El transporte actual no usa TLS. El servidor también acepta `-Datos RUTA_DE_DATOS` para una carpeta XML alternativa ya preparada. No crea automáticamente usuarios ni una base de datos.

## Comportamiento implementado

- Cada conexión tiene su sesión. El login devuelve identificación y rol sin contraseña; el cambio de clave usa la identidad almacenada en el servidor.
- Los permisos se revisan en el servidor. Un funcionario no puede administrar recursos ni consultar, crear o cancelar reservas a nombre de otro usuario.
- Las operaciones XML están coordinadas para que dos clientes no reserven simultáneamente un recurso ocupado. Se validan horarios y categorías repetidas.
- Los recursos requieren una categoría existente; no se elimina un recurso referenciado por reservas.
- Las respuestas se relacionan con su solicitud. Hay límites para los mensajes, filtros de deserialización y tiempo de espera ante desconexiones.
- Login, cambio de clave, recursos y reservas realizan las consultas fuera del hilo de JavaFX para mantener disponible la interfaz mientras esperan.

Las interfaces existentes de categorías, funcionarios, calendarización, programación y estadísticas también cuentan con transporte remoto. Su evolución funcional sigue correspondiendo a los responsables de esas partes.

## Integración de alertas

Al guardar una reserva, el servidor envía `EventoReserva` a las conexiones autenticadas de otros usuarios. Los eventos están disponibles en `ProveedorServiciosRemoto.conexion().eventos()` para el consumidor del compañero encargado de alertas. La cola es limitada y no es un historial persistente. La presentación de alertas en pantalla todavía no está implementada.

## Siguiente etapa: MySQL

`ServiciosServidor` concentra la construcción de los accesos a datos. El próximo paso es acordar el esquema e instalar/configurar MySQL Server mediante Workbench, incorporar JDBC únicamente al backend y sustituir los accesos XML por consultas y transacciones. La coordinación en memoria deberá complementarse con transacciones y restricciones de la base de datos.

Todavía no hay esquema SQL, conector MySQL ni credenciales de conexión. No se han migrado los datos. Las pruebas automáticas cubren servicios y transporte; falta la comprobación manual completa de las ventanas y entre equipos distintos.
