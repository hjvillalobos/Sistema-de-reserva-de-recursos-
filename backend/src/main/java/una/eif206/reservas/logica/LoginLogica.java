package una.eif206.reservas.logica;

import una.eif206.reservas.excepciones.ValidacionException;


import una.eif206.reservas.DTO.FuncionarioDTO;
import una.eif206.reservas.datos.FuncionarioDatos;
import una.eif206.reservas.datos.AdministradorDatos;
import una.eif206.reservas.DTO.RolDTO;
import una.eif206.reservas.DTO.UsuarioDTO;
import una.eif206.reservas.DTO.UsuarioSesionDTO;


public class LoginLogica {

    private una.eif206.reservas.datos.UsuariosAcceso usuariosMySql;

    public LoginLogica(una.eif206.reservas.datos.UsuariosAcceso usuarios) {
        this.administradorDatos = null;
        this.funcionarioDatos = null;
        this.usuariosMySql = java.util.Objects.requireNonNull(usuarios);
    }

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
        if (usuariosMySql != null) {
            var credencial = comprobarMySql(id, clave);
            return new UsuarioDTO(credencial.id(), null, credencial.rol());
        }
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
        if (usuariosMySql != null) {
            if (usuario == null) throw new ValidacionException("Debe iniciar sesión.");
            cambiarClavePorId(usuario.getId(), claveActual, claveNueva);
            return;
        }
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
        if (usuariosMySql != null) {
            if (claveNueva == null || claveNueva.isBlank() || claveNueva.length() > 1024)
                throw new ValidacionException("La clave nueva debe tener entre 1 y 1024 caracteres y no estar en blanco.");
            var credencial = comprobarMySql(id, claveActual);
            try {
                if (!usuariosMySql.cambiarHash(credencial.id(), credencial.hash(),
                        una.eif206.reservas.seguridad.Claves.proteger(claveNueva)))
                    throw new ValidacionException("La cuenta cambió durante la operación. Vuelva a iniciar sesión.");
            } catch (java.sql.SQLException e) {
                throw new ValidacionException("No se pudo actualizar la clave en MySQL. Revise la conexión del servidor.");
            }
            return;
        }

        UsuarioDTO usuario = autenticar(id, claveActual);

        cambiarClave(usuario, claveActual, claveNueva);
    }

    private una.eif206.reservas.datos.UsuariosAcceso.Credencial comprobarMySql(String id, String clave)
            throws ValidacionException {
        if (id == null || id.isBlank() || id.length() > 100 || clave == null || clave.isBlank() || clave.length() > 1024)
            throw new ValidacionException("Debe indicar un id y una clave válidos.");
        try {
            var credencial = usuariosMySql.buscar(id);
            if (credencial == null || !una.eif206.reservas.seguridad.Claves.verificar(clave, credencial.hash()))
                throw new ValidacionException("Id o clave incorrectos.");
            return credencial;
        } catch (java.sql.SQLException e) {
            throw new ValidacionException("No se pudo consultar MySQL. Revise la conexión del servidor.");
        }
    }
}
