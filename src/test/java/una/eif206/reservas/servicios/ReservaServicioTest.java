package una.eif206.reservas.servicios;

import una.eif206.reservas.DTO.RecursoDTO;
import una.eif206.reservas.DTO.ReservaDTO;
import una.eif206.reservas.datos.RecursoDatos;
import una.eif206.reservas.datos.ReservaDatos;
import una.eif206.reservas.logica.ReservaLogica;
import una.eif206.reservas.logica.ValidacionException;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ReservaServicioTest {
    static class LogicaFalsa extends ReservaLogica {
        boolean intentarRegistrarFueLlamado=false;
        boolean cancelarFueLlamado=false;

        LogicaFalsa(){
            super(new ReservaDatos("no-existe.xml"),new RecursoDatos("no-existe.xml"));
        }
        @Override
        public ReservaDTO intentarRegistrar(String actividad, String fecha, String horaInicio, String horaFin, String funcionarioId, List<String> categoriasIdsString )throws ValidacionException{
            intentarRegistrarFueLlamado=true;
            return new ReservaDTO("RES-1",actividad,fecha,horaInicio,horaFin,funcionarioId,List.of(),"ACTIVA");
        }
        @Override
        public void cancelar(String reservaId,String funcionarioId)throws ValidacionException{
            cancelarFueLlamado=true;
        }
    }
    @Test
    void intentarRegistrarDelegaDirectoALaLogica()throws ValidacionException{
        LogicaFalsa logicaFalsa=new LogicaFalsa();
        ReservaServicio servicio=new ReservaServicio(logicaFalsa);

        servicio.intentarRegistrar("Reunion","2026-08-26","09:00","10:00","111",List.of("CAT-1"));

        assertTrue(logicaFalsa.intentarRegistrarFueLlamado);
    }
    @Test
    void cancelarDelegaDirectoALaLogica()throws ValidacionException{
        LogicaFalsa logicaFalsa=new LogicaFalsa();
        ReservaServicio servicio=new ReservaServicio(logicaFalsa);
        servicio.cancelar("RES-1","111");
        assertTrue(logicaFalsa.cancelarFueLlamado);
    }
}
