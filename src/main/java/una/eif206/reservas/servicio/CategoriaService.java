// servicio/CategoriaService.java
package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.Categoria;
import una.eif206.reservas.persistencia.CategoriaDAO;
import una.eif206.reservas.persistencia.CategoriaDAOXml;
import java.util.List;

public class CategoriaService {

    private final CategoriaDAO categoriaDAO;

    public CategoriaService() { this(new CategoriaDAOXml()); }
    public CategoriaService(CategoriaDAO categoriaDAO) { this.categoriaDAO = categoriaDAO; }

    public List<Categoria> listarTodos() {
        return categoriaDAO.obtenerTodos();
    }

    public List<Categoria> buscarPorDescripcion(String texto) {
        return categoriaDAO.buscarPorDescripcion(texto);
    }

    public Categoria crear(String descripcion) throws ValidacionException {
        validarDescripcion(descripcion);
        String nuevoId = categoriaDAO.generarSiguienteId();
        Categoria categoria = new Categoria(nuevoId, descripcion.trim());
        categoriaDAO.guardar(categoria);
        return categoria;
    }

    public void modificar(String id, String nuevaDescripcion) throws ValidacionException {
        validarDescripcion(nuevaDescripcion);
        Categoria existente = categoriaDAO.buscarPorId(id);
        if (existente == null) {
            throw new ValidacionException("No existe una categoría con id " + id);
        }
        existente.setDescripcion(nuevaDescripcion.trim());
        categoriaDAO.guardar(existente);
    }

    public void eliminar(String id) throws ValidacionException {
        Categoria existente = categoriaDAO.buscarPorId(id);
        if (existente == null) {
            throw new ValidacionException("No existe una categoría con id " + id);
        }
        categoriaDAO.eliminar(id);
    }

    private void validarDescripcion(String descripcion) throws ValidacionException {
        if (descripcion == null || descripcion.isBlank()) {
            throw new ValidacionException("La descripción no puede estar vacía.");
        }
    }
}
