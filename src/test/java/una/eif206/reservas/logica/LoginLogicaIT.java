package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.RolDTO;
import una.eif206.reservas.DTO.UsuarioDTO;
import una.eif206.reservas.datos.AdministradorDatos;
import una.eif206.reservas.datos.FuncionarioDatos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginLogicaIT {
    @TempDir
    Path tempDir;

    @Test
    void loginYCambioDeClavePersistenEnXmlReal() throws ValidacionException {
        String rutaAdmins = tempDir.resolve("administradores.xml").toString();
        String rutaFuncionarios = tempDir.resolve("funcionarios.xml").toString();

        AdministradorDatos administradorDatos = new AdministradorDatos(rutaAdmins);
        administradorDatos.guardar(new UsuarioDTO("admin","admin",RolDTO.ADMINISTRADOR));
        FuncionarioDatos funcionarioDatos = new FuncionarioDatos(rutaFuncionarios);
        LoginLogica loginLogica = new LoginLogica(administradorDatos, funcionarioDatos);

        UsuarioDTO u = loginLogica.autenticar("admin", "admin");
        assertEquals(RolDTO.ADMINISTRADOR, u.getRol());

        loginLogica.cambiarClave(u, "admin", "otraClave");

        UsuarioDTO recargado = loginLogica.autenticar("admin", "otraClave");
        assertEquals("otraClave", recargado.getClave());
        assertTrue(new File(rutaAdmins).exists());
    }
}
