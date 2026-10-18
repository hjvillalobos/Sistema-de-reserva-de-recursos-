package una.eif206.reservas.api;

import una.eif206.reservas.excepciones.ValidacionException;
import una.eif206.reservas.DTO.UsuarioDTO;

/** Contrato extraido del Proyecto I; todavia no define el protocolo de red. */
public interface LoginApi {
    UsuarioDTO autenticar(String id, String clave) throws ValidacionException;

    void cambiarClave(UsuarioDTO usuario, String claveActual, String claveNueva) throws ValidacionException;
}
