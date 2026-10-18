package una.eif206.reservas.DTO;

public class CeldaCalendarizacionDTO {

    private boolean ocupada;
    private String actividad;
    private String nombreFuncionario;

    public CeldaCalendarizacionDTO(){
        this.ocupada=false;
    }
    public CeldaCalendarizacionDTO(String actividad, String nombreFuncionario){
        this.ocupada=true;
        this.actividad=actividad;
        this.nombreFuncionario=nombreFuncionario;
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public String getNombreFuncionario() {
        return nombreFuncionario;
    }

    public void setNombreFuncionario(String nombreFuncionario) {
        this.nombreFuncionario = nombreFuncionario;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }
}
