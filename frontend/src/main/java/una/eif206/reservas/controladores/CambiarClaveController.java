package una.eif206.reservas.controladores;

import una.eif206.reservas.cliente.ServiciosCliente;
import una.eif206.reservas.cliente.TareasUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import una.eif206.reservas.excepciones.ValidacionException;
import una.eif206.reservas.api.LoginApi;

public class CambiarClaveController {

    @FXML private PasswordField txtCambiarClaveActual;
    @FXML private PasswordField txtCambiarClaveNueva;
    @FXML private PasswordField txtCambiarClaveConfirmar;
    @FXML private Label lblCambiarClaveError;

    private final LoginApi loginService = ServiciosCliente.obtener().login();


    @FXML
    public void onAceptar(ActionEvent event) {
        if (!txtCambiarClaveNueva.getText().equals(txtCambiarClaveConfirmar.getText())) {
            lblCambiarClaveError.setText("La clave nueva y su confirmación no coinciden.");
            return;
        }
        String actual = txtCambiarClaveActual.getText();
        String nueva = txtCambiarClaveNueva.getText();
        TareasUI.ejecutar(lblCambiarClaveError, () -> {
            loginService.cambiarClave(actual, nueva);
            return null;
        }, resultado -> ((Stage) txtCambiarClaveActual.getScene().getWindow()).close());
    }

    @FXML
    public void onCancelar(ActionEvent event) {
        ((Stage) txtCambiarClaveActual.getScene().getWindow()).close();
    }
}