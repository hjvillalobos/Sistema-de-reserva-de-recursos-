
package una.eif206.reservas.modelo;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({Funcionario.class})
public class Usuario {

    @XmlAttribute
    protected String id;
    protected String clave;
    @XmlAttribute
    protected Rol rol;

    public Usuario() {
    }

    public Usuario(String id, String clave, Rol rol) {
        this.id = id;
        this.clave = clave;
        this.rol = rol;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}