package una.eif206.reservas.api;

import una.eif206.reservas.excepciones.ValidacionException;
import una.eif206.reservas.DTO.MatrizCalendarizacionDTO;
import java.util.List;

/** Contrato extraido del Proyecto I; todavia no define el protocolo de red. */
public interface CalendarizacionApi {
    MatrizCalendarizacionDTO obtenerMatriz(String fecha, String categoriaId) throws ValidacionException;
}
