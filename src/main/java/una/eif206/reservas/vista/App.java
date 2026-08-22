// vista/App.java
package una.eif206.reservas.vista;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/una/eif206/reservas/fxml/login.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("Sistema de Reservas");
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
