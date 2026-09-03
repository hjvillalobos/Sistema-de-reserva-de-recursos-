package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.FuncionarioDTO;
import una.eif206.reservas.DTO.RolDTO;
import una.eif206.reservas.DTO.UsuarioDTO;
import una.eif206.reservas.datos.AdministradorDatos;
import una.eif206.reservas.datos.FuncionarioDatos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LoginLogicaTest {
    private LoginLogica loginLogica;
    private List<UsuarioDTO> administradoresDePrueba;
    private List<FuncionarioDTO> funcionariosDePrueba;

    @BeforeEach
    void setUp() {
        administradoresDePrueba = new ArrayList<>();
        administradoresDePrueba.add(new UsuarioDTO("admin", "admin123", RolDTO.ADMINISTRADOR));

        funcionariosDePrueba = new ArrayList<>();
        funcionariosDePrueba.add(new FuncionarioDTO("111", "clave111", "Juan Perez", "8888-0000"));

        AdministradorDatos administradorDatosFalso=new AdministradorDatos("no-existe.xml") {
            @Override
            public UsuarioDTO buscarPorId(String id) {
                for (UsuarioDTO u : administradoresDePrueba) {
                    if (u.getId().equalsIgnoreCase(id)) {
                        return u;
                    }
                }
                return null;
            }

            @Override
            public void guardar(UsuarioDTO usuario) {
                administradoresDePrueba.removeIf(x->x.getId().equalsIgnoreCase(usuario.getId()));
                administradoresDePrueba.add(usuario);
            }
        };

        FuncionarioDatos funcionarioDatosFalso=new FuncionarioDatos("no-existe.xml") {
            @Override
            public FuncionarioDTO buscarPorId(String id) {
                for (FuncionarioDTO f : funcionariosDePrueba) {
                    if (f.getId().equals(id)) {
                        return f;
                    }
                }
                return null;
            }

            @Override
            public void guardar(FuncionarioDTO f) {
                funcionariosDePrueba.removeIf(x->x.getId().equalsIgnoreCase(f.getId()));
                funcionariosDePrueba.add(f);
            }
        };

        loginLogica = new LoginLogica(administradorDatosFalso, funcionarioDatosFalso);
    }

    @Test
    void autenticarAdminExitoso() throws ValidacionException {
        UsuarioDTO u = loginLogica.autenticar("admin", "admin123");
        assertEquals(RolDTO.ADMINISTRADOR, u.getRol());
    }

    @Test
    void autenticarFuncionarioExitoso() throws ValidacionException {
        UsuarioDTO u = loginLogica.autenticar("111", "clave111");
        assertEquals(RolDTO.FUNCIONARIO, u.getRol());
    }

    @Test
    void autenticarConClaveIncorrectaLanzaExcepcion() {
        assertThrows(ValidacionException.class, () -> loginLogica.autenticar("admin", "incorrecta"));
    }

    @Test
    void autenticarConIdVacioLanzaExcepcion() {
        assertThrows(ValidacionException.class, () -> loginLogica.autenticar("", "algo"));
    }

    @Test
    void cambiarClaveExitoso() throws ValidacionException {
        UsuarioDTO u = loginLogica.autenticar("admin", "admin123");
        loginLogica.cambiarClave(u, "admin123", "nuevaClave");
        UsuarioDTO recargado=administradoresDePrueba.stream().filter(a->a.getId().equals("admin")).findFirst().orElseThrow();
        assertEquals("nuevaClave",recargado.getClave());
    }

    @Test
    void cambiarClaveConClaveActualIncorrectaLanzaExcepcion() throws ValidacionException {
        UsuarioDTO u = loginLogica.autenticar("admin", "admin123");
        assertThrows(ValidacionException.class, () -> loginLogica.cambiarClave(u, "incorrecta", "nueva"));
    }
}
