package una.eif206.reservas.DTO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CeldaCalendarizacionDTOTest {
    @Test
    void constructorVacioCreaCeldaLibre(){
        CeldaCalendarizacionDTO celda=new CeldaCalendarizacionDTO();
        assertFalse(celda.isOcupada());
        assertNull(celda.getActividad());
        assertNull(celda.getNombreFuncionario());
    }

    @Test
    void constructorConDatosCreaCeldaOcupada(){
        CeldaCalendarizacionDTO celda=new CeldaCalendarizacionDTO("Reunion", "Juan Perez");
        assertTrue(celda.isOcupada());
        assertEquals("Reunion",celda.getActividad());
        assertEquals("Juan Perez",celda.getNombreFuncionario());
    }
}
