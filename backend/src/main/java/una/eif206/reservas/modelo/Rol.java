package una.eif206.reservas.modelo;

public enum Rol {
    ADMINISTRADOR, FUNCIONARIO;

    public String toUpperCase() {
        return this.name().toUpperCase();
    }
}