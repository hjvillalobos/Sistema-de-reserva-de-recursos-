package una.eif206.reservas.DTO;
public class UsuarioDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    protected String id;
    protected String clave;
    protected RolDTO rol;

    public UsuarioDTO() {
    }

    public UsuarioDTO(String id, String clave, RolDTO rol) {
        this.id = id;
        this.clave = clave;
        this.rol = rol;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public RolDTO getRol() { return rol; }
    public void setRol(RolDTO rol) { this.rol = rol; }
}