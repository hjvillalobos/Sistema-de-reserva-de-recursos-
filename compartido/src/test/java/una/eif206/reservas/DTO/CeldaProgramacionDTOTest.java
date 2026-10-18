package una.eif206.reservas.DTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class CeldaProgramacionDTOTest {
    @Test
    void celdaNuevaEmpiezaLibre(){
        CeldaProgramacionDTO celda=new CeldaProgramacionDTO();
        assertFalse(celda.isOcupada());
        assertTrue(celda.getActividades().isEmpty());
    }
    @Test
    void agregarUnaActividadLaMarcaOcupada(){
        CeldaProgramacionDTO celda=new CeldaProgramacionDTO();
        celda.agregar("Charla tecnica","Maria Hernandez");
        assertTrue(celda.isOcupada());
        assertEquals(1,celda.getActividades().size());
        assertEquals("Charla tecnica / Maria Hernandez",celda.getActividades().get(0));

    }
    @Test
    void agregarVariasActividadesALaMismaCelda(){
        CeldaProgramacionDTO celda=new CeldaProgramacionDTO();
        celda.agregar("Reunion 1","Maria Hernandez");
        celda.agregar("Reunion 2","Juan Sanchez");
        assertEquals(2,celda.getActividades().size());

    }
}
