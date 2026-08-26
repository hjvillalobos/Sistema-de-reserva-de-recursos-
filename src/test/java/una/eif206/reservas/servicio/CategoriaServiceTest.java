// src/test/java/una/eif206/reservas/servicio/CategoriaServiceTest.java
package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.Categoria;
import una.eif206.reservas.persistencia.CategoriaDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaServiceTest {

    private List<Categoria> categoriasDePrueba;
    private CategoriaDAO categoriaDAO;
    private CategoriaService categoriaService;

    @BeforeEach
    void setUp() {
        categoriasDePrueba = new ArrayList<>();
        categoriasDePrueba.add(new Categoria("CAT-000001", "Sala para 10 personas"));
        categoriasDePrueba.add(new Categoria("CAT-000002", "Laptop windows 11"));

        categoriaDAO = new CategoriaDAO() {
            @Override
            public List<Categoria> obtenerTodos() { return categoriasDePrueba; }

            @Override
            public List<Categoria> buscarPorDescripcion(String texto) {
                List<Categoria> resultado = new ArrayList<>();
                String textoBusqueda = (texto == null) ? "" : texto.toLowerCase();
                for (Categoria c : categoriasDePrueba) {
                    if (c.getDescripcion().toLowerCase().contains(textoBusqueda)) {
                        resultado.add(c);
                    }
                }
                return resultado;
            }

            @Override
            public Categoria buscarPorId(String id) {
                for (Categoria c : categoriasDePrueba) {
                    if (c.getId().equals(id)) return c;
                }
                return null;
            }

            @Override
            public void guardar(Categoria categoria) {
                Categoria existente = buscarPorId(categoria.getId());
                if (existente != null) categoriasDePrueba.remove(existente);
                categoriasDePrueba.add(categoria);
            }

            @Override
            public void eliminar(String id) {
                Categoria existente = buscarPorId(id);
                if (existente != null) categoriasDePrueba.remove(existente);
            }

            @Override
            public String generarSiguienteId() { return "CAT-000003"; }
        };

        categoriaService = new CategoriaService(categoriaDAO);
    }

    @Test
    void crearCategoriaExitoso() throws ValidacionException {
        Categoria creada = categoriaService.crear("Sala de Juntas");
        assertEquals("CAT-000003", creada.getId());
        assertEquals(3, categoriaDAO.obtenerTodos().size());
    }

    @Test
    void crearConDescripcionVaciaLanzaExcepcion() {
        assertThrows(ValidacionException.class, () -> categoriaService.crear("   "));
    }

    @Test
    void modificarCategoriaExistente() throws ValidacionException {
        categoriaService.modificar("CAT-000001", "Sala VIP para 10 personas");
        assertEquals("Sala VIP para 10 personas", categoriaDAO.buscarPorId("CAT-000001").getDescripcion());
    }

    @Test
    void modificarCategoriaInexistenteLanzaExcepcion() {
        assertThrows(ValidacionException.class, () -> categoriaService.modificar("CAT-999999", "algo"));
    }

    @Test
    void eliminarCategoriaExistente() throws ValidacionException {
        categoriaService.eliminar("CAT-000001");
        assertNull(categoriaDAO.buscarPorId("CAT-000001"));
    }

    @Test
    void buscarPorDescripcionFiltraCorrectamente() {
        List<Categoria> resultado = categoriaService.buscarPorDescripcion("laptop");
        assertEquals(1, resultado.size());
        assertEquals("CAT-000002", resultado.get(0).getId());
    }
}