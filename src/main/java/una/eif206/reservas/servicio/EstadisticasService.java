package una.eif206.reservas.servicio;

import una.eif206.reservas.modelo.Categoria;
import una.eif206.reservas.modelo.Reserva;
import una.eif206.reservas.modelo.Usuario;
import una.eif206.reservas.persistencia.ReservaDAO;
import una.eif206.reservas.persistencia.ReservaDaoXml;

import java.time.LocalDate;
import java.util.*;

public class EstadisticasService {
    private final ReservaDAO reservaDao;

    public EstadisticasService() { this.reservaDao = new ReservaDaoXml(); }
    public EstadisticasService(ReservaDAO reservaDao) { this.reservaDao = reservaDao; }

    public List<EstadisticaCategoria> estadisticasRecursos(Usuario usuario, LocalDate desde, LocalDate hasta) {
        validarUsuario(usuario);
        validarRango(desde, hasta);

        List<Reserva> reservas = reservaDao.obtenerPorRangoFechas(desde, hasta);

        Map<String, Categoria> categoriasPorId = new LinkedHashMap<>();
        Map<String, Long> conteoPorId = new LinkedHashMap<>();

        for (Reserva reserva : reservas) {
            if (reserva.getRecurso() == null || reserva.getRecurso().getCategoria() == null) continue;
            Categoria categoria = reserva.getRecurso().getCategoria();
            categoriasPorId.putIfAbsent(categoria.getId(), categoria);
            conteoPorId.merge(categoria.getId(), 1L, Long::sum);
        }

        List<EstadisticaCategoria> resultado = new ArrayList<>();
        for (Map.Entry<String, Long> entrada : conteoPorId.entrySet()) {
            Categoria categoria = categoriasPorId.get(entrada.getKey());
            resultado.add(new EstadisticaCategoria(categoria.getId(), categoria.getDescripcion(), entrada.getValue()));
        }
        resultado.sort(Comparator.comparingLong(EstadisticaCategoria::getCantidad).reversed());
        return resultado;
    }

    public List<EstadisticaSemana> estadisticasActividades(Usuario usuario, LocalDate desde, LocalDate hasta) {
        validarUsuario(usuario);
        validarRango(desde, hasta);

        List<Reserva> reservas = reservaDao.obtenerPorRangoFechas(desde, hasta);

        Map<LocalDate, Long> conteoPorSemana = new TreeMap<>(); // TreeMap = queda ordenado por fecha
        for (Reserva reserva : reservas) {
            if (reserva.getFecha() == null) continue;
            LocalDate inicioSemana = ProgramacionService.inicioDeSemana(reserva.getFecha());
            conteoPorSemana.merge(inicioSemana, 1L, Long::sum);
        }

        List<EstadisticaSemana> resultado = new ArrayList<>();
        for (Map.Entry<LocalDate, Long> entrada : conteoPorSemana.entrySet()) {
            LocalDate inicio = entrada.getKey();
            resultado.add(new EstadisticaSemana(inicio, inicio.plusDays(6), entrada.getValue()));
        }
        return resultado;
    }

    private void validarUsuario(Usuario usuario) {
        if (usuario == null) throw new AccesoDenegadoException("Debe iniciar sesión para consultar estadísticas.");
    }

    private void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null)
            throw new IllegalArgumentException("Debe indicar las fechas 'desde' y 'hasta'.");
        if (hasta.isBefore(desde))
            throw new IllegalArgumentException("La fecha 'hasta' no puede ser anterior a la fecha 'desde'.");
    }
}