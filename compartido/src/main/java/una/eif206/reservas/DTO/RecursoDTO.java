package una.eif206.reservas.DTO;

public class RecursoDTO {

    private String id;
    private String categoriaId;
    private String descripcion;

    public RecursoDTO() {}

    public RecursoDTO(String id, String categoriaId, String descripcion) {
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
