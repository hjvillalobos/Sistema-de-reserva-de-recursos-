package una.eif206.reservas.datos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import una.eif206.reservas.DTO.UsuarioDTO;
import una.eif206.reservas.DTO.RolDTO;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class AdministradorDatosIT {
    @TempDir
    Path tempDir;
    @Test
    void guardarYBuscarPorIdFunciona(){
        String ruta=tempDir.resolve("administradores.xml").toString();
        AdministradorDatos datos=new AdministradorDatos(ruta);

        datos.guardar(new UsuarioDTO("admin", "admin123", RolDTO.ADMINISTRADOR));

        AdministradorDatos datosRecargados=new AdministradorDatos(ruta);
        UsuarioDTO usuario=datosRecargados.buscarPorId("admin");

        assertNotNull(usuario);
        assertEquals("admin123",usuario.getClave());
    }
}
