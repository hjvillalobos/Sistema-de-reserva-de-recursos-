package una.eif206.reservas.api;

import una.eif206.reservas.DTO.RecursoDTO;
import una.eif206.reservas.excepciones.ValidacionException;
import java.util.List;

/** Contrato extraido del Proyecto I; todavia no define el protocolo de red. */
public interface RecursoApi {
    List<RecursoDTO> obtenerTodos();

    List<RecursoDTO> buscarPorCategoria(String categoriaId);

    RecursoDTO crear(RecursoDTO recursoDTO) throws ValidacionException;

    RecursoDTO modificar(RecursoDTO recurso) throws ValidacionException;

    void eliminar(String id);
}
