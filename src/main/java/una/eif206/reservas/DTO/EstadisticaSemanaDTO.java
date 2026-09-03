package una.eif206.reservas.DTO;

public class EstadisticaSemanaDTO {

    private final String inicioSemana;
    private final String finSemana;
    private final long cantidad;

    public EstadisticaSemanaDTO(String inicioSemana, String finSemana, long cantidad) {
        this.inicioSemana = inicioSemana;
        this.finSemana = finSemana;
        this.cantidad = cantidad;
    }

    public String getInicioSemana() { return inicioSemana; }
    public String getFinSemana() { return finSemana; }
    public long getCantidad() { return cantidad; }

    public String getEtiqueta() {
        return inicioSemana + " a " + finSemana;
    }
}