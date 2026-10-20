package una.eif206.reservas.DTO;

public class UsuarioSesionDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private RolDTO rol;

    public UsuarioSesionDTO(){}

    public UsuarioSesionDTO(String id, RolDTO rol) {
        this.id = id;
        this.rol = rol;
    }

    public String getId(){
        return id;
    }

    public RolDTO getRol() {
        return rol;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRol(RolDTO rol) {
        this.rol = rol;
    }
}
