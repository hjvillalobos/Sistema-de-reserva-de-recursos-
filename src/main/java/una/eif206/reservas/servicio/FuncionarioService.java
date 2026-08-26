// servicio/FuncionarioService.java
package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.Funcionario;
import una.eif206.reservas.persistencia.FuncionarioDAO;
import una.eif206.reservas.persistencia.FuncionarioDAOXml;
import java.util.List;

public class FuncionarioService {

    private final FuncionarioDAO funcionarioDAO;

    public FuncionarioService() { this(new FuncionarioDAOXml()); }
    public FuncionarioService(FuncionarioDAO funcionarioDAO) { this.funcionarioDAO = funcionarioDAO; }

    public List<Funcionario> listarTodos() {
        return funcionarioDAO.obtenerTodos();
    }

    public List<Funcionario> buscar(String id, String nombre) {
        return funcionarioDAO.buscar(id, nombre);
    }

    public Funcionario crear(String id, String nombre, String telefono) throws ValidacionException {
        validarDatos(id, nombre, telefono);

        if (funcionarioDAO.buscarPorId(id) != null) {
            throw new ValidacionException("Ya existe un funcionario con id " + id);
        }

        // Regla del enunciado: la clave inicial queda igual al id
        Funcionario funcionario = new Funcionario(id, id, nombre.trim(), telefono.trim());
        funcionarioDAO.guardar(funcionario);
        return funcionario;
    }

    public void modificar(String id, String nombre, String telefono) throws ValidacionException {
        validarDatos(id, nombre, telefono);

        Funcionario existente = funcionarioDAO.buscarPorId(id);
        if (existente == null) {
            throw new ValidacionException("No existe un funcionario con id " + id);
        }

        existente.setNombre(nombre.trim());
        existente.setTelefono(telefono.trim());
        funcionarioDAO.guardar(existente);
    }

    public void eliminar(String id) throws ValidacionException {
        Funcionario existente = funcionarioDAO.buscarPorId(id);
        if (existente == null) {
            throw new ValidacionException("No existe un funcionario con id " + id);
        }
        funcionarioDAO.eliminar(id);
    }

    private void validarDatos(String id, String nombre, String telefono) throws ValidacionException {
        if (id == null || id.isBlank()) {
            throw new ValidacionException("El id es obligatorio.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new ValidacionException("El nombre es obligatorio.");
        }
        if (telefono == null || telefono.isBlank()) {
            throw new ValidacionException("El teléfono es obligatorio.");
        }
    }
}