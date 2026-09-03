package una.eif206.reservas.servicios;

import una.eif206.reservas.DTO.EstadisticaCategoriaDTO;
import una.eif206.reservas.datos.CategoriaDatos;
import una.eif206.reservas.datos.RecursoDatos;
import una.eif206.reservas.datos.ReservaDatos;
import una.eif206.reservas.logica.EstadisticasLogica;
import una.eif206.reservas.logica.ValidacionException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EstadisticasServicioTest {
    static class LogicaFalsa extends EstadisticasLogica{
        boolean estadisticasRecursosFueLlamado=false;
        LogicaFalsa (){
            super(new ReservaDatos("no-existe.xml"),new RecursoDatos("no-existe.xml"),new CategoriaDatos("no-existe.xml"));
        }
        @Override
        public List<EstadisticaCategoriaDTO> estadisticasRecursos(String desde, String hasta)throws ValidacionException{
            estadisticasRecursosFueLlamado=true;
            return List.of();
        }
    }
    @Test
    void estadisticasRecursosDelgaDirectoALaLogica()throws ValidacionException{
        LogicaFalsa logicaFalsa=new LogicaFalsa();
        EstadisticasServicio servicio=new EstadisticasServicio(logicaFalsa);

        servicio.estadisticasRecursos("2026-08-01","2026-08-31");
        assertTrue(logicaFalsa.estadisticasRecursosFueLlamado);
    }
}
