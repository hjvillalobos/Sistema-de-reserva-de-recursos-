package una.eif206.reservas.servicio;

public class EstadisticaCategoria {
    private final String idCategoria;
    private final String descripcionCategoria;
    private final long cantidad;

    public EstadisticaCategoria(String idCategoria, String descripcionCategoria, long cantidad) {
        this.idCategoria = idCategoria;
        this.descripcionCategoria = descripcionCategoria;
        this.cantidad = cantidad;
    }

    public String getIdCategoria() { return idCategoria; }
    public String getDescripcionCategoria() { return descripcionCategoria; }
    public long getCantidad() { return cantidad; }
}