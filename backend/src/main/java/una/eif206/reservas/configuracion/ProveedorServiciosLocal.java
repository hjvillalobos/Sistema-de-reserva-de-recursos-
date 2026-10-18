package una.eif206.reservas.configuracion;

import una.eif206.reservas.api.*;
import una.eif206.reservas.servicios.*;

/** Adaptador temporal para ejecutar el Proyecto I mientras se implementa el transporte. */
public final class ProveedorServiciosLocal implements ProveedorServicios {
    private final LoginApi login = new LoginService();
    private final ReservaApi reservas = new ReservaServicio();
    private final RecursoApi recursos = new RecursoServicio();
    private final CategoriaApi categorias = new CategoriaService();
    private final FuncionarioApi funcionarios = new FuncionarioServicio();
    private final CalendarizacionApi calendarizacion = new CalendarizacionServicio();
    private final ProgramacionApi programacion = new ProgramacionServicio();
    private final EstadisticasApi estadisticas = new EstadisticasServicio();

    @Override
    public LoginApi login() { return login; }

    @Override
    public ReservaApi reservas() { return reservas; }

    @Override
    public RecursoApi recursos() { return recursos; }

    @Override
    public CategoriaApi categorias() { return categorias; }

    @Override
    public FuncionarioApi funcionarios() { return funcionarios; }

    @Override
    public CalendarizacionApi calendarizacion() { return calendarizacion; }

    @Override
    public ProgramacionApi programacion() { return programacion; }

    @Override
    public EstadisticasApi estadisticas() { return estadisticas; }

}
