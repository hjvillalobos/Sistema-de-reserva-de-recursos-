
package una.eif206.reservas.persistencia;

import jakarta.xml.bind.annotation.*;
import una.eif206.reservas.modelo.Usuario;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "administradores")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListaAdministradores {
    @XmlElement(name = "administrador")
    private List<Usuario> administradores = new ArrayList<>();

    public List<Usuario> getAdministradores() { return administradores; }
    public void setAdministradores(List<Usuario> administradores) { this.administradores = administradores; }
}