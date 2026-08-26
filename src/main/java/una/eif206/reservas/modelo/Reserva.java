package una.eif206.reservas.modelo;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "reserva")
@XmlAccessorType(XmlAccessType.FIELD)
public class Reserva {

    @XmlAttribute
    private String id;               // autogenerado, ej: RES-000001

    private String actividad;
    private String fecha;            // formato yyyy-MM-dd
    private String horaInicio;       // formato HH:mm
    private String horaFin;          // formato HH:mm
    private String funcionarioId;
    private EstadoReserva estado;

    @XmlElementWrapper(name = "categoriasSolicitadas")
    @XmlElement(name = "categoriaId")
    private List<String> categoriasSolicitadas = new ArrayList<>();

    @XmlElementWrapper(name = "recursosAsignados")
    @XmlElement(name = "recursoId")
    private List<String> recursosAsignados = new ArrayList<>();

    public Reserva() {}

    public Reserva(String id, String actividad, String fecha, String horaInicio, String horaFin,
                   String funcionarioId, List<String> categoriasSolicitadas) {
        this.id = id;
        this.actividad = actividad;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.funcionarioId = funcionarioId;
        this.categoriasSolicitadas = categoriasSolicitadas;
        this.estado = EstadoReserva.ACTIVA;
        this.recursosAsignados = new ArrayList<>();
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

    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }

    public List<String> getCategoriasSolicitadas() { return categoriasSolicitadas; }
    public void setCategoriasSolicitadas(List<String> categoriasSolicitadas) { this.categoriasSolicitadas = categoriasSolicitadas; }

    public List<String> getRecursosAsignados() { return recursosAsignados; }
    public void setRecursosAsignados(List<String> recursosAsignados) { this.recursosAsignados = recursosAsignados; }
}