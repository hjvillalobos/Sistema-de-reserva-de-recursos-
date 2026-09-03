package una.eif206.reservas.DTO;

import java.util.List;

public class ReservaDTO {

    private String id;
    private String actividad;
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private String funcionarioId;
    private String estado;
    private List<String> recursosAsignados;

    public ReservaDTO() {}

    public ReservaDTO(String id, String actividad, String fecha, String horaInicio, String horaFin,
                      String funcionarioId, List<String> recursosAsignados, String estado) {
        this.id = id;
        this.actividad = actividad;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.funcionarioId = funcionarioId;
        this.recursosAsignados=recursosAsignados;
        this.estado=estado;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getActividad() { return actividad; }
    public void setActividad(String actividad) { this.actividad = actividad; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

    public String getFuncionarioId() { return funcionarioId; }
    public void setFuncionarioId(String funcionarioId) { this.funcionarioId = funcionarioId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<String> getRecursosAsignados() { return recursosAsignados; }
    public void setRecursosAsignados(List<String> recursosAsignados) { this.recursosAsignados = recursosAsignados; }
}