package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.EstadoReserva;
import una.eif206.reservas.modelo.Recurso;
import una.eif206.reservas.modelo.Reserva;
import una.eif206.reservas.persistencia.RecursoDAO;
import una.eif206.reservas.persistencia.RecursoDAOXml;
import una.eif206.reservas.persistencia.ReservaDAO;
import una.eif206.reservas.persistencia.ReservaDAOXml;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ReservaService {

    private final ReservaDAO reservaDAO;
    private final RecursoDAO recursoDAO;

    public ReservaService() {
        this(new ReservaDAOXml(), new RecursoDAOXml());
    }

    public ReservaService(ReservaDAO reservaDAO, RecursoDAO recursoDAO) {
        this.reservaDAO = reservaDAO;
        this.recursoDAO = recursoDAO;
    }

    public List<Reserva> listarPorFuncionario(String funcionarioId) {
        return reservaDAO.buscarPorFuncionario(funcionarioId);
    }

    public void validarDatos(String actividad, String fecha, String horaInicio, String horaFin,
                             List<String> categoriasSolicitadas) throws ValidacionException {
        if (actividad == null || actividad.isBlank()) {
            throw new ValidacionException("La actividad es obligatoria.");
        }
        if (categoriasSolicitadas == null || categoriasSolicitadas.isEmpty()) {
            throw new ValidacionException("Debe seleccionar al menos una categoría de recurso.");
        }

        LocalDate fechaParseada;
        try {
            fechaParseada = LocalDate.parse(fecha);
        } catch (DateTimeParseException e) {
            throw new ValidacionException("La fecha no es válida (use formato AAAA-MM-DD).");
        }
        if (fechaParseada.isBefore(LocalDate.now())) {
            throw new ValidacionException("La fecha debe ser hoy o en el futuro.");
        }

        LocalTime inicio;
        LocalTime fin;
        try {
            inicio = LocalTime.parse(horaInicio);
            fin = LocalTime.parse(horaFin);
        } catch (DateTimeParseException e) {
            throw new ValidacionException("La hora de inicio o fin no es válida (use formato HH:mm).");
        }
        if (!fin.isAfter(inicio)) {
            throw new ValidacionException("La hora de fin debe ser posterior a la hora de inicio.");
        }
    }

    public Reserva intentarRegistrar(String actividad, String fecha, String horaInicio, String horaFin,
                                     String funcionarioId, List<String> categoriasSolicitadas)
            throws ValidacionException {
        validarDatos(actividad, fecha, horaInicio, horaFin, categoriasSolicitadas);

        LocalTime inicio = LocalTime.parse(horaInicio);
        LocalTime fin = LocalTime.parse(horaFin);
        List<Reserva> reservasExistentes = reservaDAO.obtenerTodos();

        List<String> recursosAsignados = new ArrayList<>();
        List<String> categoriasSinDisponibilidad = new ArrayList<>();

        for (String categoriaId : categoriasSolicitadas) {
            String recursoDisponible = buscarPrimerRecursoDisponible(
                    categoriaId, fecha, inicio, fin, reservasExistentes);

            if (recursoDisponible == null) {
                categoriasSinDisponibilidad.add(categoriaId);
            } else {
                recursosAsignados.add(recursoDisponible);
            }
        }

        if (!categoriasSinDisponibilidad.isEmpty()) {
            throw new ValidacionException(
                    "No hay disponibilidad para las categorías: " + String.join(", ", categoriasSinDisponibilidad));
        }

        String id = reservaDAO.generarSiguienteId();
        Reserva reserva = new Reserva(id, actividad, fecha, horaInicio, horaFin, funcionarioId, categoriasSolicitadas);
        reserva.setRecursosAsignados(recursosAsignados);
        reservaDAO.guardar(reserva);
        return reserva;
    }

    public void cancelar(String idReserva, String funcionarioId) throws ValidacionException {
        Reserva reserva = reservaDAO.buscarPorId(idReserva);
        if (reserva == null) {
            throw new ValidacionException("No existe una reserva con id " + idReserva);
        }
        if (!reserva.getFuncionarioId().equalsIgnoreCase(funcionarioId)) {
            throw new ValidacionException("Solo puede cancelar sus propias reservas.");
        }
        if (LocalDate.parse(reserva.getFecha()).isBefore(LocalDate.now())) {
            throw new ValidacionException("No se puede cancelar una reserva que ya pasó.");
        }

        // Al quedar CANCELADA, estaDisponible() ya no la cuenta como ocupación,
        // así que los recursos quedan libres automáticamente para otras reservas.
        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaDAO.guardar(reserva);
    }

    private String buscarPrimerRecursoDisponible(String categoriaId, String fecha,
                                                 LocalTime horaInicio, LocalTime horaFin,
                                                 List<Reserva> reservasExistentes) {
        List<Recurso> recursosDeCategoria = recursoDAO.buscarPorCategoria(categoriaId);

        for (Recurso recurso : recursosDeCategoria) {
            if (estaDisponible(recurso.getId(), fecha, horaInicio, horaFin, reservasExistentes)) {
                return recurso.getId();
            }
        }
        return null;
    }

    private boolean estaDisponible(String recursoId, String fecha, LocalTime horaInicio, LocalTime horaFin,
                                   List<Reserva> reservasExistentes) {
        for (Reserva r : reservasExistentes) {
            if (r.getEstado() != EstadoReserva.ACTIVA) {
                continue;
            }
            if (!r.getFecha().equals(fecha)) {
                continue;
            }
            if (!r.getRecursosAsignados().contains(recursoId)) {
                continue;
            }

            LocalTime inicioExistente = LocalTime.parse(r.getHoraInicio());
            LocalTime finExistente = LocalTime.parse(r.getHoraFin());

            boolean seSolapan = horaInicio.isBefore(finExistente) && inicioExistente.isBefore(horaFin);
            if (seSolapan) {
                return false;
            }
        }
        return true;
    }
}