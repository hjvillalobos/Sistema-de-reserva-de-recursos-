package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.CategoriaDTO;
import una.eif206.reservas.DTO.RecursoDTO;
import una.eif206.reservas.DTO.ReservaDTO;
import una.eif206.reservas.DTO.EstadisticaCategoriaDTO;
import una.eif206.reservas.datos.CategoriaDatos;
import una.eif206.reservas.datos.ReservaDatos;
import una.eif206.reservas.datos.RecursoDatos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EstadisticasLogicaIT {
    @TempDir
    Path tempDir;

    @Test
    void estadisticasRecursosConDatosPersistidosEnXmlReal() throws ValidacionException {
        String rutaCategorias = tempDir.resolve("categorias.xml").toString();
        String rutaRecursos = tempDir.resolve("recursos.xml").toString();
        String rutaReservas = tempDir.resolve("reservas.xml").toString();

        CategoriaDatos categoriaDatos = new CategoriaDatos(rutaCategorias);
        categoriaDatos.guardar(new CategoriaDTO("CAT-000001", "Sala para 10 personas"));

        RecursoDatos recursoDatos = new RecursoDatos(rutaRecursos);
        recursoDatos.guardar(new RecursoDTO("R1", "CAT-000001", "Sala 1"));

        ReservaDatos reservaDatos = new ReservaDatos(rutaReservas);
        reservaDatos.guardar(new ReservaDTO("RES-1", "Reunion", "2026-08-24", "09:00", "10:00", "111", List.of("R1"), "ACTIVA"));
        reservaDatos.guardar(new ReservaDTO("RES-2", "Otra reunion", "2026-08-25", "09:00", "10:00", "111", List.of("R1"), "ACTIVA"));

        EstadisticasLogica logica=new EstadisticasLogica(reservaDatos,recursoDatos,categoriaDatos);
        List<EstadisticaCategoriaDTO> resultado=logica.estadisticasRecursos("2026-08-01", "2026-08-31");
        assertEquals(1, resultado.size());
        assertEquals("Sala para 10 personas", resultado.get(0).getDescripcionCategoria());
        assertEquals(2, resultado.get(0).getCantidad());

    }
}
