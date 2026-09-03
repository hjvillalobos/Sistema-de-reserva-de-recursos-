package una.eif206.reservas.DTO;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MatrizCalendarizacionDTOTest {
    @Test
    void obtenerCeldaDevuelveLaCeldaEnLaPosicionCorrecta(){
        List<String> horas=List.of("09:00","10:00");
        List<RecursoDTO> recursos=List.of(new RecursoDTO("R1","Cat-1","Sala 1"));
        CeldaCalendarizacionDTO celdaOcupada=new CeldaCalendarizacionDTO("Reunion","Juan");
        CeldaCalendarizacionDTO celdaLibre=new CeldaCalendarizacionDTO();
        List<List<CeldaCalendarizacionDTO>> celdas=List.of(List.of(celdaOcupada),List.of(celdaLibre));
        MatrizCalendarizacionDTO matriz=new MatrizCalendarizacionDTO(horas,recursos,celdas);

        assertEquals(2,matriz.getHoras().size());
        assertEquals(1,matriz.getRecursos().size());
        assertTrue(matriz.obtenerCelda(0,0).isOcupada());
        assertFalse(matriz.obtenerCelda(1,0).isOcupada());


    }


}
