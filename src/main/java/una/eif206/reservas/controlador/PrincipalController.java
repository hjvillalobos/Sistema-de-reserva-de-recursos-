package una.eif206.reservas.controlador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import una.eif206.reservas.modelo.Usuario;

import java.io.IOException;

public class PrincipalController {
    @FXML private Label lblBienvenida;

    private Usuario usuario; // <-- NUEVO: ahora sí lo guardamos

    public void inicializar(Usuario usuario) {
        this.usuario = usuario; // <-- NUEVO
        lblBienvenida.setText("Bienvenido, " + usuario.getId() + " (" + usuario.getRol() + ")");
    }

    @FXML
    public void onProgramacion() throws IOException {
        abrirVentana("/una/eif206/reservas/fxml/programacion.fxml", "Programación de actividades",
                (ProgramacionController c) -> c.inicializar(usuario));
    }

    @FXML
    public void onEstadisticas() throws IOException {
        abrirVentana("/una/eif206/reservas/fxml/estadisticas.fxml", "Estadísticas",
                (EstadisticasController c) -> c.inicializar(usuario));
    }

    @SuppressWarnings("unchecked")
    private <T> void abrirVentana(String rutaFxml, String titulo, java.util.function.Consumer<T> inicializador) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
        Parent root = loader.load();
        inicializador.accept((T) loader.getController());
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle(titulo);
        stage.show();
    }
}