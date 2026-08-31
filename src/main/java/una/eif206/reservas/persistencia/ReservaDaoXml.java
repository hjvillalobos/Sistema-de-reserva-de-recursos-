package una.eif206.reservas.persistencia;
import una.eif206.reservas.modelo.EstadoReserva;
import una.eif206.reservas.modelo.Reserva;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReservaDaoXml implements ReservaDAO{
    public static final String RUTA_DEFECTO = "data/reservas.xml";
    private final String ruta;

    public ReservaDaoXml() { this(RUTA_DEFECTO); }
    public ReservaDaoXml(String ruta) { this.ruta = ruta; }

    @Override
    public List<Reserva> obtenerReservas() {
        return cargar().getReservas();
    }

    @Override
    public Optional<Reserva> busquedaPorId(String id) {
        return obtenerReservas().stream()
                .filter(f -> f.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    @Override
    public List<Reserva> obtenerPorFechaYCategoria(LocalDate fecha, String idCat) {
        return obtenerReservas().stream()
                .filter(r->r.getEstado()== EstadoReserva.ACTIVA)
                .filter(r->r.getFecha()!=null &&r.getFecha().equals(fecha))
                .filter(r->r.getRecurso()!=null
                        &&r.getRecurso().getCategoria()!=null
                        && r.getRecurso().getCategoria().getId().equalsIgnoreCase(idCat))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reserva> obtenerPorRangoFechas(LocalDate desde, LocalDate hasta) {
        return obtenerReservas().stream()
                .filter(r -> r.getEstado() == EstadoReserva.ACTIVA)
                .filter(r -> r.getFecha() != null
                        && !r.getFecha().isBefore(desde)
                        && !r.getFecha().isAfter(hasta))
                .collect(Collectors.toList());
    }

    @Override
    public void guardar(Reserva reserva) {
        ListaReservas lista = cargar();
        lista.getReservas().removeIf(f -> f.getId().equalsIgnoreCase(reserva.getId()));
        lista.getReservas().add(reserva);
        XmlDaoUtil.guardar(ruta, lista, ListaReservas.class);
    }

    @Override
    public void eliminar(String id) {
        ListaReservas lista = cargar();
        lista.getReservas().removeIf(f -> f.getId().equalsIgnoreCase(id));
        XmlDaoUtil.guardar(ruta, lista, ListaReservas.class);
    }

    private ListaReservas cargar() {
        return XmlDaoUtil.cargar(ruta, ListaReservas.class);
    }
}
