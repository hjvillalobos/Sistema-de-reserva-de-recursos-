package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.CategoriaDTO;
import una.eif206.reservas.datos.CategoriaDatos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoriaLogicaIT {
    @TempDir
    Path tempDir;

    @Test
    void crearVariasCategoriasGeneraIdsConsecutivosYPersisteEnDisco() throws ValidacionException {
        String ruta = tempDir.resolve("categorias.xml").toString();
        CategoriaDatos datos = new CategoriaDatos(ruta);
        CategoriaLogica service = new CategoriaLogica(datos);

        CategoriaDTO c1 = service.crear(new CategoriaDTO(null,"Sala para 10 personas"));
        CategoriaDTO c2 = service.crear(new CategoriaDTO(null,"Laptop windows 11"));

        assertEquals("CAT-000001", c1.getId());
        assertEquals("CAT-000002", c2.getId());

        CategoriaDatos datosRecargado = new CategoriaDatos(ruta);
        List<CategoriaDTO> lista = datosRecargado.obtenerTodos();
        assertEquals(2, lista.size());
    }
}
