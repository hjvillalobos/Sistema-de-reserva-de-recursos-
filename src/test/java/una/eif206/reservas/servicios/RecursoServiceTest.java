package una.eif206.reservas.servicios;

import una.eif206.reservas.DTO.RecursoDTO;
import una.eif206.reservas.datos.RecursoDatos;
import una.eif206.reservas.logica.RecursoLogica;
import una.eif206.reservas.logica.ValidacionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RecursoServiceTest {
    static class LogicaFalsa extends RecursoLogica{
        boolean crearFueLlamado=false;
        boolean eliminarFueLlamado=false;

        LogicaFalsa(){
            super(new RecursoDatos("no-existe.xml"));
        }
        @Override
        public RecursoDTO crear(RecursoDTO recursoDTO)throws ValidacionException{
            crearFueLlamado=true;
            return recursoDTO;
        }
        @Override
        public void eliminar(String id){
            eliminarFueLlamado=true;
        }
    }
    @Test
    void crearDelegaDirectoALaLogica()throws ValidacionException{
        LogicaFalsa logicaFalsa=new LogicaFalsa();
        RecursoServicio servicio=new RecursoServicio(logicaFalsa);

        RecursoDTO entrada=new RecursoDTO("R1","CAT-1","Sala 1");
        RecursoDTO resultado=servicio.crear(entrada);

        assertTrue(logicaFalsa.crearFueLlamado);
        assertSame(entrada,resultado);
    }
    @Test
    void eliminarDelegaDirectoALaLogica(){
        LogicaFalsa logicaFalsa=new LogicaFalsa();
        RecursoServicio servicio=new RecursoServicio(logicaFalsa);
        servicio.eliminar("R1");
        assertTrue(logicaFalsa.eliminarFueLlamado);
    }
}
