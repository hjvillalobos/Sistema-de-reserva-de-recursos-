package una.eif206.reservas.DTO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EstadisticaCategoriaDTOTest {
    @Test
    void constructorAsignaTodosLosCampos(){
        EstadisticaCategoriaDTO e=new EstadisticaCategoriaDTO("CAT-000001","Sala para 10 personas", 5L);
        assertEquals("CAT-000001",e.getCategoriaId());
        assertEquals("Sala para 10 personas",e.getDescripcionCategoria());
        assertEquals(5L,e.getCantidad());
    }
}
