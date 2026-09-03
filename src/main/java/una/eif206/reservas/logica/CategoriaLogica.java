package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.CategoriaDTO;
import una.eif206.reservas.datos.CategoriaDatos;

import java.util.List;

public class CategoriaLogica {
    private final CategoriaDatos categoriaDatos;

    public CategoriaLogica() {
        this.categoriaDatos=new CategoriaDatos();
    }
    public CategoriaLogica(CategoriaDatos categoriaDatos) {
        this.categoriaDatos = categoriaDatos;
    }

    public List<CategoriaDTO> listarTodos() {

        return categoriaDatos.obtenerTodos();
    }

    public List<CategoriaDTO> buscarPorDescripcion(String descripcion) {
        return categoriaDatos.buscarPorDescripcion(descripcion);
    }

    public CategoriaDTO crear(CategoriaDTO categoria) throws ValidacionException {
        validarDescripcion(categoria);

        categoria.setId(generarSiguienteId());
        categoriaDatos.guardar(categoria);
        return categoria;
    }

    public CategoriaDTO modificar(CategoriaDTO categoria) throws ValidacionException {
        validarDescripcion(categoria);

        if (categoriaDatos.buscarPorId(categoria.getId()) == null) {
            throw new ValidacionException("No existe un funcionario con id " + categoria.getId());
        }
        categoriaDatos.guardar(categoria);
        return categoria;
    }

    public void eliminar(String id) throws ValidacionException {
        categoriaDatos.eliminar(id);
    }

    public String generarSiguienteId(){
        return "CAT-"+String.format("%06d",categoriaDatos.obtenerTodos().size()+1);
    }

    private void validarDescripcion(CategoriaDTO categoria) throws ValidacionException {
        if (categoria.getDescripcion() == null || categoria.getDescripcion().isBlank()) {
            throw new ValidacionException("La descripcion no puede estar vacia.");
        }
    }
}
