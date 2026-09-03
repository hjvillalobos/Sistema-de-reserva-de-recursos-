package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.*;
import una.eif206.reservas.datos.RecursoDatos;
import una.eif206.reservas.datos.ReservaDatos;
import una.eif206.reservas.datos.FuncionarioDatos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProgramacionLogicaIT {
    @TempDir
    Path tempDir;

    @Test
    void obtenerMatrizSemanaConDatosPersistidosEnXmlReal()throws ValidacionException{
        String rutaReservas = tempDir.resolve("reservas.xml").toString();
        String rutaFuncionarios=tempDir.resolve("funcionarios.xml").toString();

        ReservaDatos reservaDatos = new ReservaDatos(rutaReservas);
        reservaDatos.guardar(new ReservaDTO("RES-1", "Charla tecnica", "2026-08-26","08:00","09:00","111",List.of("R1"),"ACTIVA"));

        FuncionarioDatos funcionarioDatos = new FuncionarioDatos(rutaFuncionarios);
        funcionarioDatos.guardar(new FuncionarioDTO("111", "111", "Maria Hernandez", "8888-0000"));

        ProgramacionLogica logica=new ProgramacionLogica(reservaDatos,funcionarioDatos);
        MatrizProgramacionDTO matriz=logica.obtenerMatrizSemana("2026-08-26");
        assertEquals("2026-08-24",matriz.getInicioSemana());
        assertEquals("2026-08-30",matriz.getFinSemana());
        int indiceHora = matriz.getHoras().indexOf("08:00");
        int indiceDia=matriz.getDias().indexOf("2026-08-26");

        var celda=matriz.obtenerCelda(indiceHora,indiceDia);
        assertTrue(celda.isOcupada());
        assertEquals("Charla tecnica / Maria Hernandez",celda.getActividades().get(0));
    }
}
