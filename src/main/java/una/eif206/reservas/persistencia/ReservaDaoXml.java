package una.eif206.reservas.persistencia;

import una.eif206.reservas.modelo.Reserva;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAOXml implements ReservaDAO {

    public static final String RUTA_DEFECTO = "data/reservas.xml";
    private final String ruta;

    public ReservaDAOXml() { this(RUTA_DEFECTO); }
    public ReservaDAOXml(String ruta) { this.ruta = ruta; }

    @Override
    public List<Reserva> obtenerTodos() {
        return cargar().getReservas();
    }

    @Override
    public Reserva buscarPorId(String id) {
        for (Reserva r : obtenerTodos()) {
            if (r.getId().equalsIgnoreCase(id)) {
                return r;
            }
        }
        return null;
    }

    @Override
    public List<Reserva> buscarPorFuncionario(String funcionarioId) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva r : obtenerTodos()) {
            if (r.getFuncionarioId().equalsIgnoreCase(funcionarioId)) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    @Override
    public void guardar(Reserva reserva) {
        ListaReservas lista = cargar();
        Reserva existente = null;
        for (Reserva r : lista.getReservas()) {
            if (r.getId().equalsIgnoreCase(reserva.getId())) {
                existente = r;
                break;
            }
        }
        if (existente != null) {
            lista.getReservas().remove(existente);
        }
        lista.getReservas().add(reserva);
        XmlDaoUtil.guardar(ruta, lista, ListaReservas.class);
    }

    @Override
    public String generarSiguienteId() {
        int maxNumero = 0;
        for (Reserva r : obtenerTodos()) {
            String parteNumerica = r.getId().substring(r.getId().indexOf('-') + 1);
            int numero = Integer.parseInt(parteNumerica);
            if (numero > maxNumero) {
                maxNumero = numero;
            }
        }
        int siguiente = maxNumero + 1;
        return String.format("RES-%06d", siguiente);
    }

    private ListaReservas cargar() {
        return XmlDaoUtil.cargar(ruta, ListaReservas.class);
    }
}