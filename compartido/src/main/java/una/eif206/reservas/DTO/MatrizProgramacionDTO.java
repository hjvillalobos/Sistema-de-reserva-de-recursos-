package una.eif206.reservas.DTO;

import java.util.List;

public class MatrizProgramacionDTO {

    private final String inicioSemana;
    private final String finSemana;
    private final List<String> dias;
    private final List<String> horas;
    private final List<List<CeldaProgramacionDTO>> celdas;

    public MatrizProgramacionDTO(String inicioSemana, String finSemana, List<String> dias,
                                 List<String> horas, List<List<CeldaProgramacionDTO>> celdas) {
        this.inicioSemana = inicioSemana;
        this.finSemana = finSemana;
        this.dias = dias;
        this.horas = horas;
        this.celdas = celdas;
    }

    public String getInicioSemana() { return inicioSemana; }
    public String getFinSemana() { return finSemana; }
    public List<String> getDias() { return dias; }
    public List<String> getHoras() { return horas; }

    public CeldaProgramacionDTO obtenerCelda(int indiceHora, int indiceDia) {
        return celdas.get(indiceHora).get(indiceDia);
    }
}