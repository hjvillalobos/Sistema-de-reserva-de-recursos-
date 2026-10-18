package una.eif206.reservas.api;

import una.eif206.reservas.DTO.EstadisticaCategoriaDTO;
import una.eif206.reservas.DTO.EstadisticaSemanaDTO;
import una.eif206.reservas.excepciones.ValidacionException;
import java.util.List;

/** Contrato extraido del Proyecto I; todavia no define el protocolo de red. */
public interface EstadisticasApi {
    List<EstadisticaCategoriaDTO> estadisticasRecursos(String desde, String hasta) throws ValidacionException;

    List<EstadisticaSemanaDTO> estadisticasActividades(String desde, String hasta) throws ValidacionException;
}
