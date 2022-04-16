package com.example.socialnetworkguiapplication;
import domain.validators.*;
import domain.validators.exceptions.EntityNullException;
import domain.validators.exceptions.LogInException;
import domain.validators.exceptions.NotExistenceException;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import utils.UtilMethods;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LogInController implements Initializable {
    public Button logInButton;
    public Button registerButton;
    public TextField emailTextField,passwordTextField;
    Controller controller;

    private Stage primaryStage;
    double xOffset = 0;
    double yOffset = 0;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void onLogInButtonClick() throws IOException {
        try {
            String email=emailTextField.getText();
            SocialNetworkApplication.getController().setLoggedEmail(email);
            SocialNetworkApplication.getController().setLoggedPassword(passwordTextField.getText());
            FXMLLoader profileWindowLoader=new FXMLLoader(SocialNetworkApplication.class.getResource("main-profile-view.fxml"));
            Scene profileScene=new Scene(profileWindowLoader.load());
            profileScene.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            profileScene.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    primaryStage.setX(event.getScreenX() - xOffset);
                    primaryStage.setY(event.getScreenY() - yOffset);
                }
            });
            primaryStage.setTitle("FriendZone");
            primaryStage.setScene(profileScene);
            ProfileController profileController = profileWindowLoader.getController();
            profileController.setController(controller);
            profileController.setStage(primaryStage);

        }catch (ValidationException | EntityNullException | LogInException | NotExistenceException exc){
            UtilMethods.showErrorDialog(exc.getMessage());
        }
    }

    @FXML
    public void onRegisterButtonClick() throws IOException {
        FXMLLoader registerWindowLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("register-view.fxml"));
        Scene registerScene = new Scene(registerWindowLoader.load(), 500, 500);
        primaryStage.setTitle("Register");
        primaryStage.setScene(registerScene);
        RegisterController registerController = registerWindowLoader.getController();
        registerController.setController(controller);
        registerController.setStage(primaryStage);
        primaryStage.show();
    }
}
