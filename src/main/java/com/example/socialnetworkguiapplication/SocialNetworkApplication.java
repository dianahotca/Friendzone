package com.example.socialnetworkguiapplication;

import domain.*;
import domain.validators.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.input.MouseEvent;
import repository.Repository;
import repository.db.*;
import service.*;

import java.io.IOException;

public class SocialNetworkApplication extends Application {
    private static Controller controller;
    Scene logInScene;
    private double xOffset = 0;
    private double yOffset = 0;


    public static Controller getController() {
        return controller;
    }

    public static void setController(Controller controller) {
        SocialNetworkApplication.controller = controller;
    }

    @Override
    public void start(Stage stage) throws IOException {
        Validator<User> userValidator = new UserValidator();
        Validator<String> emailValidator = new EmailValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        Validator<Event> eventValidator=new EventValidator();
        Repository<String, User> userDbRepository=new UserDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres","anda", userValidator,emailValidator);
        Repository<Tuple<String, String>, Friendship> friendshipDbRepository =new FriendshipDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres","anda",friendshipValidator);
        Repository<Long, Message> messageDbRepository = new MessageDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres","anda");
        Repository<Tuple<String, String>, FriendRequest> friendRequestDbRepository=new FriendRequestDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres","anda");
        Repository<Integer,Event> eventDbRepository=new EventDbRepository("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres","anda",eventValidator);
        Service<String, User> userService = new UserService(userDbRepository);
        Service<Tuple<String, String>, Friendship> friendshipService = new FriendshipService(friendshipDbRepository);
        Service<Long,Message> messageService = new MessageService(messageDbRepository);
        Service<Tuple<String, String>, FriendRequest> friendRequestService=new FriendRequestService(friendRequestDbRepository);
        Service<Integer,Event> eventService=new EventService((EventDbRepository) eventDbRepository);
        Controller controller = new Controller((UserService) userService,(FriendshipService) friendshipService,(MessageService) messageService,(FriendRequestService) friendRequestService, (EventService) eventService);
        setController(controller);FXMLLoader logInWindowLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("log-in-view.fxml"));
        logInScene = new Scene(logInWindowLoader.load(),612,341);
        logInScene.setFill(Color.TRANSPARENT);
        logInScene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        logInScene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
        stage.setTitle("LogIn");
        stage.setScene(logInScene);
        stage.initStyle(StageStyle.TRANSPARENT);
        LogInController logInController = logInWindowLoader.getController();
        logInController.setController(controller);
        logInController.setStage(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
