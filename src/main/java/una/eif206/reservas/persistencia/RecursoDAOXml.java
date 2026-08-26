package una.eif206.reservas.persistencia;

import una.eif206.reservas.modelo.Recurso;
import java.util.ArrayList;
import java.util.List;

public class RecursoDAOXml implements RecursoDAO {

    public static final String RUTA_DEFECTO = "data/recursos.xml";
    private final String ruta;

    public RecursoDAOXml() { this(RUTA_DEFECTO); }
    public RecursoDAOXml(String ruta) { this.ruta = ruta; }

    @Override
    public List<Recurso> obtenerTodos() {
        return cargar().getRecursos();
    }

    @Override
    public List<Recurso> buscarPorCategoria(String categoriaId) {
        List<Recurso> resultado = new ArrayList<>();
        for (Recurso r : obtenerTodos()) {
            if (r.getCategoriaId().equalsIgnoreCase(categoriaId)) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    @Override
    public Recurso buscarPorId(String id) {
        for (Recurso r : obtenerTodos()) {
            if (r.getId().equalsIgnoreCase(id)) {
                return r;
            }
        }
        return null;
    }

    @Override
    public void guardar(Recurso recurso) {
        ListaRecursos lista = cargar();
        Recurso existente = null;
        for (Recurso r : lista.getRecursos()) {
            if (r.getId().equalsIgnoreCase(recurso.getId())) {
                existente = r;
                break;
            }
        }
        if (existente != null) {
            lista.getRecursos().remove(existente);
        }
        lista.getRecursos().add(recurso);
        XmlDaoUtil.guardar(ruta, lista, ListaRecursos.class);
    }

    @Override
    public void eliminar(String id) {
        ListaRecursos lista = cargar();
        Recurso aEliminar = null;
        for (Recurso r : lista.getRecursos()) {
            if (r.getId().equalsIgnoreCase(id)) {
                aEliminar = r;
                break;
            }
        }
        if (aEliminar != null) {
            lista.getRecursos().remove(aEliminar);
        }
        XmlDaoUtil.guardar(ruta, lista, ListaRecursos.class);
    }

    private ListaRecursos cargar() {
        return XmlDaoUtil.cargar(ruta, ListaRecursos.class);
    }
}