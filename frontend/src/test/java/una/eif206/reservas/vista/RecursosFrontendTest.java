package una.eif206.reservas.vista;

import org.junit.jupiter.api.Test;

import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.jupiter.api.Assertions.*;

class RecursosFrontendTest {
    @Test
    void lasVistasConservanSusRutasYControladoresTrasElMovimiento() throws Exception {
        String base = "/una/eif206/reservas/fxml/";
        String[] vistas = {"login", "cambiarClave", "principal", "reservas", "recursos",
                "categorias", "funcionarios", "calendarizacionReservas", "programacion", "estadisticas"};
        var factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        for (String vista : vistas) {
            try (var stream = App.class.getResourceAsStream(base + vista + ".fxml")) {
                assertNotNull(stream, vista);
                var root = factory.newDocumentBuilder().parse(stream).getDocumentElement();
                String controller = root.getAttributeNS("http://javafx.com/fxml/1", "controller");
                assertFalse(controller.isBlank(), vista);
                assertNotNull(Class.forName(controller, false, App.class.getClassLoader()));
            }
        }
        assertNotNull(App.class.getResource(base + "css/estilos.css"));
    }
}
