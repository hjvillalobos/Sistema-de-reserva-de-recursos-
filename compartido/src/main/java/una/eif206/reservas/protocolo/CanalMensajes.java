package una.eif206.reservas.protocolo;

import java.io.*;
import java.util.Set;

/** Cada mensaje lleva longitud y un objeto independiente: sin caché entre respuestas. */
public final class CanalMensajes {
    private static final int MAX_BYTES = 2 * 1024 * 1024;
    private static final Set<String> TIPOS = Set.of(
            "java.lang.Object", "java.lang.String", "java.lang.Enum",
            "java.util.ArrayList", "java.util.CollSer", "java.util.ImmutableCollections$List12",
            "java.util.ImmutableCollections$ListN", "java.util.Arrays$ArrayList",
            "java.util.Collections$EmptyList", "java.util.Collections$SingletonList");
    private final DataInputStream entrada;
    private final DataOutputStream salida;

    public CanalMensajes(InputStream entrada, OutputStream salida) {
        this.entrada = new DataInputStream(entrada);
        this.salida = new DataOutputStream(salida);
    }

    public synchronized void enviar(Object mensaje) throws IOException {
        var bytes = new ByteArrayOutputStream();
        try (var objetos = new ObjectOutputStream(bytes)) { objetos.writeObject(mensaje); }
        if (bytes.size() > MAX_BYTES) throw new IOException("Mensaje demasiado grande.");
        salida.writeInt(bytes.size());
        bytes.writeTo(salida);
        salida.flush();
    }

    public Object recibir() throws IOException {
        int longitud = entrada.readInt();
        if (longitud <= 0 || longitud > MAX_BYTES) throw new IOException("Longitud de mensaje inválida.");
        byte[] bytes = entrada.readNBytes(longitud);
        if (bytes.length != longitud) throw new EOFException("Mensaje incompleto.");
        try (var objetos = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            objetos.setObjectInputFilter(info -> {
                if (info.depth() > 32 || info.references() > 100000 || info.arrayLength() > 100000)
                    return ObjectInputFilter.Status.REJECTED;
                Class<?> tipo = info.serialClass();
                if (tipo == null) return ObjectInputFilter.Status.UNDECIDED;
                while (tipo.isArray()) tipo = tipo.getComponentType();
                String nombre = tipo.getName();
                return tipo.isPrimitive() || TIPOS.contains(nombre)
                        || nombre.startsWith("una.eif206.reservas.DTO.")
                        || nombre.equals(Peticion.class.getName()) || nombre.equals(Respuesta.class.getName())
                        || nombre.equals(EventoReserva.class.getName())
                        ? ObjectInputFilter.Status.ALLOWED : ObjectInputFilter.Status.REJECTED;
            });
            return objetos.readObject();
        } catch (ClassNotFoundException e) { throw new IOException("Tipo desconocido.", e); }
    }
}
