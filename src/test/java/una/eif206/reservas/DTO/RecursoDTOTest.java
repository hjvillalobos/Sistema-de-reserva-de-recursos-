package una.eif206.reservas.DTO;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class RecursoDTOTest {

    @Test
    void constructorAsignaTodosLosCampos(){
        RecursoDTO r=new RecursoDTO("238715","CAT-000001","Laptop #238715");
        assertEquals("238715",r.getId());
        assertEquals("CAT-000001",r.getCategoriaId());
        assertEquals("Laptop #238715",r.getDescripcion());
    }

    @Test
    void settersActualizanValores(){
        RecursoDTO r=new RecursoDTO();
        r.setId("1");
        r.setCategoriaId("CAT-1");
        r.setDescripcion("Silla");
        assertEquals("1",r.getId());
        assertEquals("CAT-1",r.getCategoriaId());
        assertEquals("Silla",r.getDescripcion());
    }
}
