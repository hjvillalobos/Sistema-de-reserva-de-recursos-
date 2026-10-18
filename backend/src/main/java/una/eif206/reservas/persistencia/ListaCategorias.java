
package una.eif206.reservas.persistencia;

import jakarta.xml.bind.annotation.*;
import una.eif206.reservas.modelo.Categoria;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "categorias")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListaCategorias {
    @XmlElement(name = "categoria")
    private List<Categoria> categorias = new ArrayList<>();

    public List<Categoria> getCategorias() { return categorias; }
    public void setCategorias(List<Categoria> categorias) { this.categorias = categorias; }
}