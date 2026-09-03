package una.eif206.reservas.servicios;

import una.eif206.reservas.DTO.MatrizCalendarizacionDTO;
import una.eif206.reservas.DTO.RecursoDTO;
import una.eif206.reservas.DTO.ReservaDTO;
import una.eif206.reservas.datos.FuncionarioDatos;
import una.eif206.reservas.datos.ReservaDatos;
import una.eif206.reservas.datos.RecursoDatos;
import una.eif206.reservas.logica.CalendarizacionLogica;
import una.eif206.reservas.logica.ValidacionException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CalendarizacionServiceTest {
    static class LogicaFalsa extends CalendarizacionLogica {
        boolean obtenerMatrizFueLlamado = false;

        LogicaFalsa() {
            super(new RecursoDatos("no-existe.xml"), new ReservaDatos("no-existe.xml"), new FuncionarioDatos("no-existe.xml"));

        }

        @Override
        public MatrizCalendarizacionDTO obtenerMatriz(String fecha, String categoriaId) throws ValidacionException {
            obtenerMatrizFueLlamado = true;
            return new MatrizCalendarizacionDTO(List.of(), List.of(), List.of());
        }
    }
    @Test
    void  obtenerMatrizDelegaDirectoALaLogica() throws ValidacionException{
        LogicaFalsa logicaFalsa=new LogicaFalsa();
        CalendarizacionServicio servicio=new CalendarizacionServicio(logicaFalsa);

        servicio.obtenerMatriz("2026-08-26","CAT-000001");
        assertTrue(logicaFalsa.obtenerMatrizFueLlamado);
    }
}
