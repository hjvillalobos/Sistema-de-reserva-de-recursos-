package una.eif206.reservas.datos.mysql;

import java.sql.*;
import java.util.*;
import una.eif206.reservas.configuracion.ConexionBD;
import una.eif206.reservas.excepciones.ValidacionException;

public final class Jdbc {
    private Jdbc() { }
    @FunctionalInterface public interface Fila<T> { T leer(ResultSet rs) throws SQLException; }
    @FunctionalInterface public interface Trabajo<T> {
        T ejecutar(Connection c) throws SQLException, ValidacionException;
    }

    public static PreparedStatement preparar(Connection c, String sql, Object... valores) throws SQLException {
        PreparedStatement p = c.prepareStatement(sql);
        try {
            p.setQueryTimeout(8);
            for (int i = 0; i < valores.length; i++) p.setObject(i + 1, valores[i]);
            return p;
        } catch (SQLException e) { p.close(); throw e; }
    }

    public static <T> List<T> consultar(String sql, Fila<T> fila, Object... valores) {
        try (var c = ConexionBD.abrir(); var p = preparar(c, sql, valores); var rs = p.executeQuery()) {
            List<T> lista = new ArrayList<>();
            while (rs.next()) lista.add(fila.leer(rs));
            return lista;
        } catch (SQLException e) { throw new ErrorDatos(e); }
    }

    public static int actualizar(String sql, Object... valores) {
        try (var c = ConexionBD.abrir(); var p = preparar(c, sql, valores)) {
            return p.executeUpdate();
        } catch (SQLException e) { throw new ErrorDatos(e); }
    }

    public static <T> T transaccion(Trabajo<T> trabajo) throws ValidacionException {
        try (var c = ConexionBD.abrir()) {
            c.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            try (var p = c.createStatement()) { p.execute("SET SESSION innodb_lock_wait_timeout = 5"); }
            c.setAutoCommit(false);
            try {
                T resultado = trabajo.ejecutar(c);
                c.commit();
                return resultado;
            } catch (SQLException | ValidacionException | RuntimeException e) {
                try { c.rollback(); } catch (SQLException rollback) { e.addSuppressed(rollback); }
                throw e;
            }
        } catch (SQLException e) { throw new ErrorDatos(e); }
    }
}
