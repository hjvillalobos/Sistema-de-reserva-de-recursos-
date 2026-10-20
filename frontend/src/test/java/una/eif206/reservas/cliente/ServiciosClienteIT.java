package una.eif206.reservas.cliente;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import una.eif206.reservas.DTO.*;
import una.eif206.reservas.excepciones.*;
import una.eif206.reservas.servidor.*;
import una.eif206.reservas.protocolo.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.net.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

class ServiciosClienteIT {
    @TempDir Path carpeta;
    ServidorReservas servidor;
    ServiciosServidor datos;
    final List<ProveedorServiciosRemoto> clientes = new ArrayList<>();
    @BeforeEach void preparar() throws Exception {
        datos = new ServiciosServidor(carpeta);
        datos.administradores.guardar(new UsuarioDTO("admin", "abc", RolDTO.ADMINISTRADOR));
        datos.funcionarios.guardar(new FuncionarioDTO("111", "uno", "Ana", "8888"));
        datos.funcionarios.guardar(new FuncionarioDTO("222", "dos", "Luis", "8889"));
        datos.categorias.guardar(new CategoriaDTO("CAT", "Salas"));
        datos.recursos.guardar(new RecursoDTO("R1", "CAT", "Sala 1"));
        servidor = new ServidorReservas("127.0.0.1", 0, datos);
        Thread.ofVirtual().start(() -> { try { servidor.ejecutar(); } catch (IOException e) { throw new UncheckedIOException(e); } });
    }
    @AfterEach void cerrar() throws Exception {
        for (var cliente : clientes) cliente.close();
        servidor.close();
    }
    ProveedorServiciosRemoto cliente() throws Exception {
        var c = new ProveedorServiciosRemoto("127.0.0.1", servidor.puerto(), 5000);
        clientes.add(c); return c;
    }
    String fecha() { return LocalDate.now().plusDays(1).toString(); }
    ReservaDTO reservar(ProveedorServiciosRemoto c, String id) throws Exception {
        return c.reservas().intentarRegistrar("Reunión", fecha(), "09:00", "10:00", id, List.of("CAT"));
    }
    @Test void autenticaSinDevolverClaveYCambiaSoloLaDeSuSesion() throws Exception {
        var c = cliente();
        UsuarioSesionDTO usuario = c.login().autenticarSesion("111", "uno");
        assertEquals("111", usuario.getId());
        assertEquals(RolDTO.FUNCIONARIO, usuario.getRol());
        assertThrows(NoSuchFieldException.class, () -> UsuarioSesionDTO.class.getDeclaredField("clave"));
        assertThrows(ValidacionException.class, () -> c.login().cambiarClave("incorrecta", "nueva"));
        assertThrows(ValidacionException.class, () -> c.login().cambiarClave("uno", " "));
        c.login().cambiarClave("uno", "nueva");
        assertEquals("nueva", datos.funcionarios.buscarPorId("111").getClave());
        assertEquals("dos", datos.funcionarios.buscarPorId("222").getClave());
        c.login().cerrarSesion();
        assertThrows(ValidacionException.class, () -> c.login().cambiarClave("nueva", "otra"));
        assertThrows(ValidacionException.class, () -> c.login().autenticarSesion("111", "uno"));
        assertEquals("111", c.login().autenticarSesion("111", "nueva").getId());
    }
    @Test void exigeSesionYPermisosYUnLoginFallidoNoConservaPrivilegios() throws Exception {
        var c=cliente();
        assertThrows(ServicioRemotoException.class, () -> c.recursos().obtenerTodos());
        c.login().autenticarSesion("111", "uno");
        assertThrows(ValidacionException.class, () -> c.recursos().crear(new RecursoDTO("R2", "CAT", "Otra")));
        assertThrows(ServicioRemotoException.class, () -> c.funcionarios().obtenerTodos());
        c.login().autenticarSesion("admin", "abc");
        assertThrows(ValidacionException.class, () -> c.login().autenticarSesion("admin", "mala"));
        assertThrows(ValidacionException.class, () -> c.recursos().crear(new RecursoDTO("R2", "CAT", "Otra")));
        assertNull(datos.recursos.buscarPorId("R2"));
    }
    @Test void recursosValidanCategoriaDuplicadosYReferencias() throws Exception {
        var admin=cliente(); admin.login().autenticarSesion("admin", "abc");
        assertThrows(ValidacionException.class, () -> admin.recursos().crear(new RecursoDTO("R2", "NO", "Otra")));
        admin.recursos().crear(new RecursoDTO("R2", "CAT", "Otra"));
        assertThrows(ValidacionException.class, () -> admin.recursos().crear(new RecursoDTO("R2", "CAT", "Otra")));
        admin.recursos().modificar(new RecursoDTO("R2", "CAT", "Editada"));
        assertTrue(admin.recursos().obtenerTodos().stream().anyMatch(r -> "Editada".equals(r.getDescripcion())));
        admin.recursos().eliminar("R2");
        assertNull(datos.recursos.buscarPorId("R2"));
        var funcionario=cliente(); funcionario.login().autenticarSesion("111", "uno"); reservar(funcionario,"111");
        assertThrows(ServicioRemotoException.class, () -> admin.recursos().eliminar("R1"));
    }
    @Test void reservaNotificaSoloAOtrosUsuariosYSePuedeCancelar() throws Exception {
        var a=cliente();var b=cliente();var mismaCuenta=cliente();
        a.login().autenticarSesion("111", "uno");b.login().autenticarSesion("222", "dos");
        mismaCuenta.login().autenticarSesion("111", "uno");
        ReservaDTO reserva=reservar(a,"111");
        EventoReserva evento=b.conexion().eventos().poll(3,TimeUnit.SECONDS);
        assertNotNull(evento);assertEquals(reserva.getId(),evento.reserva().getId());
        assertNull(a.conexion().eventos().poll(100,TimeUnit.MILLISECONDS));
        assertNull(mismaCuenta.conexion().eventos().poll(100,TimeUnit.MILLISECONDS));
        assertThrows(ServicioRemotoException.class, () -> b.reservas().listarPorFuncionario("111"));
        assertThrows(ValidacionException.class, () -> b.reservas().cancelar(reserva.getId(), "111"));
        assertThrows(ValidacionException.class, () -> b.reservas().cancelar(reserva.getId(), "222"));
        assertThrows(ValidacionException.class, () -> reservar(b,"111"));
        a.reservas().cancelar(reserva.getId(),"111");
        assertEquals("CANCELADA",a.reservas().listarPorFuncionario("111").get(0).getEstado());
        assertNotNull(reservar(b,"222"));
    }
    @Test void dosReservasSimultaneasNoObtienenElMismoRecurso() throws Exception {
        var a=cliente();var b=cliente();
        a.login().autenticarSesion("111","uno");b.login().autenticarSesion("222","dos");
        var inicio=new CountDownLatch(1);
        try(var executor=Executors.newVirtualThreadPerTaskExecutor()) {
            Future<Boolean> fa=executor.submit(() -> { inicio.await();try { reservar(a,"111");return true; }catch(ValidacionException e){return false;} });
            Future<Boolean> fb=executor.submit(() -> { inicio.await();try { reservar(b,"222");return true; }catch(ValidacionException e){return false;} });
            inicio.countDown();
            assertNotEquals(fa.get(5,TimeUnit.SECONDS),fb.get(5,TimeUnit.SECONDS));
        }
        assertEquals(1,datos.reservas.obtenerTodos().size());
    }
    @Test void rechazaHorariosYParametrosInvalidosSinCrearReserva() throws Exception {
        var c=cliente();c.login().autenticarSesion("111","uno");
        assertThrows(ValidacionException.class, () -> c.reservas().intentarRegistrar("Reunión",fecha(),"25:00","26:00","111",List.of("CAT")));
        assertThrows(ValidacionException.class, () -> c.reservas().intentarRegistrar("Reunión",fecha(),"09:00","10:00","111",List.of("CAT","CAT")));
        assertTrue(datos.reservas.obtenerTodos().isEmpty());
        var error=c.conexion().solicitar("login","autenticar","admin","abc");
        assertNotNull(error.error()); // Método viejo no está expuesto a la red.
    }
    @Test void transportaLasConsultasExistentesSinContraseñas() throws Exception {
        var c=cliente();c.login().autenticarSesion("admin","abc");
        assertTrue(c.funcionarios().obtenerTodos().stream().allMatch(f -> f.getClave()==null));
        assertFalse(c.categorias().listarTodos().isEmpty());
        assertNotNull(c.calendarizacion().obtenerMatriz(fecha(),"CAT"));
        assertNotNull(c.programacion().obtenerMatrizSemana(fecha()));
        assertNotNull(c.estadisticas().estadisticasRecursos(fecha(),fecha()));
        assertNotNull(c.estadisticas().estadisticasActividades(fecha(),fecha()));
    }
    @Test void variasSolicitudesDeUnaConexionNoMezclanRespuestas() throws Exception {
        var c=cliente();c.login().autenticarSesion("admin","abc");
        try(var executor=Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> resultados=new ArrayList<>();
            for(int i=0;i<15;i++) {
                resultados.add(executor.submit(() -> assertEquals("R1",c.recursos().obtenerTodos().get(0).getId())));
                resultados.add(executor.submit(() -> assertEquals("CAT",c.categorias().listarTodos().get(0).getId())));
            }
            for(var f:resultados) f.get(10,TimeUnit.SECONDS);
        }
    }
    @Test void laDesconexionNoDejaSolicitudesEsperandoIndefinidamente() throws Exception {
        var c=cliente();c.login().autenticarSesion("111","uno");
        servidor.close();
        assertTimeoutPreemptively(java.time.Duration.ofSeconds(3), () ->
            assertThrows(ServicioRemotoException.class, () -> c.recursos().obtenerTodos()));
    }
    @Test void unServidorQueNoRespondeAgotaElTiempoYCierraLaConexion() throws Exception {
        try(var lento=new ServerSocket(0);var conexion=new ConexionServidor("127.0.0.1",lento.getLocalPort(),150);
            var aceptado=lento.accept()) {
            assertTimeoutPreemptively(java.time.Duration.ofSeconds(2), () ->
                assertThrows(ServicioRemotoException.class, () -> conexion.solicitar("login","cerrarSesion")));
            assertThrows(ServicioRemotoException.class, () -> conexion.solicitar("login","cerrarSesion"));
        }
    }
}
