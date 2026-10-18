
package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.Rol;
import una.eif206.reservas.modelo.Usuario;
import una.eif206.reservas.persistencia.AdministradorDAOXml;
import una.eif206.reservas.persistencia.FuncionarioDAOXml;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class LoginServiceIT {

    @TempDir
    Path tempDir;

    @Test
    void loginYCambioDeClavePersistenEnXmlReal() throws CredencialesInvalidasException {
        String rutaAdmins = tempDir.resolve("administradores.xml").toString();
        String rutaFuncionarios = tempDir.resolve("funcionarios.xml").toString();

        AdministradorDAOXml adminDAO = new AdministradorDAOXml(rutaAdmins); // se autosemilla admin/admin
        FuncionarioDAOXml funcionarioDAO = new FuncionarioDAOXml(rutaFuncionarios);
        LoginService loginService = new LoginService(adminDAO, funcionarioDAO);

        Usuario u = loginService.autenticar("admin", "admin");
        assertEquals(Rol.ADMINISTRADOR, u.getRol());

        loginService.cambiarClave(u, "admin", "otraClave");

        Usuario recargado = loginService.autenticar("admin", "otraClave");
        assertEquals("otraClave", recargado.getClave());
        assertTrue(new File(rutaAdmins).exists());
    }
}