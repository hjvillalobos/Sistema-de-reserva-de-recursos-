package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.FuncionarioDTO;
import una.eif206.reservas.datos.FuncionarioDatos;
import una.eif206.reservas.datos.AdministradorDatos;
import una.eif206.reservas.DTO.RolDTO;
import una.eif206.reservas.DTO.UsuarioDTO;


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
}
