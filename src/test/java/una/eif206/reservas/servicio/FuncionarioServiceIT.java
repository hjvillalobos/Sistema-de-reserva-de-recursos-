
package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.Funcionario;
import una.eif206.reservas.persistencia.FuncionarioDAOXml;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioServiceIT {

    @TempDir
    Path tempDir;

    @Test
    void crearModificarYPersistirEnXmlReal() throws ValidacionException {
        String ruta = tempDir.resolve("funcionarios.xml").toString();
        FuncionarioDAOXml dao = new FuncionarioDAOXml(ruta);
        FuncionarioService service = new FuncionarioService(dao);

        service.crear("555", "Ana Solis", "7777-1111");
        service.modificar("555", "Ana Solis Mora", "7777-2222");

        FuncionarioDAOXml daoRecargado = new FuncionarioDAOXml(ruta);
        List<Funcionario> lista = daoRecargado.obtenerTodos();

        assertEquals(1, lista.size());
        assertEquals("Ana Solis Mora", lista.get(0).getNombre());
        assertEquals("555", lista.get(0).getClave()); // la clave no cambia al modificar datos
    }
}