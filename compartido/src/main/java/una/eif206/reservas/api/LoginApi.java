package una.eif206.reservas.api;

import una.eif206.reservas.excepciones.ValidacionException;
import una.eif206.reservas.DTO.UsuarioSesionDTO;


public interface LoginApi {
    UsuarioSesionDTO autenticarSesion(String id, String clave) throws ValidacionException;

    void cambiarClave(String claveActual, String claveNueva) throws ValidacionException;

    void cerrarSesion();
}
