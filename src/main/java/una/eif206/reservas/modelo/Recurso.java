package una.eif206.reservas.modelo;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "recurso")
@XmlAccessorType(XmlAccessType.FIELD)
public class Recurso {
    @XmlAttribute
    private String id;
    private String descripcion;
    private Categoria categoria;

    public Recurso(){}

    public Recurso(String id, String descripcion, Categoria categoria){
        this.id=id;
        this.descripcion=descripcion;
        this.categoria=categoria;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    @Override
    public String toString() {
        return getDescripcion()+"-"+
                (categoria!=null?categoria.getId():"sin categoria")
                +"id: "+getId();}
}
