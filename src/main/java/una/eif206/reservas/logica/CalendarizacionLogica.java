package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.CeldaCalendarizacionDTO;
import una.eif206.reservas.DTO.FuncionarioDTO;
import una.eif206.reservas.DTO.MatrizCalendarizacionDTO;
import una.eif206.reservas.DTO.RecursoDTO;
import una.eif206.reservas.DTO.ReservaDTO;
import una.eif206.reservas.datos.RecursoDatos;
import una.eif206.reservas.datos.ReservaDatos;
import una.eif206.reservas.datos.FuncionarioDatos;

import java.util.List;
import java.util.ArrayList;


public class CalendarizacionLogica {
    private final RecursoDatos recursoDatos;
    private final ReservaDatos reservaDatos;
    private final FuncionarioDatos funcionarioDatos;

    public CalendarizacionLogica(){
        this.reservaDatos=new ReservaDatos();
        this.funcionarioDatos=new FuncionarioDatos();
        this.recursoDatos=new RecursoDatos();
    }

    public CalendarizacionLogica(RecursoDatos recursoDatos, ReservaDatos reservaDatos, FuncionarioDatos funcionarioDatos){
        this.reservaDatos=reservaDatos;
        this.recursoDatos=recursoDatos;
        this.funcionarioDatos=funcionarioDatos;
    }

    public MatrizCalendarizacionDTO obtenerMatriz(String fecha, String categoriaId)throws ValidacionException{
        validarDatos(fecha,categoriaId);

        List<RecursoDTO> recursos=recursoDatos.buscarPorCategoria(categoriaId);
        List<ReservaDTO> reservasDelDia=obtenerReservasActivasDelDia(fecha);
        List<String> horas= generarHoras();

        List<List<CeldaCalendarizacionDTO>> celdas=construirCeldas(horas,recursos,reservasDelDia);
        return new MatrizCalendarizacionDTO(horas,recursos,celdas);
    }

    private List<ReservaDTO>obtenerReservasActivasDelDia(String fecha){
        List<ReservaDTO> resultado=new ArrayList<>();
        for(ReservaDTO reserva: reservaDatos.obtenerTodos()){
            boolean esDelDia=reserva.getFecha().equals(fecha);
            boolean estaActiva="ACTIVA".equals(reserva.getEstado());
            if(esDelDia&&estaActiva){
                resultado.add(reserva);
            }
        }
        return resultado;
    }

    private List<List<CeldaCalendarizacionDTO>> construirCeldas(List<String>horas, List<RecursoDTO> recursos, List<ReservaDTO> reservas){
        List<List<CeldaCalendarizacionDTO>> filas=new ArrayList<>();

        for(String hora:horas){
            List<CeldaCalendarizacionDTO> fila=new ArrayList<>();
            for(RecursoDTO recurso: recursos){
                ReservaDTO reserva=buscarReserva(hora,recurso,reservas);
                if(reserva!=null){
                    String nombreFuncionario=obtenerNombreFuncionario(reserva.getFuncionarioId());
                    fila.add(new CeldaCalendarizacionDTO(reserva.getActividad(),nombreFuncionario));
                }else{
                    fila.add(new CeldaCalendarizacionDTO());
                }
            }
            filas.add(fila);
        }
        return filas;
    }
    private ReservaDTO buscarReserva(String hora, RecursoDTO recursoDTO,List<ReservaDTO> reservas){
        for(ReservaDTO reserva:reservas){
            boolean ocupaEsteRecurso= reserva.getRecursosAsignados().contains(recursoDTO.getId());
            boolean estanEnRango= hora.compareTo(reserva.getHoraInicio())>=0 &&hora.compareTo(reserva.getHoraFin())<0;
            if(ocupaEsteRecurso&&estanEnRango){
                return reserva;
            }
        }
        return null;
    }
    private String obtenerNombreFuncionario(String funcionarioId){
        FuncionarioDTO funcionario=funcionarioDatos.buscarPorId(funcionarioId);
        if(funcionario!=null){
            return funcionario.getNombre();
        }else{
            return funcionarioId;
        }

    }
    private void validarDatos(String fecha, String categoriaId) throws ValidacionException{
        if(fecha==null||fecha.isBlank()){
            throw new ValidacionException("Debe seleccionar una fecha.");
        }
        if(categoriaId==null||categoriaId.isBlank()){
            throw new ValidacionException("Debe seleccionar una categoria.");
        }
    }
    private List<String> generarHoras(){
        List<String> horas=new ArrayList<>();
        for(int h=0; h<24; h++){
            horas.add(String.format("%02d:00",h));
        }
        return horas;
    }
}
