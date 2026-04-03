package com.synergy.controller.fx;

import com.synergy.App;
import com.synergy.controller.AuthenticationController;
import com.synergy.model.User;
import com.synergy.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;

//controller per la gestione della schermata di accesso
public class LoginController {

	//vabiabili del file login.fxml
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    //
    private AuthenticationController auth = new AuthenticationController();

    //metodo per il tasto accedi
    @FXML
    private void handleLogin() throws IOException {
    	//prende gli imput dell'utente
        String email = emailField.getText();
        String pass = passwordField.getText();

        //tenta il login tramite l'autentication controller
        User user = auth.login(email, pass);

        //se l'utente non è nullo le credeziali sono corrette
        if (user != null) {
        	//memoriazza l'utente loggato
            SessionManager.getInstance().login(user);
            //cambia schermata e carica la dashboard
            App.setRoot("dashboard");
        } else {
        	//mostra un messaggio di errore se le credenziali sono errate
            errorLabel.setText("Email o password errati");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }
    
    //metodo chiamato quando l'utente clicca su ""Non hai un account"
    @FXML
    private void goToRegister() throws IOException {
        App.setRoot("register");
    }
}