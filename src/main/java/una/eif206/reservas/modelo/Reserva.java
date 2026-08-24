package una.eif206.reservas.modelo;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlRootElement;

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import una.eif206.reservas.persistencia.adaptadores.LocalDateAdapter;
import una.eif206.reservas.persistencia.adaptadores.LocalTimeAdapter;

import java.time.LocalDate;
import java.time.LocalTime;

@XmlRootElement(name = "reserva")
@XmlAccessorType(XmlAccessType.FIELD)

public class Reserva {
    @XmlAttribute
    private String id;
    private Recurso recurso;
    private String actividad;
    private EstadoReserva estado;
    private Funcionario funcionario;

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fecha;
    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    private LocalTime hora;


    public Reserva(){}

    public Reserva(String id, Recurso recurso, String actividad,EstadoReserva estado,Funcionario funcionario,
                   LocalDate fecha, LocalTime hora){
        this.id=id;
        this.recurso=recurso;
        this.actividad=actividad;
        this.estado=estado;
        this.hora=hora;
        this.fecha=fecha;
        this.funcionario=funcionario;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Recurso getRecurso() { return recurso; }
    public void setRecurso(Recurso recurso) { this.recurso = recurso; }

    public String getActividad() { return actividad; }
    public void setActividad(String actividad) { this.actividad = actividad; }

    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }

    @Override
    public String toString() {
        return getActividad()+"-"+
                (recurso!=null?recurso.getId():"sin recurso")+
                "("+fecha+" "+hora+")";
    }
}
