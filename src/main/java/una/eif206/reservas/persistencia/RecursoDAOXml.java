package una.eif206.reservas.persistencia;
import una.eif206.reservas.modelo.Recurso;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RecursoDAOXml implements RecursoDao{
    public static final String RUTA_DEFECTO = "data/recursos.xml";
    private final String ruta;

    public RecursoDAOXml() { this(RUTA_DEFECTO); }
    public RecursoDAOXml(String ruta) { this.ruta = ruta; }

    @Override
    public List<Recurso> obtenerRecursos() {
        return cargar().getRecursos();
    }

    @Override
    public Optional<Recurso> busquedaPorId(String id) {
        return obtenerRecursos().stream()
                .filter(f -> f.getId().equalsIgnoreCase(id))
                .findFirst();

    }

    @Override
    public List<Recurso> obtenerPorCategoria(String idCat) {
        return obtenerRecursos().stream()
                .filter(r->r.getCategoria()!=null && r.getCategoria().getId().equalsIgnoreCase(idCat))
                .collect(Collectors.toList());
    }

    @Override
    public void guardar(Recurso recurso) {
        ListaRecursos lista = cargar();
        lista.getRecursos().removeIf(f -> f.getId().equalsIgnoreCase(recurso.getId()));
        lista.getRecursos().add(recurso);
        XmlDaoUtil.guardar(ruta, lista, ListaRecursos.class);
    }

    @Override
    public void eliminar(String id) {
        ListaRecursos lista = cargar();
        lista.getRecursos().removeIf(f -> f.getId().equalsIgnoreCase(id));
        XmlDaoUtil.guardar(ruta, lista, ListaRecursos.class);
    }

    private ListaRecursos cargar() {
        return XmlDaoUtil.cargar(ruta, ListaRecursos.class);
    }
}
