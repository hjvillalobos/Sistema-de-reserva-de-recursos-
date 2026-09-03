package una.eif206.reservas.servicios;

import una.eif206.reservas.DTO.MatrizProgramacionDTO;
import una.eif206.reservas.logica.ProgramacionLogica;
import una.eif206.reservas.logica.ValidacionException;

public class ProgramacionServicio {

    private final ProgramacionLogica programacionLogica;

    public ProgramacionServicio() {
        this.programacionLogica = new ProgramacionLogica();
    }

    public ProgramacionServicio(ProgramacionLogica programacionLogica) {
        this.programacionLogica = programacionLogica;
    }

    public MatrizProgramacionDTO obtenerMatrizSemana(String fecha) throws ValidacionException {
        return programacionLogica.obtenerMatrizSemana(fecha);
    }
}