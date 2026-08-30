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

    @FXML private PasswordField txtCambiarClaveActual;
    @FXML private PasswordField txtCambiarClaveNueva;
    @FXML private PasswordField txtCambiarClaveConfirmar;
    @FXML private Label lblCambiarClaveError;

    private final LoginService loginService = new LoginService();
    private Usuario usuario;

    public void inicializar(Usuario usuario) { this.usuario = usuario; }

    @FXML
    public void onAceptar(ActionEvent event) {
        lblCambiarClaveError.setText("");
        if (!txtCambiarClaveNueva.getText().equals(txtCambiarClaveConfirmar.getText())) {
            lblCambiarClaveError.setText("La clave nueva y su confirmación no coinciden.");
            return;
        }
        try {
            loginService.cambiarClave(usuario, txtCambiarClaveActual.getText(), txtCambiarClaveNueva.getText());
            ((Stage) txtCambiarClaveActual.getScene().getWindow()).close();
        } catch (CredencialesInvalidasException e) {
            lblCambiarClaveError.setText(e.getMessage());
        }
    }

    @FXML
    public void onCancelar(ActionEvent event) {
        ((Stage) txtCambiarClaveActual.getScene().getWindow()).close();
    }
}