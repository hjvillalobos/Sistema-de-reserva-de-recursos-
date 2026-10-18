package una.eif206.reservas.persistencia;

import jakarta.xml.bind.annotation.*;
import una.eif206.reservas.modelo.Reserva;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "reservas")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListaReservas {
    @XmlElement(name = "reserva")
    private List<Reserva> reservas = new ArrayList<>();

    public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }
}
