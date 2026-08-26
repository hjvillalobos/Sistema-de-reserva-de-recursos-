package una.eif206.reservas.modelo;

import una.eif206.reservas.persistencia.RecursoDAO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import una.eif206.reservas.servicio.AccesoDenegadoException;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class RecursoServiceTest {
    private RecursoDAO fakeDao;
    private RecursoService recursoService;
    private Usuario admin;
    private Usuario funcionario;
    private Categoria categoria;

    @BeforeEach
    void setUp(){
        fakeDao=new FakeRecursoDao();
        recursoService=new RecursoService();
        admin=new Funcionario("A1","clave","Pedro Admin","0000-0000");
        admin.setRol(Rol.ADMINISTRADOR);

        funcionario=new Funcionario("F1","clave","Juan Funcionario","1111-1111");
        funcionario.setRol(Rol.FUNCIONARIO);

        categoria=new Categoria("C1","Sala de 10 personas");
    }
    @Test
    void testListaRecursosComoAdminFunciona(){
        fakeDao.guardar(new Recurso("R1","Sala 1",categoria));
        List<Recurso> lista=recursoService.listaRecursos(admin);
        assertEquals(1,lista.size());
    }
    @Test
    void testListaRecursosComoFuncionarioLanzaAccesoDenegado(){
        assertThrows(AccesoDenegadoException.class,()->recursoService.listaRecursos(funcionario));
    }
    @Test
    void testCrearRecursoValidoLoGuarda(){
        Recurso nuevo=new Recurso("R2","Laptop #238715",categoria);
        recursoService.crear(admin,nuevo);
        assertTrue(fakeDao.busquedaPorId("R2").isPresent());
    }
    @Test
    void testCrearRecursionConIdDuplicadoLanzaExcepcion(){
        fakeDao.guardar(new Recurso("R3","Proyector",categoria));
        Recurso duplicado=new Recurso("R3","Otro proyector",categoria);
        assertThrows(IllegalArgumentException.class,()->recursoService.crear(admin,duplicado));
    }
    @Test
    void testCrearRecursoConDescripcionVaciaLanzaExcepcion(){
        Recurso invalido=new Recurso("R4","",categoria);
        assertThrows(IllegalArgumentException.class,()->recursoService.crear(admin,invalido));
    }
    @Test
    void testCrearRecursosSinCategoriaLanzaExcepcion(){
        Recurso invalido=new Recurso("R5","Silla",null);
        assertThrows(IllegalArgumentException.class,()->recursoService.crear(admin,invalido));
    }
    @Test
    void testActualizarRecursoInexistenteLanzaExcepcion(){

    }

}
