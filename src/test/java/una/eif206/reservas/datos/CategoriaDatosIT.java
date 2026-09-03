package una.eif206.reservas.datos;

import una.eif206.reservas.DTO.CategoriaDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoriaDatosIT {
    @TempDir
    Path tempDir;

    @Test
    void guardarYObtenerTodosPersisteCorrectamente(){
        String ruta=tempDir.resolve("categorias.xml").toString();
        CategoriaDatos datos=new CategoriaDatos(ruta);

        datos.guardar(new CategoriaDTO("CAT-000001","Sala 1"));

        CategoriaDatos datosRecargados=new CategoriaDatos(ruta);
        List<CategoriaDTO> lista=datosRecargados.obtenerTodos();
        assertEquals(1,lista.size());
    }
    @Test
    void buscarPorDescripcionFiltraPorTextoParcial(){
        String ruta=tempDir.resolve("categorias.xml").toString();
        CategoriaDatos datos=new CategoriaDatos(ruta);

        datos.guardar(new CategoriaDTO("CAT-000001","Laptop Windows"));

        List<CategoriaDTO> resultado=datos.buscarPorDescripcion("windows");
        assertEquals(1,resultado.size());
    }
}
