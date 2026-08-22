package una.eif206.reservas.modelo;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "categoria")
@XmlAccessorType(XmlAccessType.FIELD)
public class Categoria {

    @XmlAttribute
    private String id;          // autogenerado, ej: CAT-000001
    private String descripcion;

    public Categoria() {}

    public Categoria(String id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() { return descripcion; } // útil para mostrar en ComboBox/ListView
}