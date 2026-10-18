
package una.eif206.reservas.persistencia;

import una.eif206.reservas.modelo.Rol;
import una.eif206.reservas.modelo.Usuario;
import java.io.File;
import java.util.List;

public class AdministradorDAOXml implements AdministradorDAO {

    public static final String RUTA_DEFECTO = "data/administradores.xml";
    private final String ruta;

    public AdministradorDAOXml() { this(RUTA_DEFECTO); }

    public AdministradorDAOXml(String ruta) {
        this.ruta = ruta;
        if (!new File(ruta).exists()) {
            // Semilla: admin/admin, para poder ingresar la primera vez
            ListaAdministradores inicial = new ListaAdministradores();
            inicial.getAdministradores().add(new Usuario("admin", "admin", Rol.ADMINISTRADOR));
            XmlDaoUtil.guardar(ruta, inicial, ListaAdministradores.class);
        }
    }

    @Override
    public List<Usuario> obtenerTodos() {
        return cargar().getAdministradores();
    }

    @Override
    public Usuario buscarPorId(String id) {
        List<Usuario> lista = obtenerTodos();
        for (Usuario u : lista) {
            if (u.getId().equalsIgnoreCase(id)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public void actualizarClave(String id, String nuevaClave) {
        ListaAdministradores lista = cargar();
        for (Usuario u : lista.getAdministradores()) {
            if (u.getId().equalsIgnoreCase(id)) {
                u.setClave(nuevaClave);
                break;
            }
        }
        XmlDaoUtil.guardar(ruta, lista, ListaAdministradores.class);
    }

    private ListaAdministradores cargar() {
        return XmlDaoUtil.cargar(ruta, ListaAdministradores.class);
    }
}