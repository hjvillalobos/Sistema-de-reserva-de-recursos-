package una.eif206.reservas.servidor;

import una.eif206.reservas.DTO.*;
import una.eif206.reservas.excepciones.ValidacionException;
import una.eif206.reservas.protocolo.*;
import una.eif206.reservas.servicios.LoginService;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/** En MySQL las transacciones coordinan clientes; el modo XML conserva su bloqueo global. */
public final class ServidorReservas implements AutoCloseable {
    private final ServerSocket servidor;
    private final ServiciosServidor datos;
    private final Set<Sesion> sesiones = ConcurrentHashMap.newKeySet();
    private volatile boolean cerrado;

    public ServidorReservas(String host, int puerto, ServiciosServidor datos) throws IOException {
        this.datos = datos;
        servidor = new ServerSocket();
        servidor.bind(new InetSocketAddress(host, puerto));
    }
    public int puerto() { return servidor.getLocalPort(); }

    public void ejecutar() throws IOException {
        while (!cerrado) {
            try {
                Socket socket = servidor.accept();
                socket.setTcpNoDelay(true);
                Sesion sesion = new Sesion(socket);
                sesiones.add(sesion);
                Thread.ofVirtual().name("cliente-reservas").start(sesion::atender);
            } catch (SocketException e) { if (!cerrado) throw e; }
        }
    }

    @Override public void close() throws IOException {
        cerrado = true;
        servidor.close();
        for (Sesion sesion : sesiones) sesion.cerrar();
    }

    private final class Sesion {
        private final Socket socket;
        private final CanalMensajes canal;
        private final LoginService login = new LoginService(datos.login);
        private final BlockingQueue<Object> salientes = new ArrayBlockingQueue<>(128);
        private volatile UsuarioSesionDTO usuario;
        private Thread escritor;

        Sesion(Socket socket) throws IOException {
            this.socket = socket;
            canal = new CanalMensajes(socket.getInputStream(), socket.getOutputStream());
        }
        void enviar(Object objeto) {
            if (!salientes.offer(objeto)) cerrar(); // Un cliente lento no detiene al resto.
        }
        void atender() {
            escritor = Thread.ofVirtual().name("respuestas-reservas").start(() -> {
                try { while (!socket.isClosed()) canal.enviar(salientes.take()); }
                catch (IOException | InterruptedException e) { cerrar(); }
            });
            try {
                while (!socket.isClosed()) {
                    Object objeto = canal.recibir();
                    if (!(objeto instanceof Peticion peticion)) throw new IOException("Se esperaba una petición.");
                    // MySQL coordina en la BD, incluso entre instancias diferentes del servidor.
                    synchronized (datos.mysql ? this : datos) {
                        try {
                            Object resultado = procesar(peticion);
                            enviar(new Respuesta(peticion.id(), sinCredenciales(resultado), null));
                            if (resultado instanceof ReservaDTO reserva && peticion.metodo().equals("intentarRegistrar")) {
                                for (Sesion otra : sesiones) {
                                    UsuarioSesionDTO destinatario = otra.usuario;
                                    if (destinatario != null && !destinatario.getId().equalsIgnoreCase(reserva.getFuncionarioId()))
                                        otra.enviar(new EventoReserva(reserva));
                                }
                            }
                        } catch (Exception e) {
                            Throwable causa = e instanceof InvocationTargetException ? e.getCause() : e;
                            String mensaje = causa instanceof ValidacionException || causa instanceof una.eif206.reservas.datos.mysql.ErrorDatos ? causa.getMessage()
                                    : causa instanceof IllegalArgumentException ? "Petición inválida."
                                    : "No se pudo completar la operación en el servidor.";
                            if (!(causa instanceof ValidacionException) && !(causa instanceof IllegalArgumentException))
                                System.err.println("Error en " + peticion.servicio() + "/" + peticion.metodo() + ": " + causa.getClass().getSimpleName());
                            enviar(new Respuesta(peticion.id(), null, mensaje));
                        }
                    }
                }
            } catch (IOException e) { /* Desconexión: no se conserva la sesión. */ }
            finally { cerrar(); }
        }

        Object procesar(Peticion p) throws Exception {
            Class<?> contrato = Contratos.TIPOS.get(p.servicio());
            if (contrato == null || p.argumentos() == null) throw new IllegalArgumentException();
            Method metodo = Arrays.stream(contrato.getDeclaredMethods())
                    .filter(m -> m.getName().equals(p.metodo()) && m.getParameterCount() == p.argumentos().length)
                    .findFirst().orElseThrow(IllegalArgumentException::new);
            Object[] args = p.argumentos();
            if (p.servicio().equals("login")) {
                if (p.metodo().equals("autenticarSesion")) {
                    usuario = null;
                    Object respuesta = metodo.invoke(login, args);
                    usuario = (UsuarioSesionDTO) respuesta;
                    return respuesta;
                }
                if (p.metodo().equals("cerrarSesion")) { usuario = null; login.cerrarSesion(); return null; }
                exigirSesion();
                return metodo.invoke(login, args);
            }
            exigirSesion();
            if (p.servicio().equals("funcionarios") ||
                    (Set.of("recursos", "categorias").contains(p.servicio()) &&
                     Set.of("crear", "modificar", "eliminar").contains(p.metodo()))) exigirAdministrador();
            if (p.servicio().equals("reservas")) {
                if (usuario.getRol() != RolDTO.FUNCIONARIO) throw new ValidacionException("Solo un funcionario puede gestionar sus reservas.");
                int posicion = switch (p.metodo()) { case "listarPorFuncionario" -> 0; case "cancelar" -> 1; default -> 4; };
                if (!(args[posicion] instanceof String id) || !usuario.getId().equalsIgnoreCase(id))
                    throw new ValidacionException("Solo puede gestionar sus propias reservas.");
                args[posicion] = usuario.getId();
            }
            if (p.servicio().equals("recursos")) validarRecurso(p.metodo(), args);
            return metodo.invoke(datos.servicios.get(p.servicio()), args);
        }
        void exigirSesion() throws ValidacionException {
            if (usuario == null) throw new ValidacionException("Debe iniciar sesión.");
        }
        void exigirAdministrador() throws ValidacionException {
            if (usuario.getRol() != RolDTO.ADMINISTRADOR) throw new ValidacionException("Esta operación requiere un administrador.");
        }
        void validarRecurso(String operacion, Object[] args) throws ValidacionException {
            if (operacion.equals("crear") || operacion.equals("modificar")) {
                if (!(args[0] instanceof RecursoDTO recurso)) throw new ValidacionException("Debe indicar un recurso.");
                if (recurso.getCategoriaId() == null || datos.categorias.buscarPorId(recurso.getCategoriaId()) == null)
                    throw new ValidacionException("La categoría no existe.");
            }
            if (operacion.equals("eliminar")) {
                if (!(args[0] instanceof String id) || datos.recursos.buscarPorId(id) == null)
                    throw new ValidacionException("El recurso no existe.");
                for (ReservaDTO r : datos.reservas.obtenerTodos())
                    if (r.getRecursosAsignados().contains(id))
                        throw new ValidacionException("No puede eliminar un recurso asociado a reservas.");
            }
        }
        void cerrar() {
            usuario = null;
            sesiones.remove(this);
            try { socket.close(); } catch (IOException ignored) { }
            if (escritor != null) escritor.interrupt();
        }
    }

    private static Object sinCredenciales(Object valor) {
        if (valor instanceof FuncionarioDTO f) return new FuncionarioDTO(f.getId(), null, f.getNombre(), f.getTelefono());
        if (valor instanceof List<?> lista) {
            var copia = new ArrayList<>();
            for (Object item : lista) copia.add(sinCredenciales(item));
            return copia;
        }
        return valor;
    }
}
