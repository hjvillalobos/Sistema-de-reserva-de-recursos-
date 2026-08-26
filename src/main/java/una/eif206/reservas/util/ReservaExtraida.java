package una.eif206.reservas.util;

import java.util.ArrayList;
import java.util.List;

// Objeto simple (no se persiste) para pasar del texto de la IA al formulario
public class ReservaExtraida {
    private String actividad = "";
    private String fecha = "";
    private String horaInicio = "";
    private String horaFin = "";
    private List<String> categoriasDescripcion = new ArrayList<>();

    public String getActividad() { return actividad; }
    public void setActividad(String actividad) { this.actividad = actividad; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

    public List<String> getCategoriasDescripcion() { return categoriasDescripcion; }
    public void setCategoriasDescripcion(List<String> categoriasDescripcion) { this.categoriasDescripcion = categoriasDescripcion; }
}