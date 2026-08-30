
package una.eif206.reservas.persistencia;

import una.eif206.reservas.modelo.Categoria;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAOXml implements CategoriaDAO {

    public static final String RUTA_DEFECTO = "data/categorias.xml";
    private final String ruta;

    public CategoriaDAOXml() { this(RUTA_DEFECTO); }
    public CategoriaDAOXml(String ruta) { this.ruta = ruta; }

    @Override
    public List<Categoria> obtenerTodos() {
        return cargar().getCategorias();
    }





    @Override
    public List<Categoria> buscarPorDescripcion(String texto) {
        List<Categoria> resultado = new ArrayList<>();
        String textoBusqueda = (texto == null) ? "" : texto.toLowerCase();
        for (Categoria c : obtenerTodos()) {
            if (c.getDescripcion().toLowerCase().contains(textoBusqueda)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    @Override
    public Categoria buscarPorId(String id) {
        for (Categoria c : obtenerTodos()) {
            if (c.getId().equalsIgnoreCase(id)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public void guardar(Categoria categoria) {
        ListaCategorias lista = cargar();
        Categoria existente = null;
        for (Categoria c : lista.getCategorias()) {
            if (c.getId().equalsIgnoreCase(categoria.getId())) {
                existente = c;
                break;
            }
        }
        if (existente != null) {
            lista.getCategorias().remove(existente);
        }
        lista.getCategorias().add(categoria);
        XmlDaoUtil.guardar(ruta, lista, ListaCategorias.class);
    }

    @Override
    public void eliminar(String id) {
        ListaCategorias lista = cargar();
        Categoria aEliminar = null;
        for (Categoria c : lista.getCategorias()) {
            if (c.getId().equalsIgnoreCase(id)) {
                aEliminar = c;
                break;
            }
        }
        if (aEliminar != null) {
            lista.getCategorias().remove(aEliminar);
        }
        XmlDaoUtil.guardar(ruta, lista, ListaCategorias.class);
    }

    @Override
    public String generarSiguienteId() {
        int maxNumero = 0;
        for (Categoria c : obtenerTodos()) {
            String idActual = c.getId(); // formato CAT-000001
            String parteNumerica = idActual.substring(idActual.indexOf('-') + 1);
            int numero = Integer.parseInt(parteNumerica);
            if (numero > maxNumero) {
                maxNumero = numero;
            }
        }
        int siguiente = maxNumero + 1;
        return String.format("CAT-%06d", siguiente);
    }

    private ListaCategorias cargar() {
        return XmlDaoUtil.cargar(ruta, ListaCategorias.class);
    }
}