package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.*;
import una.eif206.reservas.datos.CategoriaDatos;
import una.eif206.reservas.datos.RecursoDatos;
import una.eif206.reservas.datos.ReservaDatos;

import java.time.LocalDate;
import java.util.*;

public class EstadisticasLogica {

    private final ReservaDatos reservaDatos;
    private final RecursoDatos recursoDatos;
    private final CategoriaDatos categoriaDatos;

    public EstadisticasLogica() {
        this.reservaDatos = new ReservaDatos();
        this.recursoDatos = new RecursoDatos();
        this.categoriaDatos = new CategoriaDatos();
    }

    public EstadisticasLogica(ReservaDatos reservaDatos, RecursoDatos recursoDatos, CategoriaDatos categoriaDatos) {
        this.reservaDatos = reservaDatos;
        this.recursoDatos = recursoDatos;
        this.categoriaDatos = categoriaDatos;
    }

    public List<EstadisticaCategoriaDTO> estadisticasRecursos(String desde, String hasta) throws ValidacionException {
        validarRango(desde, hasta);
        List<ReservaDTO> reservas = obtenerReservasActivasEntre(desde, hasta);

        Map<String, Long> conteoPorCategoria = new LinkedHashMap<>();
        for (ReservaDTO reserva : reservas) {
            for (String recursoId : reserva.getRecursosAsignados()) {
                RecursoDTO recurso = recursoDatos.buscarPorId(recursoId);
                if (recurso == null) {
                    continue;
                }
                conteoPorCategoria.merge(recurso.getCategoriaId(), 1L, Long::sum);
            }
        }

        List<EstadisticaCategoriaDTO> resultado = new ArrayList<>();
        for (Map.Entry<String, Long> entrada : conteoPorCategoria.entrySet()) {
            CategoriaDTO categoria = categoriaDatos.buscarPorId(entrada.getKey());
            String descripcion = categoria != null ? categoria.getDescripcion() : entrada.getKey();
            resultado.add(new EstadisticaCategoriaDTO(entrada.getKey(), descripcion, entrada.getValue()));
        }
        resultado.sort(Comparator.comparingLong(EstadisticaCategoriaDTO::getCantidad).reversed());
        return resultado;
    }

    public List<EstadisticaSemanaDTO> estadisticasActividades(String desde, String hasta) throws ValidacionException {
        validarRango(desde, hasta);
        List<ReservaDTO> reservas = obtenerReservasActivasEntre(desde, hasta);

        Map<String, Long> conteoPorSemana = new TreeMap<>();
        for (ReservaDTO reserva : reservas) {
            String inicioSemana = ProgramacionLogica.inicioDeSemana(reserva.getFecha());
            conteoPorSemana.merge(inicioSemana, 1L, Long::sum);
        }

        List<EstadisticaSemanaDTO> resultado = new ArrayList<>();
        for (Map.Entry<String, Long> entrada : conteoPorSemana.entrySet()) {
            String inicio = entrada.getKey();
            String fin = LocalDate.parse(inicio).plusDays(6).toString();
            resultado.add(new EstadisticaSemanaDTO(inicio, fin, entrada.getValue()));
        }
        return resultado;
    }

    private List<ReservaDTO> obtenerReservasActivasEntre(String desde, String hasta) {
        List<ReservaDTO> resultado = new ArrayList<>();
        for (ReservaDTO reserva : reservaDatos.obtenerTodos()) {
            boolean enRango = reserva.getFecha().compareTo(desde) >= 0 && reserva.getFecha().compareTo(hasta) <= 0;
            boolean estaActiva = "ACTIVA".equals(reserva.getEstado());
            if (enRango && estaActiva) {
                resultado.add(reserva);
            }
        }
        return resultado;
    }

    private void validarRango(String desde, String hasta) throws ValidacionException {
        if (desde == null || desde.isBlank() || hasta == null || hasta.isBlank()) {
            throw new ValidacionException("Debe indicar las fechas 'desde' y 'hasta'.");
        }
        if (hasta.compareTo(desde) < 0) {
            throw new ValidacionException("La fecha 'hasta' no puede ser anterior a la fecha 'desde'.");
        }
    }
}