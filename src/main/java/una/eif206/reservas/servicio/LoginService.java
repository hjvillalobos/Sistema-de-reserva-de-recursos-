// servicio/LoginService.java
package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.Funcionario;
import una.eif206.reservas.modelo.Rol;
import una.eif206.reservas.modelo.Usuario;
import una.eif206.reservas.persistencia.AdministradorDAO;
import una.eif206.reservas.persistencia.AdministradorDAOXml;
import una.eif206.reservas.persistencia.FuncionarioDAO;
import una.eif206.reservas.persistencia.FuncionarioDAOXml;

public class LoginService {

    private final AdministradorDAO administradorDAO;
    private final FuncionarioDAO funcionarioDAO;

    public LoginService() {
        this(new AdministradorDAOXml(), new FuncionarioDAOXml());
    }

    public LoginService(AdministradorDAO administradorDAO, FuncionarioDAO funcionarioDAO) {
        this.administradorDAO = administradorDAO;
        this.funcionarioDAO = funcionarioDAO;
    }

    public Usuario autenticar(String id, String clave) throws CredencialesInvalidasException {
        if (id == null || id.isBlank() || clave == null || clave.isBlank()) {
            throw new CredencialesInvalidasException("Debe indicar id y clave.");
        }

        Usuario admin = administradorDAO.buscarPorId(id);
        if (admin != null && admin.getClave().equals(clave)) {
            return admin;
        }

        Funcionario funcionario = funcionarioDAO.buscarPorId(id);
        if (funcionario != null && funcionario.getClave().equals(clave)) {
            return funcionario;
        }

        throw new CredencialesInvalidasException("Id o clave incorrectos.");
    }

    public void cambiarClave(Usuario usuario, String claveActual, String claveNueva)
            throws CredencialesInvalidasException {
        if (!usuario.getClave().equals(claveActual)) {
            throw new CredencialesInvalidasException("La clave actual no coincide.");
        }
        if (claveNueva == null || claveNueva.isBlank()) {
            throw new CredencialesInvalidasException("La clave nueva no puede estar vacía.");
        }

        if (usuario.getRol() == Rol.ADMINISTRADOR) {
            administradorDAO.actualizarClave(usuario.getId(), claveNueva);
        } else {
            Funcionario f = (Funcionario) usuario;
            f.setClave(claveNueva);
            funcionarioDAO.guardar(f);
        }
        usuario.setClave(claveNueva);
    }
}