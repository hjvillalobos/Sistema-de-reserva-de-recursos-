package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.CategoriaDTO;
import una.eif206.reservas.datos.CategoriaDatos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoriaLogicaTest {
    private List<CategoriaDTO> categoriasDePrueba;
    private CategoriaLogica categoriaLogica;

    @BeforeEach
    void setUp() {
        categoriasDePrueba = new ArrayList<>();
        categoriasDePrueba.add(new CategoriaDTO("CAT-000001", "Sala para 10 personas"));
        categoriasDePrueba.add(new CategoriaDTO("CAT-000002", "Laptop windows 11"));

        CategoriaDatos categoriaDatosFalso = new CategoriaDatos("no-existe.xml") {
            @Override
            public List<CategoriaDTO> obtenerTodos() { return categoriasDePrueba; }

            @Override
            public List<CategoriaDTO> buscarPorDescripcion(String texto) {
                List<CategoriaDTO> resultado = new ArrayList<>();
                String textoBusqueda = (texto == null) ? "" : texto.toLowerCase();
                for (CategoriaDTO c : categoriasDePrueba) {
                    if (c.getDescripcion().toLowerCase().contains(textoBusqueda)) {
                        resultado.add(c);
                    }
                }
                return resultado;
            }

            @Override
            public CategoriaDTO buscarPorId(String id) {
                for (CategoriaDTO c : categoriasDePrueba) {
                    if (c.getId().equals(id)) return c;
                }
                return null;
            }

            @Override
            public void guardar(CategoriaDTO categoria) {
                categoriasDePrueba.removeIf(x->x.getId().equalsIgnoreCase(categoria.getId()));
                categoriasDePrueba.add(categoria);
            }

            @Override
            public void eliminar(String id) {
                categoriasDePrueba.removeIf(x->x.getId().equalsIgnoreCase(id));
            }
        };

        categoriaLogica = new CategoriaLogica(categoriaDatosFalso);
    }

    @Test
    void crearCategoriaExitoso() throws ValidacionException {
        CategoriaDTO creada = categoriaLogica.crear(new CategoriaDTO(null, "Sala de Juntas"));
        assertEquals("CAT-000003", creada.getId());
        assertEquals(3, categoriasDePrueba.size());
    }

    @Test
    void crearConDescripcionVaciaLanzaExcepcion() {
        assertThrows(ValidacionException.class, () -> categoriaLogica.crear(new CategoriaDTO(null," ")));
    }

    @Test
    void modificarCategoriaExistente() throws ValidacionException {
        categoriaLogica.modificar(new CategoriaDTO("CAT-000001", "Sala VIP para 10 personas"));
        CategoriaDTO buscada=categoriasDePrueba.stream().filter(c->c.getId().equals("CAT-000001")).findFirst().orElseThrow();
        assertEquals("Sala VIP para 10 personas", buscada.getDescripcion());
    }

    @Test
    void modificarCategoriaInexistenteLanzaExcepcion() {
        assertThrows(ValidacionException.class, () -> categoriaLogica.modificar(new CategoriaDTO("CAT-999999", "algo")));
    }

    @Test
    void eliminarCategoriaExistente() throws ValidacionException {
        categoriaLogica.eliminar("CAT-000001");
        boolean sigueExistiendo=categoriasDePrueba.stream().anyMatch(c->c.getId().equals("CAT-000001"));
        assertFalse(sigueExistiendo);
    }

    @Test
    void buscarPorDescripcionFiltraCorrectamente() {
        List<CategoriaDTO> resultado = categoriaLogica.buscarPorDescripcion("laptop");
        assertEquals(1, resultado.size());
        assertEquals("CAT-000002", resultado.get(0).getId());
    }
}
