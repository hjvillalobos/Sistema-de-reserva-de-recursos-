package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.Recurso;
import una.eif206.reservas.persistencia.RecursoDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecursoServiceTest {

    private List<Recurso> recursosDePrueba;
    private RecursoDAO recursoDAO;
    private RecursoService recursoService;

    @BeforeEach
    void setUp() {
        recursosDePrueba = new ArrayList<>();
        recursosDePrueba.add(new Recurso("238715", "CAT-000002", "Laptop #238715"));
        recursosDePrueba.add(new Recurso("34343", "CAT-000001", "Sala 1 primer piso"));

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
            public void guardar(Recurso r) {
                Recurso existente = buscarPorId(r.getId());
                if (existente != null) recursosDePrueba.remove(existente);
                recursosDePrueba.add(r);
            }

            @Override
            public void eliminar(String id) {
                Recurso existente = buscarPorId(id);
                if (existente != null) recursosDePrueba.remove(existente);
            }
        };

        recursoService = new RecursoService(recursoDAO);
    }

    @Test
    void crearRecursoExitoso() throws ValidacionException {
        Recurso creado = recursoService.crear("45238", "CAT-000002", "Laptop #45238");
        assertEquals(3, recursoDAO.obtenerTodos().size());
        assertEquals("CAT-000002", creado.getCategoriaId());
    }

    @Test
    void crearConIdDuplicadoLanzaExcepcion() {
        assertThrows(ValidacionException.class,
                () -> recursoService.crear("238715", "CAT-000001", "Otro"));
    }

    @Test
    void crearSinCategoriaLanzaExcepcion() {
        assertThrows(ValidacionException.class,
                () -> recursoService.crear("999", "", "Algo"));
    }

    @Test
    void modificarRecursoExistente() throws ValidacionException {
        recursoService.modificar("238715", "CAT-000001", "Laptop reasignada");
        Recurso actualizado = recursoDAO.buscarPorId("238715");
        assertEquals("CAT-000001", actualizado.getCategoriaId());
        assertEquals("Laptop reasignada", actualizado.getDescripcion());
    }

    @Test
    void eliminarRecursoExistente() throws ValidacionException {
        recursoService.eliminar("238715");
        assertNull(recursoDAO.buscarPorId("238715"));
    }

    @Test
    void buscarPorCategoriaYDescripcionFiltraCorrectamente() {
        List<Recurso> resultado = recursoService.buscar("CAT-000002", "laptop");
        assertEquals(1, resultado.size());
        assertEquals("238715", resultado.get(0).getId());
    }
}