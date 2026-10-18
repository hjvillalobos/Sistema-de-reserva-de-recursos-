package una.eif206.reservas.DTO;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MatrizProgramacionDTOTest {
    @Test
    void obtenerCeldaDevuelveLaCeldaEnLaPosicionCorrecta(){
        List<String> dias=List.of("2026-08-24", "2026-08-25");
        List<String> horas=List.of("09:00", "10:00");

        CeldaProgramacionDTO celdaOcupada=new CeldaProgramacionDTO();
        celdaOcupada.agregar("Reunion","Juan");
        CeldaProgramacionDTO celdaLibre=new CeldaProgramacionDTO();

        List<List<CeldaProgramacionDTO>> celdas=List.of(List.of(celdaOcupada,celdaLibre),List.of(celdaLibre,celdaLibre));

        MatrizProgramacionDTO matriz=new MatrizProgramacionDTO("2026-08-24", "2026-08-30", dias, horas, celdas);

        assertEquals("2026-08-24",matriz.getInicioSemana());
        assertEquals("2026-08-30",matriz.getFinSemana());
        assertEquals(2,matriz.getDias().size());
        assertEquals(2,matriz.getHoras().size());
        assertTrue(matriz.obtenerCelda(0,0).isOcupada());
        assertFalse(matriz.obtenerCelda(0,1).isOcupada());

    }
}
