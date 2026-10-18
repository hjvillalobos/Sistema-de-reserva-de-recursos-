package una.eif206.reservas.servicios;

import una.eif206.reservas.api.ReservaApi;

import una.eif206.reservas.DTO.ReservaDTO;
import una.eif206.reservas.logica.ReservaLogica;
import una.eif206.reservas.excepciones.ValidacionException;

import java.util.List;

public class ReservaServicio implements ReservaApi {
    private final ReservaLogica reservaLogica;

    public ReservaServicio(){
        this.reservaLogica=new ReservaLogica();
    }
    public ReservaServicio(ReservaLogica reservaLogica){
        this.reservaLogica=reservaLogica;
    }

    public List<ReservaDTO>listarPorFuncionario(String funcionarioId){
        return reservaLogica.listarPorFuncionario(funcionarioId);
    }
    public ReservaDTO intentarRegistrar(String actividad, String fecha, String horaInicio, String horaFin, String funcionarioId, List<String> categoriasIds) throws ValidacionException {
        return reservaLogica.intentarRegistrar(actividad,fecha,horaInicio,horaFin,funcionarioId,categoriasIds);
    }
    public void cancelar(String reservaId,String funcionarioId) throws ValidacionException{
        reservaLogica.cancelar(reservaId,funcionarioId);
    }
}
