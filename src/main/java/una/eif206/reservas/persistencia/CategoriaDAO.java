// persistencia/CategoriaDAO.java
package una.eif206.reservas.persistencia;

import una.eif206.reservas.modelo.Categoria;
import java.util.List;

public interface CategoriaDAO {
    List<Categoria> obtenerTodos();
    List<Categoria> buscarPorDescripcion(String texto);
    Categoria buscarPorId(String id);
    void guardar(Categoria categoria);
    void eliminar(String id);
    String generarSiguienteId();
}