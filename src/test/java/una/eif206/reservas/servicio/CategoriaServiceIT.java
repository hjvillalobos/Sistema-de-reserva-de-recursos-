// src/test/java/una/eif206/reservas/servicio/CategoriaServiceIT.java
package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.Categoria;
import una.eif206.reservas.persistencia.CategoriaDAOXml;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaServiceIT {

    @TempDir
    Path tempDir;

    @Test
    void crearVariasCategoriasGeneraIdsConsecutivosYPersisteEnDisco() throws ValidacionException {
        String ruta = tempDir.resolve("categorias.xml").toString();
        CategoriaDAOXml dao = new CategoriaDAOXml(ruta);
        CategoriaService service = new CategoriaService(dao);

        Categoria c1 = service.crear("Sala para 10 personas");
        Categoria c2 = service.crear("Laptop windows 11");

        assertEquals("CAT-000001", c1.getId());
        assertEquals("CAT-000002", c2.getId());

        CategoriaDAOXml daoRecargado = new CategoriaDAOXml(ruta);
        List<Categoria> lista = daoRecargado.obtenerTodos();
        assertEquals(2, lista.size());
    }
}