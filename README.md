# EIF206 - Sistema de Reserva de Recursos

Universidad Nacional · Escuela de Informática · Programación 3 · 2026-II

## Integrantes

- Heifryl Joel Villalobos Ureña
- Pablo Hernandez Esquivel
- Diego Jimenez Villalobos

## Estado: primer paso de la migración al Proyecto II

El repositorio ahora es un proyecto Maven con tres módulos. Se conserva la lógica
del Proyecto I y su persistencia XML. **Todavía no hay servidor de sockets, proxies
remotos, conexión MySQL ni alertas.** Esta etapa separa el código y sus dependencias.

```text
pom.xml                 Padre: versiones, Java 21, pruebas y módulos
compartido/
  src/main/java/        DTO, interfaces de servicios y excepción compartida
  src/test/java/        Pruebas de DTO
backend/
  src/main/java/        Servicios, lógica y persistencia XML existente
  src/main/resources/   Registro del proveedor local transitorio
  src/test/java/        Pruebas de lógica, servicios, modelo y persistencia
frontend/
  src/main/java/        JavaFX, controladores activos y utilidades de presentación
  src/main/resources/   FXML y CSS con sus rutas originales
  src/test/java/        Verificación de vistas y controladores
database/               Lugar para el futuro script MySQL
data/                   XML del Proyecto I, usados por el modo local
legacy/                 Controladores antiguos sin uso en los FXML actuales
```

## Por qué se hizo así

1. **El POM raíz coordina la compilación.** Tiene `packaging=pom`; no genera el
   ejecutable de la aplicación. Centraliza versiones y plugins. Cada módulo
   declara las bibliotecas que necesita. Se usa `release=21` para compilar con
   las APIs de Java 21 incluso cuando el JDK instalado es posterior.
2. **Compartido no conoce la interfaz ni la persistencia.** Contiene los DTO
   existentes, ocho contratos `*Api` y `ValidacionException`. No depende de JavaFX,
   JAXB ni MySQL. Las firmas actuales se conservan: todavía no son el contrato
   definitivo de red y los DTO de usuario aún requieren separar las credenciales.
3. **Backend concentra la implementación.** Sus servicios implementan los
   contratos compartidos y conservan las reglas y los accesos XML. JAXB solo se
   declara aquí. No depende de JavaFX ni del frontend.
4. **Frontend depende de los contratos.** Los controladores dejaron de construir
   servicios concretos. `ServiciosCliente` encuentra un `ProveedorServicios`
   mediante `ServiceLoader`, mecanismo estándar de Java para cargar una
   implementación sin importar sus clases. No es todavía el patrón Proxy remoto.
5. **Las interfaces fueron necesarias para separar los módulos.** Mover solamente
   los archivos dejaba a todos los controladores dependiendo del backend. Se
   extrajeron las firmas actuales, sin rediseñar todavía login o reservas.
6. **Se conserva la ejecución anterior de forma explícita.** El perfil opcional
   `local` añade el backend únicamente al ejecutar la aplicación y conserva XML.
   Es un puente temporal: frontend y backend corren juntos en ese modo, por lo
   que **no cumple todavía la arquitectura distribuida final**. Sin ese perfil,
   frontend solo depende de compartido y avisa que falta el proveedor remoto.

Dependencias normales:

```text
frontend ---> compartido <--- backend
```

La única excepción transitoria es el perfil `local`, que incorpora `backend` con
alcance Maven `runtime`. El compilador del frontend sigue sin poder importar
las implementaciones del backend.

## Compilar y verificar

Requisitos: JDK 21 o posterior y Maven. Configure `JAVA_HOME` con el JDK correcto;
un JRE de Java 8 no permite compilar este proyecto.

Desde la raíz del repositorio:

```powershell
mvn verify
```

Compila los tres módulos, ejecuta pruebas unitarias con Surefire y pruebas de
integración con Failsafe. Las pruebas de integración existentes siguen siendo
sobre XML y archivos temporales, no sobre MySQL.

Para compilar únicamente frontend y sus dependencias:

```powershell
mvn -pl frontend -am verify
```

Esta segunda orden no necesita compilar backend y permite comprobar el aislamiento.

## Ejecutar temporalmente la aplicación anterior

Desde la raíz, instalar los módulos y lanzar el frontend con el perfil local:

```powershell
mvn -Plocal install
mvn -pl frontend -Plocal javafx:run
```

El plugin JavaFX usa la raíz como directorio de trabajo para conservar las rutas
`data/*.xml`. Este modo lee y puede modificar los mismos datos XML del Proyecto I.
No requiere MySQL y no demuestra comunicación por red.

En IntelliJ, abrir o recargar el `pom.xml` raíz como proyecto Maven. Seleccionar
el JDK correcto y utilizar las órdenes anteriores desde la ventana Maven o una
terminal con Maven configurado. Una ejecución directa del Launcher sin el perfil
local no incluye el proveedor y mostrará el aviso de migración pendiente.

## Continuación

- Rediseñar el contrato de login: DTO de sesión sin contraseña y cambio de clave
  asociado a la sesión del servidor.
- Acordar mensajes de red y sustituir el proveedor local por proxies en frontend.
- Implementar el servidor de sockets en backend.
- Incorporar JDBC y el esquema MySQL coordinado con el compañero A.
- Migrar Recursos y Reservas; conectar el registro con las alertas del compañero B.

La extracción de contratos de las otras funciones es exclusivamente estructural;
su migración funcional sigue correspondiendo a sus responsables.
