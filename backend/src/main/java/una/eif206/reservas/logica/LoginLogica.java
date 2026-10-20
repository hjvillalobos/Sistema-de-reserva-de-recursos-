package una.eif206.reservas.logica;

import una.eif206.reservas.excepciones.ValidacionException;


import una.eif206.reservas.DTO.FuncionarioDTO;
import una.eif206.reservas.datos.FuncionarioDatos;
import una.eif206.reservas.datos.AdministradorDatos;
import una.eif206.reservas.DTO.RolDTO;
import una.eif206.reservas.DTO.UsuarioDTO;
import una.eif206.reservas.DTO.UsuarioSesionDTO;


public class LoginLogica {

    private final AdministradorDatos administradorDatos;
    private final FuncionarioDatos funcionarioDatos;

    public LoginLogica(){
        this.administradorDatos=new AdministradorDatos();
        this.funcionarioDatos=new FuncionarioDatos();
    }
    public LoginLogica(AdministradorDatos administradorDatos,FuncionarioDatos funcionarioDatos){
        this.funcionarioDatos=funcionarioDatos;
        this.administradorDatos=administradorDatos;
    }

    public UsuarioDTO autenticar(String id, String clave) throws ValidacionException {
        if (id == null || id.isBlank() || clave == null || clave.isBlank()) {
            throw new ValidacionException("Debe indicar id y clave.");
        }

        UsuarioDTO admin = administradorDatos.buscarPorId(id);
        if (admin != null && admin.getClave().equals(clave)) {
            return admin;
        }

        FuncionarioDTO funcionario = funcionarioDatos.buscarPorId(id);
        if (funcionario != null && funcionario.getClave().equals(clave)) {
            return funcionario;
        }

        throw new ValidacionException("Id o clave incorrectos.");
    }

    public UsuarioSesionDTO autenticarSesion(String id, String clave) throws ValidacionException {

        UsuarioDTO usuario = autenticar(id, clave);

        return new UsuarioSesionDTO(usuario.getId(), usuario.getRol());
    }

    public void cambiarClave(UsuarioDTO usuario, String claveActual, String claveNueva) throws ValidacionException {
        if (!usuario.getClave().equals(claveActual)) {
            throw new ValidacionException("La clave actual no coincide.");
        }
        if (claveNueva == null || claveNueva.isBlank()) {
            throw new ValidacionException("La clave nueva no puede estar vacía.");
        }

        if (usuario.getRol() == RolDTO.ADMINISTRADOR) {
            usuario.setClave(claveNueva);
            administradorDatos.guardar(usuario);
        } else {
            FuncionarioDTO funcionario = (FuncionarioDTO) usuario;
            funcionario.setClave(claveNueva);
            funcionarioDatos.guardar(funcionario);
        }
    }

    public void cambiarClavePorId(String id, String claveActual, String claveNueva) throws ValidacionException{

        UsuarioDTO usuario = autenticar(id, claveActual);

        cambiarClave(usuario, claveActual, claveNueva);
    }
}
