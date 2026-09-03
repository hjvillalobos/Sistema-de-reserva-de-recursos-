package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.RecursoDTO;
import una.eif206.reservas.datos.RecursoDatos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RecursoLogicaTest {
    private List<RecursoDTO> recursos;
    private RecursoLogica recursoLogica;

    @BeforeEach
    void setUp() {
        recursos = new ArrayList<>();
        recursos.add(new RecursoDTO("238715", "CAT-000002", "Laptop #238715"));
        recursos.add(new RecursoDTO("34343", "CAT-000001", "Sala 1 primer piso"));

        RecursoDatos recursoDatosFalso= new RecursoDatos("no-existe.xml") {
            @Override
            public List<RecursoDTO> obtenerTodos() { return recursos; }

            @Override
            public List<RecursoDTO> buscarPorCategoria(String categoriaId) {
                List<RecursoDTO> resultado = new ArrayList<>();
                for (RecursoDTO r : recursos) {
                    if (r.getCategoriaId().equals(categoriaId)) resultado.add(r);
                }
                return resultado;
            }
            @Override
            public RecursoDTO buscarPorId(String id) {
                for (RecursoDTO r : recursos) {
                    if (r.getId().equals(id)) return r;
                }
                return null;
            }

            @Override
            public void guardar(RecursoDTO r) {
                recursos.removeIf(x->x.getId().equalsIgnoreCase(r.getId()));
                recursos.add(r);
            }
            @Override
            public void eliminar(String id) {
                recursos.removeIf(x->x.getId().equalsIgnoreCase(id));
            }
        };

        recursoLogica = new RecursoLogica(recursoDatosFalso);
    }

    @Test
    void crearRecursoExitoso() throws ValidacionException {
        RecursoDTO nuevo = new RecursoDTO("45238", "CAT-000002", "Laptop #45238");
        recursoLogica.crear(nuevo);
        assertEquals(3, recursos.size());
    }
    @Test
    void crearConIdDuplicadoLanzaExcepcion() {
        RecursoDTO duplicado= new RecursoDTO("238715","CAT-000001","Otro");
        assertThrows(ValidacionException.class, () -> recursoLogica.crear(duplicado));
    }
    @Test
    void crearSinCategoriaLanzaExcepcion() {
        RecursoDTO invalido= new RecursoDTO("999","","Algo");
        assertThrows(ValidacionException.class, () -> recursoLogica.crear(invalido));
    }
    @Test
    void crearConDescripcionVaciaLanzaExcepcion() {
        RecursoDTO invalido= new RecursoDTO("999","CAT-000001","");
        assertThrows(ValidacionException.class, () -> recursoLogica.crear(invalido));
    }
    @Test
    void modificarRecursoExistente() throws ValidacionException {
        RecursoDTO actualizado= new RecursoDTO("238715", "CAT-000001", "Laptop reasignada");
        recursoLogica.modificar(actualizado);
        RecursoDTO buscado=recursos.stream().filter(r->r.getId().equals("238715")).findFirst().orElseThrow();
        assertEquals("CAT-000001", actualizado.getCategoriaId());
        assertEquals("Laptop reasignada", actualizado.getDescripcion());
    }

    @Test
    void eliminarRecursoExistente() throws ValidacionException {
        recursoLogica.eliminar("238715");
        boolean sigueExistiendo=recursos.stream().anyMatch(r->r.getId().equals("238715"));
        assertFalse(sigueExistiendo);
    }

    @Test
    void buscarPorCategoriaFiltraCorrectamente() {
        List<RecursoDTO> resultado = recursoLogica.buscarPorCategoria("CAT-000002");
        assertEquals(1, resultado.size());
        assertEquals("238715", resultado.get(0).getId());
    }
}
