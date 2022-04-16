package com.example.socialnetworkguiapplication;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AddFriendController implements Initializable {
    private Controller controller;
    private Stage primaryStage;
    private ObservableList<User> model = FXCollections.observableArrayList();

    double xOffset = 0;
    double yOffset = 0;

    public void setController(Controller controller) {
        this.controller = controller;
        initModel();
    }

    public void setStage(Stage stage) {
        this.primaryStage = stage;
        initModel();
        usersTable.setItems(model);
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
    public void onUnsendRequestButtonClick(ActionEvent actionEvent) {
        User selectedUser=usersTable.getSelectionModel().getSelectedItem();
        if(selectedUser!=null){
            try {
                controller.unsendRequest(selectedUser.getEmail());
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Friend Request", "Successfully deleted!");
            }
            catch (ExistenceException ex){
                MessageAlert.showErrorMessage(null,ex.getMessage());
            }
        }else
            MessageAlert.showErrorMessage(null,"Please select an user!");
    }

    @FXML
    void onBackButtonClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("main-profile-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

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
        primaryStage.setTitle("Add Friend");
        primaryStage.setScene(chatScene);
        ChatController chatController = chatWindowLoader.getController();
        chatController.setController(controller);
        chatController.setStage(primaryStage);
    }
}
