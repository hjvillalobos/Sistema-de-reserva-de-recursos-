package una.eif206.reservas.DTO;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ReservaDTOTest {
    @Test
    void constructorAsignaTodosLosCampos(){
        ReservaDTO r=new ReservaDTO("RES-1","Reunion","2026-08-26","09:00","11:00","111",List.of("238715"),"ACTIVA");
        assertEquals("RES-1", r.getId());
        assertEquals("Reunion", r.getActividad());
        assertEquals("2026-08-26", r.getFecha());
        assertEquals("09:00", r.getHoraInicio());
        assertEquals("11:00", r.getHoraFin());
        assertEquals("111", r.getFuncionarioId());
        assertEquals(1, r.getRecursosAsignados().size());
        assertEquals("ACTIVA", r.getEstado());
    }

    @Test
    void settersActualizanValores(){
        ReservaDTO r=new ReservaDTO();
        r.setId("RES-1");
        r.setActividad("Reunion");
        r.setFecha("2026-08-26");
        r.setHoraInicio("09:00");
        r.setHoraFin("11:00");
        r.setFuncionarioId("111");
        r.setRecursosAsignados(List.of("238715"));
        r.setEstado("ACTIVA");

        assertEquals("RES-1", r.getId());
        assertEquals("Reunion", r.getActividad());
        assertEquals("2026-08-26", r.getFecha());
        assertEquals("09:00", r.getHoraInicio());
        assertEquals("11:00", r.getHoraFin());
        assertEquals("111", r.getFuncionarioId());
        assertEquals(1, r.getRecursosAsignados().size());
        assertEquals("ACTIVA", r.getEstado());
    }
}
