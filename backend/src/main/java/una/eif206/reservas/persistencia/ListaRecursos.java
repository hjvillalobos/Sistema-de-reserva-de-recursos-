package una.eif206.reservas.persistencia;

import jakarta.xml.bind.annotation.*;
import una.eif206.reservas.modelo.Recurso;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "recursos")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListaRecursos {
    @XmlElement(name = "recurso")
    private List<Recurso> recursos = new ArrayList<>();

    public List<Recurso> getRecursos() { return recursos; }
    public void setRecursos(List<Recurso> recursos) { this.recursos = recursos; }
}