package una.eif206.reservas.vista;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import una.eif206.reservas.cliente.ServiciosCliente;
import una.eif206.reservas.cliente.TareasUI;

public class App extends Application {
    @Override public void start(Stage stage) {
        Thread.currentThread().setUncaughtExceptionHandler((hilo, error) -> {
            Throwable causa = error;
            while (causa.getCause() != null) causa = causa.getCause();
            Alert aviso = new Alert(Alert.AlertType.ERROR);
            aviso.setHeaderText("No se pudo completar la operación");
            aviso.setContentText(causa.getMessage());
            aviso.showAndWait();
        });
        Label estado = new Label("Conectando al servidor...");
        VBox espera = new VBox(20, estado);
        espera.setStyle("-fx-padding: 35;");
        stage.setTitle("Sistema de Reservas");
        stage.setScene(new Scene(espera, 650, 180));
        stage.show();
        TareasUI.ejecutar(estado, ServiciosCliente::obtener, proveedor -> {
            try {
                stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/una/eif206/reservas/fxml/login.fxml"))));
                stage.sizeToScene();
            } catch (Exception e) { estado.setText("No se pudo abrir el login: " + e.getMessage()); }
        });
    }
    @Override public void stop() { ServiciosCliente.cerrar(); }
    public static void main(String[] args) { launch(args); }
}
