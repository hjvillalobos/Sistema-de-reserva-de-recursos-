package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.FuncionarioDTO;
import una.eif206.reservas.datos.FuncionarioDatos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FuncionarioLogicaTest {
    private List<FuncionarioDTO> funcionariosDePrueba;
    private FuncionarioLogica funcionarioLogica;

    @BeforeEach
    void setUp() {
        funcionariosDePrueba = new ArrayList<>();
        funcionariosDePrueba.add(new FuncionarioDTO("111", "111", "Juan Perez", "3323"));
        funcionariosDePrueba.add(new FuncionarioDTO("222", "222", "Maria Perez", "222222"));

        FuncionarioDatos funcionarioDatosFalso = new FuncionarioDatos("no-existe.xml") {
            @Override
            public List<FuncionarioDTO> obtenerTodos() { return funcionariosDePrueba; }

            @Override
            public FuncionarioDTO buscarPorId(String id) {
                for (FuncionarioDTO f : funcionariosDePrueba) {
                    if (f.getId().equals(id)) return f;
                }
                return null;
            }

            @Override
            public List<FuncionarioDTO> buscarPorNombre(String nombre) {
                List<FuncionarioDTO> resultado = new ArrayList<>();
                String nombreB = (nombre == null) ? "" : nombre.toLowerCase();
                for (FuncionarioDTO f : funcionariosDePrueba) {
                    if (f.getNombre().toLowerCase().contains(nombreB)) resultado.add(f);
                }
                return resultado;
            }

            @Override
            public void guardar(FuncionarioDTO f) {
                funcionariosDePrueba.removeIf(x->x.getId().equalsIgnoreCase(f.getId()));
                funcionariosDePrueba.add(f);
            }

            @Override
            public void eliminar(String id) {
                funcionariosDePrueba.removeIf(x->x.getId().equalsIgnoreCase(id));
            }
        };
        funcionarioLogica = new FuncionarioLogica(funcionarioDatosFalso);
    }

    @Test
    void crearFuncionarioConClaveIgualAlId() throws ValidacionException {
        FuncionarioDTO nuevo= new FuncionarioDTO("333",null, "Carlos Ruiz", "8888-1234");
        FuncionarioDTO creado = funcionarioLogica.crear(nuevo);
        assertEquals("333", creado.getClave());
    }

    @Test
    void crearConIdDuplicadoLanzaExcepcion() {
        FuncionarioDTO duplicado= new FuncionarioDTO("111",null, "Otro Nombre", "9999");
        assertThrows(ValidacionException.class, () -> funcionarioLogica.crear(duplicado));
    }

    @Test
    void crearConNombreVacioLanzaExcepcion() {
        FuncionarioDTO invalido= new FuncionarioDTO("444",null, " ", "9999");
        assertThrows(ValidacionException.class, () -> funcionarioLogica.crear(invalido));
    }

    @Test
    void modificarFuncionarioExistente() throws ValidacionException {
        FuncionarioDTO actualizado= new FuncionarioDTO("111",null, "Juan Perez Mora", "9999-0000");
        funcionarioLogica.modificar(actualizado);
        FuncionarioDTO buscado = funcionariosDePrueba.stream().filter(f->f.getId().equals("111")).findFirst().orElseThrow();
        assertEquals("Juan Perez Mora", buscado.getNombre());
        assertEquals("9999-0000", buscado.getTelefono());
    }

    @Test
    void modificarFuncionarioInexistenteLanzaExcepcion() {
        FuncionarioDTO noExiste= new FuncionarioDTO("999",null, "Nombre", "1234");
        assertThrows(ValidacionException.class, () -> funcionarioLogica.modificar(noExiste));
    }

    @Test
    void eliminarFuncionarioExistente() throws ValidacionException {
        funcionarioLogica.eliminar("111");
        boolean sigueExistiendo=funcionariosDePrueba.stream().anyMatch(f->f.getId().equals("111"));
        assertFalse(sigueExistiendo);
    }

    @Test
    void buscarPorNombreFiltraCorrectamente() {
        List<FuncionarioDTO> resultado = funcionarioLogica.buscar("Maria");
        assertEquals(1, resultado.size());
        assertEquals("222", resultado.get(0).getId());
    }

    @Test
    void buscarPorIdFiltraCorrectamente() {
        List<FuncionarioDTO> resultado = funcionarioLogica.buscar("111");
        assertEquals(1, resultado.size());
        assertEquals("111", resultado.get(0).getId());
    }
}
