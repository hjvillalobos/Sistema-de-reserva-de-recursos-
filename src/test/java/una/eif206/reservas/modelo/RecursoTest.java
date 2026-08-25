package una.eif206.reservas.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class RecursoTest {
    @Test
    void testCrearRecursoConDatosValidos(){
        Categoria categoria= new Categoria("C1","Sala de 10 personas");
        Recurso recurso=new Recurso("R1","Sala 1 primer piso",categoria);

        assertEquals("R1",recurso.getId());
        assertEquals("Sala 1 primer piso",recurso.getDescripcion());
        assertEquals(categoria,recurso.getCategoria());
    }
    @Test
    void testSettersActualizanValores(){
        Recurso recurso=new Recurso();
        recurso.setId("R2");
        recurso.setDescripcion("Laptop #238715");

        assertEquals("R2",recurso.getId());
        assertEquals("R2",recurso.getDescripcion());
    }
    @Test
    void testToStringNoFallaConCategoriaNull(){
        Recurso recurso=new Recurso("R3","Proyector",null);
        assertDoesNotThrow(recurso::toString);
    }
}
