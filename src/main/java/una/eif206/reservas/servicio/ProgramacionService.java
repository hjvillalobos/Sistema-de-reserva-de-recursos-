package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.Reserva;
import una.eif206.reservas.modelo.Usuario;
import una.eif206.reservas.persistencia.ReservaDAO;
import una.eif206.reservas.persistencia.ReservaDaoXml;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ProgramacionService {
    private final ReservaDAO reservaDao;

    public ProgramacionService() { this.reservaDao = new ReservaDaoXml(); }
    public ProgramacionService(ReservaDAO reservaDao) { this.reservaDao = reservaDao; }

    public MatrizProgramacion obtenerMatrizSemana(Usuario usuario, LocalDate fechaDeLaSemana) {
        validarUsuario(usuario);
        if (fechaDeLaSemana == null)
            throw new IllegalArgumentException("Debe indicar una fecha de referencia para la semana.");

        LocalDate inicio = inicioDeSemana(fechaDeLaSemana);
        LocalDate fin = inicio.plusDays(6);

        List<Reserva> reservas = reservaDao.obtenerPorRangoFechas(inicio, fin);
        List<LocalDate> dias = generarDias(inicio);
        List<LocalTime> horas = generarHoras();

        return new MatrizProgramacion(inicio, fin, dias, horas, reservas);
    }

    private void validarUsuario(Usuario usuario) {
        if (usuario == null)
            throw new AccesoDenegadoException("Debe iniciar sesión para ver la programación de actividades.");
    }

    public static LocalDate inicioDeSemana(LocalDate fecha) {
        return fecha.minusDays(fecha.getDayOfWeek().getValue() - 1L);
    }

    private List<LocalDate> generarDias(LocalDate inicio) {
        List<LocalDate> dias = new ArrayList<>();
        for (int i = 0; i < 7; i++) dias.add(inicio.plusDays(i));
        return dias;
    }

    private List<LocalTime> generarHoras() {
        List<LocalTime> horas = new ArrayList<>();
        for (int h = 0; h < 24; h++) horas.add(LocalTime.of(h, 0));
        return horas;
    }
}