package una.eif206.reservas.protocolo;

import una.eif206.reservas.api.*;
import java.util.Map;

/** Lista cerrada: la red nunca puede invocar clases o métodos ajenos a estas interfaces. */
public final class Contratos {
    public static final Map<String, Class<?>> TIPOS = Map.of(
            "login", LoginApi.class, "reservas", ReservaApi.class, "recursos", RecursoApi.class,
            "categorias", CategoriaApi.class, "funcionarios", FuncionarioApi.class,
            "calendarizacion", CalendarizacionApi.class, "programacion", ProgramacionApi.class,
            "estadisticas", EstadisticasApi.class);
    private Contratos() { }
}
