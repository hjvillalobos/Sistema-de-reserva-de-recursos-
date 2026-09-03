package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.FuncionarioDTO;
import una.eif206.reservas.DTO.MatrizProgramacionDTO;
import una.eif206.reservas.DTO.ReservaDTO;
import una.eif206.reservas.datos.ReservaDatos;
import una.eif206.reservas.datos.FuncionarioDatos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class ProgramacionLogicaTest {
    private List<ReservaDTO> reservaDTOS;
    private List<FuncionarioDTO> funcionarioDTOS;
    private ProgramacionLogica programacionLogica;

    @BeforeEach
    void setUp(){
        funcionarioDTOS=new ArrayList<>();
        funcionarioDTOS.add(new FuncionarioDTO("111","111","Maria Hernandez","1234-5678"));

        reservaDTOS=new ArrayList<>();
        reservaDTOS.add(new ReservaDTO("RES-1","Charla tecnica","2026-08-26","08:00","09:00","111",List.of("R1"),"ACTIVA"));
        ReservaDatos reservaDatosFalso=new ReservaDatos("no-existe.xml"){
            @Override
            public List<ReservaDTO> obtenerTodos(){
                return reservaDTOS;
            }
        };
        FuncionarioDatos funcionarioDatosFalso=new FuncionarioDatos("no-existe.xml"){
            @Override
            public FuncionarioDTO buscarPorId(String id){
                for(FuncionarioDTO f: funcionarioDTOS){
                    if(f.getId().equalsIgnoreCase(id)){
                        return f;
                    }
                }
                return null;
            }
        };
        programacionLogica=new ProgramacionLogica(reservaDatosFalso,funcionarioDatosFalso);
    }

    @Test
    void obtenerMatrizSemanaSinFechaLanzaExcepcion(){
        assertThrows(ValidacionException.class, () -> programacionLogica.obtenerMatrizSemana(null));
    }
    @Test
    void inicioDeSemanaConUnLunesSeDevuelveAsiMismo(){
        assertEquals("2026-08-24", ProgramacionLogica.inicioDeSemana("2026-08-24"));

    }
    @Test
    void obtenerMatrizSemanaDevuelve7DiasY24Horas()throws ValidacionException{
        MatrizProgramacionDTO matriz=programacionLogica.obtenerMatrizSemana("2026-08-26");
        assertEquals(7,matriz.getDias().size());
        assertEquals(24,matriz.getHoras().size());
    }
    @Test
    void obtenerMatrizSemanaCalculaRangoCorrecto()throws ValidacionException{
        MatrizProgramacionDTO matriz=programacionLogica.obtenerMatrizSemana("2026-08-26");
        int indiceHora=matriz.getHoras().indexOf("08:00");
        int indiceDia=matriz.getDias().indexOf("2026-08-26");

        var celda=matriz.obtenerCelda(indiceHora,indiceDia);
        assertTrue(celda.isOcupada());
        assertEquals("Charla tecnica / Maria Hernandez",celda.getActividades().get(0));
    }
    @Test void celdaSinReservaApareceLibre() throws ValidacionException{
        MatrizProgramacionDTO matriz=programacionLogica.obtenerMatrizSemana("2026-08-26");
        int indiceHora=matriz.getHoras().indexOf("15:00");
        int indiceDia=matriz.getDias().indexOf("2026-08-26");
        assertFalse(matriz.obtenerCelda(indiceHora,indiceDia).isOcupada());
    }
}
