package una.eif206.reservas.modelo;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "recurso")
@XmlAccessorType(XmlAccessType.FIELD)
public class Recurso {

    @XmlAttribute
    private String id;            // id o número de activo (se ingresa manualmente, no autogenerado)
    private String categoriaId;
    private String descripcion;

    public Recurso() {}

    public Recurso(String id, String categoriaId, String descripcion) {
        this.id = id;
        this.categoriaId = categoriaId;
        this.descripcion = descripcion;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCategoriaId() { return categoriaId; }
    public void setCategoriaId(String categoriaId) { this.categoriaId = categoriaId; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
