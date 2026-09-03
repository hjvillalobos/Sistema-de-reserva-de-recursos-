package una.eif206.reservas.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import una.eif206.reservas.DTO.UsuarioDTO;
import una.eif206.reservas.logica.ValidacionException;
import una.eif206.reservas.servicios.LoginService;

public class CambiarClaveController {

    @FXML private PasswordField txtCambiarClaveActual;
    @FXML private PasswordField txtCambiarClaveNueva;
    @FXML private PasswordField txtCambiarClaveConfirmar;
    @FXML private Label lblCambiarClaveError;

    private final LoginService loginService = new LoginService();
    private UsuarioDTO usuario;

    public void inicializar(UsuarioDTO usuario) { this.usuario = usuario; }

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
        } catch (ValidacionException e) {
            lblCambiarClaveError.setText(e.getMessage());
        }
    }

    @FXML
    public void onCancelar(ActionEvent event) {
        ((Stage) txtCambiarClaveActual.getScene().getWindow()).close();
    }
}