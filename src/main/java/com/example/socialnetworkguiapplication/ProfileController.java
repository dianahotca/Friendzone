package com.example.socialnetworkguiapplication;

import domain.*;
import javafx.application.Platform;
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
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import repository.paging.Page;
import repository.paging.PageableImplementation;

import javax.swing.colorchooser.DefaultColorSelectionModel;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ProfileController implements Initializable,FriendRequestListener, Observer {
    public TableView<UserModel> friendsTable;
    public TableColumn<UserModel,String> friendEmailColumn;
    public TableColumn<UserModel,String> friendFirstNameColumn;
    public TableColumn<UserModel,String> friendLastNameColumn;
    public TableColumn<UserModel,String> friendshipDateColumn;
    public Button addFriendsButton;
    public Button friendRequestsButton;
    public Button chatButton;
    public Button logOutButton;
    public Button removeFriendButton;
    public ImageView eventNotificationIcon;
    public Label eventNotificationNumberLabel;
    private ObservableList<UserModel> friendsModels = FXCollections.observableArrayList();

    @FXML
    private TextField searchBar;

    @FXML
    private Label loggedEmail;

    Controller controller=SocialNetworkApplication.getController();
    private Stage primaryStage;

    double xOffset = 0;
    double yOffset = 0;
    private Page<UserModel> firstLoadedPage;
    private Page<UserModel> secondLoadedPage;


    public void setController(Controller controller) {
        this.controller = controller;
        loggedEmail.setText(controller.getUser(controller.getLoggedEmail()).getFirstName().concat(" ").concat(controller.getUser(controller.getLoggedEmail()).getLastName()));
        this.controller.addObserver(this);
        initModel();

    }

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }

    private void initModel() {
        firstLoadedPage = controller.getFriends(new PageableImplementation<>(1, 5), controller.getLoggedEmail());
        secondLoadedPage = controller.getFriends(new PageableImplementation<>(2, 5), controller.getLoggedEmail());
        setModel();
    }

    private void setModel() {
        List<UserModel> friends = firstLoadedPage.getContent();
        friends.addAll(secondLoadedPage.getContent());
        friendsModels.setAll(friends);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        friendEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        friendFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        friendLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        friendshipDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        friendsTable.setItems(friendsModels);
        searchBar.textProperty().addListener(o->handleSearch());

        Platform.runLater(() -> {
            ScrollBar tvScrollBar = (ScrollBar) friendsTable.lookup(".scroll-bar:vertical");
            tvScrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
                if ((Double) newValue == 0.0) {
                    if (firstLoadedPage.getPageable().getPageNumber() > 1) {
                        secondLoadedPage = firstLoadedPage;
                        firstLoadedPage = controller.getFriends(firstLoadedPage.previousPageable(), controller.getLoggedEmail());
                        setModel();
                    }
                } else if ((Double) newValue == 1.0) {
                    if (secondLoadedPage.getContent().size() == secondLoadedPage.getPageable().getPageSize()) {
                        Page<UserModel> newUsers = controller.getFriends(secondLoadedPage.nextPageable(), controller.getLoggedEmail());

                        if (!newUsers.getContent().isEmpty()) {
                            firstLoadedPage = secondLoadedPage;
                            secondLoadedPage = newUsers;
                            setModel();
                        }
                    }
                }
            });
        });
        Set<Event> tomorrowEvents=controller.getTomorrowEvents();
        int tomorrowEventsNumber=tomorrowEvents.size();
        if(tomorrowEventsNumber!=0) {
            eventNotificationNumberLabel.setText(String.valueOf(tomorrowEventsNumber));
            eventNotificationNumberLabel.setVisible(true);
            eventNotificationIcon.setVisible(true);
        }
    }

    public ObservableList<UserModel> getTableData(){
        friendsModels=FXCollections.observableArrayList();
        List<UserDto> loggedUsersFriends= SocialNetworkApplication.getController().getFriends(SocialNetworkApplication.getController().getLoggedEmail());
        for (UserDto friend:loggedUsersFriends) {
            UserModel friendModel=new UserModel(SocialNetworkApplication.getController().getUserEmail(friend.getFirstName(),friend.getLastName()),friend.getFirstName(),friend.getLastName(),friend.getDate());
            friendsModels.add(friendModel);
        }
        return friendsModels;
    }

    public void onFriendRequestsButtonClick() throws IOException {
        FXMLLoader friendRequestsWindowLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("friend-requests-view2.fxml"));
        Stage friendRequestsStage=new Stage();
        Scene friendRequestsScene = new Scene(friendRequestsWindowLoader.load());
        friendRequestsStage.setTitle("FriendRequests");
        friendRequestsStage.setScene(friendRequestsScene);
        friendRequestsStage.initModality(Modality.APPLICATION_MODAL);
        friendRequestsStage.show();
        ((FriendRequestController)friendRequestsWindowLoader.getController()).addListener(this);
    }

    @Override
    public void onFriendRequestAccepted(Friendship friendship) {
        friendsTable.setItems(friendsModels);
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
    void onRemoveFriendButtonClicked(ActionEvent event) {
        UserModel selected = friendsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            controller.removeFriend(selected.getEmail());
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Remove Friend", "Successfully removed!");
            friendsTable.setItems(friendsModels);
        } else {
            MessageAlert.showErrorMessage(null, "Please select an user!");
        }
    }

    void handleSearch(){
        Predicate<UserModel> p1 = n->n.getFirstName().contains(searchBar.getText());
        Predicate<UserModel> p2 = n->n.getLastName().contains(searchBar.getText());
        List<UserModel> userModelList = this.getTableData().stream().filter(p1.or(p2)).collect(Collectors.toList());
        friendsModels.setAll(userModelList);
        friendsTable.setItems(friendsModels);
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

    @Override
    public void update(Observable o, Object arg) {
        initModel();
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
