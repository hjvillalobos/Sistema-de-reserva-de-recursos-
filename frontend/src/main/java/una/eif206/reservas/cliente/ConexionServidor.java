package una.eif206.reservas.cliente;

import una.eif206.reservas.protocolo.*;
import una.eif206.reservas.excepciones.ServicioRemotoException;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/** Un lector separa respuestas correlacionadas y eventos asíncronos. No reintenta escrituras. */
public final class ConexionServidor implements AutoCloseable {
    private final Socket socket = new Socket();
    private final CanalMensajes canal;
    private final AtomicLong secuencia = new AtomicLong();
    private final ConcurrentMap<Long, CompletableFuture<Respuesta>> pendientes = new ConcurrentHashMap<>();
    private final BlockingQueue<EventoReserva> eventos = new ArrayBlockingQueue<>(1000);
    private final AtomicBoolean cerrada = new AtomicBoolean();
    private final int esperaMs;

    public ConexionServidor(String host, int puerto, int esperaMs) throws IOException {
        if (esperaMs <= 0) throw new IllegalArgumentException("El tiempo de espera debe ser positivo.");
        this.esperaMs = esperaMs;
        try {
            socket.connect(new InetSocketAddress(host, puerto), esperaMs);
            socket.setTcpNoDelay(true);
            canal = new CanalMensajes(socket.getInputStream(), socket.getOutputStream());
        } catch (IOException | RuntimeException e) { socket.close(); throw e; }
        Thread.ofVirtual().name("lector-servidor").start(this::escuchar);
    }

    public Respuesta solicitar(String servicio, String metodo, Object... argumentos) {
        if (cerrada.get()) throw new ServicioRemotoException("La conexión está cerrada. Vuelva a abrir la aplicación.");
        long id = secuencia.incrementAndGet();
        var futura = new CompletableFuture<Respuesta>();
        pendientes.put(id, futura);
        try {
            if (cerrada.get()) throw new IOException("Conexión cerrada.");
            canal.enviar(new Peticion(id, servicio, metodo, argumentos));
            Respuesta respuesta = futura.get(esperaMs, TimeUnit.MILLISECONDS);
            if (servicio.equals("login") && (metodo.equals("cerrarSesion") || metodo.equals("autenticarSesion"))) eventos.clear();
            return respuesta;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); close();
            throw new ServicioRemotoException("Operación interrumpida; compruebe el resultado antes de repetirla.", e);
        } catch (TimeoutException e) {
            close();
            throw new ServicioRemotoException("El servidor no respondió a tiempo. El resultado es desconocido; compruébelo antes de repetir la operación.", e);
        } catch (IOException | ExecutionException e) {
            close();
            throw new ServicioRemotoException("Se perdió la conexión. Vuelva a ingresar y compruebe el resultado antes de repetir la operación.", e);
        } finally { pendientes.remove(id); }
    }

    private void escuchar() {
        try {
            while (!cerrada.get()) {
                Object mensaje = canal.recibir();
                if (mensaje instanceof Respuesta respuesta) {
                    var futura = pendientes.get(respuesta.id());
                    if (futura != null) futura.complete(respuesta);
                } else if (mensaje instanceof EventoReserva evento) {
                    if (!eventos.offer(evento)) throw new IOException("Cola de eventos llena.");
                } else throw new IOException("Respuesta inválida del servidor.");
            }
        } catch (IOException e) { close(); }
    }

    /** Consumir desde un hilo de escucha; el futuro panel de alertas usará esta cola. */
    public BlockingQueue<EventoReserva> eventos() { return eventos; }

    @Override public void close() {
        if (cerrada.compareAndSet(false, true)) {
            try { socket.close(); } catch (IOException ignored) { }
            pendientes.values().forEach(f -> f.completeExceptionally(new IOException("Conexión cerrada.")));
            eventos.clear();
        }
    }
}
