package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.EstadoReserva;
import una.eif206.reservas.modelo.Recurso;
import una.eif206.reservas.modelo.Reserva;
import una.eif206.reservas.persistencia.RecursoDAO;
import una.eif206.reservas.persistencia.ReservaDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservaServiceTest {

    private List<Reserva> reservasDePrueba;
    private List<Recurso> recursosDePrueba;
    private ReservaDAO reservaDAO;
    private RecursoDAO recursoDAO;
    private ReservaService reservaService;

    @BeforeEach
    void setUp() {
        reservasDePrueba = new ArrayList<>();
        recursosDePrueba = new ArrayList<>();
        recursosDePrueba.add(new Recurso("238715", "CAT-000002", "Laptop #238715"));
        recursosDePrueba.add(new Recurso("34343", "CAT-000001", "Sala 1 primer piso"));

        reservaDAO = new ReservaDAO() {
            @Override
            public List<Reserva> obtenerTodos() { return reservasDePrueba; }

            @Override
            public Reserva buscarPorId(String id) {
                for (Reserva r : reservasDePrueba) {
                    if (r.getId().equals(id)) return r;
                }
                return null;
            }

            @Override
            public List<Reserva> buscarPorFuncionario(String funcionarioId) {
                List<Reserva> resultado = new ArrayList<>();
                for (Reserva r : reservasDePrueba) {
                    if (r.getFuncionarioId().equals(funcionarioId)) resultado.add(r);
                }
                return resultado;
            }

            @Override
            public void guardar(Reserva reserva) {
                Reserva existente = buscarPorId(reserva.getId());
                if (existente != null) reservasDePrueba.remove(existente);
                reservasDePrueba.add(reserva);
            }

            @Override
            public String generarSiguienteId() {
                return "RES-" + String.format("%06d", reservasDePrueba.size() + 1);
            }
        };

        recursoDAO = new RecursoDAO() {
            @Override
            public List<Recurso> obtenerTodos() { return recursosDePrueba; }

            @Override
            public List<Recurso> buscarPorCategoria(String categoriaId) {
                List<Recurso> resultado = new ArrayList<>();
                for (Recurso r : recursosDePrueba) {
                    if (r.getCategoriaId().equals(categoriaId)) resultado.add(r);
                }
                return resultado;
            }

            @Override
            public List<Recurso> buscar(String categoriaId, String descripcion) {
                List<Recurso> resultado = new ArrayList<>();
                String catB = (categoriaId == null) ? "" : categoriaId;
                String descB = (descripcion == null) ? "" : descripcion.toLowerCase();
                for (Recurso r : recursosDePrueba) {
                    boolean coincideCat = catB.isEmpty() || r.getCategoriaId().equals(catB);
                    boolean coincideDesc = descB.isEmpty() || r.getDescripcion().toLowerCase().contains(descB);
                    if (coincideCat && coincideDesc) resultado.add(r);
                }
                return resultado;
            }

            @Override
            public Recurso buscarPorId(String id) {
                for (Recurso r : recursosDePrueba) {
                    if (r.getId().equals(id)) return r;
                }
                return null;
            }

            @Override
            public void guardar(Recurso recurso) { recursosDePrueba.add(recurso); }

            @Override
            public void eliminar(String id) { }
        };

        reservaService = new ReservaService(reservaDAO, recursoDAO);
    }

    private String fechaManana() {
        return LocalDate.now().plusDays(1).toString();
    }

    @Test
    void registrarReservaExitosaAsignaElPrimerRecursoDisponible() throws ValidacionException {
        Reserva reserva = reservaService.intentarRegistrar(
                "Sesion de Junta", fechaManana(), "09:00", "11:00",
                "111", List.of("CAT-000002"));

        assertEquals(1, reserva.getRecursosAsignados().size());
        assertEquals("238715", reserva.getRecursosAsignados().get(0));
        assertEquals(EstadoReserva.ACTIVA, reserva.getEstado());
    }

    @Test
    void registrarSinDisponibilidadLanzaExcepcion() throws ValidacionException {
        // Ocupamos el único recurso de esa categoría en el mismo horario
        reservaService.intentarRegistrar("Reunion 1", fechaManana(), "09:00", "11:00",
                "111", List.of("CAT-000002"));

        // Segunda reserva pidiendo la misma categoría, en horario que se sobrepone
        assertThrows(ValidacionException.class, () ->
                reservaService.intentarRegistrar("Reunion 2", fechaManana(), "10:00", "12:00",
                        "222", List.of("CAT-000002")));
    }

    @Test
    void registrarEnHorarioDiferenteNoChocaConReservaExistente() throws ValidacionException {
        reservaService.intentarRegistrar("Reunion 1", fechaManana(), "09:00", "10:00",
                "111", List.of("CAT-000002"));

        // No se solapa (empieza justo cuando termina la anterior)
        Reserva segunda = reservaService.intentarRegistrar("Reunion 2", fechaManana(), "10:00", "11:00",
                "222", List.of("CAT-000002"));

        assertEquals("238715", segunda.getRecursosAsignados().get(0));
    }

    @Test
    void cancelarLiberaElRecursoParaOtraReserva() throws ValidacionException {
        Reserva primera = reservaService.intentarRegistrar("Reunion 1", fechaManana(), "09:00", "11:00",
                "111", List.of("CAT-000002"));

        reservaService.cancelar(primera.getId(), "111");

        // Ahora otra reserva en el mismo horario sí debería poder tomar el recurso
        Reserva segunda = reservaService.intentarRegistrar("Reunion 2", fechaManana(), "09:00", "11:00",
                "222", List.of("CAT-000002"));

        assertEquals("238715", segunda.getRecursosAsignados().get(0));
    }

    @Test
    void cancelarConFuncionarioDiferenteLanzaExcepcion() throws ValidacionException {
        Reserva reserva = reservaService.intentarRegistrar("Reunion 1", fechaManana(), "09:00", "11:00",
                "111", List.of("CAT-000002"));

        assertThrows(ValidacionException.class, () -> reservaService.cancelar(reserva.getId(), "999"));
    }
}