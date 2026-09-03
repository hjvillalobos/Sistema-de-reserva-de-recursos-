package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.*;
import una.eif206.reservas.datos.CategoriaDatos;
import una.eif206.reservas.datos.ReservaDatos;
import una.eif206.reservas.datos.RecursoDatos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EstadisticaLogicaTest {
    private List<RecursoDTO>recursoDTOS;
    private List<ReservaDTO>reservaDTOS;
    private List<CategoriaDTO>categoriaDTOS;
    private EstadisticasLogica estadisticasLogica;
    @BeforeEach
    void setUp(){
        categoriaDTOS=new ArrayList<>();
        categoriaDTOS.add(new CategoriaDTO("CAT-000001","Sala para 10 personas"));
        categoriaDTOS.add(new CategoriaDTO("CAT-000002","Laptop Windows"));

        recursoDTOS=new ArrayList<>();
        recursoDTOS.add(new RecursoDTO("R1","CAT-000001","Sala 1"));
        recursoDTOS.add(new RecursoDTO("R2","CAT-000002","Laptop A"));

        reservaDTOS=new ArrayList<>();
        reservaDTOS.add(new ReservaDTO("RES-1", "Reunion 1", "2026-08-24", "09:00", "10:00", "111", List.of("R1"), "ACTIVA"));
        reservaDTOS.add(new ReservaDTO("RES-2", "Reunion 2", "2026-08-25", "09:00", "10:00", "111", List.of("R1"), "ACTIVA"));
        reservaDTOS.add(new ReservaDTO("RES-3", "Reunion 3", "2026-08-26", "09:00", "10:00", "111", List.of("R2"), "ACTIVA"));
        reservaDTOS.add(new ReservaDTO("RES-4", "Fuera de rango", "2026-09-15", "09:00", "10:00", "111", List.of("R1"), "ACTIVA"));
        reservaDTOS.add(new ReservaDTO("RES-5", "Cancelada", "2026-08-24", "11:00", "12:00", "111", List.of("R2"), "CANCELADA"));

        ReservaDatos reservaDatosFalso=new ReservaDatos("no-existe.xml"){
            @Override
            public List<ReservaDTO> obtenerTodos(){return reservaDTOS;}
        };
        RecursoDatos recursoDatosFalso=new RecursoDatos("no-existe.xml"){
            @Override
            public RecursoDTO buscarPorId(String id){
                for(RecursoDTO r: recursoDTOS){
                    if(r.getId().equalsIgnoreCase(id)){
                        return r;
                    }
                }
                return null;
            }
        };
        CategoriaDatos categoriaDatosFalso=new CategoriaDatos("no-existe.xml"){
            @Override
            public CategoriaDTO buscarPorId(String id){
                for(CategoriaDTO c:categoriaDTOS){
                    if(c.getId().equalsIgnoreCase(id)){
                        return c;
                    }
                }
                return null;
            }
        };
        estadisticasLogica=new EstadisticasLogica(reservaDatosFalso,recursoDatosFalso,categoriaDatosFalso);
    }
    @Test
    void estadisticasRecursosSinFechasLanzaExcepcion(){
        assertThrows(ValidacionException.class, () -> estadisticasLogica.estadisticasRecursos(null, "2026-08-30"));
    }
    @Test
    void estadisticasRecursosConHastaAnteriorADesdeLanzaExcepcion(){
        assertThrows(ValidacionException.class, () -> estadisticasLogica.estadisticasRecursos("2026-08-30", "2026-08-01"));
    }
    @Test
    void estadisticasRecursosCuentaPorCategoriaCorrectamente()throws ValidacionException{
        List<EstadisticaCategoriaDTO> resultado=estadisticasLogica.estadisticasRecursos("2026-08-01", "2026-08-31");
        EstadisticaCategoriaDTO cat1=resultado.stream().filter(e->e.getCategoriaId().equals("CAT-000001")).findFirst().orElseThrow();
        assertEquals(2,cat1.getCantidad());
        EstadisticaCategoriaDTO cat2=resultado.stream().filter(e->e.getCategoriaId().equals("CAT-000002")).findFirst().orElseThrow();
        assertEquals(1,cat2.getCantidad());
    }
    @Test
    void estadisticasRecursosIgnoraReservasCanceladas() throws ValidacionException{
        List<EstadisticaCategoriaDTO> resultado=estadisticasLogica.estadisticasRecursos("2026-08-01", "2026-08-31");

        long totalCat2=resultado.stream().filter(e->e.getCategoriaId().equals("CAT-000002")).mapToLong(EstadisticaCategoriaDTO::getCantidad).sum();
        assertEquals(1,totalCat2);
    }
    @Test
    void estadisticasRecursosIgnoraReservasFueraDeRango()throws ValidacionException{
        List<EstadisticaCategoriaDTO> resultado=estadisticasLogica.estadisticasRecursos("2026-08-01", "2026-08-31");
        long total = resultado.stream().mapToLong(EstadisticaCategoriaDTO::getCantidad).sum();
        assertEquals(3,total);
    }
    @Test
    void estadisticasRecursosOrdenaDeMayorAMenorCantidad() throws ValidacionException{
        List<EstadisticaCategoriaDTO> resultado=estadisticasLogica.estadisticasRecursos("2026-08-01", "2026-08-31");
        assertTrue(resultado.get(0).getCantidad()>=resultado.get(1).getCantidad());
    }
    @Test
    void estadisticasActividadesAgrupaPorSemana()throws ValidacionException{
        List<EstadisticaSemanaDTO> resultado=estadisticasLogica.estadisticasActividades("2026-08-01", "2026-08-31");
        assertEquals(1,resultado.size());
        assertEquals(3,resultado.get(0).getCantidad());
    }

}
