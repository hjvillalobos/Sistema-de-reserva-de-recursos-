// src/test/java/una/eif206/reservas/servicio/FuncionarioServiceTest.java
package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.Funcionario;
import una.eif206.reservas.persistencia.FuncionarioDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioServiceTest {

    private List<Funcionario> funcionariosDePrueba;
    private FuncionarioDAO funcionarioDAO;
    private FuncionarioService funcionarioService;

    @BeforeEach
    void setUp() {
        funcionariosDePrueba = new ArrayList<>();
        funcionariosDePrueba.add(new Funcionario("111", "111", "Juan Perez", "3323"));
        funcionariosDePrueba.add(new Funcionario("222", "222", "Maria Perez", "222222"));

        funcionarioDAO = new FuncionarioDAO() {
            @Override
            public List<Funcionario> obtenerTodos() { return funcionariosDePrueba; }

            @Override
            public Funcionario buscarPorId(String id) {
                for (Funcionario f : funcionariosDePrueba) {
                    if (f.getId().equals(id)) return f;
                }
                return null;
            }

            @Override
            public List<Funcionario> buscar(String id, String nombre) {
                List<Funcionario> resultado = new ArrayList<>();
                String idB = (id == null) ? "" : id.toLowerCase();
                String nombreB = (nombre == null) ? "" : nombre.toLowerCase();
                for (Funcionario f : funcionariosDePrueba) {
                    boolean coincideId = idB.isEmpty() || f.getId().toLowerCase().contains(idB);
                    boolean coincideNombre = nombreB.isEmpty() || f.getNombre().toLowerCase().contains(nombreB);
                    if (coincideId && coincideNombre) resultado.add(f);
                }
                return resultado;
            }

            @Override
            public void guardar(Funcionario f) {
                Funcionario existente = buscarPorId(f.getId());
                if (existente != null) funcionariosDePrueba.remove(existente);
                funcionariosDePrueba.add(f);
            }

            @Override
            public void eliminar(String id) {
                Funcionario existente = buscarPorId(id);
                if (existente != null) funcionariosDePrueba.remove(existente);
            }
        };

        funcionarioService = new FuncionarioService(funcionarioDAO);
    }

    @Test
    void crearFuncionarioConClaveIgualAlId() throws ValidacionException {
        Funcionario creado = funcionarioService.crear("333", "Carlos Ruiz", "8888-1234");
        assertEquals("333", creado.getClave());
    }

    @Test
    void crearConIdDuplicadoLanzaExcepcion() {
        assertThrows(ValidacionException.class,
                () -> funcionarioService.crear("111", "Otro Nombre", "9999"));
    }

    @Test
    void crearConNombreVacioLanzaExcepcion() {
        assertThrows(ValidacionException.class,
                () -> funcionarioService.crear("444", "  ", "9999"));
    }

    @Test
    void modificarFuncionarioExistente() throws ValidacionException {
        funcionarioService.modificar("111", "Juan Perez Mora", "9999-0000");
        Funcionario actualizado = funcionarioDAO.buscarPorId("111");
        assertEquals("Juan Perez Mora", actualizado.getNombre());
        assertEquals("9999-0000", actualizado.getTelefono());
    }

    @Test
    void modificarFuncionarioInexistenteLanzaExcepcion() {
        assertThrows(ValidacionException.class,
                () -> funcionarioService.modificar("999", "Nombre", "1234"));
    }

    @Test
    void eliminarFuncionarioExistente() throws ValidacionException {
        funcionarioService.eliminar("111");
        assertNull(funcionarioDAO.buscarPorId("111"));
    }

    @Test
    void buscarPorNombreFiltraCorrectamente() {
        List<Funcionario> resultado = funcionarioService.buscar("", "Maria");
        assertEquals(1, resultado.size());
        assertEquals("222", resultado.get(0).getId());
    }

    @Test
    void buscarPorIdFiltraCorrectamente() {
        List<Funcionario> resultado = funcionarioService.buscar("11", "");
        assertEquals(1, resultado.size());
        assertEquals("111", resultado.get(0).getId());
    }
}
