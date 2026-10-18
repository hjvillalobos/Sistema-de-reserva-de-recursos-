package una.eif206.reservas.DTO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class CategoriaDTOTest {
    @Test
    void constructorAsignaTodosLosCampos(){
        CategoriaDTO c=new CategoriaDTO("CAT-000001","Sala para 10 personas");
        assertEquals("CAT-000001", c.getId());
        assertEquals("Sala para 10 personas", c.getDescripcion());
    }

    @Test
    void settersActualizanValores(){
        CategoriaDTO c=new CategoriaDTO();
        c.setId("CAT-1");
        c.setDescripcion("Laptop");

        assertEquals("CAT-1", c.getId());
        assertEquals("Laptop", c.getDescripcion());
    }

    @Test
    void toStringDevuelveLaDescripcion() {
        CategoriaDTO c = new CategoriaDTO("CAT-1", "Sala VIP");
        assertEquals("Sala VIP", c.toString());
    }
}
