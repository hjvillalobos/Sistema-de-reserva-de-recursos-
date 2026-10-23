package una.eif206.reservas.datos.mysql;

import una.eif206.reservas.DTO.RecursoDTO;
import una.eif206.reservas.datos.RecursoDatos;
import java.sql.*;
import java.util.List;

public final class RecursoMySqlDatos extends RecursoDatos {
    private static final String SELECT = "SELECT id, categoria_id, descripcion FROM recursos";
    private static RecursoDTO fila(ResultSet r) throws SQLException {
        return new RecursoDTO(r.getString(1), r.getString(2), r.getString(3));
    }
    @Override public List<RecursoDTO> obtenerTodos() {
        return Jdbc.consultar(SELECT + " ORDER BY id", RecursoMySqlDatos::fila);
    }
    @Override public RecursoDTO buscarPorId(String id) {
        return Jdbc.consultar(SELECT + " WHERE id = ?", RecursoMySqlDatos::fila, id).stream().findFirst().orElse(null);
    }
    @Override public List<RecursoDTO> buscarPorCategoria(String id) {
        return Jdbc.consultar(SELECT + " WHERE categoria_id = ? ORDER BY id", RecursoMySqlDatos::fila, id);
    }
    @Override public void crear(RecursoDTO r) {
        Jdbc.actualizar("INSERT INTO recursos (id, categoria_id, descripcion) VALUES (?, ?, ?)",
                r.getId(), r.getCategoriaId(), r.getDescripcion());
    }
    @Override public void modificar(RecursoDTO r) {
        if (Jdbc.actualizar("UPDATE recursos SET categoria_id = ?, descripcion = ? WHERE id = ?",
                r.getCategoriaId(), r.getDescripcion(), r.getId()) == 0)
            throw new ErrorDatos("El recurso ya no existe.");
    }
    @Override public void eliminar(String id) {
        if (Jdbc.actualizar("DELETE FROM recursos WHERE id = ?", id) == 0)
            throw new ErrorDatos("El recurso ya no existe.");
    }
    @Override public void guardar(RecursoDTO r) {
        throw new UnsupportedOperationException("Use crear o modificar explícitamente en MySQL.");
    }
}
