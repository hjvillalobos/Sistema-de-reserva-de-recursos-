// persistencia/FuncionarioDAOXml.java
package una.eif206.reservas.persistencia;

import una.eif206.reservas.modelo.Funcionario;
import java.util.List;
import java.util.Optional;

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
    public Optional<Funcionario> buscarPorId(String id) {
        return obtenerTodos().stream()
                .filter(f -> f.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    @Override
    public void guardar(Funcionario funcionario) {
        ListaFuncionarios lista = cargar();
        lista.getFuncionarios().removeIf(f -> f.getId().equalsIgnoreCase(funcionario.getId()));
        lista.getFuncionarios().add(funcionario);
        XmlDaoUtil.guardar(ruta, lista, ListaFuncionarios.class);
    }

    @Override
    public void eliminar(String id) {
        ListaFuncionarios lista = cargar();
        lista.getFuncionarios().removeIf(f -> f.getId().equalsIgnoreCase(id));
        XmlDaoUtil.guardar(ruta, lista, ListaFuncionarios.class);
    }

    private ListaFuncionarios cargar() {
        return XmlDaoUtil.cargar(ruta, ListaFuncionarios.class);
    }
}