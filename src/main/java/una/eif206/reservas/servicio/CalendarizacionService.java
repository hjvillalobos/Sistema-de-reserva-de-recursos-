package una.eif206.reservas.servicio;
import una.eif206.reservas.modelo.Recurso;
import una.eif206.reservas.modelo.Reserva;
import una.eif206.reservas.modelo.Usuario;

import una.eif206.reservas.persistencia.RecursoDAO;
import una.eif206.reservas.persistencia.RecursoDAOXml;
import una.eif206.reservas.persistencia.ReservaDaoXml;
import una.eif206.reservas.persistencia.ReservaDAO;

import java.time.LocalTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class CalendarizacionService {
    private final RecursoDAO recursoDao;
    private final ReservaDAO reservaDao;

    public CalendarizacionService(){
        this.recursoDao=new RecursoDAOXml();
        this.reservaDao=new ReservaDaoXml();
    }
    public CalendarizacionService(ReservaDAO reserva, RecursoDAO recurso){
        this.recursoDao=recurso;
        this.reservaDao=reserva;
    }
    public MatrizDeCalendarizacion obtenerMatriz(Usuario usuario, LocalDate fecha, String idCat){
        validarDatos(fecha, idCat);
        List<Recurso> recursos=recursoDao.obtenerPorCategoria(idCat);
        List<Reserva> reservas=reservaDao.obtenerPorFechaYCategoria(fecha,idCat);
        List<LocalTime> horas=generarHoras();
        return new MatrizDeCalendarizacion(horas,recursos,reservas);
    }
    private void validarDatos(LocalDate fecha, String idCat){
        if(fecha==null){
            throw new IllegalArgumentException("La reserva debe tener una fecha definida.");
        }
        if(idCat==null||idCat.isBlank()){
            throw new IllegalArgumentException("La reserva debe tener una categoria definida.");
        }
    }
    private List<LocalTime> generarHoras(){
        List<LocalTime> horas=new ArrayList<>();
        for(int h=0; h<24;h++){
            horas.add(LocalTime.of(h,0));
        }
        return horas;
    }

}
