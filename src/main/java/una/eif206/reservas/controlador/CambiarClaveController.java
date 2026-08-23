// controlador/CambiarClaveController.java
package una.eif206.reservas.controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import una.eif206.reservas.modelo.Usuario;
import una.eif206.reservas.servicio.CredencialesInvalidasException;
import una.eif206.reservas.servicio.LoginService;

public class CambiarClaveController {

    @FXML private PasswordField txtClaveActual;
    @FXML private PasswordField txtClaveNueva;
    @FXML private PasswordField txtClaveConfirmar;
    @FXML private Label lblError;

    private final LoginService loginService = new LoginService();
    private Usuario usuario;

    public void inicializar(Usuario usuario) { this.usuario = usuario; }

    @FXML
    public void onAceptar(ActionEvent event) {
        lblError.setText("");
        if (!txtClaveNueva.getText().equals(txtClaveConfirmar.getText())) {
            lblError.setText("La clave nueva y su confirmación no coinciden.");
            return;
        }
        try {
            loginService.cambiarClave(usuario, txtClaveActual.getText(), txtClaveNueva.getText());
            ((Stage) txtClaveActual.getScene().getWindow()).close();
        } catch (CredencialesInvalidasException e) {
            lblError.setText(e.getMessage());
        }
    }

    @FXML
    public void onCancelar(ActionEvent event) {
        ((Stage) txtClaveActual.getScene().getWindow()).close();
    }
}