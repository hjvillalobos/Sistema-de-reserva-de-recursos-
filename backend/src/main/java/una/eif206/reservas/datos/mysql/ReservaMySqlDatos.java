package una.eif206.reservas.datos.mysql;

import una.eif206.reservas.DTO.ReservaDTO;
import una.eif206.reservas.configuracion.ConexionBD;
import una.eif206.reservas.datos.ReservaDatos;
import una.eif206.reservas.excepciones.ValidacionException;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class ReservaMySqlDatos extends ReservaDatos {
    @Override public List<ReservaDTO> obtenerTodos() { return leer("", new Object[0]); }
    @Override public List<ReservaDTO> buscarPorFuncionario(String id) { return leer(" WHERE r.funcionario_id = ?", id); }
    @Override public ReservaDTO buscarPorId(String id) {
        return leer(" WHERE r.id = ?", id).stream().findFirst().orElse(null);
    }
    private List<ReservaDTO> leer(String filtro, Object... parametros) {
        String sql = "SELECT r.*, rr.recurso_id FROM reservas r LEFT JOIN reserva_recursos rr ON rr.reserva_id = r.id"
                + filtro + " ORDER BY r.fecha, r.hora_inicio, r.id, rr.recurso_id";
        try (var c = ConexionBD.abrir(); var p = Jdbc.preparar(c, sql, parametros); var rs = p.executeQuery()) {
            Map<String, ReservaDTO> resultado = new LinkedHashMap<>();
            while (rs.next()) {
                String id = rs.getString("id");
                ReservaDTO reserva = resultado.get(id);
                if (reserva == null) {
                    reserva = new ReservaDTO(id, rs.getString("actividad"), rs.getDate("fecha").toLocalDate().toString(),
                            rs.getTime("hora_inicio").toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                            rs.getTime("hora_fin").toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                            rs.getString("funcionario_id"), new ArrayList<>(), rs.getString("estado"));
                    resultado.put(id, reserva);
                }
                if (rs.getString("recurso_id") != null) reserva.getRecursosAsignados().add(rs.getString("recurso_id"));
            }
            return new ArrayList<>(resultado.values());
        } catch (SQLException e) { throw new ErrorDatos(e); }
    }

    /** Bloquea los recursos antes de leer disponibilidad; mantiene esos bloqueos hasta commit. */
    public ReservaDTO registrar(String actividad, String fecha, String inicio, String fin,
            String funcionario, List<String> categorias) throws ValidacionException {
        return Jdbc.transaccion(c -> {
            String propietario;
            try (var p = Jdbc.preparar(c, "SELECT f.usuario_id FROM funcionarios f JOIN usuarios u ON u.id = f.usuario_id "
                    + "WHERE f.usuario_id = ? AND u.rol = 'FUNCIONARIO'", funcionario); var rs = p.executeQuery()) {
                if (!rs.next()) throw new ValidacionException("El funcionario no existe en MySQL.");
                propietario = rs.getString(1);
            }
            // Obtener los IDs canónicos respeta la comparación de la base (mayúsculas y acentos).
            Set<String> canonicas = new TreeSet<>();
            for (String categoria : categorias) {
                try (var p = Jdbc.preparar(c, "SELECT id FROM categorias WHERE id = ?", categoria); var rs = p.executeQuery()) {
                    if (!rs.next()) throw new ValidacionException("La categoría no existe: " + categoria);
                    if (!canonicas.add(rs.getString(1))) throw new ValidacionException("No repita categorías.");
                }
            }
            String marcas = String.join(",", Collections.nCopies(canonicas.size(), "?"));
            Map<String, List<String>> candidatos = new TreeMap<>();
            try (var p = Jdbc.preparar(c, "SELECT r.id, cat.id FROM recursos r JOIN categorias cat ON cat.id = r.categoria_id "
                    + "WHERE r.categoria_id IN (" + marcas + ") ORDER BY r.id FOR UPDATE", canonicas.toArray()); var rs = p.executeQuery()) {
                while (rs.next()) candidatos.computeIfAbsent(rs.getString(2), k -> new ArrayList<>()).add(rs.getString(1));
            }
            // READ COMMITTED ve las reservas confirmadas por quien tuvo antes el bloqueo del recurso.
            List<String> asignados = new ArrayList<>();
            for (String categoria : canonicas) {
                String elegido = null;
                for (String recurso : candidatos.getOrDefault(categoria, List.of())) {
                    try (var p = Jdbc.preparar(c, "SELECT 1 FROM reservas r JOIN reserva_recursos rr ON r.id = rr.reserva_id "
                            + "WHERE rr.recurso_id = ? AND r.estado = 'ACTIVA' AND r.fecha = ? "
                            + "AND r.hora_inicio < ? AND r.hora_fin > ? LIMIT 1",
                            recurso, LocalDate.parse(fecha), LocalTime.parse(fin), LocalTime.parse(inicio)); var rs = p.executeQuery()) {
                        if (!rs.next()) { elegido = recurso; break; }
                    }
                }
                if (elegido == null) throw new ValidacionException("No hay disponibilidad para la categoría: " + categoria);
                asignados.add(elegido);
            }
            String id = generarSiguienteId();
            try (var p = Jdbc.preparar(c, "INSERT INTO reservas (id, actividad, fecha, hora_inicio, hora_fin, funcionario_id, estado) "
                    + "VALUES (?, ?, ?, ?, ?, ?, 'ACTIVA')", id, actividad, LocalDate.parse(fecha),
                    LocalTime.parse(inicio), LocalTime.parse(fin), propietario)) { p.executeUpdate(); }
            for (String recurso : asignados) {
                try (var p = Jdbc.preparar(c, "INSERT INTO reserva_recursos (reserva_id, recurso_id) VALUES (?, ?)", id, recurso)) {
                    p.executeUpdate();
                }
            }
            return new ReservaDTO(id, actividad, fecha, inicio, fin, propietario, asignados, "ACTIVA");
        });
    }

    public void cancelar(String id, String funcionario) throws ValidacionException {
        Jdbc.transaccion(c -> {
            try (var p = Jdbc.preparar(c, "SELECT id FROM reservas WHERE id = ? AND funcionario_id = ? FOR UPDATE", id, funcionario);
                 var rs = p.executeQuery()) {
                if (!rs.next()) throw new ValidacionException("La reserva no existe o no le pertenece.");
            }
            try (var p = Jdbc.preparar(c, "UPDATE reservas SET estado = 'CANCELADA' WHERE id = ?", id)) { p.executeUpdate(); }
            return null;
        });
    }
    @Override public String generarSiguienteId() { return "RES-" + UUID.randomUUID(); }
    @Override public void guardar(ReservaDTO r) {
        throw new UnsupportedOperationException("Use registrar o cancelar: la asignación requiere una transacción.");
    }
}
