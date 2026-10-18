package una.eif206.reservas.DTO;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FuncionarioDTOTest {
    @Test
    void constructorAsignaTodosLosCampos(){
        FuncionarioDTO u=new FuncionarioDTO("111","clave111","Juan Perez","8888-0000");
        assertEquals("111",u.getId());
        assertEquals("clave111",u.getClave());
        assertEquals("Juan Perez",u.getNombre());
        assertEquals("8888-0000",u.getTelefono());
        assertEquals(RolDTO.FUNCIONARIO,u.getRol());
    }
    @Test
    void esUnaSubClaseDeUsuarioDTO(){
        FuncionarioDTO f=new FuncionarioDTO("111","clave","Juan","8888");
        assertInstanceOf(UsuarioDTO.class,f);
    }

    @Test
    void settersActualizanValores(){
        FuncionarioDTO f = new FuncionarioDTO();
        f.setNombre("Maria Solis");
        f.setTelefono("7777-1111");
        assertEquals("Maria Solis", f.getNombre());
        assertEquals("7777-1111", f.getTelefono());
    }
}
