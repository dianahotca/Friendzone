package com.example.socialnetworkguiapplication;
import domain.validators.*;
import domain.validators.exceptions.EntityNullException;
import domain.validators.exceptions.ExistenceException;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.UtilMethods;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    public TextField firstNameTextField;
    public TextField lastNameTextField;
    public TextField emailTextField;
    public TextField passwordTextField;
    public Button registerButton;
    private Controller controller;
    private Stage primaryStage;

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void onRegisterButtonClick() throws IOException {
        String email = emailTextField.getText();
        String password = passwordTextField.getText();
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        try {
            SocialNetworkApplication.getController().addUser(firstName, lastName, email, password);
            FXMLLoader logInWindowLoader=new FXMLLoader(SocialNetworkApplication.class.getResource("log-in-view.fxml"));
            Scene logInScene = new Scene(logInWindowLoader.load(), 612, 341);
            primaryStage.setTitle("Log In");
            primaryStage.setScene(logInScene);
            LogInController logInController = logInWindowLoader.getController();
            logInController.setController(controller);
            logInController.setStage(primaryStage);

        } catch (EntityNullException | ValidationException | ExistenceException exc) {
            UtilMethods.showErrorDialog(exc.getMessage());
        }
    }
}
