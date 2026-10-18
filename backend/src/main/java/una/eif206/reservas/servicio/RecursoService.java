package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.Recurso;
import una.eif206.reservas.persistencia.RecursoDAO;
import una.eif206.reservas.persistencia.RecursoDAOXml;
import java.util.List;

public class RecursoService {

    private final RecursoDAO recursoDAO;

    public RecursoService() { this(new RecursoDAOXml()); }
    public RecursoService(RecursoDAO recursoDAO) { this.recursoDAO = recursoDAO; }

    public List<Recurso> listarTodos() {
        return recursoDAO.obtenerTodos();
    }

    public List<Recurso> buscar(String categoriaId, String descripcion) {
        return recursoDAO.buscar(categoriaId, descripcion);
    }

    public Recurso crear(String id, String categoriaId, String descripcion) throws ValidacionException {
        validarDatos(id, categoriaId, descripcion);

        if (recursoDAO.buscarPorId(id) != null) {
            throw new ValidacionException("Ya existe un recurso con id " + id);
        }

        Recurso recurso = new Recurso(id.trim(), categoriaId, descripcion.trim());
        recursoDAO.guardar(recurso);
        return recurso;
    }

    public void modificar(String id, String categoriaId, String descripcion) throws ValidacionException {
        validarDatos(id, categoriaId, descripcion);

        Recurso existente = recursoDAO.buscarPorId(id);
        if (existente == null) {
            throw new ValidacionException("No existe un recurso con id " + id);
        }

        existente.setCategoriaId(categoriaId);
        existente.setDescripcion(descripcion.trim());
        recursoDAO.guardar(existente);
    }

    public void eliminar(String id) throws ValidacionException {
        Recurso existente = recursoDAO.buscarPorId(id);
        if (existente == null) {
            throw new ValidacionException("No existe un recurso con id " + id);
        }
        recursoDAO.eliminar(id);
    }

    private void validarDatos(String id, String categoriaId, String descripcion) throws ValidacionException {
        if (id == null || id.isBlank()) {
            throw new ValidacionException("El id (número de activo) es obligatorio.");
        }
        if (categoriaId == null || categoriaId.isBlank()) {
            throw new ValidacionException("Debe seleccionar una categoría.");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new ValidacionException("La descripción es obligatoria.");
        }
    }
}