package una.eif206.reservas.modelo;

import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "funcionario")
@XmlAccessorType(XmlAccessType.FIELD)
public class Funcionario extends Usuario {

    private String nombre;
    private String telefono;

    public Funcionario() {
        super();
    }

    public Funcionario(String id, String clave, String nombre, String telefono) {
        super(id, clave, Rol.FUNCIONARIO);
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
