package una.eif206.reservas.logica;

import una.eif206.reservas.excepciones.ValidacionException;

import una.eif206.reservas.DTO.RecursoDTO;
import una.eif206.reservas.datos.RecursoDatos;
import java.util.List;
public class RecursoLogica {
    private final RecursoDatos recursoDatos;

    public RecursoLogica(){
        this.recursoDatos=new RecursoDatos();
    }
    public RecursoLogica(RecursoDatos recursoDatos){
        this.recursoDatos=recursoDatos;
    }

    public List<RecursoDTO> obtenerTodos(){
        return recursoDatos.obtenerTodos();
    }
    public List<RecursoDTO> buscarPorCategoria(String categoriaId){
        return recursoDatos.buscarPorCategoria(categoriaId);
    }

    public RecursoDTO crear(RecursoDTO recursoDTO) throws ValidacionException{
        validarDatos(recursoDTO);
        if(recursoDatos.buscarPorId(recursoDTO.getId())!=null){
            throw new ValidacionException("Ya existe un recurso con ese id.");
        }
        recursoDatos.crear(recursoDTO);
        return recursoDTO;
    }

    public RecursoDTO modificar(RecursoDTO recurso)throws ValidacionException{
        validarDatos(recurso);

        if (recursoDatos.buscarPorId(recurso.getId()) == null) {
            throw new ValidacionException("No existe un recurso con id " + recurso.getId());
        }
        recursoDatos.modificar(recurso);
        return recurso;
    }

    public void eliminar(String id){
        recursoDatos.eliminar(id);
    }

    private void validarDatos(RecursoDTO recurso) throws ValidacionException {
        if (recurso == null) throw new ValidacionException("Debe indicar un recurso.");
        if (recurso.getId() == null || recurso.getId().isBlank()) {
            throw new ValidacionException("El id (número de activo) es obligatorio.");
        }
        if (recurso.getCategoriaId() == null || recurso.getCategoriaId().isBlank()) {
            throw new ValidacionException("Debe seleccionar una categoría.");
        }
        if (recurso.getDescripcion() == null || recurso.getDescripcion().isBlank()) {
            throw new ValidacionException("La descripción es obligatoria.");
        }
        if (recurso.getId().length() > 100 || recurso.getCategoriaId().length() > 100 || recurso.getDescripcion().length() > 255) {
            throw new ValidacionException("Los identificadores admiten hasta 100 caracteres y la descripción hasta 255.");
        }
    }


}
