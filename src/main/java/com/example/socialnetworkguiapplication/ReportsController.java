package com.example.socialnetworkguiapplication;

import domain.Event;
import domain.Friendship;
import domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReportsController implements Initializable {

    public ImageView eventNotificationIcon;
    public Label eventNotificationNumberLabel;
    private Controller controller=SocialNetworkApplication.getController();
    private Stage primaryStage;
    double xOffset = 0;
    double yOffset = 0;
    @FXML
    private DatePicker endDate;
    @FXML
    private DatePicker startDate;
    @FXML
    private TextField friendEmail;
    @FXML
    private Label loggedEmail;

    public void setController(Controller controller) {
        this.controller = controller;
        loggedEmail.setText(controller.getUser(controller.getLoggedEmail()).getFirstName().concat(" ").concat(controller.getUser(controller.getLoggedEmail()).getLastName()));

    }

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    void onlogOutButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader logOutWindowLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("log-in-view.fxml"));
        Scene logInScene = new Scene(logOutWindowLoader.load(), 612,341);
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
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }
        });
        primaryStage.setTitle("LogIn");
        primaryStage.setScene(logInScene);
        LogInController logInController = logOutWindowLoader.getController();
        logInController.setController(controller);
        logInController.setStage(primaryStage);
    }
    @FXML
    public void onFriendRequestsButtonClick() throws IOException {
        FXMLLoader friendRequestsWindowLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("friend-requests-view2.fxml"));
        Stage friendRequestsStage=new Stage();
        Scene friendRequestsScene = new Scene(friendRequestsWindowLoader.load());
        friendRequestsScene.setFill(Color.TRANSPARENT);
        friendRequestsStage.setTitle("FriendRequests");
        friendRequestsStage.setScene(friendRequestsScene);
        friendRequestsStage.initModality(Modality.APPLICATION_MODAL);
        friendRequestsStage.show();
    }

    @FXML
    public void onAddFriendButtonClicked() throws IOException {
        FXMLLoader addFriendWindowLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("add-friends-view2.fxml"));
        Scene addFriendScene = new Scene(addFriendWindowLoader.load());
        addFriendScene.setFill(Color.TRANSPARENT);
        primaryStage.setTitle("Add Friend");
        primaryStage.setScene(addFriendScene);
        AddFriendController addFriendController = addFriendWindowLoader.getController();
        addFriendController.setController(controller);
        addFriendController.setStage(primaryStage);
    }

    @FXML
    private void onStatisticsButtonCliked() throws IOException {
        FXMLLoader statisticsWindowLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("statistics.fxml"));
        Scene statistics = new Scene(statisticsWindowLoader.load(), 612,341);
        statistics.setFill(Color.TRANSPARENT);
        statistics.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        statistics.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }
        });
        primaryStage.setTitle("Statistics");
        primaryStage.setScene(statistics);
        ReportsController reportsController = statisticsWindowLoader.getController();
        reportsController.setController(controller);
        reportsController.setStage(primaryStage);
    }


    @FXML
    void onGeneralRaportButtonClicked(ActionEvent event) {
        LocalDate from = startDate.getValue();
        LocalDate to = endDate.getValue();

        if (from.isAfter(to)) {
            MessageAlert.showErrorMessage(null, "Invalid time interval!");
        } else {
            try {
                controller.saveActivity(controller.getUser(controller.getLoggedEmail()), Timestamp.valueOf(from.atStartOfDay()), Timestamp.valueOf(to.atStartOfDay().plusDays(1).minusSeconds(1)));
                MessageAlert.showMessage(null,Alert.AlertType.INFORMATION,"Succesfully generated","Your raport has been generated to D:/ProiectExtins");
            } catch (IOException e) {
                MessageAlert.showErrorMessage(null, "Could not save report as pdf!");
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onFriendRaportButtonClicked(ActionEvent event) {
        LocalDate from = startDate.getValue();
        LocalDate to = endDate.getValue();

        if (from.isAfter(to)) {
            MessageAlert.showErrorMessage(null, "Invalid time interval!");
        } else {
            if (friendEmail.getText().isEmpty()) {
                MessageAlert.showErrorMessage(null, "Please write an email first!");
                return;
            }

            try {
                controller.saveConversation(controller.getLoggedEmail(), friendEmail.getText(), Timestamp.valueOf(from.atStartOfDay().plusDays(1).minusSeconds(1)),
                        Timestamp.valueOf(to.atStartOfDay()));
                MessageAlert.showMessage(null,Alert.AlertType.INFORMATION,"Succesfully generated","Your raport has been generated to D:/ProiectExtins");
            } catch (IOException e) {
                MessageAlert.showErrorMessage(null, "Could not save report as pdf!");
                e.printStackTrace();
            }
        }
    }

    public void onChatButtonClicked(ActionEvent actionEvent) throws IOException {
        FXMLLoader chatWindowLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("chat-view.fxml"));
        Scene chatScene = new Scene(chatWindowLoader.load());
        chatScene.setFill(Color.TRANSPARENT);
        primaryStage.setTitle("Chat");
        primaryStage.setScene(chatScene);
        ChatController chatController = chatWindowLoader.getController();
        chatController.setController(controller);
        chatController.setStage(primaryStage);
    }

    @FXML
    void onBackButtonClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("main-profile-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setTitle("FriendZone");
        primaryStage.setScene(scene);

        ProfileController profileController = fxmlLoader.getController();
        profileController.setController(controller);
        profileController.setStage(primaryStage);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Set<Event> tomorrowEvents=controller.getTomorrowEvents();
        int tomorrowEventsNumber=tomorrowEvents.size();
        if(tomorrowEventsNumber!=0) {
            eventNotificationNumberLabel.setText(String.valueOf(tomorrowEventsNumber));
            eventNotificationNumberLabel.setVisible(true);
            eventNotificationIcon.setVisible(true);
        }
    }

    public void onEventsButtonClicked(ActionEvent actionEvent) throws IOException {
        FXMLLoader eventsWindowLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("events-view.fxml"));
        Scene eventsScene = new Scene(eventsWindowLoader.load());
        eventsScene.setFill(Color.TRANSPARENT);
        eventsScene.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        eventsScene.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });
        primaryStage.setTitle("Events");
        primaryStage.setScene(eventsScene);
        EventsController eventsController = eventsWindowLoader.getController();
        eventsController.setController(controller);
        eventsController.setStage(primaryStage);
    }
}
