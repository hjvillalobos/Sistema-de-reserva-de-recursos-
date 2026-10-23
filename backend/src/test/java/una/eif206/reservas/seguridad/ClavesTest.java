package una.eif206.reservas.seguridad;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClavesTest {
    @Test
    void usaSalAleatoriaYVerificaExactamenteLaClave() {
        String primero = Claves.proteger("Clave con ñ y espacios");
        String segundo = Claves.proteger("Clave con ñ y espacios");
        assertNotEquals(primero, segundo);
        assertTrue(primero.length() <= 255);
        assertTrue(Claves.verificar("Clave con ñ y espacios", primero));
        assertFalse(Claves.verificar("clave con ñ y espacios", primero));
        assertFalse(Claves.verificar("Clave con ñ y espacios ", primero));
    }

    @Test
    void rechazaHashesMalformadosYClavesVacias() {
        assertFalse(Claves.verificar("abc", "abc"));
        assertFalse(Claves.verificar("abc", "pbkdf2-sha256$600000$!$!"));
        assertFalse(Claves.verificar("abc", "pbkdf2-sha256$999999999$abc$abc"));
        assertFalse(Claves.verificar(null, null));
        assertThrows(IllegalArgumentException.class, () -> Claves.proteger(" "));
        assertThrows(IllegalArgumentException.class, () -> Claves.proteger(null));
    }
}
