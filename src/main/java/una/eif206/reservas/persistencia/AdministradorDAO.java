// persistencia/AdministradorDAO.java
package una.eif206.reservas.persistencia;

import una.eif206.reservas.modelo.Usuario;
import java.util.List;
import java.util.Optional;

public interface AdministradorDAO {
    List<Usuario> obtenerTodos();
    Optional<Usuario> buscarPorId(String id);
    void actualizarClave(String id, String nuevaClave);
}