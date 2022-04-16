package com.example.socialnetworkguiapplication;
import domain.Event;
import domain.Friendship;
import domain.User;
import domain.validators.exceptions.ExistenceException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AddFriendController implements Initializable , Observer{
    public Label eventNotificationNumberLabel;
    public ImageView eventNotificationIcon;
    private Controller controller=SocialNetworkApplication.getController();
    private Stage primaryStage;
    private ObservableList<User> model = FXCollections.observableArrayList();

    double xOffset = 0;
    double yOffset = 0;
    @FXML
    private Label loggedEmail;

    public void setController(Controller controller) {
        this.controller = controller;
        loggedEmail.setText(controller.getUser(controller.getLoggedEmail()).getFirstName().concat(" ").concat(controller.getUser(controller.getLoggedEmail()).getLastName()));
        this.controller.addObserver(this);
        initModel();

    }

    public void setStage(Stage stage) {
        this.primaryStage = stage;
        /*initModel();
        usersTable.setItems(model);*/
    }

    @FXML
    private TextField searchBar;

    @FXML
    private TableColumn<User, String> userEmailColumn;

    @FXML
    private TableColumn<User, String> userFirstNameColumn;

    @FXML
    private TableColumn<User, String> userLastNameColumn;

    @FXML
    private TableView<User> usersTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        userFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        userLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        usersTable.setItems(model);
        searchBar.textProperty().addListener(o->handleSearch());
        Set<Event> tomorrowEvents=controller.getTomorrowEvents();
        int tomorrowEventsNumber=tomorrowEvents.size();
        if(tomorrowEventsNumber!=0) {
            eventNotificationNumberLabel.setText(String.valueOf(tomorrowEventsNumber));
            eventNotificationNumberLabel.setVisible(true);
            eventNotificationIcon.setVisible(true);
        }
    }

    private void initModel(){
        Iterable<User> users = controller.getAllUsers();
        List<User> allUsers = new ArrayList<>();
        users.forEach(allUsers::add);
        List<User> userList = allUsers.stream().filter(x-> !controller.getFriendsOfUser(controller.getUser(controller.getLoggedEmail())).contains(x) && !x.getEmail().equals(controller.getLoggedEmail())).collect(Collectors.toList());
        model.setAll(userList);
    }

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
    void onSendRequestButtonClick(ActionEvent event) {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if(selected != null){
            try {
                controller.sendRequest(selected.getEmail());
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Friend Request", "Successfully sent!");
            }
            catch (ExistenceException ex){
                MessageAlert.showErrorMessage(null,ex.getMessage()+"\nChanged your mind about "+selected.getEmail()+"?\nSelect him/her again and click Unsend Request.");
            }
        }
        else{
            MessageAlert.showErrorMessage(null,"Please select an user!");
        }
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

    void handleSearch(){
        Predicate<User> p1 = n->n.getFirstName().contains(searchBar.getText());
        Predicate<User> p2 = n->n.getLastName().contains(searchBar.getText());
        Iterable<User> users = controller.getAllUsers();
        List<User> allUsers = new ArrayList<>();
        users.forEach(allUsers::add);
        List<User> userList = allUsers.stream().filter(x->!controller.getFriendsOfUser(controller.getUser(controller.getLoggedEmail())).contains(x) && !x.getEmail().equals(controller.getLoggedEmail())).collect(Collectors.toList());
        List<User> newUserList = userList.stream().filter(p1.or(p2)).collect(Collectors.toList());
        model.setAll(newUserList);

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

    public void onChatButtonClicked(ActionEvent actionEvent) throws IOException {
        FXMLLoader chatWindowLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("chat-view.fxml"));
        Scene chatScene = new Scene(chatWindowLoader.load());
        chatScene.setFill(Color.TRANSPARENT);
        primaryStage.setTitle("Add Friend");
        primaryStage.setScene(chatScene);
        ChatController chatController = chatWindowLoader.getController();
        chatController.setController(controller);
        chatController.setStage(primaryStage);
    }

    @Override
    public void update(Observable o, Object arg) {
        initModel();
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

    /*@FXML
    public void onEventsButtonCliked() throws  IOException{
        FXMLLoader eventsWindowLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("statistics.fxml"));
        Scene events = new Scene(eventsWindowLoader.load());
        events.setFill(Color.TRANSPARENT);
        events.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        events.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }
        });
        primaryStage.setScene(events);
        EventsController eventsController = eventsWindowLoader.getController();
        eventsController.setController(controller);
        eventsController.setStage(primaryStage);
    }*/
}
