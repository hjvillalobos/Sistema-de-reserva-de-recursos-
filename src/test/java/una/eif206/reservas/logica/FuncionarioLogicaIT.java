package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.FuncionarioDTO;
import una.eif206.reservas.datos.FuncionarioDatos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FuncionarioLogicaIT {
    @TempDir
    Path tempDir;

    @Test
    void crearModificarYPersistirEnXmlReal() throws ValidacionException {
        String ruta = tempDir.resolve("funcionarios.xml").toString();
        FuncionarioDatos datos = new FuncionarioDatos(ruta);
        FuncionarioLogica logica = new FuncionarioLogica(datos);

        logica.crear(new FuncionarioDTO("555",null, "Ana Solis", "7777-1111"));
        logica.modificar(new FuncionarioDTO("555",null, "Ana Solis Mora", "7777-2222"));

        FuncionarioDatos datosRecargado = new FuncionarioDatos(ruta);
        List<FuncionarioDTO> lista = datosRecargado.obtenerTodos();

        assertEquals(1, lista.size());
        assertEquals("Ana Solis Mora", lista.get(0).getNombre());
        assertEquals("555", lista.get(0).getClave());
    }
}
