package una.eif206.reservas.api;

import una.eif206.reservas.DTO.ReservaDTO;
import una.eif206.reservas.excepciones.ValidacionException;
import java.util.List;

/** Contrato extraido del Proyecto I; todavia no define el protocolo de red. */
public interface ReservaApi {
    List<ReservaDTO> listarPorFuncionario(String funcionarioId);

    ReservaDTO intentarRegistrar(String actividad, String fecha, String horaInicio, String horaFin, String funcionarioId, List<String> categoriasIds) throws ValidacionException;

    void cancelar(String reservaId,String funcionarioId) throws ValidacionException;
}
