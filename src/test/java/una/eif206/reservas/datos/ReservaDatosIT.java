package una.eif206.reservas.datos;

import una.eif206.reservas.DTO.ReservaDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReservaDatosIT {
    @TempDir
    Path tempDir;
    @Test
    void guardarYRecuperarRecursosAsignadosSeLeenCorrectamente(){
        String ruta=tempDir.resolve("reservas.xml").toString();
        ReservaDatos datos=new ReservaDatos(ruta);

        datos.guardar(new ReservaDTO("RES-1","Reunion","2026-08-26","09:00","11:00","111",List.of("238715","34343"),"ACTIVA"));

        ReservaDatos datosRecargado=new ReservaDatos(ruta);
        ReservaDTO recargada=datosRecargado.buscarPorId("RES-1");

        assertNotNull(recargada);
        assertEquals(2,recargada.getRecursosAsignados().size());
        assertTrue(recargada.getRecursosAsignados().contains("238715"));
        assertTrue(recargada.getRecursosAsignados().contains("34343"));
    }
    @Test
    void generarSiguienteIdIncrementaSegunCantidadExistente(){
        String ruta=tempDir.resolve("reservas.xml").toString();
        ReservaDatos datos=new ReservaDatos(ruta);

        assertEquals("RES-000001",datos.generarSiguienteId());

        datos.guardar(new ReservaDTO("RES-000001","A","2026-08-26","09:00","10:00","111",List.of(),"ACTIVA"));
        assertEquals("RES-000002",datos.generarSiguienteId());
    }
    @Test
    void obtenerTodosDeArchivoInexistenteDevuelveListaVacia() {
        String ruta = tempDir.resolve("no-existe.xml").toString();
        RecursoDatos datos = new RecursoDatos(ruta);
        assertTrue(datos.obtenerTodos().isEmpty());
    }
}
