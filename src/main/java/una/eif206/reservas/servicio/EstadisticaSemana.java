package una.eif206.reservas.servicio;

import java.time.LocalDate;

public class EstadisticaSemana {
    private final LocalDate inicioSemana;
    private final LocalDate finSemana;
    private final long cantidad;

    public EstadisticaSemana(LocalDate inicioSemana, LocalDate finSemana, long cantidad) {
        this.inicioSemana = inicioSemana;
        this.finSemana = finSemana;
        this.cantidad = cantidad;
    }

    public LocalDate getInicioSemana() { return inicioSemana; }
    public LocalDate getFinSemana() { return finSemana; }
    public long getCantidad() { return cantidad; }
    public String getEtiqueta() { return inicioSemana + " - " + finSemana; }
}