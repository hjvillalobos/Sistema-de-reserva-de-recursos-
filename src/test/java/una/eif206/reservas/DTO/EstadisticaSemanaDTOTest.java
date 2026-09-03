package una.eif206.reservas.DTO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EstadisticaSemanaDTOTest {
    @Test
    void constructorAsignaTodosLosCampos(){
        EstadisticaSemanaDTO e=new EstadisticaSemanaDTO("2026-08-24","2026-08-30", 3L);
        assertEquals("2026-08-24",e.getInicioSemana());
        assertEquals("2026-08-30",e.getFinSemana());
        assertEquals(3L,e.getCantidad());
    }
    @Test
    void getEtiquetaCombinaInicioYFin(){
        EstadisticaSemanaDTO e=new EstadisticaSemanaDTO("2026-08-24","2026-08-30", 3L);
        assertEquals("2026-08-24 a 2026-08-30",e.getEtiqueta());
    }
}
