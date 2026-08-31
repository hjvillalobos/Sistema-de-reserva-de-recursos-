package una.eif206.reservas.persistencia;

import una.eif206.reservas.modelo.Reserva;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaDAO {
    List<Reserva> obtenerReservas();
    List<Reserva> obtenerPorFechaYCategoria(LocalDate fecha,String idCat);
    List<Reserva> obtenerPorRangoFechas(LocalDate desde, LocalDate hasta);
    Optional<Reserva> busquedaPorId(String id);
    void guardar(Reserva recurso);
    void eliminar(String id);
}
