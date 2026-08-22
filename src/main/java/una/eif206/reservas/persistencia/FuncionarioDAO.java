// persistencia/FuncionarioDAO.java
package una.eif206.reservas.persistencia;

import una.eif206.reservas.modelo.Funcionario;
import java.util.List;
import java.util.Optional;

public interface FuncionarioDAO {
    List<Funcionario> obtenerTodos();
    Optional<Funcionario> buscarPorId(String id);
    void guardar(Funcionario funcionario);
    void eliminar(String id);
}