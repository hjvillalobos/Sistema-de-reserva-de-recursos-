package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.RecursoDTO;
import una.eif206.reservas.DTO.ReservaDTO;
import una.eif206.reservas.datos.RecursoDatos;
import una.eif206.reservas.datos.ReservaDatos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class ReservaLogicaTest {
    private List<ReservaDTO> reservas;
    private List<RecursoDTO> recursos;
    private ReservaLogica reservaLogica;

    private String fechaManana(){
        return LocalDate.now().plusDays(1).toString();
    }
    @BeforeEach
    void setUp() {
        recursos = new ArrayList<>();
        reservas = new ArrayList<>();
        recursos.add(new RecursoDTO("238715", "CAT-000002", "Laptop #238715"));

        RecursoDatos recursoDatosFalso = new RecursoDatos("no-existe.xml") {
            @Override
            public List<RecursoDTO> buscarPorCategoria(String categoriaId) {
                List<RecursoDTO> resultado=new ArrayList<>();
                for (RecursoDTO r : recursos) {
                    if (r.getCategoriaId().equalsIgnoreCase(categoriaId)) {
                        resultado.add(r);
                    }
                }
                return resultado;
            }
        };
        ReservaDatos reservaDatosFalso=new ReservaDatos("no-existe.xml"){
            @Override
            public List<ReservaDTO>obtenerTodos(){return reservas;}

            @Override
            public ReservaDTO buscarPorId(String id){
                for(ReservaDTO reserva:reservas){
                    if(reserva.getId().equalsIgnoreCase(id)){
                        return reserva;
                    }
                }
                return null;
            }

            @Override
            public void guardar(ReservaDTO reserva){
                reservas.removeIf(x->x.getId().equalsIgnoreCase(reserva.getId()));
                reservas.add(reserva);
            }

            @Override
            public String generarSiguienteId(){
                return "RES-"+String.format("%06d",reservas.size()+1);
            }
        };
        reservaLogica=new ReservaLogica(reservaDatosFalso,recursoDatosFalso);
    }
    @Test
    void registrarReservaExitosaAsignaElPrimerRecursoDisponible() throws ValidacionException {
        ReservaDTO reserva = reservaLogica.intentarRegistrar("Sesion de Junta", fechaManana(), "09:00", "11:00", "111", List.of("CAT-000002"));
        assertEquals(1, reserva.getRecursosAsignados().size());
        assertEquals("238715", reserva.getRecursosAsignados().get(0));
        assertEquals("ACTIVA", reserva.getEstado());
    }

    @Test
    void registrarSinDisponibilidadLanzaExcepcion() throws ValidacionException {
        reservaLogica.intentarRegistrar("Reunion 1", fechaManana(), "09:00", "11:00", "111", List.of("CAT-000002"));
        assertThrows(ValidacionException.class, () -> reservaLogica.intentarRegistrar("Reunion 2", fechaManana(), "10:00", "12:00", "222", List.of("CAT-000002")));
    }

    @Test
    void registrarEnHorarioDiferenteNoChocaConReservaExistente() throws ValidacionException {
        reservaLogica.intentarRegistrar("Reunion 1", fechaManana(), "09:00", "10:00", "111", List.of("CAT-000002"));
        ReservaDTO segunda = reservaLogica.intentarRegistrar("Reunion 2", fechaManana(), "10:00", "11:00", "222", List.of("CAT-000002"));
        assertEquals("238715", segunda.getRecursosAsignados().get(0));
    }

    @Test
    void cancelarLiberaElRecursoParaOtraReserva() throws ValidacionException {
        ReservaDTO primera = reservaLogica.intentarRegistrar("Reunion 1", fechaManana(), "09:00", "11:00", "111", List.of("CAT-000002"));
        reservaLogica.cancelar(primera.getId(), "111");
        ReservaDTO segunda = reservaLogica.intentarRegistrar("Reunion 2", fechaManana(), "09:00", "11:00", "222", List.of("CAT-000002"));
        assertEquals("238715", segunda.getRecursosAsignados().get(0));
    }

    @Test
    void cancelarConFuncionarioDiferenteLanzaExcepcion() throws ValidacionException {
        ReservaDTO reserva = reservaLogica.intentarRegistrar("Reunion 1", fechaManana(), "09:00", "11:00", "111", List.of("CAT-000002"));
        assertThrows(ValidacionException.class, () -> reservaLogica.cancelar(reserva.getId(), "999"));
    }
}
