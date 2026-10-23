package una.eif206.reservas.datos.mysql;

import una.eif206.reservas.DTO.FuncionarioDTO;
import una.eif206.reservas.datos.FuncionarioDatos;
import java.util.List;

/** Lee nombres para los informes sin mezclar las identidades MySQL con XML. */
public final class FuncionarioMySqlConsulta extends FuncionarioDatos {
    private static final String SELECT = "SELECT f.usuario_id, f.nombre, f.telefono FROM funcionarios f "
            + "JOIN usuarios u ON u.id = f.usuario_id WHERE u.rol = 'FUNCIONARIO'";
    @Override public List<FuncionarioDTO> obtenerTodos() {
        return Jdbc.consultar(SELECT + " ORDER BY f.usuario_id",
                r -> new FuncionarioDTO(r.getString(1), null, r.getString(2), r.getString(3)));
    }
    @Override public FuncionarioDTO buscarPorId(String id) {
        return Jdbc.consultar(SELECT + " AND f.usuario_id = ?",
                r -> new FuncionarioDTO(r.getString(1), null, r.getString(2), r.getString(3)), id)
                .stream().findFirst().orElse(null);
    }
    @Override public void guardar(FuncionarioDTO f) {
        throw new ErrorDatos("La gestión de funcionarios MySQL está pendiente de integrar con su responsable.");
    }
    @Override public void eliminar(String id) {
        throw new ErrorDatos("La gestión de funcionarios MySQL está pendiente de integrar con su responsable.");
    }
}
