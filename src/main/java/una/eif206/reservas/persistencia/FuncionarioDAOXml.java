// persistencia/FuncionarioDAOXml.java
package una.eif206.reservas.persistencia;

import una.eif206.reservas.modelo.Funcionario;
import java.util.List;

public class FuncionarioDAOXml implements FuncionarioDAO {

    public static final String RUTA_DEFECTO = "data/funcionarios.xml";
    private final String ruta;

    public FuncionarioDAOXml() { this(RUTA_DEFECTO); }
    public FuncionarioDAOXml(String ruta) { this.ruta = ruta; }

    @Override
    public List<Funcionario> obtenerTodos() {
        return cargar().getFuncionarios();
    }

    @Override
    public Funcionario buscarPorId(String id) {
        List<Funcionario> lista = obtenerTodos();
        for (Funcionario f : lista) {
            if (f.getId().equalsIgnoreCase(id)) {
                return f;
            }
        }
        return null;
    }

    @Override
    public void guardar(Funcionario funcionario) {
        ListaFuncionarios lista = cargar();

        // Si ya existe uno con ese id, lo quitamos antes de volver a agregarlo (actualizar)
        Funcionario existente = null;
        for (Funcionario f : lista.getFuncionarios()) {
            if (f.getId().equalsIgnoreCase(funcionario.getId())) {
                existente = f;
                break;
            }
        }
        if (existente != null) {
            lista.getFuncionarios().remove(existente);
        }

        lista.getFuncionarios().add(funcionario);
        XmlDaoUtil.guardar(ruta, lista, ListaFuncionarios.class);
    }

    @Override
    public void eliminar(String id) {
        ListaFuncionarios lista = cargar();
        Funcionario aEliminar = null;
        for (Funcionario f : lista.getFuncionarios()) {
            if (f.getId().equalsIgnoreCase(id)) {
                aEliminar = f;
                break;
            }
        }
        if (aEliminar != null) {
            lista.getFuncionarios().remove(aEliminar);
        }
        XmlDaoUtil.guardar(ruta, lista, ListaFuncionarios.class);
    }

    private ListaFuncionarios cargar() {
        return XmlDaoUtil.cargar(ruta, ListaFuncionarios.class);
    }
}