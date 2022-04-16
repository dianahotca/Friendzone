package com.example.socialnetworkguiapplication;

import domain.Friendship;
import domain.Tuple;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import utils.Constants;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FriendRequestController implements Initializable {
    public AnchorPane friendRequestsStage;
    public TableView<FriendRequestModel> friendRequestsTable;
    public TableColumn<FriendRequestModel, String> fromUserColumn;
    public TableColumn<FriendRequestModel, String> statusColumn;
    public TableColumn<FriendRequestModel, String> dateColumn;
    public Button acceptRequestButton;
    public Button declineRequestButton;
    private ObservableList<FriendRequestModel> friendRequests;
    private List<FriendRequestListener> listeners = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromUserColumn.setCellValueFactory(new PropertyValueFactory<>("fromUserEmail"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        friendRequests = FXCollections.observableArrayList(SocialNetworkApplication.getController().getRecievedRequests());
        friendRequestsTable.setItems(friendRequests);
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

    public void onFriendRequestsButtonClick(ActionEvent actionEvent) {
    }

    public void onAddFriendButtonClicked(ActionEvent actionEvent) {
    }

    public void onlogOutButtonClicked(ActionEvent actionEvent) {
    }
}
