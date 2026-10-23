package una.eif206.reservas.cliente;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import una.eif206.reservas.DTO.*;
import una.eif206.reservas.configuracion.ConexionBD;
import una.eif206.reservas.datos.UsuarioMySqlDatos;
import una.eif206.reservas.datos.mysql.Jdbc;
import una.eif206.reservas.excepciones.*;
import una.eif206.reservas.seguridad.Claves;
import una.eif206.reservas.servidor.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named="RESERVAS_PRUEBAS_MYSQL", matches="true")
class FlujoMySqlIT {
    @Test void clientesRemotosReservanCancelanYConservanDatosAlReiniciar() throws Exception {
        try (var c = ConexionBD.abrir()) { assertTrue(c.getCatalog().endsWith("_pruebas")); }
        String base = "RED-" + UUID.randomUUID(), admin = base + "-A", f1 = base + "-F1", f2 = base + "-F2";
        String cat = base + "-C", recurso = base + "-R";
        String hash = Claves.proteger("Clave de prueba");
        var servidores = new ArrayList<ServidorReservas>();
        var clientes = new ArrayList<ProveedorServiciosRemoto>();
        try {
            for (String id : List.of(admin, f1, f2)) {
                Jdbc.actualizar("INSERT INTO usuarios VALUES (?, ?, ?)", id, hash, id.equals(admin) ? "ADMINISTRADOR" : "FUNCIONARIO");
                if (!id.equals(admin)) Jdbc.actualizar("INSERT INTO funcionarios VALUES (?, 'Persona prueba red', '0000')", id);
            }
            Jdbc.actualizar("INSERT INTO categorias VALUES (?, 'Categoría prueba red')", cat);
            var servidor = iniciar(servidores);
            var a = cliente(servidor, clientes, admin);
            var uno = cliente(servidor, clientes, f1);
            var dos = cliente(servidor, clientes, f2);
            assertTrue(a.categorias().listarTodos().stream().anyMatch(c -> c.getId().equals(cat)));
            a.recursos().crear(new RecursoDTO(recurso, cat, "Recurso prueba red"));
            a.recursos().modificar(new RecursoDTO(recurso, cat, "Recurso editado"));
            assertEquals("Recurso editado", a.recursos().buscarPorCategoria(cat).getFirst().getDescripcion());
            assertThrows(ValidacionException.class, () -> uno.recursos().crear(new RecursoDTO(base + "-NO", cat, "Sin permisos")));
            assertThrows(ValidacionException.class, () -> uno.reservas().intentarRegistrar("Ajena", "2030-05-12", "09:00", "10:00", f2, List.of(cat)));
            var salida = new CountDownLatch(1);
            try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
                var x = pool.submit(() -> intento(uno, f1, cat, salida));
                var y = pool.submit(() -> intento(dos, f2, cat, salida));
                salida.countDown();
                ReservaDTO rx = x.get(15, TimeUnit.SECONDS), ry = y.get(15, TimeUnit.SECONDS);
                assertNotEquals(rx == null, ry == null);
                ReservaDTO ganadora = rx != null ? rx : ry;
                var propietario = rx != null ? uno : dos;
                var otro = rx != null ? dos : uno;
                String idOtro = rx != null ? f2 : f1;
                assertNotNull(otro.conexion().eventos().poll(2, TimeUnit.SECONDS));
                assertThrows(ServicioRemotoException.class, () -> a.recursos().eliminar(recurso));
                assertThrows(ValidacionException.class, () -> otro.reservas().cancelar(ganadora.getId(), idOtro));
                propietario.reservas().cancelar(ganadora.getId(), ganadora.getFuncionarioId());
                ReservaDTO nueva = otro.reservas().intentarRegistrar("Reutilizada", "2030-05-12", "09:00", "10:00", idOtro, List.of(cat));
                servidor.close();
                var reiniciado = iniciar(servidores);
                var reconectado = cliente(reiniciado, clientes, idOtro);
                assertTrue(reconectado.reservas().listarPorFuncionario(idOtro).stream().anyMatch(r -> r.getId().equals(nueva.getId())));
            }
        } finally {
            for (var c : clientes) c.close();
            for (var s : servidores) s.close();
            Jdbc.actualizar("DELETE rr FROM reserva_recursos rr JOIN reservas r ON r.id=rr.reserva_id WHERE r.funcionario_id IN (?, ?)", f1, f2);
            Jdbc.actualizar("DELETE FROM reservas WHERE funcionario_id IN (?, ?)", f1, f2);
            Jdbc.actualizar("DELETE FROM recursos WHERE categoria_id = ?", cat);
            Jdbc.actualizar("DELETE FROM categorias WHERE id = ?", cat);
            Jdbc.actualizar("DELETE FROM funcionarios WHERE usuario_id IN (?, ?)", f1, f2);
            Jdbc.actualizar("DELETE FROM usuarios WHERE id IN (?, ?, ?)", admin, f1, f2);
        }
    }
    private ServidorReservas iniciar(List<ServidorReservas> servidores) throws Exception {
        var servidor = new ServidorReservas("127.0.0.1", 0,
                new ServiciosServidor(Path.of("no-se-utiliza-xml"), new UsuarioMySqlDatos()));
        servidores.add(servidor);
        Thread.ofVirtual().start(() -> { try { servidor.ejecutar(); } catch (java.io.IOException e) { throw new RuntimeException(e); } });
        return servidor;
    }
    private ProveedorServiciosRemoto cliente(ServidorReservas s, List<ProveedorServiciosRemoto> clientes, String id) throws Exception {
        var c = new ProveedorServiciosRemoto("127.0.0.1", s.puerto(), 15000);
        clientes.add(c); c.login().autenticarSesion(id, "Clave de prueba"); return c;
    }
    private ReservaDTO intento(ProveedorServiciosRemoto c, String id, String cat, CountDownLatch salida) throws Exception {
        salida.await();
        try { return c.reservas().intentarRegistrar("Prueba simultánea", "2030-05-12", "09:00", "10:00", id, List.of(cat)); }
        catch (ValidacionException e) { assertTrue(e.getMessage().contains("disponibilidad")); return null; }
    }
}
