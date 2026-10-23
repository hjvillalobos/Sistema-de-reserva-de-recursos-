package una.eif206.reservas.datos.mysql;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import una.eif206.reservas.configuracion.ConexionBD;
import una.eif206.reservas.DTO.*;
import una.eif206.reservas.excepciones.ValidacionException;
import una.eif206.reservas.logica.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

/** Requiere una base de pruebas explícita. Nunca usa sistema_reservas. */
@EnabledIfEnvironmentVariable(named = "RESERVAS_PRUEBAS_MYSQL", matches = "true")
class ReservasMySqlIT {
    String prefijo, f1, f2, cat, otra, recurso;
    final RecursoMySqlDatos recursos = new RecursoMySqlDatos();
    final ReservaMySqlDatos reservas = new ReservaMySqlDatos();
    ReservaMySqlLogica logica() { return new ReservaMySqlLogica(new ReservaMySqlDatos(), new RecursoMySqlDatos()); }
    @BeforeEach void preparar() throws Exception {
        try (var c = ConexionBD.abrir()) {
            assertTrue(c.getCatalog().endsWith("_pruebas"), "Solo se permite una base con sufijo _pruebas");
        }
        prefijo = "IT-" + UUID.randomUUID();
        f1 = prefijo + "-F1"; f2 = prefijo + "-F2";
        cat = prefijo + "-C1"; otra = prefijo + "-C2"; recurso = prefijo + "-R1";
        for (String id : List.of(f1, f2)) {
            Jdbc.actualizar("INSERT INTO usuarios VALUES (?, ?, 'FUNCIONARIO')", id, "no-utilizado-en-esta-prueba");
            Jdbc.actualizar("INSERT INTO funcionarios VALUES (?, ?, '88888888')", id, "Persona de prueba");
        }
        Jdbc.actualizar("INSERT INTO categorias VALUES (?, 'Categoría de prueba')", cat);
        Jdbc.actualizar("INSERT INTO categorias VALUES (?, 'Segunda categoría')", otra);
        new RecursoLogica(recursos).crear(new RecursoDTO(recurso, cat, "Recurso de prueba"));
    }
    @AfterEach void limpiar() {
        if (prefijo == null) return;
        Jdbc.actualizar("DELETE rr FROM reserva_recursos rr JOIN reservas r ON r.id=rr.reserva_id WHERE r.funcionario_id IN (?, ?)", f1, f2);
        Jdbc.actualizar("DELETE FROM reservas WHERE funcionario_id IN (?, ?)", f1, f2);
        Jdbc.actualizar("DELETE FROM recursos WHERE categoria_id IN (?, ?)", cat, otra);
        Jdbc.actualizar("DELETE FROM categorias WHERE id IN (?, ?)", cat, otra);
        Jdbc.actualizar("DELETE FROM funcionarios WHERE usuario_id IN (?, ?)", f1, f2);
        Jdbc.actualizar("DELETE FROM usuarios WHERE id IN (?, ?)", f1, f2);
    }
    ReservaDTO reservar(String quien, String inicio, String fin, List<String> categorias) throws Exception {
        return logica().intentarRegistrar("Actividad", "2030-05-12", inicio, fin, quien, categorias);
    }
    @Test void recursosCreanModificanFiltranYRespetanRelaciones() throws Exception {
        var logica = new RecursoLogica(recursos);
        assertEquals(1, logica.buscarPorCategoria(cat).size());
        logica.modificar(new RecursoDTO(recurso, cat, "Editado"));
        logica.modificar(new RecursoDTO(recurso, cat, "Editado"));
        assertEquals("Editado", recursos.buscarPorId(recurso).getDescripcion());
        assertThrows(ValidacionException.class, () -> logica.crear(new RecursoDTO(recurso, cat, "Duplicado")));
        assertThrows(ErrorDatos.class, () -> logica.crear(new RecursoDTO(prefijo + "-MAL", "inexistente", "Mal")));
        logica.eliminar(recurso);
        assertNull(recursos.buscarPorId(recurso));
    }
    @Test void reservaCancelaYPermiteReutilizarSinEliminarHistorial() throws Exception {
        var r = reservar(f1, "09:00", "10:00", List.of(cat));
        assertEquals(List.of(recurso), r.getRecursosAsignados());
        assertEquals("09:00", reservas.buscarPorId(r.getId()).getHoraInicio());
        assertEquals(1, reservas.buscarPorFuncionario(f1).size());
        assertTrue(reservas.buscarPorFuncionario(f2).isEmpty());
        assertThrows(ValidacionException.class, () -> logica().cancelar(r.getId(), f2));
        assertThrows(ErrorDatos.class, () -> recursos.eliminar(recurso));
        assertThrows(ValidacionException.class, () -> reservar(f2, "09:30", "10:30", List.of(cat)));
        reservar(f2, "10:00", "11:00", List.of(cat)); // Horarios contiguos válidos.
        logica().cancelar(r.getId(), f1);
        assertEquals("CANCELADA", reservas.buscarPorId(r.getId()).getEstado());
        assertEquals(List.of(recurso), reservas.buscarPorId(r.getId()).getRecursosAsignados());
        reservar(f2, "09:00", "10:00", List.of(cat));
        assertThrows(ErrorDatos.class, () -> recursos.eliminar(recurso));
    }
    @Test void fallaCompletaSiUnaCategoriaNoTieneDisponibilidad() throws Exception {
        assertThrows(ValidacionException.class, () -> reservar(f1, "09:00", "10:00", List.of(cat, otra)));
        assertTrue(reservas.buscarPorFuncionario(f1).isEmpty());
        assertThrows(ValidacionException.class, () -> reservar(f1, "09:00", "10:00", List.of(cat, cat.toLowerCase(Locale.ROOT))));
        assertThrows(ValidacionException.class, () -> reservar("inexistente", "09:00", "10:00", List.of(cat)));
        assertThrows(ValidacionException.class, () -> reservar(f1, "10:00", "09:00", List.of(cat)));
        assertEquals(1, reservar(f1, "09:00", "10:00", List.of(cat)).getRecursosAsignados().size());
    }
    @Test void dosConexionesEsperanElBloqueoYSoloUnaObtieneElRecurso() throws Exception {
        try (var bloqueo = ConexionBD.abrir(); var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            bloqueo.setAutoCommit(false);
            try (var p = Jdbc.preparar(bloqueo, "SELECT id FROM recursos WHERE id = ? FOR UPDATE", recurso);
                 var rs = p.executeQuery()) { assertTrue(rs.next()); }
            var listas = new CountDownLatch(2);
            Callable<Boolean> intento = () -> {
                listas.countDown();
                try { reservar(f1, "09:00", "10:00", List.of(cat)); return true; }
                catch (ValidacionException e) { assertTrue(e.getMessage().contains("disponibilidad")); return false; }
            };
            var a = pool.submit(intento); var b = pool.submit(intento);
            assertTrue(listas.await(2, TimeUnit.SECONDS));
            try { assertThrows(TimeoutException.class, () -> a.get(150, TimeUnit.MILLISECONDS)); }
            finally { bloqueo.commit(); }
            assertNotEquals(a.get(10, TimeUnit.SECONDS), b.get(10, TimeUnit.SECONDS));
            assertEquals(1, reservas.buscarPorFuncionario(f1).size());
        }
    }
    @Test void dosRecursosPermitenDosReservasSimultaneasSinCompartirRecurso() throws Exception {
        recursos.crear(new RecursoDTO(prefijo + "-R2", cat.toLowerCase(Locale.ROOT), "Segundo"));
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            var salida = new CountDownLatch(1);
            var a = pool.submit(() -> { salida.await(); return reservar(f1, "09:00", "10:00", List.of(cat)); });
            var b = pool.submit(() -> { salida.await(); return reservar(f2, "09:00", "10:00", List.of(cat)); });
            salida.countDown();
            assertNotEquals(a.get(10, TimeUnit.SECONDS).getRecursosAsignados(), b.get(10, TimeUnit.SECONDS).getRecursosAsignados());
        }
    }
    @Test void revierteCabeceraYPrimerDetalleSiFallaElSegundoDetalle() throws Exception {
        String segundo = prefijo + "-R2";
        recursos.crear(new RecursoDTO(segundo, otra, "Segundo"));
        String trigger = "fallo_" + UUID.randomUUID().toString().replace("-", "");
        // Identificadores generados por la prueba, nunca entrada de usuarios.
        Jdbc.actualizar("CREATE TRIGGER " + trigger + " BEFORE INSERT ON reserva_recursos FOR EACH ROW BEGIN "
                + "IF NEW.recurso_id = '" + segundo + "' THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='fallo de prueba'; END IF; END");
        try {
            assertThrows(ErrorDatos.class, () -> reservar(f1, "09:00", "10:00", List.of(cat, otra)));
            assertTrue(reservas.buscarPorFuncionario(f1).isEmpty());
            assertEquals(0L, Jdbc.consultar("SELECT COUNT(*) FROM reserva_recursos WHERE recurso_id = ?", r -> r.getLong(1), recurso).getFirst());
        } finally { Jdbc.actualizar("DROP TRIGGER " + trigger); }
        assertEquals(2, reservar(f1, "09:00", "10:00", List.of(cat, otra)).getRecursosAsignados().size());
    }
}
