package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.Reserva;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MatrizProgramacion {
    private final LocalDate inicioSemana;
    private final LocalDate finSemana;
    private final List<LocalDate> dias;
    private final List<LocalTime> horas;
    private final List<Reserva> reservas;

    public MatrizProgramacion(LocalDate inicioSemana, LocalDate finSemana, List<LocalDate> dias,
                              List<LocalTime> horas, List<Reserva> reservas) {
        this.inicioSemana = inicioSemana;
        this.finSemana = finSemana;
        this.dias = dias;
        this.horas = horas;
        this.reservas = reservas;
    }

    public LocalDate getInicioSemana() { return inicioSemana; }
    public LocalDate getFinSemana() { return finSemana; }
    public List<LocalDate> getDias() { return dias; }
    public List<LocalTime> getHoras() { return horas; }

    public List<Reserva> obtenerActividades(LocalDate dia, LocalTime hora) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva reserva : reservas) {
            boolean mismoDia = reserva.getFecha() != null && reserva.getFecha().equals(dia);
            boolean mismaHora = reserva.getHora() != null && reserva.getHora().equals(hora);
            if (mismoDia && mismaHora) resultado.add(reserva);
        }
        return resultado;
    }
}
