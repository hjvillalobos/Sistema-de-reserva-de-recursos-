package una.eif206.reservas.DTO;

import java.util.ArrayList;
import java.util.List;

public class CeldaProgramacionDTO {

    private final List<String> actividades;

    public CeldaProgramacionDTO() {
        this.actividades = new ArrayList<>();
    }

    public void agregar(String actividad, String nombreFuncionario) {
        actividades.add(actividad + " / " + nombreFuncionario);
    }

    public boolean isOcupada() {
        return !actividades.isEmpty();
    }

    public List<String> getActividades() {
        return actividades;
    }
}
