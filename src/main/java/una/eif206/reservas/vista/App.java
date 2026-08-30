
package una.eif206.reservas.vista;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/una/eif206/reservas/fxml/login.fxml"));

        Scene scene = new Scene(root);

        URL cssUrl = getClass().getResource("/una/eif206/reservas/css/estilos.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("Advertencia: No se pudo cargar estilos.css en el Login.");
        }
        stage.setScene(scene);
        stage.setTitle("Sistema de Reservas");
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
