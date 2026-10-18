package una.eif206.reservas.DTO;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsuarioDTOTest {
    @Test
    void constructorAsignaTodosLosCampos(){
        UsuarioDTO u=new UsuarioDTO("admin","clave123",RolDTO.ADMINISTRADOR);
        assertEquals("admin",u.getId());
        assertEquals("clave123",u.getClave());
        assertEquals(RolDTO.ADMINISTRADOR,u.getRol());
    }

    @Test
    void settersActualizanValores(){
        UsuarioDTO u=new UsuarioDTO();
        u.setId("111");
        u.setClave("nueva");
        u.setRol(RolDTO.FUNCIONARIO);
        assertEquals("111",u.getId());
        assertEquals("nueva",u.getClave());
        assertEquals(RolDTO.FUNCIONARIO,u.getRol());
    }
}
