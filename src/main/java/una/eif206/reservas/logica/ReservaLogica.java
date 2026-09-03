package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.RecursoDTO;
import una.eif206.reservas.datos.RecursoDatos;
import una.eif206.reservas.DTO.ReservaDTO;
import una.eif206.reservas.datos.ReservaDatos;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class ReservaLogica {

    private final ReservaDatos reservaDatos;
    private final RecursoDatos recursoDatos;

    public ReservaLogica() {
        this.recursoDatos=new RecursoDatos();
        this.reservaDatos=new ReservaDatos();
    }

    public ReservaLogica(ReservaDatos reservaDatos, RecursoDatos recursoDatos) {
        this.reservaDatos = reservaDatos;
        this.recursoDatos = recursoDatos;
    }

    public List<ReservaDTO> listarPorFuncionario(String funcionarioId) {
        return reservaDatos.buscarPorFuncionario(funcionarioId);
    }

    private void validarDatos(String actividad, String fecha, String horaInicio, String horaFin, List<String> categoriasIds) throws ValidacionException {
        if (actividad == null || actividad.isBlank()) {
            throw new ValidacionException("Debe indicar la actividad.");
        }
        if (fecha == null || fecha.isBlank()) {
            throw new ValidacionException("Debe indicar la fecha.");
        }
        if (horaInicio == null || horaInicio.isBlank()){
            throw new ValidacionException("Debe indicar la hora de inicio.");
        }
        if (horaFin == null || horaFin.isBlank()){
            throw new ValidacionException("Debe indicar la hora de fin.");
        }
        if (horaInicio.compareTo(horaFin) >= 0){
            throw new ValidacionException("La hora de inicio debe ser antes que la hora de fin.");
        }
        if (categoriasIds == null || categoriasIds.isEmpty()){
            throw new ValidacionException("Debe seleccionar al menos una categoría.");
        }
    }

    public ReservaDTO intentarRegistrar(String actividad, String fecha, String horaInicio, String horaFin, String funcionarioId, List<String> categoriasIds) throws ValidacionException {
        validarDatos(actividad, fecha, horaInicio, horaFin, categoriasIds);

        List<String> recursosAsignados = new ArrayList<>();
        List<String> categoriasNoDisponibles = new ArrayList<>();
        for (String categoriaId : categoriasIds) {
            String recursoDisponible = buscarPrimerRecursoDisponible(categoriaId, fecha, horaInicio, horaFin);
            if (recursoDisponible == null) {
                categoriasNoDisponibles.add(categoriaId);
            } else {
                recursosAsignados.add(recursoDisponible);
            }
        }
        if (!categoriasNoDisponibles.isEmpty()) {
            throw new ValidacionException("No hay disponibilidad para las categorias: " + String.join(",", categoriasNoDisponibles));
        }
        ReservaDTO reserva = new ReservaDTO(reservaDatos.generarSiguienteId(), actividad, fecha, horaInicio, horaFin, funcionarioId, recursosAsignados, "ACTIVA");
        reservaDatos.guardar(reserva);
        return reserva;
    }

    public void cancelar(String reservaId, String funcionarioId)throws ValidacionException{
        ReservaDTO reserva=reservaDatos.buscarPorId(reservaId);
        if (reserva == null) {
            throw new ValidacionException("No existe una reserva con id " + reservaId);
        }
        if (!reserva.getFuncionarioId().equalsIgnoreCase(funcionarioId)) {
            throw new ValidacionException("Solo puede cancelar sus propias reservas.");
        }
        reserva.setEstado("CANCELADA");
        reservaDatos.guardar(reserva);
    }
    private String buscarPrimerRecursoDisponible(String categoriaId, String fecha, String horaInicio, String horaFin) {
        for (RecursoDTO recurso : recursoDatos.buscarPorCategoria(categoriaId)) {
            if (estaDisponible(recurso.getId(), fecha, horaInicio, horaFin)) {
                return recurso.getId();
            }
        }
        return null;
    }

    private boolean estaDisponible(String recursoId, String fecha, String horaInicio, String horaFin) {
        for (ReservaDTO reserva: reservaDatos.obtenerTodos()) {
            if (!"ACTIVA".equals(reserva.getEstado())) continue;
            if (!reserva.getFecha().equals(fecha)) continue;
            if (!reserva.getRecursosAsignados().contains(recursoId)) continue;
            boolean seSolapan = horaInicio.compareTo(reserva.getHoraFin()) < 0 && horaFin.compareTo(reserva.getHoraInicio()) > 0;
            if (seSolapan) {
                return false;
            }
        }
        return true;
    }
}
