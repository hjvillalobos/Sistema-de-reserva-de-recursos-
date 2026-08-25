package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.Recurso;
import una.eif206.reservas.modelo.Reserva;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class MatrizDeCalendarizacion {
    private final List<LocalTime> horas;
    private final List<Recurso> recursos;
    private final List<Reserva> reservas;

    public MatrizDeCalendarizacion(List<LocalTime> horas, List<Recurso> recursos, List<Reserva> reservas){
        this.horas=horas;
        this.recursos=recursos;
        this.reservas=reservas;
    }
    public List<LocalTime> getHoras(){return horas;}
    public List<Recurso> getRecursos(){return recursos;}
    public Optional<Reserva> obtenerReserva(LocalTime hora, Recurso recurso){
        for(Reserva reserva:reservas){
            boolean mismaHora=reserva.getHora().equals(hora);
            boolean mismoRecurso=reserva.getRecurso().getId().equals(recurso.getId());
            if(mismaHora&&mismoRecurso){
                return Optional.of(reserva);
            }
        }
        return Optional.empty();
    }

}
