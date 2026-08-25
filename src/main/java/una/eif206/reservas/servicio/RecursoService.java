package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.Recurso;
import una.eif206.reservas.modelo.Rol;
import una.eif206.reservas.modelo.Usuario;

import una.eif206.reservas.persistencia.RecursoDao;
import una.eif206.reservas.persistencia.RecursoDAOXml;

import java.util.List;
import java.util.Optional;

public class RecursoService {

    private final RecursoDao recursoDao;

    public RecursoService(){
        this.recursoDao=new RecursoDAOXml();
    }
    public RecursoService(RecursoDao recursoDao){
        this.recursoDao=recursoDao;
    }
    public List<Recurso> listaRecursos(Usuario usuario){
        validarAdmin(usuario);
        return recursoDao.obtenerRecursos();
    }
    public List<Recurso> listaPorCategoria(Usuario usuario, String idCat){
        validarAdmin(usuario);
        return recursoDao.obtenerPorCategoria(idCat);
    }
    public void crear(Usuario usuario, Recurso recurso){
        validarAdmin(usuario);
        validarDatos(recurso);
        Optional<Recurso> existente=recursoDao.busquedaPorId(recurso.getId());
        if(existente.isPresent()){
            throw new IllegalArgumentException("Recurso previamente existente con ese id.");
        }
        recursoDao.guardar(recurso);
    }
    public void actualizar(Usuario usuario, Recurso recurso){
        validarAdmin(usuario);
        validarDatos(recurso);
        recursoDao.busquedaPorId(recurso.getId()).orElseThrow(()->new IllegalArgumentException("No hay un recurso con el id digitado."));
        recursoDao.guardar(recurso);
    }
    public void eliminar(Usuario usuario, String id){
        try{
            validarAdmin(usuario);
            recursoDao.eliminar(id);
        }catch (AccesoDenegadoException error){
            throw error;
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al eliminar el recurso:"+id);
        }

    }
    private void validarAdmin(Usuario usuario){
        if(usuario==null || usuario.getRol()!= Rol.ADMINISTRADOR){
            throw new AccesoDenegadoException("Solo un administrador puede gestionar esta funcion.");
        }
    }
    private void validarDatos(Recurso recurso){
        if(recurso.getId()==null|| recurso.getId().isBlank()){
            throw new IllegalArgumentException("El id del recurso no puede estar vacio.");
        }
        if(recurso.getDescripcion()==null|| recurso.getDescripcion().isBlank()){
            throw new IllegalArgumentException("La descripcion del recurso no puede estar vacio.");
        }
        if(recurso.getCategoria()==null){
            throw new IllegalArgumentException("El recurso debe pertencer a una categoria.");
        }
    }
}
