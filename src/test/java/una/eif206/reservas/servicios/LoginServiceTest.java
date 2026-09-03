package una.eif206.reservas.servicios;

import una.eif206.reservas.DTO.RolDTO;
import una.eif206.reservas.DTO.UsuarioDTO;
import una.eif206.reservas.datos.AdministradorDatos;
import una.eif206.reservas.datos.FuncionarioDatos;
import una.eif206.reservas.logica.LoginLogica;
import una.eif206.reservas.logica.ValidacionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {
    static class LogicaFalsa extends LoginLogica{
        boolean autenticarFueLlamado=false;

        LogicaFalsa(){
           super(new AdministradorDatos("no-existe.xml"),new FuncionarioDatos("no-existe.xml"));
        }
        @Override
        public UsuarioDTO autenticar(String id, String clave)throws ValidacionException{
            autenticarFueLlamado=true;
            return new UsuarioDTO(id,clave,RolDTO.ADMINISTRADOR);
        }
    }
    @Test
    void autenticarDelegaDirectoALaLogica()throws ValidacionException{
        LogicaFalsa logicaFalsa= new LogicaFalsa();
        LoginService service=new LoginService(logicaFalsa);

        UsuarioDTO resultado=service.autenticar("admin","admin123");
        assertTrue(logicaFalsa.autenticarFueLlamado);
        assertEquals("admin",resultado.getId());

    }
}
