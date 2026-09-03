package una.eif206.reservas.servicios;

import una.eif206.reservas.DTO.FuncionarioDTO;
import una.eif206.reservas.logica.FuncionarioLogica;
import una.eif206.reservas.logica.ValidacionException;

import java.util.List;

public class FuncionarioServicio {

    private final FuncionarioLogica funcionarioLogica;

    public FuncionarioServicio(){
        this.funcionarioLogica=new FuncionarioLogica();
    }
    public FuncionarioServicio(FuncionarioLogica funcionarioLogica){
        this.funcionarioLogica=funcionarioLogica;
    }
    public List<FuncionarioDTO> obtenerTodos() {

        return funcionarioLogica.listarTodos();
    }

    public List<FuncionarioDTO> buscar(String idONombre) {
        return funcionarioLogica.buscar(idONombre);
    }

    public FuncionarioDTO crear(FuncionarioDTO funcionario) throws ValidacionException {
        return funcionarioLogica.crear(funcionario);
    }

    public FuncionarioDTO modificar(FuncionarioDTO funcionario) throws ValidacionException {
        return funcionarioLogica.modificar(funcionario);
    }

    public void eliminar(String id) throws ValidacionException {
        funcionarioLogica.eliminar(id);
    }
}
