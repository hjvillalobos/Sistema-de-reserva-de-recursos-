// src/test/java/una/eif206/reservas/servicio/LoginServiceTest.java
package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.Funcionario;
import una.eif206.reservas.modelo.Rol;
import una.eif206.reservas.modelo.Usuario;
import una.eif206.reservas.persistencia.AdministradorDAO;
import una.eif206.reservas.persistencia.FuncionarioDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTest {

    private AdministradorDAO administradorDAO;
    private FuncionarioDAO funcionarioDAO;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        Usuario admin = new Usuario("admin", "admin123", Rol.ADMINISTRADOR);
        Funcionario funcionario = new Funcionario("111", "clave111", "Juan Perez", "8888-0000");

        administradorDAO = new AdministradorDAO() {
            private final List<Usuario> datos = new ArrayList<>(List.of(admin));
            public List<Usuario> obtenerTodos() { return datos; }
            public Optional<Usuario> buscarPorId(String id) {
                return datos.stream().filter(u -> u.getId().equals(id)).findFirst();
            }
            public void actualizarClave(String id, String nuevaClave) {
                buscarPorId(id).ifPresent(u -> u.setClave(nuevaClave));
            }
        };

        funcionarioDAO = new FuncionarioDAO() {
            private final List<Funcionario> datos = new ArrayList<>(List.of(funcionario));
            public List<Funcionario> obtenerTodos() { return datos; }
            public Optional<Funcionario> buscarPorId(String id) {
                return datos.stream().filter(f -> f.getId().equals(id)).findFirst();
            }
            public void guardar(Funcionario f) {
                datos.removeIf(x -> x.getId().equals(f.getId()));
                datos.add(f);
            }
            public void eliminar(String id) { datos.removeIf(f -> f.getId().equals(id)); }
        };

        loginService = new LoginService(administradorDAO, funcionarioDAO);
    }

    @Test
    void autenticarAdminExitoso() throws CredencialesInvalidasException {
        Usuario u = loginService.autenticar("admin", "admin123");
        assertEquals(Rol.ADMINISTRADOR, u.getRol());
    }

    @Test
    void autenticarFuncionarioExitoso() throws CredencialesInvalidasException {
        Usuario u = loginService.autenticar("111", "clave111");
        assertEquals(Rol.FUNCIONARIO, u.getRol());
    }

    @Test
    void autenticarConClaveIncorrectaLanzaExcepcion() {
        assertThrows(CredencialesInvalidasException.class,
                () -> loginService.autenticar("admin", "incorrecta"));
    }

    @Test
    void autenticarConIdVacioLanzaExcepcion() {
        assertThrows(CredencialesInvalidasException.class,
                () -> loginService.autenticar("", "algo"));
    }

    @Test
    void cambiarClaveExitoso() throws CredencialesInvalidasException {
        Usuario u = loginService.autenticar("admin", "admin123");
        loginService.cambiarClave(u, "admin123", "nuevaClave");
        assertEquals("nuevaClave", administradorDAO.buscarPorId("admin").get().getClave());
    }

    @Test
    void cambiarClaveConClaveActualIncorrectaLanzaExcepcion() throws CredencialesInvalidasException {
        Usuario u = loginService.autenticar("admin", "admin123");
        assertThrows(CredencialesInvalidasException.class,
                () -> loginService.cambiarClave(u, "incorrecta", "nueva"));
    }
}