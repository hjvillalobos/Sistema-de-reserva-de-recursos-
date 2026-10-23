package una.eif206.reservas.cliente;

import una.eif206.reservas.api.*;
import una.eif206.reservas.excepciones.*;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.*;

/** Proxy remoto: los controladores usan interfaces, las invocaciones viajan por socket. */
public final class ProveedorServiciosRemoto implements ProveedorServicios, AutoCloseable {
    private final ConexionServidor conexion;
    private final Map<Class<?>, Object> proxies = new HashMap<>();

    public ProveedorServiciosRemoto(String host, int puerto, int esperaMs) throws IOException {
        conexion = new ConexionServidor(host, puerto, esperaMs);
    }
    private synchronized <T> T proxy(String servicio, Class<T> tipo) {
        return tipo.cast(proxies.computeIfAbsent(tipo, clave -> Proxy.newProxyInstance(
                tipo.getClassLoader(), new Class<?>[]{tipo}, (objeto, metodo, args) -> {
                    if (metodo.getDeclaringClass() == Object.class) return switch (metodo.getName()) {
                        case "toString" -> "Proxy remoto de " + servicio;
                        case "hashCode" -> System.identityHashCode(objeto);
                        case "equals" -> objeto == args[0];
                        default -> throw new UnsupportedOperationException();
                    };
                    var respuesta = conexion.solicitar(servicio, metodo.getName(), args == null ? new Object[0] : args);
                    if (respuesta.error() != null) {
                        if (Arrays.asList(metodo.getExceptionTypes()).contains(ValidacionException.class))
                            throw new ValidacionException(respuesta.error());
                        throw new ServicioRemotoException(respuesta.error());
                    }
                    return respuesta.resultado();
                })));
    }
    public ConexionServidor conexion() { return conexion; }
    @Override public LoginApi login() { return proxy("login", LoginApi.class); }
    @Override public ReservaApi reservas() { return proxy("reservas", ReservaApi.class); }
    @Override public RecursoApi recursos() { return proxy("recursos", RecursoApi.class); }
    @Override public CategoriaApi categorias() { return proxy("categorias", CategoriaApi.class); }
    @Override public FuncionarioApi funcionarios() { return proxy("funcionarios", FuncionarioApi.class); }
    @Override public CalendarizacionApi calendarizacion() { return proxy("calendarizacion", CalendarizacionApi.class); }
    @Override public ProgramacionApi programacion() { return proxy("programacion", ProgramacionApi.class); }
    @Override public EstadisticasApi estadisticas() { return proxy("estadisticas", EstadisticasApi.class); }
    @Override public void close() { conexion.close(); }
}
