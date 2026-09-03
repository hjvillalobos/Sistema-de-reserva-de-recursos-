package una.eif206.reservas.DTO;

import java.util.List;

public class MatrizCalendarizacionDTO {

    private final List<String> horas;
    private final List<RecursoDTO> recursos;
    private final List<List<CeldaCalendarizacionDTO>> celdas;

    public MatrizCalendarizacionDTO(List<String> horas, List<RecursoDTO> recursos, List<List<CeldaCalendarizacionDTO>> celdas){
        this.horas=horas;
        this.recursos=recursos;
        this.celdas=celdas;
    }

    public List<String> getHoras() {
        return horas;
    }

    public List<RecursoDTO> getRecursos() {
        return recursos;
    }

    public CeldaCalendarizacionDTO obtenerCelda(int indiceHora,int indiceRecurso){
        return  celdas.get(indiceHora).get(indiceRecurso);
    }
}
