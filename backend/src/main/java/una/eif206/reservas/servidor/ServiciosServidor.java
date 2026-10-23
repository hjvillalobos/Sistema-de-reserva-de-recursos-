package una.eif206.reservas.servidor;

import una.eif206.reservas.datos.*;
import una.eif206.reservas.datos.mysql.*;
import una.eif206.reservas.logica.*;
import una.eif206.reservas.servicios.*;
import java.nio.file.Path;
import java.util.Map;

/** Composición transitoria: acceso MySQL inyectable, otros servicios XML. */
public final class ServiciosServidor {
    public final AdministradorDatos administradores;
    public final FuncionarioDatos funcionarios;
    public final CategoriaDatos categorias;
    public final RecursoDatos recursos;
    public final ReservaDatos reservas;
    public final LoginLogica login;
    public final boolean mysql;
    final Map<String, Object> servicios;

    public ServiciosServidor(Path carpeta) {
        this(carpeta, null);
    }

    public ServiciosServidor(Path carpeta, UsuariosAcceso usuarios) {
        mysql = usuarios != null;
        administradores = new AdministradorDatos(carpeta.resolve("administradores.xml").toString());
        funcionarios = mysql ? new FuncionarioMySqlConsulta() : new FuncionarioDatos(carpeta.resolve("funcionarios.xml").toString());
        categorias = mysql ? new CategoriaMySqlConsulta() : new CategoriaDatos(carpeta.resolve("categorias.xml").toString());
        recursos = mysql ? new RecursoMySqlDatos() : new RecursoDatos(carpeta.resolve("recursos.xml").toString());
        reservas = mysql ? new ReservaMySqlDatos() : new ReservaDatos(carpeta.resolve("reservas.xml").toString());
        login = usuarios == null ? new LoginLogica(administradores, funcionarios) : new LoginLogica(usuarios);
        servicios = Map.of(
                "recursos", new RecursoServicio(new RecursoLogica(recursos)),
                "reservas", new ReservaServicio(mysql
                        ? new ReservaMySqlLogica((ReservaMySqlDatos) reservas, (RecursoMySqlDatos) recursos)
                        : new ReservaLogica(reservas, recursos)),
                "categorias", new CategoriaService(new CategoriaLogica(categorias)),
                "funcionarios", new FuncionarioServicio(new FuncionarioLogica(funcionarios)),
                "calendarizacion", new CalendarizacionServicio(new CalendarizacionLogica(recursos, reservas, funcionarios)),
                "programacion", new ProgramacionServicio(new ProgramacionLogica(reservas, funcionarios)),
                "estadisticas", new EstadisticasServicio(new EstadisticasLogica(reservas, recursos, categorias)));
    }
}
