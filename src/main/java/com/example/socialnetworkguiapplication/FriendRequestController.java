package com.example.socialnetworkguiapplication;

import com.example.socialnetworkguiapplication.FriendRequestSentModel;
import domain.Friendship;
import domain.Tuple;
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
import utils.Constants;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class FriendRequestController implements Initializable, Observer {
    public AnchorPane friendRequestsStage;
    public TableView<FriendRequestModel> friendRequestsTable;
    public TableColumn<FriendRequestModel, String> fromUserColumn;
    public TableColumn<FriendRequestModel, String> statusColumn;
    public TableColumn<FriendRequestModel, String> dateColumn;

    public TableView<FriendRequestSentModel> sendFriendRequestsTable;
    public TableColumn<FriendRequestSentModel, String> toUserColumn;
    public TableColumn<FriendRequestSentModel, String> statusColumn1;
    public TableColumn<FriendRequestSentModel, String> dateColumn1;

    public Button acceptRequestButton;
    public Button declineRequestButton;
    public Label sentRequestsLabel;
    public Label selectReceivedRequestLabel;
    public Button unsendRequestButton;
    public Label selectSentRequestLabel;
    public Label receivedRequestsLabel;
    public Label chooseRequestsLabel;
    public RadioButton sentRadioButton;
    public RadioButton receivedRadioButton;
    private ObservableList<FriendRequestModel> friendRequests;
    private ObservableList<FriendRequestSentModel> sentRequests;
    private final List<FriendRequestListener> listeners = new ArrayList<>();
    private Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
        this.controller.addObserver(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromUserColumn.setCellValueFactory(new PropertyValueFactory<>("fromUserEmail"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        friendRequests = FXCollections.observableArrayList(SocialNetworkApplication.getController().getRecievedRequests());
        friendRequestsTable.setItems(friendRequests);
        showInitialElements();
    }

    private void showInitialElements() {
        friendRequestsTable.setVisible(true);
        chooseRequestsLabel.setVisible(true);
        sentRadioButton.setVisible(true);
        receivedRadioButton.setVisible(true);
        sentRequestsLabel.setVisible(false);
        receivedRequestsLabel.setVisible(false);
        selectReceivedRequestLabel.setVisible(false);
        selectSentRequestLabel.setVisible(false);
        unsendRequestButton.setVisible(false);
        acceptRequestButton.setVisible(false);
        declineRequestButton.setVisible(false);
        sentRequestsLabel.setVisible(false);
        receivedRequestsLabel.setVisible(false);
        selectReceivedRequestLabel.setVisible(false);
        selectSentRequestLabel.setVisible(false);
        unsendRequestButton.setVisible(false);
        acceptRequestButton.setVisible(false);
        declineRequestButton.setVisible(false);
    }

    public void initModelRequest(){
        List<FriendRequestModel> friendRequestModelList =  FXCollections.observableArrayList(SocialNetworkApplication.getController().getRecievedRequests());
        friendRequests.setAll(friendRequestModelList);
    }

    public void initModelSent(){
        List<FriendRequestModel> friendRequestSentList =  FXCollections.observableArrayList(SocialNetworkApplication.getController().sentRequests(SocialNetworkApplication.getController().getLoggedEmail()));
        friendRequests.setAll(friendRequestSentList);
    }


    public void onAcceptButtonClicked() {
        FriendRequestModel selectedRequest = friendRequestsTable.getSelectionModel().getSelectedItem();
        SocialNetworkApplication.getController().acceptRequest(selectedRequest.getFromUserEmail());
        for (FriendRequestListener listener : listeners) {
            listener.onFriendRequestAccepted(new Friendship(new Tuple<>(selectedRequest.getFromUserEmail(), SocialNetworkApplication.getController().getLoggedEmail()), LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER)));
        }
        friendRequests = FXCollections.observableArrayList(SocialNetworkApplication.getController().getRecievedRequests());
        friendRequestsTable.setItems(friendRequests);
    }

    public void onDeclineButtonClicked() {
        FriendRequestModel selectedRequest = friendRequestsTable.getSelectionModel().getSelectedItem();
        SocialNetworkApplication.getController().declineRequest(selectedRequest.getFromUserEmail());
        friendRequests = FXCollections.observableArrayList(SocialNetworkApplication.getController().getRecievedRequests());
        friendRequestsTable.setItems(friendRequests);
    }

    public void addListener(FriendRequestListener listener) {
        listeners.add(listener);
    }

    @FXML
    public void onUnsendRequestButtonClick(ActionEvent actionEvent) {
     /*   FriendRequestModel selectedUser=friendRequestsTable.getSelectionModel().getSelectedItem();
        if(selectedUser!=null){
            try {
                SocialNetworkApplication.getController().unsendRequest(selectedUser.getFromUserEmail());
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Friend Request", "Successfully deleted!");
            }
            catch (ExistenceException ex){
                MessageAlert.showErrorMessage(null,ex.getMessage());
            }
        }else
            MessageAlert.showErrorMessage(null,"Please select an user!");
*/
        FriendRequestModel selectedRequest = friendRequestsTable.getSelectionModel().getSelectedItem();
        SocialNetworkApplication.getController().unsendRequest(selectedRequest.getFromUserEmail());
        for (FriendRequestListener listener : listeners) {
            listener.onFriendRequestAccepted(new Friendship(new Tuple<>(selectedRequest.getFromUserEmail(), SocialNetworkApplication.getController().getLoggedEmail()), LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER)));
        }
        friendRequests = FXCollections.observableArrayList(SocialNetworkApplication.getController().getRecievedRequests());
        friendRequestsTable.setItems(friendRequests);

    }

    @Override
    public void update(Observable o, Object arg) {
       if(!sentRadioButton.isSelected()){
           initModelRequest();
       }
       else
        initModelSent();
    }

    public  void onSent(){
        initModelSent();
        fromUserColumn.setCellValueFactory(new PropertyValueFactory<>("fromUserEmail"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        fromUserColumn.setText("To User");
        showSentRequestsElements();
        receivedRadioButton.setSelected(false);
    }

    public  void onReceived(){
        initModelRequest();
        fromUserColumn.setCellValueFactory(new PropertyValueFactory<>("fromUserEmail"));
        fromUserColumn.setText("From User");
        showReceivedRequestsElements();
        sentRadioButton.setSelected(false);
    }

    private void showReceivedRequestsElements(){
        sentRequestsLabel.setVisible(false);
        receivedRequestsLabel.setVisible(true);
        selectReceivedRequestLabel.setVisible(true);
        selectSentRequestLabel.setVisible(false);
        unsendRequestButton.setVisible(false);
        acceptRequestButton.setVisible(true);
        declineRequestButton.setVisible(true);
    }

    private void showSentRequestsElements(){
        sentRequestsLabel.setVisible(true);
        receivedRequestsLabel.setVisible(false);
        selectReceivedRequestLabel.setVisible(false);
        selectSentRequestLabel.setVisible(true);
        unsendRequestButton.setVisible(true);
        acceptRequestButton.setVisible(false);
        declineRequestButton.setVisible(false);
    }
}
