// controlador/PrincipalController.java
package una.eif206.reservas.controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import una.eif206.reservas.modelo.Rol;
import una.eif206.reservas.modelo.Usuario;

import java.io.IOException;

public class PrincipalController {

    @FXML private Label lblBienvenida;
    @FXML private Button btnCategorias;

    public void inicializar(Usuario usuario) {
        lblBienvenida.setText("Bienvenido, " + usuario.getId() + " (" + usuario.getRol() + ")");
        if (usuario.getRol() == Rol.ADMINISTRADOR) {
            btnCategorias.setVisible(true);
            btnCategorias.setManaged(true);
        }
    }

    @FXML
    public void onAbrirCategorias(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/una/eif206/reservas/fxml/categorias.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Lista de Categorías");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}