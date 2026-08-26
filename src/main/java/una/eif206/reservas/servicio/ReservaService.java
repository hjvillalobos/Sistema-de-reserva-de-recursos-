package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.EstadoReserva;
import una.eif206.reservas.modelo.Reserva;
import una.eif206.reservas.persistencia.ReservaDAO;
import una.eif206.reservas.persistencia.ReservaDAOXml;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ReservaService {

    private final ReservaDAO reservaDAO;

    public ReservaService() { this(new ReservaDAOXml()); }
    public ReservaService(ReservaDAO reservaDAO) { this.reservaDAO = reservaDAO; }

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

    /**
     * PENDIENTE DE INTEGRACIÓN CON RECURSO.
     * Cuando el compañero suba su módulo, aquí se debe:
     *   1. Por cada categoría solicitada, buscar el primer Recurso disponible
     *      para esa fecha/horario (consultando también las reservas ya activas).
     *   2. Si TODAS las categorías tienen recurso disponible: crear la Reserva,
     *      guardar los ids de recurso asignados y persistir (éxito).
     *   3. Si alguna categoría no tiene disponibilidad: lanzar ValidacionException
     *      indicando cuáles categorías fallaron (no tuvo éxito).
     * Por ahora solo valida los datos del formulario.
     */
    public Reserva intentarRegistrar(String actividad, String fecha, String horaInicio, String horaFin,
                                     String funcionarioId, List<String> categoriasSolicitadas)
            throws ValidacionException {
        validarDatos(actividad, fecha, horaInicio, horaFin, categoriasSolicitadas);

        throw new ValidacionException(
                "Registro de reservas pendiente: falta integrar el módulo de Recursos del equipo.");
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

        reserva.setEstado(EstadoReserva.CANCELADA);
        // PENDIENTE: cuando exista RecursoService, liberar aquí los recursos
        // en reserva.getRecursosAsignados().
        reservaDAO.guardar(reserva);
    }
}