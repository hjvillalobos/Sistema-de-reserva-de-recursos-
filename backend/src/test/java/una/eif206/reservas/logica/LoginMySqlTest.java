package una.eif206.reservas.logica;

import org.junit.jupiter.api.Test;
import una.eif206.reservas.DTO.RolDTO;
import una.eif206.reservas.datos.UsuariosAcceso;
import una.eif206.reservas.excepciones.ValidacionException;
import una.eif206.reservas.seguridad.Claves;
import una.eif206.reservas.servicios.LoginService;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class LoginMySqlTest {
    static class UsuariosMemoria implements UsuariosAcceso {
        final Map<String, Credencial> datos = new HashMap<>();
        boolean conflicto;
        boolean desconectado;
        public Credencial buscar(String id) throws SQLException {
            if (desconectado) throw new SQLException("Información interna de la conexión");
            return datos.get(id);
        }
        public boolean cambiarHash(String id, String anterior, String nuevo) {
            var usuario = datos.get(id);
            if (conflicto || usuario == null || !usuario.hash().equals(anterior)) return false;
            datos.put(id, new Credencial(id, nuevo, usuario.rol()));
            return true;
        }
    }

    @Test
    void cambiaSoloLaCuentaAutenticadaYRechazaClaveAnterior() throws Exception {
        var datos = new UsuariosMemoria();
        String original = Claves.proteger("Clave original ñ");
        datos.datos.put("admin", new UsuariosAcceso.Credencial("admin", original, RolDTO.ADMINISTRADOR));
        datos.datos.put("otro", new UsuariosAcceso.Credencial("otro", original, RolDTO.FUNCIONARIO));
        var logica = new LoginLogica(datos);
        var servicio = new LoginService(logica);
        assertThrows(ValidacionException.class, () -> servicio.cambiarClave("Clave original ñ", "nueva"));
        assertEquals(RolDTO.ADMINISTRADOR, servicio.autenticarSesion("admin", "Clave original ñ").getRol());
        assertNull(logica.autenticar("admin", "Clave original ñ").getClave());
        assertThrows(ValidacionException.class, () -> servicio.cambiarClave("incorrecta", "nueva"));
        assertThrows(ValidacionException.class, () -> servicio.cambiarClave("Clave original ñ", " "));
        assertEquals(original, datos.datos.get("admin").hash());
        servicio.cambiarClave("Clave original ñ", "Clave nueva ñ");
        assertEquals(original, datos.datos.get("otro").hash());
        assertThrows(ValidacionException.class, () -> servicio.autenticarSesion("admin", "Clave original ñ"));
        assertThrows(ValidacionException.class, () -> servicio.cambiarClave("Clave nueva ñ", "otra"));
        assertEquals("admin", servicio.autenticarSesion("admin", "Clave nueva ñ").getId());
        servicio.cerrarSesion();
        assertThrows(ValidacionException.class, () -> servicio.cambiarClave("Clave nueva ñ", "otra"));
    }

    @Test
    void noSobrescribeCambiosConcurrentesNiOcultaFallosComoClavesIncorrectas() throws Exception {
        var datos = new UsuariosMemoria();
        String original = Claves.proteger("original");
        datos.datos.put("funcionario", new UsuariosAcceso.Credencial("funcionario", original, RolDTO.FUNCIONARIO));
        var logica = new LoginLogica(datos);
        assertEquals(RolDTO.FUNCIONARIO, logica.autenticarSesion("funcionario", "original").getRol());
        datos.conflicto = true;
        assertThrows(ValidacionException.class, () -> logica.cambiarClavePorId("funcionario", "original", "nueva"));
        assertEquals(original, datos.datos.get("funcionario").hash());
        datos.desconectado = true;
        var error = assertThrows(ValidacionException.class, () -> logica.autenticarSesion("funcionario", "original"));
        assertTrue(error.getMessage().contains("MySQL"));
        assertFalse(error.getMessage().contains("Información interna"));
    }

    @Test
    void rechazaUsuarioAusenteYHashInvalidoSinAceptarTextoPlano() {
        var datos = new UsuariosMemoria();
        datos.datos.put("admin", new UsuariosAcceso.Credencial("admin", "textoPlano", RolDTO.ADMINISTRADOR));
        var logica = new LoginLogica(datos);
        assertThrows(ValidacionException.class, () -> logica.autenticarSesion("admin", "textoPlano"));
        assertThrows(ValidacionException.class, () -> logica.autenticarSesion("ausente", "algo"));
        assertThrows(ValidacionException.class, () -> logica.autenticarSesion(null, "algo"));
    }
}
