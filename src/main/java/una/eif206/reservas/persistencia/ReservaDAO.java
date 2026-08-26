package una.eif206.reservas.persistencia;

import una.eif206.reservas.modelo.Reserva;
import java.util.List;

public interface ReservaDAO {
    List<Reserva> obtenerTodos();
    Reserva buscarPorId(String id);
    List<Reserva> buscarPorFuncionario(String funcionarioId);
    void guardar(Reserva reserva);
    String generarSiguienteId();
}