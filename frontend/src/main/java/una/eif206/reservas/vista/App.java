
package una.eif206.reservas.vista;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import una.eif206.reservas.cliente.ServiciosCliente;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // El perfil local aporta el proveedor XML; después se añadirá el proveedor remoto.
        try {
            ServiciosCliente.obtener();
        } catch (IllegalStateException e) {
            Alert aviso = new Alert(Alert.AlertType.INFORMATION);
            aviso.setTitle("Proyecto II - migración en curso");
            aviso.setHeaderText("Falta configurar el proveedor de servicios");
            aviso.setContentText(e.getMessage());
            aviso.showAndWait();
            javafx.application.Platform.exit();
            return;
        }
        Parent root = FXMLLoader.load(getClass().getResource("/una/eif206/reservas/fxml/login.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("Sistema de Reservas");
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
