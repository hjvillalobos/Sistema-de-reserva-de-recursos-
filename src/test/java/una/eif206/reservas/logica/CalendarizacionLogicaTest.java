package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.MatrizCalendarizacionDTO;
import una.eif206.reservas.DTO.RecursoDTO;
import una.eif206.reservas.DTO.ReservaDTO;
import una.eif206.reservas.DTO.FuncionarioDTO;
import una.eif206.reservas.datos.RecursoDatos;
import una.eif206.reservas.datos.ReservaDatos;
import una.eif206.reservas.datos.FuncionarioDatos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CalendarizacionLogicaTest {
    private List<RecursoDTO> recursos;
    private List<ReservaDTO> reservas;
    private List<FuncionarioDTO> funcionarios;
    private CalendarizacionLogica calendarizacionLogica;

    @BeforeEach
    void setUp() {
        recursos = new ArrayList<>();
        recursos.add(new RecursoDTO("238715", "CAT-000001", "Laptop #238715"));
        recursos.add(new RecursoDTO("34343", "CAT-000001", "Sala 1"));

        funcionarios = new ArrayList<>();
        funcionarios.add(new FuncionarioDTO("111", "111", "Joel Ramirez", "8888-0000"));

        reservas = new ArrayList<ReservaDTO>();
        reservas.add(new ReservaDTO("RES-000001", "Jugar futbol", "2026-08-26", "09:00", "11:00", "111", List.of("238715"), "ACTIVA"));

        RecursoDatos recursoDatosFalso = new RecursoDatos("no-existe.xml") {
            @Override
            public List<RecursoDTO> buscarPorCategoria(String categoriaId) {
                List<RecursoDTO> resultado = new ArrayList<>();
                for (RecursoDTO recurso : recursos) {
                    if (recurso.getCategoriaId().equalsIgnoreCase(categoriaId)) {
                        resultado.add(recurso);
                    }
                }
                return resultado;
            }
        };

        ReservaDatos reservaDatosFalso = new ReservaDatos("no-existe.xml") {
            @Override
            public List<ReservaDTO> obtenerTodos() {
                return reservas;
            }
        };
        FuncionarioDatos funcionarioDatosFalso = new FuncionarioDatos("no-existe.xml") {
            @Override
            public FuncionarioDTO buscarPorId(String id) {
                for (FuncionarioDTO funcionario : funcionarios) {
                    if (funcionario.getId().equalsIgnoreCase(id)) {
                        return funcionario;
                    }
                }
                return null;
            }
        };
        calendarizacionLogica = new CalendarizacionLogica(recursoDatosFalso, reservaDatosFalso, funcionarioDatosFalso);
    }

    @Test
    void obtenerMatrizSinFechaLanzaExcepcion(){
        assertThrows(ValidacionException.class,()->calendarizacionLogica.obtenerMatriz(null,"CAT-000001"));
    }
    @Test
    void obtenerMatrizSinCategoriaLanzaExcepcion(){
        assertThrows(ValidacionException.class,()->calendarizacionLogica.obtenerMatriz("2026-08-26",""));
    }
    @Test
    void obtenerMatrizDevuelveColumnaPorCadaRecursoDeLaCategoria()throws ValidacionException{
        MatrizCalendarizacionDTO matriz=calendarizacionLogica.obtenerMatriz("2026-08-26","CAT-000001");
        assertEquals(24,matriz.getHoras().size());
    }
    @Test
    void celdaOcupadaMuestraActividadYFuncionario()throws ValidacionException{
        MatrizCalendarizacionDTO matriz=calendarizacionLogica.obtenerMatriz("2026-08-26","CAT-000001");
        int indiceHora=matriz.getHoras().indexOf("09:00");
        assertEquals("Jugar futbol",matriz.obtenerCelda(indiceHora,0).getActividad());
        assertEquals("Joel Ramirez",matriz.obtenerCelda(indiceHora,0).getNombreFuncionario());
    }
    @Test
    void celdaDeOtraFechaApareceLibre()throws ValidacionException{
        MatrizCalendarizacionDTO matriz=calendarizacionLogica.obtenerMatriz("2026-08-27","CAT-000001");
        int indiceHora=matriz.getHoras().indexOf("09:00");
        assertFalse(matriz.obtenerCelda(indiceHora,0).isOcupada());
    }
    @Test
    void reservaCanceladaNoOcupaLaCelda()throws ValidacionException{
        reservas.add(new ReservaDTO("RES-000002","Cancelada","2026-08-26","14:00","16:00","111",List.of("34343"),"CANCELADA"));
        MatrizCalendarizacionDTO matriz=calendarizacionLogica.obtenerMatriz("2026-08-26","CAT-000001");
        int indiceHora=matriz.getHoras().indexOf("14:00");
        int indiceRecurso=matriz.getRecursos().indexOf(matriz.getRecursos().stream().filter(r->r.getId().equals("34343")).findFirst().get());
        assertFalse(matriz.obtenerCelda(indiceHora,indiceRecurso).isOcupada());
    }
}
