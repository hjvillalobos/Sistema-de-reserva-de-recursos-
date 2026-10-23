package una.eif206.reservas.logica;

import una.eif206.reservas.DTO.ReservaDTO;
import una.eif206.reservas.datos.mysql.ReservaMySqlDatos;
import una.eif206.reservas.datos.mysql.RecursoMySqlDatos;
import una.eif206.reservas.excepciones.ValidacionException;
import java.util.List;

public final class ReservaMySqlLogica extends ReservaLogica {
    private final ReservaMySqlDatos datos;
    public ReservaMySqlLogica(ReservaMySqlDatos datos, RecursoMySqlDatos recursos) {
        super(datos, recursos);
        this.datos = datos;
    }
    @Override public ReservaDTO intentarRegistrar(String actividad, String fecha, String inicio,
            String fin, String funcionario, List<String> categorias) throws ValidacionException {
        validarDatos(actividad, fecha, inicio, fin, categorias);
        if (actividad.length() > 255) throw new ValidacionException("La actividad admite hasta 255 caracteres.");
        if (funcionario == null || funcionario.isBlank()) throw new ValidacionException("Debe indicar el funcionario.");
        return datos.registrar(actividad, fecha, inicio, fin, funcionario, categorias);
    }
    @Override public void cancelar(String id, String funcionario) throws ValidacionException {
        datos.cancelar(id, funcionario);
    }
}
