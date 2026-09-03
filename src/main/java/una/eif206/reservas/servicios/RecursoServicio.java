package una.eif206.reservas.servicios;

import una.eif206.reservas.DTO.RecursoDTO;
import una.eif206.reservas.logica.RecursoLogica;
import una.eif206.reservas.logica.ValidacionException;

import java.util.List;

public class RecursoServicio {
    private final RecursoLogica recursoLogica;

    public RecursoServicio(){
        this.recursoLogica=new RecursoLogica();
    }
    public RecursoServicio(RecursoLogica recursoLogica){
        this.recursoLogica=recursoLogica;
    }

    public List<RecursoDTO> obtenerTodos(){
        return recursoLogica.obtenerTodos();
    }
    public List<RecursoDTO> buscarPorCategoria(String categoriaId){
        return recursoLogica.buscarPorCategoria(categoriaId);
    }

    public RecursoDTO crear(RecursoDTO recursoDTO) throws ValidacionException{
        return recursoLogica.crear(recursoDTO);
    }

    public RecursoDTO modificar(RecursoDTO recurso)throws ValidacionException{
        return recursoLogica.modificar(recurso);
    }

    public void eliminar(String id){
        recursoLogica.eliminar(id);
    }
}
