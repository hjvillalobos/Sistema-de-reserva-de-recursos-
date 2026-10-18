package una.eif206.reservas.api;

import una.eif206.reservas.DTO.CategoriaDTO;
import una.eif206.reservas.excepciones.ValidacionException;
import java.util.List;

/** Contrato extraido del Proyecto I; todavia no define el protocolo de red. */
public interface CategoriaApi {
    List<CategoriaDTO> listarTodos();

    List<CategoriaDTO> buscarPorDescripcion(String descripcion);

    CategoriaDTO crear(CategoriaDTO categoria) throws ValidacionException;

    CategoriaDTO modificar(CategoriaDTO categoria) throws ValidacionException;

    void eliminar(String id) throws ValidacionException;
}
