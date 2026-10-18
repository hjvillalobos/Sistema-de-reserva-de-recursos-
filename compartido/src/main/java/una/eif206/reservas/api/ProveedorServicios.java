package una.eif206.reservas.api;

/** Punto de conexión entre la presentación y una implementación de los contratos. */
public interface ProveedorServicios {
    LoginApi login();
    ReservaApi reservas();
    RecursoApi recursos();
    CategoriaApi categorias();
    FuncionarioApi funcionarios();
    CalendarizacionApi calendarizacion();
    ProgramacionApi programacion();
    EstadisticasApi estadisticas();
}
