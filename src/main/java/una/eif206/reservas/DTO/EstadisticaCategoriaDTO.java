package una.eif206.reservas.DTO;

public class EstadisticaCategoriaDTO {

    private final String categoriaId;
    private final String descripcionCategoria;
    private final long cantidad;

    public EstadisticaCategoriaDTO(String categoriaId, String descripcionCategoria, long cantidad) {
        this.categoriaId = categoriaId;
        this.descripcionCategoria = descripcionCategoria;
        this.cantidad = cantidad;
    }

    public String getCategoriaId() { return categoriaId; }
    public String getDescripcionCategoria() { return descripcionCategoria; }
    public long getCantidad() { return cantidad; }
}