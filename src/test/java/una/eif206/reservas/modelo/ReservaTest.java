package una.eif206.reservas.modelo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalTime;
import java.time.LocalDate;
class ReservaTest {
    @Test
    void testCrearReservaConDatosValidos(){
        Categoria categoria=new Categoria("C1","Sala de 10 personas");
        Recurso recurso=new Recurso("R1","Sala 1 de primer piso",categoria);
        Funcionario funcionario=new Funcionario("F1", "clave123","Juan Sanchez","1234-5689");
        EstadoReserva estado=EstadoReserva.values()[0];
        Reserva reserva=new Reserva("RES1",recurso,"Reunion de planeacion",estado,funcionario,LocalDate.of(2026,8,25),LocalTime.of(14,0));

        assertEquals("RES1",reserva.getId());
        assertEquals(recurso,reserva.getRecurso());
        assertEquals("Reunion de planeacion",reserva.getActividad());
        assertEquals(estado,reserva.getEstado());
        assertEquals(funcionario,reserva.getFuncionario());
        assertEquals(LocalDate.of(2026,8,25),reserva.getFecha());
        assertEquals(LocalTime.of(14,0),reserva.getHora());
    }
    @Test
    void testSettersActualizanValores(){
        Reserva reserva=new Reserva();
        reserva.setActividad("Capacitacion");
        reserva.setFecha(LocalDate.of(2026,8,25));
        reserva.setHora(LocalTime.of(9,30));
    }
    @Test
    void testToStringNoFallaConRercursoNull(){
        Reserva reserva=new Reserva();
        reserva.setActividad("Reunion");
        assertDoesNotThrow(reserva::toString);
    }
}
