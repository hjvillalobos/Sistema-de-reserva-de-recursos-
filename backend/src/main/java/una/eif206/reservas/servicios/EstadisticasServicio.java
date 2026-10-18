package una.eif206.reservas.servicios;

import una.eif206.reservas.api.EstadisticasApi;

import una.eif206.reservas.DTO.EstadisticaCategoriaDTO;
import una.eif206.reservas.DTO.EstadisticaSemanaDTO;
import una.eif206.reservas.logica.EstadisticasLogica;
import una.eif206.reservas.excepciones.ValidacionException;

import java.util.List;

public class EstadisticasServicio implements EstadisticasApi {

    private final EstadisticasLogica estadisticasLogica;

    public EstadisticasServicio() {
        this.estadisticasLogica = new EstadisticasLogica();
    }

    public EstadisticasServicio(EstadisticasLogica estadisticasLogica) {
        this.estadisticasLogica = estadisticasLogica;
    }

    public List<EstadisticaCategoriaDTO> estadisticasRecursos(String desde, String hasta) throws ValidacionException {
        return estadisticasLogica.estadisticasRecursos(desde, hasta);
    }

    public List<EstadisticaSemanaDTO> estadisticasActividades(String desde, String hasta) throws ValidacionException {
        return estadisticasLogica.estadisticasActividades(desde, hasta);
    }
}