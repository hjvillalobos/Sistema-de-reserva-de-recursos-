package una.eif206.reservas.servicios;

import una.eif206.reservas.logica.LoginLogica;
import una.eif206.reservas.logica.ValidacionException;
import una.eif206.reservas.DTO.UsuarioDTO;


public class LoginService {

    private final LoginLogica loginLogica;

    public LoginService(){
        this.loginLogica=new LoginLogica();
    }
    public LoginService(LoginLogica loginLogica){
        this.loginLogica=loginLogica;
    }

    public UsuarioDTO autenticar(String id, String clave) throws ValidacionException{
        return loginLogica.autenticar(id,clave);
    }
    public void cambiarClave(UsuarioDTO usuario, String claveActual, String claveNueva) throws ValidacionException {
        loginLogica.cambiarClave(usuario,claveActual,claveNueva);
    }
}
