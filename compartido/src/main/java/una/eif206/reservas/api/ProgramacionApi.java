package una.eif206.reservas.api;

import una.eif206.reservas.DTO.MatrizProgramacionDTO;
import una.eif206.reservas.excepciones.ValidacionException;

/** Contrato extraido del Proyecto I; todavia no define el protocolo de red. */
public interface ProgramacionApi {
    MatrizProgramacionDTO obtenerMatrizSemana(String fecha) throws ValidacionException;
}
