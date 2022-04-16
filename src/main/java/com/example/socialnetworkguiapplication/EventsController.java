package com.example.socialnetworkguiapplication;

import domain.Event;
import domain.validators.ValidationException;
import domain.validators.exceptions.EntityNullException;
import domain.validators.exceptions.ExistenceException;
import domain.validators.exceptions.NotExistenceException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.UtilMethods;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EventsController implements Initializable,Observer {
    public Button createEventButton;
    public Label upcomingEventsLabel;
    public Button unsubscribeButton;
    public Button subscribeButton;
    public Label publicEventsLabel;
    public Label chooseEventsLabel;
    public RadioButton subscribedEventsRadioButton;
    public RadioButton publicEventsRadioButton;
    public TextField eventDescription;
    public DatePicker eventDatePicker;
    public Label createEventLabel;
    public Label eventNotificationNumberLabel;
    public ImageView eventNotificationIcon;
    Controller controller = SocialNetworkApplication.getController();
    Stage primaryStage;
    double xOffset = 0;
    double yOffset = 0;
    private ObservableList<Event> publicEvents = FXCollections.observableArrayList();
    private ObservableList<Event> comingEvents = FXCollections.observableArrayList();
    @FXML
    private TableColumn<Event,String> publicEventDescriptionColumn;
    @FXML
    private TableColumn<Event,String> publicEventDateColumn;
    @FXML
    public TableView<Event> comingEventsTable;
    @FXML
    private TableColumn<Event,String> comingEventDescriptionColumn;
    @FXML
    private TableColumn<Event,String> comingEventDateColumn;
    @FXML
    public TableView<Event> publicEventsTable;

    @FXML
    private Label loggedEmail;

    public void setController(Controller controller) {
        this.controller=controller;
        this.controller.addObserver(this);
    }

    public void setStage(Stage primaryStage) {
        this.primaryStage=primaryStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
        publicEventDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        publicEventDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        comingEventDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        comingEventDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        publicEventsTable.setItems(publicEvents);
        comingEventsTable.setItems(comingEvents);
        showInitialElements();
        showEventNotifications();
    }

    private void showInitialElements() {
        createEventLabel.setVisible(true);
        eventDescription.setVisible(true);
        eventDatePicker.setVisible(true);
        createEventButton.setVisible(true);
        chooseEventsLabel.setVisible(true);
        publicEventsRadioButton.setVisible(true);
        subscribedEventsRadioButton.setVisible(true);
        publicEventsLabel.setVisible(false);
        upcomingEventsLabel.setVisible(false);
        publicEventsTable.setVisible(false);
        comingEventsTable.setVisible(false);
        subscribeButton.setVisible(false);
        unsubscribeButton.setVisible(false);
    }

    private void initPublicEventsModel(){
        Set<Event> allEvents = (Set<Event>) controller.getAllEvents();
        publicEvents.setAll(allEvents);
    }

    private void initSubscribedEventsModel(){
        Set<Event> subscribedEvents= (Set<Event>) controller.getSubscribedEvents();
        comingEvents.setAll(subscribedEvents);
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
    private void onStatisticsButtonClicked() throws IOException {
        FXMLLoader statisticsWindowLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("statistics.fxml"));
        Scene statistics = new Scene(statisticsWindowLoader.load());
        statistics.setFill(Color.TRANSPARENT);
        statistics.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        statistics.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });
        primaryStage.setTitle("Statistics");
        primaryStage.setScene(statistics);
        ReportsController reportsController = statisticsWindowLoader.getController();
        reportsController.setController(controller);
        reportsController.setStage(primaryStage);
    }

    @FXML
    void onlogOutButtonClicked() throws IOException {
        FXMLLoader logOutWindowLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("log-in-view.fxml"));
        Scene logInScene = new Scene(logOutWindowLoader.load(), 612,341);
        logInScene.setFill(Color.TRANSPARENT);
        logInScene.setOnMousePressed(event1 -> {
            xOffset = event1.getSceneX();
            yOffset = event1.getSceneY();
        });
        logInScene.setOnMouseDragged(event12 -> {
            primaryStage.setX(event12.getScreenX() - xOffset);
            primaryStage.setY(event12.getScreenY() - yOffset);
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

    @Override
    public void update(Observable o, Object arg) {
        showEventNotifications();
        if(!publicEventsRadioButton.isSelected()){
            initPublicEventsModel();
        }
        else
            initSubscribedEventsModel();
    }

    @FXML
    public void onChatButtonClicked() throws IOException {
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
    public void onCreateEventButtonClicked() {
        String description=eventDescription.getText();
        String date=eventDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Integer id= Math.toIntExact(controller.getPublicEventsNumber()+1);
        try{
            controller.addEvent(id,description,date);
            publicEvents.setAll((Set<Event>)controller.getAllEvents());
            publicEventsTable.setItems(publicEvents);
        }catch ( ValidationException | EntityNullException | ExistenceException exc){
            UtilMethods.showErrorDialog(exc.getMessage());
        }
    }

    @FXML
    public void onSubscribeButtonClicked() {
        Event selectedEvent=publicEventsTable.getSelectionModel().getSelectedItem();
        try {
            controller.subscribeEvent(selectedEvent.getId(), controller.getLoggedEmail());
            comingEvents.setAll((Set<Event>)controller.getSubscribedEvents());
            comingEventsTable.setItems(comingEvents);
        }catch ( EntityNullException | NotExistenceException | ValidationException exc)
        {
            UtilMethods.showErrorDialog(exc.getMessage());
        }
    }

    @FXML
    public void onUnsubscribeButtonClicked() {
        Event selectedEvent=comingEventsTable.getSelectionModel().getSelectedItem();
        try {
            controller.unsubscribeEvent(selectedEvent.getId(), controller.getLoggedEmail());
            comingEvents.setAll((Set<Event>)controller.getSubscribedEvents());
            comingEventsTable.setItems(comingEvents);
        }catch ( EntityNullException | NotExistenceException | ValidationException exc)
        {
            UtilMethods.showErrorDialog(exc.getMessage());
        }
    }

    public void onPublicEventsRadioButtonSelected() {
        showPublicEventsElements();
        initPublicEventsModel();
        publicEventsRadioButton.setSelected(true);
        subscribedEventsRadioButton.setSelected(false);
    }

    private void showPublicEventsElements() {
        publicEventsLabel.setVisible(true);
        upcomingEventsLabel.setVisible(false);
        publicEventsTable.setVisible(true);
        comingEventsTable.setVisible(false);
        subscribeButton.setVisible(true);
        unsubscribeButton.setVisible(false);
    }

    public void onSubscribedEventsRadioButtonSelected() {
        showSubscribedEventsElements();
        initSubscribedEventsModel();
        publicEventsRadioButton.setSelected(false);
        subscribedEventsRadioButton.setSelected(true);
    }

    private void showSubscribedEventsElements() {
        publicEventsLabel.setVisible(false);
        upcomingEventsLabel.setVisible(true);
        publicEventsTable.setVisible(false);
        comingEventsTable.setVisible(true);
        subscribeButton.setVisible(false);
        unsubscribeButton.setVisible(true);
    }

    private void showEventNotifications(){
        Set<Event> tomorrowEvents=controller.getTomorrowEvents();
        int tomorrowEventsNumber=tomorrowEvents.size();
        if(tomorrowEventsNumber!=0) {
            eventNotificationNumberLabel.setText(String.valueOf(tomorrowEventsNumber));
            eventNotificationNumberLabel.setVisible(true);
            eventNotificationIcon.setVisible(true);
            Set<Integer> tomorrowEventsIds=new HashSet<>();
            for (Event upcomingEvent : tomorrowEvents)
                tomorrowEventsIds.add(upcomingEvent.getId());

            comingEventsTable.setRowFactory(tv -> new TableRow<>() {
                @Override
                protected void updateItem(Event item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty)
                        setStyle("");
                    else if (tomorrowEventsIds.contains(item.getId()))
                        setStyle("-fx-background-color: #baffba;");
                    else
                        setStyle("");
                }
            });
        }
    }

    public void onBackProfileClick(ActionEvent actionEvent) throws IOException {
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