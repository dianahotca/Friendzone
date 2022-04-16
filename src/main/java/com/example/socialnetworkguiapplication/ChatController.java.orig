package com.example.socialnetworkguiapplication;

import domain.Event;
import domain.Message;
import domain.User;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.tools.Utils;
import utils.Constants;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class ChatController implements Observer {
    public TextField searchBar;
    public HBox chatBox;
    public VBox chatVBox;
    public Label friendsName;
    public AnchorPane conversationPane;
    public AnchorPane defaultPane;
    public AnchorPane mainPane;
    public TextField messageTextField;
    public Label eventNotificationNumberLabel;
    public ImageView eventNotificationIcon;
    Controller controller = SocialNetworkApplication.getController();
    Stage primaryStage;
    double xOffset = 0;
    double yOffset = 0;
    List<User> options = controller.getFriendsOfUser(controller.getUser(controller.getLoggedEmail()));
    TextField text = new TextField();
    private List<CheckBox> checkBoxList = new ArrayList<>();
    private Label messageToReply = null;
    @FXML
    private Label loggedEmail;

    @FXML
    private CheckBox replyToManyCheckBox;

    @FXML
    BorderPane borderPaneChat;


    @FXML
    private ScrollPane scrollPane;
    public void setController(Controller controller) {
        this.controller=controller;
        loggedEmail.setText(controller.getUser(controller.getLoggedEmail()).getFirstName().concat(" ").concat(controller.getUser(controller.getLoggedEmail()).getLastName()));
        this.controller.addObserver(this);
    }

    public void setStage(Stage primaryStage) {
        this.primaryStage=primaryStage;

    }

    @FXML
    public void initialize() {
        List<String> emails = new ArrayList<>();
        for(User user : options)
            emails.add(user.getEmail());

        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            if(chatVBox.getChildren().size()>1){
                chatVBox.getChildren().remove(1);
            }

            chatVBox.getChildren().add(populateDropDownMenu(newValue, emails));
        });

        List<User> friends = controller.getFriendsOfUser(controller.getUser(controller.getLoggedEmail()));
        double layoutY = 2d;
        double layoutX = 5d;
        checkBoxList.clear();
        for(User user : friends){
            Label name = new Label(user.getFirstName() + user.getLastName());
            name.setTranslateX(29);
            name.setTranslateY(layoutY);
            name.setFont(new Font(20));
            CheckBox checkBox = new CheckBox();
            checkBox.setTranslateY(layoutY);
            checkBox.setTranslateX(layoutX);
            checkBox.setId(user.getEmail());
            checkBoxList.add(checkBox);
            defaultPane.getChildren().add(name);
            defaultPane.getChildren().add(checkBox);
            layoutY += 25;
        }
        borderPaneChat.setStyle(borderPaneChat.getStyle() + ";" + "-fx-border-radius: 20px; -fx-background-radius: 20px;");
        conversationPane.setStyle(conversationPane.getStyle() + ";" + "-fx-border-radius: 20px; -fx-background-radius: 20px;");
        defaultPane.setStyle(defaultPane.getStyle() + ";" + "-fx-border-radius: 20px; -fx-background-radius: 20px;");
        Set<Event> tomorrowEvents=controller.getTomorrowEvents();
        int tomorrowEventsNumber=tomorrowEvents.size();
        if(tomorrowEventsNumber!=0) {
            eventNotificationNumberLabel.setText(String.valueOf(tomorrowEventsNumber));
            eventNotificationNumberLabel.setVisible(true);
            eventNotificationIcon.setVisible(true);
        }
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
        Scene statistics = new Scene(statisticsWindowLoader.load());
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
    public void sendMessage(ActionEvent actionEvent) {
        List<User> toSend = new ArrayList<>();
        String message = messageTextField.getText();
        if(!friendsName.getText().isEmpty()){
            User to = controller.getUser(friendsName.getText());
            toSend.add(to);
        }
        else if(checkBoxList.size() > 0){
            for(CheckBox checkBox : checkBoxList){
                if(checkBox.isSelected())
                    toSend.add(controller.getUser(checkBox.getId()));
            }
        }
        controller.sendMessage(toSend,message);
    }

    @FXML
    public void replyMessage(ActionEvent actionEvent) {
        String message = messageTextField.getText();
        if(!friendsName.getText().isEmpty()) {
            if (replyToManyCheckBox.isSelected()) {
                controller.replyAll(Long.parseLong(messageToReply.getId()), message);
                if (messageToReply == null)
                    MessageAlert.showErrorMessage(null, "Select a message to reply!");
            } else {
                controller.replyMessage(Long.parseLong(messageToReply.getId()), message);
                messageToReply.setStyle("-fx-background-color: #C0C0C0");
                messageToReply = null;
            }
        }

    }

    public Node populateDropDownMenu(String text, List<String> emails){
        VBox dropDownMenu = new VBox();
        dropDownMenu.setAlignment(Pos.CENTER_LEFT);
        dropDownMenu.setStyle("-fx-background-color: #639690");

        for(String option : emails){
            if(!text.replace(" ", "").isEmpty() && option.toUpperCase().contains(text.toUpperCase())){
                Label label = new Label(option);
                label.setPrefSize(276,38);
                dropDownMenu.getChildren().add(label);
                label.setOnMouseClicked(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.getButton().equals(MouseButton.PRIMARY)){
                            if(event.getClickCount() == 1){
                                searchBar.setText(label.getText());
                                label.setStyle(label.getStyle() + ";"+"-fx-background-color: #a99d9d");
                                dropDownMenu.getChildren().clear();
                            }
                        }
                    }
                });
            }
        }
        return dropDownMenu;
    }



    @FXML
    public void onShowConversationClicked(){
        defaultPane.setVisible(false);
        conversationPane.setVisible(true);
        String friendEmail = searchBar.getText();
        friendsName.setText(friendEmail);
        List<Message> conversation = controller.viewConversation(friendEmail,controller.getLoggedEmail());
        double layoutY = 10d;
        for(Message message:conversation){
            Label replyMessage = null;
            boolean hasReply = false;
            if(message.getReply() != null) {
                hasReply = true;
                replyMessage = new Label("Replied: " + message.getReply().getMessage());
                replyMessage.setFont(new Font(15));
                replyMessage.setStyle("-fx-border-radius: 20px; -fx-background-radius: 20px;");
                replyMessage.setTranslateY(layoutY - 30);
            }

            Label messageLabel = new Label(message.getMessage());
            messageLabel.setFont(new Font(20));
            messageLabel.setStyle("-fx-border-radius: 20px; -fx-background-radius: 20px;");
            messageLabel.setTranslateY(layoutY);
            messageLabel.setId(message.getId().toString());
            messageLabel.setOnMouseClicked(new EventHandler<MouseEvent>(){

                @Override
                public void handle(MouseEvent event) {
                    if(event.getButton().equals(MouseButton.PRIMARY)){
                        if(event.getClickCount() == 1){
                            messageToReply = messageLabel;
                            messageToReply.setStyle(messageToReply.getStyle() + ";"+"-fx-background-color: #a99d9d");

                        }
                        if(event.getClickCount() == 2){
                            messageToReply.setStyle(messageToReply.getStyle() +";" + "-fx-background-color:  #C0C0C0");
                            messageToReply = null;
                        }
                    }
                }
            });

            Label dateLabel = new Label("Sent on " + message.getDate().format(Constants.DATE_TIME_FORMATTER));
            dateLabel.setFont(new Font(10));
            dateLabel.setStyle("-fx-border-radius: 20 20 0 0; -fx-background-radius: 20px;");
            dateLabel.setTranslateY(layoutY + 40);

            layoutY += 90;
            if(message.getFrom().getEmail().equals(controller.getLoggedEmail())){
                messageLabel.setTranslateX(450);
                messageLabel.setTextFill(Color.web("#FFFFFF"));
                messageLabel.setStyle(messageLabel.getStyle() + ";" +"-fx-background-color: #9260cc ; -fx-border-radius: 5,4;");

                if(hasReply){
                    replyMessage.setTranslateX(435);
                }
                dateLabel.setTranslateX(470);
            }
            else{
                messageLabel.setTranslateX(20);
                messageLabel.setStyle(messageLabel.getStyle() + ";" + "-fx-background-color: #C0C0C0");
                if(hasReply){
                    replyMessage.setTranslateX(5);
                }
                dateLabel.setTranslateX(40);
            }
            if(hasReply){
                conversationPane.getChildren().add(replyMessage);
            }
            conversationPane.getChildren().add(messageLabel);
            conversationPane.getChildren().add(dateLabel);
        }
    }

    @FXML
    void onBackButtonClick(ActionEvent event) throws IOException {
        conversationPane.setVisible(false);
        defaultPane.setVisible(true);
        friendsName.setText("");
    }

    @Override
    public void update(Observable o, Object arg) {
        if(friendsName.getText().isEmpty()) {
            defaultPane.getChildren().clear();
            initialize();
        }
        else{
            conversationPane.getChildren().clear();
            onShowConversationClicked();
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

    @FXML
    public void onBackProfileClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("main-profile-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setTitle("FriendZone");
        primaryStage.setScene(scene);

        ProfileController profileController = fxmlLoader.getController();
        profileController.setController(controller);
        profileController.setStage(primaryStage);

    }



}