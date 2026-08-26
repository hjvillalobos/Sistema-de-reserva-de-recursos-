package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.Recurso;
import una.eif206.reservas.modelo.Reserva;
import una.eif206.reservas.persistencia.RecursoDAOXml;
import una.eif206.reservas.persistencia.ReservaDAOXml;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservaServiceIT {

    @TempDir
    Path tempDir;

    private String fechaManana() {
        return LocalDate.now().plusDays(1).toString();
    }

    @Test
    void registrarReservaCompletaConPersistenciaXmlReal() throws ValidacionException {
        String rutaReservas = tempDir.resolve("reservas.xml").toString();
        String rutaRecursos = tempDir.resolve("recursos.xml").toString();

        RecursoDAOXml recursoDAO = new RecursoDAOXml(rutaRecursos);
        recursoDAO.guardar(new Recurso("238715", "CAT-000002", "Laptop #238715"));

        ReservaDAOXml reservaDAO = new ReservaDAOXml(rutaReservas);
        ReservaService reservaService = new ReservaService(reservaDAO, recursoDAO);

        Reserva creada = reservaService.intentarRegistrar(
                "Sesion de Junta", fechaManana(), "09:00", "11:00",
                "111", List.of("CAT-000002"));

        assertEquals("238715", creada.getRecursosAsignados().get(0));

        // Releemos desde disco con instancias nuevas, para confirmar que quedó persistido
        ReservaDAOXml reservaDAORecargado = new ReservaDAOXml(rutaReservas);
        List<Reserva> reservasEnDisco = reservaDAORecargado.obtenerTodos();

        assertEquals(1, reservasEnDisco.size());
        assertEquals("Sesion de Junta", reservasEnDisco.get(0).getActividad());
        assertEquals("238715", reservasEnDisco.get(0).getRecursosAsignados().get(0));
    }

    @Test
    void cancelarReservaPersisteEstadoCanceladaEnXml() throws ValidacionException {
        String rutaReservas = tempDir.resolve("reservas.xml").toString();
        String rutaRecursos = tempDir.resolve("recursos.xml").toString();

        RecursoDAOXml recursoDAO = new RecursoDAOXml(rutaRecursos);
        recursoDAO.guardar(new Recurso("34343", "CAT-000001", "Sala 1"));

        ReservaDAOXml reservaDAO = new ReservaDAOXml(rutaReservas);
        ReservaService reservaService = new ReservaService(reservaDAO, recursoDAO);

        Reserva creada = reservaService.intentarRegistrar(
                "Charla tecnica", fechaManana(), "08:00", "09:00",
                "222", List.of("CAT-000001"));

        reservaService.cancelar(creada.getId(), "222");

        ReservaDAOXml reservaDAORecargado = new ReservaDAOXml(rutaReservas);
        Reserva recargada = reservaDAORecargado.buscarPorId(creada.getId());

        assertEquals(una.eif206.reservas.modelo.EstadoReserva.CANCELADA, recargada.getEstado());
    }
}