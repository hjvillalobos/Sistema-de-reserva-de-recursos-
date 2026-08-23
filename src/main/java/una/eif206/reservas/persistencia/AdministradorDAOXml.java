// persistencia/AdministradorDAOXml.java
package una.eif206.reservas.persistencia;

import una.eif206.reservas.modelo.Rol;
import una.eif206.reservas.modelo.Usuario;
import java.io.File;
import java.util.List;
import java.util.Optional;

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
    public List<Usuario> obtenerTodos() { return cargar().getAdministradores(); }

    @Override
    public Optional<Usuario> buscarPorId(String id) {
        return obtenerTodos().stream()
                .filter(a -> a.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    @Override
    public void actualizarClave(String id, String nuevaClave) {
        ListaAdministradores lista = cargar();
        lista.getAdministradores().stream()
                .filter(a -> a.getId().equalsIgnoreCase(id))
                .findFirst()
                .ifPresent(a -> a.setClave(nuevaClave));
        XmlDaoUtil.guardar(ruta, lista, ListaAdministradores.class);
    }

    private ListaAdministradores cargar() {
        return XmlDaoUtil.cargar(ruta, ListaAdministradores.class);
    }
}