package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.RecursoDTO;
import una.eif206.reservas.datos.RecursoDatos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RecursoLogicaIT {
    @TempDir
    Path tempDir;

    @Test
    void crearModificarYPersistirEnXmlReal() throws ValidacionException {
        String ruta = tempDir.resolve("recursos.xml").toString();
        RecursoDatos datos = new RecursoDatos(ruta);
        RecursoLogica logica = new RecursoLogica(datos);

        logica.crear(new RecursoDTO("111222", "CAT-000001", "Proyector Epson"));
        logica.modificar(new RecursoDTO("111222", "CAT-000001", "Proyector Epson - Sala principal"));

        RecursoDatos datosRecargado = new RecursoDatos(ruta);
        List<RecursoDTO> lista = datosRecargado.obtenerTodos();

        assertEquals(1, lista.size());
        assertEquals("Proyector Epson - Sala principal", lista.get(0).getDescripcion());
    }
}
