// controlador/PrincipalController.java
package una.eif206.reservas.controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import una.eif206.reservas.modelo.Rol;
import una.eif206.reservas.modelo.Usuario;

import java.io.IOException;
import java.net.URL;

public class PrincipalController {

    @FXML private Label lblBienvenida;
    @FXML private Button btnCategorias;
    @FXML private Button btnFuncionarios;

    public void inicializar(Usuario usuario) {
        lblBienvenida.setText("Bienvenido, " + usuario.getId() + " (" + usuario.getRol() + ")");
        if (usuario.getRol() == Rol.ADMINISTRADOR) {
            btnCategorias.setVisible(true);
            btnCategorias.setManaged(true);
            btnFuncionarios.setVisible(true);
            btnFuncionarios.setManaged(true);
        }
    }

    @FXML
    public void onAbrirCategorias(ActionEvent event) {
        abrirVentana("/una/eif206/reservas/fxml/categorias.fxml", "Lista de Categorías");
    }

    @FXML
    public void onAbrirFuncionarios(ActionEvent event) {
        abrirVentana("/una/eif206/reservas/fxml/funcionarios.fxml", "Lista de Funcionarios");
    }

    private void abrirVentana(String rutaFxml, String titulo) {
        URL recurso = getClass().getResource(rutaFxml);

        if (recurso == null) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText("No se encontró la vista");
            alerta.setContentText("No se pudo ubicar el archivo: " + rutaFxml
                    + "\nVerifique que exista en src/main/resources y que el proyecto esté compilado.");
            alerta.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(recurso);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();
        } catch (IOException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText("No se pudo cargar la vista");
            alerta.setContentText(e.getMessage());
            alerta.showAndWait();
        }
    }
}