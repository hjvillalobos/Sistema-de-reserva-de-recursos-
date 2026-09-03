package una.eif206.reservas.datos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import una.eif206.reservas.DTO.FuncionarioDTO;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FuncionarioDatosIT {
    @TempDir
    Path tempDir;

    @Test
    void guardarYBuscarPorIdFunciona(){
        String ruta=tempDir.resolve("funcionarios.xml").toString();
        FuncionarioDatos datos=new FuncionarioDatos(ruta);

        datos.guardar(new FuncionarioDTO("111", "111", "Juan Perez", "8888-0000"));

        FuncionarioDatos datosRecargados=new FuncionarioDatos(ruta);
        FuncionarioDTO funcionario=datosRecargados.buscarPorId("111");

        assertNotNull(funcionario);
        assertEquals("Juan Perez",funcionario.getNombre());
    }
    @Test
    void buscarPorNombreEsInsensibleAMayusculas(){
        String ruta=tempDir.resolve("funcionarios.xml").toString();
        FuncionarioDatos datos=new FuncionarioDatos(ruta);

        datos.guardar(new  FuncionarioDTO("111", "111", "Juan Perez", "8888-0000"));

        assertEquals(1,datos.buscarPorNombre("JUAN").size());
    }
}
