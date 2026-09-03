package una.eif206.reservas.servicios;

import una.eif206.reservas.DTO.FuncionarioDTO;
import una.eif206.reservas.datos.FuncionarioDatos;
import una.eif206.reservas.logica.FuncionarioLogica;
import una.eif206.reservas.logica.ValidacionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FuncionarioServicioTest {
    static class LogicaFalsa extends FuncionarioLogica{
        boolean crearFueLlamado=false;

        LogicaFalsa(){
            super(new FuncionarioDatos("no-existe.xml"));
        }
        @Override
        public FuncionarioDTO crear(FuncionarioDTO funcionarioDTO)throws ValidacionException{
            crearFueLlamado=true;
            return funcionarioDTO;
        }
    }
    @Test
    void crearDelegaDirectoALaLogica() throws ValidacionException{
        LogicaFalsa logicaFalsa=new LogicaFalsa();
        FuncionarioServicio servicio=new FuncionarioServicio(logicaFalsa);

        FuncionarioDTO entrada=new FuncionarioDTO("111",null,"Juan","8888-8888");
        FuncionarioDTO resultado=servicio.crear(entrada);

        assertTrue(logicaFalsa.crearFueLlamado);
        assertSame(entrada,resultado);
    }
}
