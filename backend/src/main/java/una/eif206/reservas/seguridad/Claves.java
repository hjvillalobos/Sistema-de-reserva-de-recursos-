package una.eif206.reservas.seguridad;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Formato versionado que cabe en usuarios.clave_hash. Nunca guarda la clave original. */
public final class Claves {
    private static final int ITERACIONES = 600_000;
    private Claves() { }

    public static String proteger(String clave) {
        if (clave == null || clave.isBlank() || clave.length() > 1024)
            throw new IllegalArgumentException("La clave debe tener entre 1 y 1024 caracteres y no estar en blanco.");
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        return "pbkdf2-sha256$" + ITERACIONES + "$" + Base64.getEncoder().encodeToString(sal)
                + "$" + Base64.getEncoder().encodeToString(derivar(clave, sal));
    }

    public static boolean verificar(String clave, String almacenada) {
        if (clave == null || clave.length() > 1024 || almacenada == null) return false;
        try {
            String[] partes = almacenada.split("\\$", -1);
            if (partes.length != 4 || !partes[0].equals("pbkdf2-sha256")
                    || !partes[1].equals(Integer.toString(ITERACIONES))) return false;
            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            return sal.length == 16 && esperado.length == 32
                    && MessageDigest.isEqual(esperado, derivar(clave, sal));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derivar(String clave, byte[] sal) {
        PBEKeySpec especificacion = new PBEKeySpec(clave.toCharArray(), sal, ITERACIONES, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(especificacion).getEncoded();
        } catch (java.security.GeneralSecurityException e) {
            throw new IllegalStateException("No está disponible el algoritmo para proteger claves.", e);
        } finally {
            especificacion.clearPassword();
        }
    }
}
