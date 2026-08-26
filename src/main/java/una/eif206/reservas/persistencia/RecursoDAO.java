package una.eif206.reservas.persistencia;

import una.eif206.reservas.modelo.Recurso;
import java.util.List;

public interface RecursoDAO {
    List<Recurso> obtenerTodos();
    List<Recurso> buscarPorCategoria(String categoriaId);
    Recurso buscarPorId(String id);
    void guardar(Recurso recurso);
    void eliminar(String id);
}