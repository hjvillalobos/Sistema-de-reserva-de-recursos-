package una.eif206.reservas.datos.mysql;

import una.eif206.reservas.DTO.CategoriaDTO;
import una.eif206.reservas.datos.CategoriaDatos;
import java.util.List;

/** Lectura compartida para recursos; la gestión corresponde al compañero encargado. */
public final class CategoriaMySqlConsulta extends CategoriaDatos {
    @Override public List<CategoriaDTO> obtenerTodos() {
        return Jdbc.consultar("SELECT id, descripcion FROM categorias ORDER BY id",
                r -> new CategoriaDTO(r.getString(1), r.getString(2)));
    }
    @Override public CategoriaDTO buscarPorId(String id) {
        return Jdbc.consultar("SELECT id, descripcion FROM categorias WHERE id = ?",
                r -> new CategoriaDTO(r.getString(1), r.getString(2)), id).stream().findFirst().orElse(null);
    }
    @Override public void guardar(CategoriaDTO categoria) {
        throw new ErrorDatos("La gestión de categorías MySQL está pendiente de integrar con su responsable.");
    }
    @Override public void eliminar(String id) {
        throw new ErrorDatos("La gestión de categorías MySQL está pendiente de integrar con su responsable.");
    }
}
