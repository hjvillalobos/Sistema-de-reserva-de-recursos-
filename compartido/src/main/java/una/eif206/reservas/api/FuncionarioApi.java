package una.eif206.reservas.api;

import una.eif206.reservas.DTO.FuncionarioDTO;
import una.eif206.reservas.excepciones.ValidacionException;
import java.util.List;

/** Contrato extraido del Proyecto I; todavia no define el protocolo de red. */
public interface FuncionarioApi {
    List<FuncionarioDTO> obtenerTodos();

    List<FuncionarioDTO> buscar(String idONombre);

    FuncionarioDTO crear(FuncionarioDTO funcionario) throws ValidacionException;

    FuncionarioDTO modificar(FuncionarioDTO funcionario) throws ValidacionException;

    void eliminar(String id) throws ValidacionException;
}
