package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.FuncionarioDTO;
import una.eif206.reservas.datos.FuncionarioDatos;

import java.util.ArrayList;
import java.util.List;

public class FuncionarioLogica {

    private final FuncionarioDatos funcionarioDatos;

    public FuncionarioLogica() {
        this.funcionarioDatos=new FuncionarioDatos();
    }
    public FuncionarioLogica(FuncionarioDatos funcionarioDatos) {
        this.funcionarioDatos = funcionarioDatos;
    }

    public List<FuncionarioDTO> listarTodos() {

        return funcionarioDatos.obtenerTodos();
    }

    public List<FuncionarioDTO> buscar(String idONombre) {
        if(idONombre==null||idONombre.isBlank()){
            return funcionarioDatos.obtenerTodos();
        }
        FuncionarioDTO porid=funcionarioDatos.buscarPorId(idONombre);
        if(porid!=null){
            List<FuncionarioDTO>resultado=new ArrayList<>();
            resultado.add(porid);
            return resultado;
        }
        return funcionarioDatos.buscarPorNombre(idONombre);
    }

    public FuncionarioDTO crear(FuncionarioDTO funcionario) throws ValidacionException {
        validarDatos(funcionario);

        if (funcionarioDatos.buscarPorId(funcionario.getId()) != null) {
            throw new ValidacionException("Ya existe un funcionario con id " + funcionario.getId());
        }
        funcionario.setClave(funcionario.getId());
        funcionarioDatos.guardar(funcionario);
        return funcionario;
    }

    public FuncionarioDTO modificar(FuncionarioDTO funcionario) throws ValidacionException {
        validarDatos(funcionario);

        FuncionarioDTO existente = funcionarioDatos.buscarPorId(funcionario.getId());
        if (existente == null) {
            throw new ValidacionException("No existe un funcionario con id " + funcionario.getId());
        }
        funcionario.setClave(existente.getClave());
        funcionarioDatos.guardar(funcionario);
        return funcionario;
    }

    public void eliminar(String id) throws ValidacionException {
        funcionarioDatos.eliminar(id);
    }

    private void validarDatos(FuncionarioDTO funcionario) throws ValidacionException {
        if (funcionario.getId() == null || funcionario.getId().isBlank()) {
            throw new ValidacionException("El id es obligatorio.");
        }
        if (funcionario.getNombre() == null || funcionario.getNombre().isBlank()) {
            throw new ValidacionException("El nombre es obligatorio.");
        }
        if (funcionario.getTelefono() == null || funcionario.getTelefono().isBlank()) {
            throw new ValidacionException("El teléfono es obligatorio.");
        }
    }

}
