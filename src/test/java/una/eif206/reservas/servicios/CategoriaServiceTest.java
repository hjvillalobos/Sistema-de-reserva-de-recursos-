package una.eif206.reservas.servicios;

import una.eif206.reservas.DTO.CategoriaDTO;
import una.eif206.reservas.datos.CategoriaDatos;
import una.eif206.reservas.logica.CategoriaLogica;
import una.eif206.reservas.logica.ValidacionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CategoriaServiceTest {
    static class LogicaFalsa extends CategoriaLogica{
        boolean crearFueLlamado=false;
        boolean eliminarFueLlamado=false;
        LogicaFalsa(){
            super(new CategoriaDatos("no-existe.xml"));
        }
        @Override
        public CategoriaDTO crear(CategoriaDTO categoria)throws ValidacionException{
            crearFueLlamado=true;
            return categoria;
        }
        @Override
        public void eliminar(String id)throws ValidacionException{
            eliminarFueLlamado=true;
        }
    }
    @Test
    void eliminarDelegaDirectoALaLogica()throws ValidacionException{
        LogicaFalsa logicaFalsa=new LogicaFalsa();
        CategoriaService service=new CategoriaService(logicaFalsa);
        service.eliminar("CAT-000001");
        assertTrue(logicaFalsa.eliminarFueLlamado);
    }
    @Test
    void crearDelegaDirectoALaLogica()throws ValidacionException{
        LogicaFalsa logicaFalsa=new LogicaFalsa();
        CategoriaService service=new CategoriaService(logicaFalsa);

        CategoriaDTO entrada=new CategoriaDTO(null,"Sala VIP");
        CategoriaDTO resultado=service.crear(entrada);

        assertTrue(logicaFalsa.crearFueLlamado);
        assertSame(entrada,resultado);
    }
}
