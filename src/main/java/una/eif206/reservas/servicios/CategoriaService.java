package una.eif206.reservas.servicios;

import una.eif206.reservas.DTO.CategoriaDTO;
import una.eif206.reservas.datos.CategoriaDatos;
import una.eif206.reservas.logica.CategoriaLogica;
import una.eif206.reservas.logica.ValidacionException;

import java.util.List;

public class CategoriaService {

    private final CategoriaLogica categoriaLogica;

    public CategoriaService() {
        this.categoriaLogica=new CategoriaLogica();
    }
    public CategoriaService(CategoriaLogica categoriaLogica) {
        this.categoriaLogica = categoriaLogica;
    }

    public List<CategoriaDTO> listarTodos() {
        return categoriaLogica.listarTodos();
    }

    public List<CategoriaDTO> buscarPorDescripcion(String descripcion) {
        return categoriaLogica.buscarPorDescripcion(descripcion);
    }

    public CategoriaDTO crear(CategoriaDTO categoria) throws ValidacionException {
        return categoriaLogica.crear(categoria);
    }

    public CategoriaDTO modificar(CategoriaDTO categoria) throws ValidacionException {
        return categoriaLogica.modificar(categoria);
    }

    public void eliminar(String id) throws ValidacionException {
        categoriaLogica.eliminar(id);
    }
}
