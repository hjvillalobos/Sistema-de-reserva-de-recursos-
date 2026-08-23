// controlador/PrincipalController.java
package una.eif206.reservas.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import una.eif206.reservas.modelo.Usuario;

public class PrincipalController {
    @FXML private Label lblBienvenida;

    public void inicializar(Usuario usuario) {
        lblBienvenida.setText("Bienvenido, " + usuario.getId() + " (" + usuario.getRol() + ")");
    }
}