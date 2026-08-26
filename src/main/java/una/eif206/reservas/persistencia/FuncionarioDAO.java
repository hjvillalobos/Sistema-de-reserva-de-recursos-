// persistencia/FuncionarioDAO.java
package una.eif206.reservas.persistencia;

import una.eif206.reservas.modelo.Funcionario;
import java.util.List;

public interface FuncionarioDAO {
    List<Funcionario> obtenerTodos();
    Funcionario buscarPorId(String id);
    List<Funcionario> buscar(String id, String nombre);   // NUEVO: filtro por id y/o nombre
    void guardar(Funcionario funcionario);
    void eliminar(String id);
}