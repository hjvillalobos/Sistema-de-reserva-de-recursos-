package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.MatrizCalendarizacionDTO;
import una.eif206.reservas.DTO.RecursoDTO;
import una.eif206.reservas.DTO.ReservaDTO;
import una.eif206.reservas.datos.FuncionarioDatos;
import una.eif206.reservas.datos.ReservaDatos;
import una.eif206.reservas.datos.RecursoDatos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CalendarizacionLogicaIT {
    @TempDir
    Path tempDir;

    @Test
    void obtenerMatrizConDatosPersistidosEnXmlReal()throws ValidacionException{
        String rutaReservas = tempDir.resolve("reservas.xml").toString();
        String rutaRecursos = tempDir.resolve("recursos.xml").toString();
        String rutaFuncionarios=tempDir.resolve("funcionarios.xml").toString();

        RecursoDatos recursoDatos = new RecursoDatos(rutaRecursos);
        recursoDatos.guardar(new RecursoDTO("238715", "CAT-000001", "Laptop #238715"));

        ReservaDatos reservaDatos = new ReservaDatos(rutaReservas);
        reservaDatos.guardar(new ReservaDTO("RES-000001", "Jugar futbol", LocalDate.now().toString(),"09:00","11:00","111",List.of("238715"),"ACTIVA"));

        FuncionarioDatos funcionarioDatos = new FuncionarioDatos(rutaFuncionarios);
        CalendarizacionLogica calendarizacionLogica = new CalendarizacionLogica(recursoDatos, reservaDatos,funcionarioDatos);
        MatrizCalendarizacionDTO matriz=calendarizacionLogica.obtenerMatriz(LocalDate.now().toString(),"CAT-000001");

        assertEquals(1, matriz.getRecursos().size());
        int indiceHora = matriz.getHoras().indexOf("09:00");
        assertTrue(matriz.obtenerCelda(indiceHora, 0).isOcupada());
    }
}
