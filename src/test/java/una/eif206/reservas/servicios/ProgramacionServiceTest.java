package una.eif206.reservas.servicios;

import una.eif206.reservas.DTO.MatrizProgramacionDTO;
import una.eif206.reservas.datos.FuncionarioDatos;
import una.eif206.reservas.datos.ReservaDatos;
import una.eif206.reservas.logica.ProgramacionLogica;
import una.eif206.reservas.logica.ValidacionException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProgramacionServiceTest {
    static class LogicaFalsa extends ProgramacionLogica{
        boolean obtenerMatrizSemanaFueLlamado=false;
        LogicaFalsa(){
            super(new ReservaDatos("no-existe.xml"),new FuncionarioDatos("no-existe.xml"));
        }
        @Override
        public MatrizProgramacionDTO obtenerMatrizSemana(String fecha)throws ValidacionException{
            obtenerMatrizSemanaFueLlamado=true;
            return new MatrizProgramacionDTO(fecha,fecha, List.of(),List.of(),List.of());
        }
    }
    @Test
    void obtenerMatrizSemanaDelegaDirectoALaLogica()throws ValidacionException{
        LogicaFalsa logicaFalsa=new LogicaFalsa();
        ProgramacionServicio servicio=new ProgramacionServicio(logicaFalsa);

        servicio.obtenerMatrizSemana("2026-08-26");
        assertTrue(logicaFalsa.obtenerMatrizSemanaFueLlamado);
    }
}
