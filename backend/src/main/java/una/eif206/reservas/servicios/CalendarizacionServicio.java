package una.eif206.reservas.servicios;

import una.eif206.reservas.api.CalendarizacionApi;

import una.eif206.reservas.logica.CalendarizacionLogica;
import una.eif206.reservas.excepciones.ValidacionException;
import una.eif206.reservas.DTO.MatrizCalendarizacionDTO;

import java.util.List;

public class CalendarizacionServicio implements CalendarizacionApi {

    private final CalendarizacionLogica calendarizacionLogica;

    public CalendarizacionServicio(){
        this.calendarizacionLogica=new CalendarizacionLogica();
    }
    public CalendarizacionServicio(CalendarizacionLogica calendarizacionLogica){
        this.calendarizacionLogica=calendarizacionLogica;
    }

    public MatrizCalendarizacionDTO obtenerMatriz(String fecha, String categoriaId)throws ValidacionException{
        return calendarizacionLogica.obtenerMatriz(fecha,categoriaId);
    }
}
