// src/test/java/una/eif206/reservas/servicio/LoginServiceTest.java
package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.Funcionario;
import una.eif206.reservas.modelo.Rol;
import una.eif206.reservas.modelo.Usuario;
import una.eif206.reservas.persistencia.AdministradorDAO;
import una.eif206.reservas.persistencia.FuncionarioDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTest {

    private AdministradorDAO administradorDAO;
    private FuncionarioDAO funcionarioDAO;
    private LoginService loginService;

    private List<Usuario> administradoresDePrueba;
    private List<Funcionario> funcionariosDePrueba;

    @BeforeEach
    void setUp() {
        administradoresDePrueba = new ArrayList<>();
        administradoresDePrueba.add(new Usuario("admin", "admin123", Rol.ADMINISTRADOR));

        funcionariosDePrueba = new ArrayList<>();
        funcionariosDePrueba.add(new Funcionario("111", "clave111", "Juan Perez", "8888-0000"));

        administradorDAO = new AdministradorDAO() {
            @Override
            public List<Usuario> obtenerTodos() {
                return administradoresDePrueba;
            }

            @Override
            public Usuario buscarPorId(String id) {
                for (Usuario u : administradoresDePrueba) {
                    if (u.getId().equals(id)) {
                        return u;
                    }
                }
                return null;
            }

            @Override
            public void actualizarClave(String id, String nuevaClave) {
                Usuario u = buscarPorId(id);
                if (u != null) {
                    u.setClave(nuevaClave);
                }
            }
        };

        funcionarioDAO = new FuncionarioDAO() {

            @Override
            public List<Funcionario> obtenerTodos() {
                return funcionariosDePrueba;
            }

            @Override
            public Funcionario buscarPorId(String id) {
                for (Funcionario f : funcionariosDePrueba) {
                    if (f.getId().equals(id)) {
                        return f;
                    }
                }
                return null;
            }

            @Override
            public List<Funcionario> buscar(String id, String nombre) {
                return List.of();
            }

            @Override
            public void guardar(Funcionario f) {
                Funcionario existente = buscarPorId(f.getId());

                if (existente != null) {
                    funcionariosDePrueba.remove(existente);
                }

                funcionariosDePrueba.add(f);
            }

            @Override
            public void eliminar(String id) {
                Funcionario existente = buscarPorId(id);

                if (existente != null) {
                    funcionariosDePrueba.remove(existente);
                }
            }
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
        assertEquals("nuevaClave", administradorDAO.buscarPorId("admin").getClave());
    }

    @Test
    void cambiarClaveConClaveActualIncorrectaLanzaExcepcion() throws CredencialesInvalidasException {
        Usuario u = loginService.autenticar("admin", "admin123");
        assertThrows(CredencialesInvalidasException.class,
                () -> loginService.cambiarClave(u, "incorrecta", "nueva"));
    }
}