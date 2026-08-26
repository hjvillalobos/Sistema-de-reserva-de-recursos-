// persistencia/AdministradorDAO.java
package una.eif206.reservas.persistencia;

import una.eif206.reservas.modelo.Usuario;
import java.util.List;

public interface AdministradorDAO {
    List<Usuario> obtenerTodos();
    Usuario buscarPorId(String id);   // devuelve null si no existe
    void actualizarClave(String id, String nuevaClave);
}