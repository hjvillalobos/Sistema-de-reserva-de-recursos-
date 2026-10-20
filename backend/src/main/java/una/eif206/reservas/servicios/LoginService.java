package una.eif206.reservas.servicios;

import una.eif206.reservas.api.LoginApi;

import una.eif206.reservas.logica.LoginLogica;
import una.eif206.reservas.excepciones.ValidacionException;
import una.eif206.reservas.DTO.UsuarioDTO;
import una.eif206.reservas.DTO.UsuarioSesionDTO;


public class LoginService implements LoginApi {

    private final LoginLogica loginLogica;
    private UsuarioSesionDTO sesion;

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
    @Override
    public UsuarioSesionDTO autenticarSesion(String id, String clave) throws ValidacionException {
        sesion = null;
        UsuarioSesionDTO usuario = loginLogica.autenticarSesion(id, clave);
        sesion = new UsuarioSesionDTO(usuario.getId(), usuario.getRol());
        return usuario;
    }

    public void cambiarClavePorId(String id, String claveActual, String claveNueva) throws ValidacionException {
        loginLogica.cambiarClavePorId(id, claveActual, claveNueva);
    }

    @Override
    public void cambiarClave(String claveActual, String claveNueva) throws ValidacionException {
        if (sesion == null) throw new ValidacionException("Debe iniciar sesión.");
        loginLogica.cambiarClavePorId(sesion.getId(), claveActual, claveNueva);
    }

    @Override
    public void cerrarSesion() { sesion = null; }
}
