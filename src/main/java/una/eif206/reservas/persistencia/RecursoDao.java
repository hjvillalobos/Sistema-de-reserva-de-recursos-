package una.eif206.reservas.persistencia;
import una.eif206.reservas.modelo.Recurso;
import java.util.List;
import java.util.Optional;


public interface RecursoDao {
    List<Recurso> obtenerRecursos();
    List<Recurso> obtenerPorCategoria(String idCat);
    Optional<Recurso> busquedaPorId(String id);
    void guardar(Recurso recurso);
    void eliminar(String id);
}
