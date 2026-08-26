package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.Recurso;
import una.eif206.reservas.persistencia.RecursoDAOXml;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecursoServiceIT {

    @TempDir
    Path tempDir;

    @Test
    void crearModificarYPersistirEnXmlReal() throws ValidacionException {
        String ruta = tempDir.resolve("recursos.xml").toString();
        RecursoDAOXml dao = new RecursoDAOXml(ruta);
        RecursoService service = new RecursoService(dao);

        service.crear("111222", "CAT-000001", "Proyector Epson");
        service.modificar("111222", "CAT-000001", "Proyector Epson - Sala principal");

        RecursoDAOXml daoRecargado = new RecursoDAOXml(ruta);
        List<Recurso> lista = daoRecargado.obtenerTodos();

        assertEquals(1, lista.size());
        assertEquals("Proyector Epson - Sala principal", lista.get(0).getDescripcion());
    }
}