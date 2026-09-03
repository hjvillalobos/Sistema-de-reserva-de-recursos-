package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.CeldaProgramacionDTO;
import una.eif206.reservas.DTO.FuncionarioDTO;
import una.eif206.reservas.DTO.MatrizProgramacionDTO;
import una.eif206.reservas.DTO.ReservaDTO;
import una.eif206.reservas.datos.FuncionarioDatos;
import una.eif206.reservas.datos.ReservaDatos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProgramacionLogica {

    private final ReservaDatos reservaDatos;
    private final FuncionarioDatos funcionarioDatos;

    public ProgramacionLogica() {
        this.reservaDatos = new ReservaDatos();
        this.funcionarioDatos = new FuncionarioDatos();
    }

    public ProgramacionLogica(ReservaDatos reservaDatos, FuncionarioDatos funcionarioDatos) {
        this.reservaDatos = reservaDatos;
        this.funcionarioDatos = funcionarioDatos;
    }

    public MatrizProgramacionDTO obtenerMatrizSemana(String fechaCualquiera) throws ValidacionException {
        validarFecha(fechaCualquiera);

        String inicioSemana = inicioDeSemana(fechaCualquiera);
        String finSemana = LocalDate.parse(inicioSemana).plusDays(6).toString();

        List<String> dias = generarDias(inicioSemana);
        List<String> horas = generarHoras();
        List<ReservaDTO> reservasSemana = obtenerReservasActivasEntre(inicioSemana, finSemana);

        List<List<CeldaProgramacionDTO>> celdas = construirCeldas(horas, dias, reservasSemana);
        return new MatrizProgramacionDTO(inicioSemana, finSemana, dias, horas, celdas);
    }

    public static String inicioDeSemana(String fechaIso) {
        LocalDate fecha = LocalDate.parse(fechaIso);
        return fecha.minusDays(fecha.getDayOfWeek().getValue() - 1L).toString();
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

    private List<List<CeldaProgramacionDTO>> construirCeldas(List<String> horas, List<String> dias, List<ReservaDTO> reservas) {
        List<List<CeldaProgramacionDTO>> filas = new ArrayList<>();
        for (String hora : horas) {
            List<CeldaProgramacionDTO> fila = new ArrayList<>();
            for (String dia : dias) {
                CeldaProgramacionDTO celda = new CeldaProgramacionDTO();
                for (ReservaDTO reserva : reservas) {
                    boolean esEseDia = reserva.getFecha().equals(dia);
                    boolean enRangoHora = hora.compareTo(reserva.getHoraInicio()) >= 0
                            && hora.compareTo(reserva.getHoraFin()) < 0;
                    if (esEseDia && enRangoHora) {
                        celda.agregar(reserva.getActividad(), obtenerNombreFuncionario(reserva.getFuncionarioId()));
                    }
                }
                fila.add(celda);
            }
            filas.add(fila);
        }
        return filas;
    }

    private String obtenerNombreFuncionario(String funcionarioId) {
        FuncionarioDTO funcionario = funcionarioDatos.buscarPorId(funcionarioId);
        return funcionario != null ? funcionario.getNombre() : funcionarioId;
    }

    private void validarFecha(String fecha) throws ValidacionException {
        if (fecha == null || fecha.isBlank()) {
            throw new ValidacionException("Debe seleccionar una fecha de referencia para la semana.");
        }
    }

    private List<String> generarDias(String inicioSemana) {
        List<String> dias = new ArrayList<>();
        LocalDate inicio = LocalDate.parse(inicioSemana);
        for (int i = 0; i < 7; i++) {
            dias.add(inicio.plusDays(i).toString());
        }
        return dias;
    }

    private List<String> generarHoras() {
        List<String> horas = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            horas.add(String.format("%02d:00", h));
        }
        return horas;
    }
}