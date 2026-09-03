package una.eif206.reservas.datos;

import una.eif206.reservas.DTO.RecursoDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RecursoDatosIT {
    @TempDir
    Path tempDir;

    @Test
    void obtenerTodosConArchivoInexistenteDevuelveListaVacia(){
        RecursoDatos datos=new RecursoDatos(tempDir.resolve("no-existe.xml").toString());
        assertTrue(datos.obtenerTodos().isEmpty());
    }
    @Test
    void guardarYObtenerTodosPersisteCorrectamente(){
        String ruta=tempDir.resolve("recursos.xml").toString();
        RecursoDatos datos=new RecursoDatos(ruta);

        datos.guardar(new RecursoDTO("R1","CAT-000001","Sala 1"));
        datos.guardar(new RecursoDTO("R2","CAT-000002","Laptop"));

        RecursoDatos datosRecargados=new RecursoDatos(ruta);
        List<RecursoDTO> lista=datosRecargados.obtenerTodos();

        assertEquals(2,lista.size());
    }
    @Test
    void buscarPorCategoriaEsInsensibleAMayusculas(){
        String ruta=tempDir.resolve("recursos.xml").toString();
        RecursoDatos datos=new RecursoDatos(ruta);

        datos.guardar(new RecursoDTO("R1","CAT-000001","Sala 1"));

        List<RecursoDTO> resultado=datos.buscarPorCategoria("cat-000001");
        assertEquals(1,resultado.size());
    }
    @Test
    void buscarPorIdInexistenteDevuelveNull(){
        String ruta=tempDir.resolve("recursos.xml").toString();
        RecursoDatos datos=new RecursoDatos(ruta);
        assertNull(datos.buscarPorId("no-existe"));
    }
    @Test
    void guardarConIdExistenteReemplazaElRecurso(){
        String ruta=tempDir.resolve("recursos.xml").toString();
        RecursoDatos datos=new RecursoDatos(ruta);

        datos.guardar(new RecursoDTO("R1","CAT-000001","Sala 1"));
        datos.guardar(new RecursoDTO("R1","CAT-000001","Sala 1 Renovada"));

        List<RecursoDTO> lista = datos.obtenerTodos();
        assertEquals(1, lista.size());
        assertEquals("Sala 1 Renovada", lista.get(0).getDescripcion());

    }
    @Test
    void eliminarRecursoLoQuitaDelArchivo(){
        String ruta=tempDir.resolve("recursos.xml").toString();
        RecursoDatos datos=new RecursoDatos(ruta);
        datos.guardar(new RecursoDTO("R1","CAT-000001","Sala 1"));
        datos.eliminar("R1");
        assertTrue(datos.obtenerTodos().isEmpty());
    }
}
