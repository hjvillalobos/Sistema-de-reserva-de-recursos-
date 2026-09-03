package una.eif206.reservas.DTO;

public class FuncionarioDTO extends UsuarioDTO {

    private String nombre;
    private String telefono;

    public FuncionarioDTO() {
        super();
    }

    public FuncionarioDTO(String id, String clave, String nombre, String telefono) {
        super(id,clave,RolDTO.FUNCIONARIO);
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
