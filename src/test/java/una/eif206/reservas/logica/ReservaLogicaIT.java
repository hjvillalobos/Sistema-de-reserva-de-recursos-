package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.RecursoDTO;
import una.eif206.reservas.DTO.ReservaDTO;
import una.eif206.reservas.datos.RecursoDatos;
import una.eif206.reservas.datos.ReservaDatos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReservaLogicaIT {
    @TempDir
    Path tempDir;

    private String fechaManana() {
        return LocalDate.now().plusDays(1).toString();
    }

    @Test
    void registrarReservaCompletaConPersistenciaXmlReal() throws ValidacionException {
        String rutaReservas = tempDir.resolve("reservas.xml").toString();
        String rutaRecursos = tempDir.resolve("recursos.xml").toString();

        RecursoDatos recursoDatos = new RecursoDatos(rutaRecursos);
        recursoDatos.guardar(new RecursoDTO("238715", "CAT-000002", "Laptop #238715"));

        ReservaDatos reservaDatos = new ReservaDatos(rutaReservas);
        ReservaLogica reservaLogica = new ReservaLogica(reservaDatos, recursoDatos);

        ReservaDTO creada = reservaLogica.intentarRegistrar(
                "Sesion de Junta", fechaManana(), "09:00", "11:00",
                "111", List.of("CAT-000002"));

        assertEquals("238715", creada.getRecursosAsignados().get(0));

        ReservaDatos reservaDatosRecargado = new ReservaDatos(rutaReservas);
        List<ReservaDTO> reservasEnDisco = reservaDatosRecargado.obtenerTodos();

        assertEquals(1, reservasEnDisco.size());
        assertEquals("Sesion de Junta", reservasEnDisco.get(0).getActividad());
        assertEquals("238715", reservasEnDisco.get(0).getRecursosAsignados().get(0));
    }

    @Test
    void cancelarReservaPersisteEstadoCanceladaEnXml() throws ValidacionException {
        String rutaReservas = tempDir.resolve("reservas.xml").toString();
        String rutaRecursos = tempDir.resolve("recursos.xml").toString();

        RecursoDatos recursoDatos = new RecursoDatos(rutaRecursos);
        recursoDatos.guardar(new RecursoDTO("34343", "CAT-000001", "Sala 1"));

        ReservaDatos reservaDatos = new ReservaDatos(rutaReservas);
        ReservaLogica reservaService = new ReservaLogica(reservaDatos, recursoDatos);

        ReservaDTO creada = reservaService.intentarRegistrar(
                "Charla tecnica", fechaManana(), "08:00", "09:00",
                "222", List.of("CAT-000001"));

        reservaService.cancelar(creada.getId(), "222");

        ReservaDatos reservaDatosRecargado = new ReservaDatos(rutaReservas);
        ReservaDTO recargada = reservaDatosRecargado.buscarPorId(creada.getId());

        assertEquals("CANCELADA", recargada.getEstado());
    }
}
