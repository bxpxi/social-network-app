package org.example.socialnetworkfx.socialnetworkfx.controller;

import com.almasb.fxgl.app.services.FXGLAssetLoaderService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.socialnetworkfx.socialnetworkfx.domain.Friendship;
import org.example.socialnetworkfx.socialnetworkfx.domain.FriendshipRequest;
import org.example.socialnetworkfx.socialnetworkfx.domain.ProfilePage;
import org.example.socialnetworkfx.socialnetworkfx.domain.User;
import org.example.socialnetworkfx.socialnetworkfx.domain.event.FriendshipEntityChange;
import org.example.socialnetworkfx.socialnetworkfx.service.FriendshipRequestService;
import org.example.socialnetworkfx.socialnetworkfx.service.FriendshipService;
import org.example.socialnetworkfx.socialnetworkfx.service.MessageService;
import org.example.socialnetworkfx.socialnetworkfx.service.UserService;
import org.example.socialnetworkfx.socialnetworkfx.utils.Observer;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MainMenuView implements Observer<FriendshipEntityChange> {
    @FXML
    public TableColumn firstnameColumn;
    @FXML
    public TableView tableView;
    @FXML
    public TableColumn lastnameColumn;
    @FXML
    public TableColumn<User, String> sinceColumn;
    @FXML
    public Label userNameField;
    @FXML
    public ImageView chatImage;
    @FXML
    public ImageView settingImage;
    @FXML
    public ImageView addImage;
    @FXML
    public ImageView deleteImage;
    @FXML
    public ImageView editImage;
    @FXML
    public Label numberOfRequests;
    @FXML
    public ImageView redDot;

    @FXML
    public Button nextButton;
    @FXML
    public Button previousButton;
    @FXML
    public ComboBox<Integer> itemsPerPageDropdown;
    @FXML
    public Label pageInfoLabel;

    private Long IDUser;
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendshipRequestService requestService;
    private MessageService messageService;
    private Stage stage;
    ObservableList<User> model = FXCollections.observableArrayList();
    ObservableList<FriendshipRequest> model2 = FXCollections.observableArrayList();

    private int pageNumber = 0;
    private int pageSize = 5;

    public void setService(Long IDUser, UserService userService, FriendshipService friendshipService, FriendshipRequestService requestService,MessageService messageService,Stage stage) {
        this.IDUser = IDUser;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.requestService = requestService;
        this.messageService=messageService;
        this.stage = stage;
        friendshipService.addObserver(this);
        initModel();
    }

    public void initialize() {
        firstnameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        lastnameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        sinceColumn.setCellValueFactory(cellData -> {
            Long senderId = cellData.getValue().getID();
            Friendship sender = friendshipService.findOne(senderId, IDUser);
            return new SimpleStringProperty(sender != null ? sender.getDatesince().toString() : "Unknown");
        });
        tableView.setItems(model);

        itemsPerPageDropdown.getItems().addAll(1,2,3,4,5);
        itemsPerPageDropdown.setValue(5);
        itemsPerPageDropdown.setOnAction(this::onItemsPerPageChanged);

        nextButton.setOnAction(this::onNextPage);
        previousButton.setOnAction(this::onPreviousPage);
    }

    private void initModel() {
//        Iterable<User> messages = friendshipService.getFriends(IDUser);
//        List<User> users = StreamSupport.stream(messages.spliterator(), false)
//                .collect(Collectors.toList());
//        model.setAll(users);
//        setUser(IDUser);

        int totalFriends = (int) friendshipService.countFriends(IDUser);

        if (pageNumber * pageSize >= totalFriends) {
            pageNumber = Math.max(0, (totalFriends - 1) / pageSize);
        }

//        List<User> users = StreamSupport.stream(friendshipService.getFriends2(IDUser).spliterator(), false)
//                .skip(pageNumber * pageSize)
//                .limit(pageSize)
//                .collect(Collectors.toList());

        List<User> users = friendshipService.getFriends2(IDUser)
                .getElements()
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

        model.setAll(users);
        setUser(IDUser);
        updatePageNavigation();

        if(requestService.getNoNewRequest(IDUser)>0){
            redDot.setVisible(true);
            numberOfRequests.setVisible(true);
            numberOfRequests.setText(String.valueOf(requestService.getNoNewRequest(IDUser)));
        }
        else{
            redDot.setVisible(false);
            numberOfRequests.setVisible(false);
        }

    }

    public void setUser(Long IDUser) {
//        User user = userService.findOne(IDUser);
//        String fullName = user.getFirstName() + " " + user.getLastName();
//        userNameField.setText(fullName);
        ProfilePage profilePage = userService.getProfilePage(IDUser);
        userNameField.setText(profilePage.getFullName());
    }

    private void updatePageNavigation() {
        int totalFriends = (int) friendshipService.countFriends(IDUser);
        int totalPages = (int) Math.ceil((double) totalFriends / pageSize);
        pageInfoLabel.setText(totalFriends > 0
                ? "Page " + (pageNumber + 1) + " of " + totalPages
                : "No friends found");

        previousButton.setDisable(pageNumber <= 0 || totalFriends == 0);
        nextButton.setDisable(pageNumber >= totalPages - 1 || totalFriends == 0);
    }

    public void onNextPage(ActionEvent actionEvent) {
        if (pageNumber < (int) Math.ceil((double) friendshipService.countFriends(IDUser) / pageSize) - 1) {
            pageNumber++;
            initModel();
        }
    }

    public void onPreviousPage(ActionEvent actionEvent) {
        if (pageNumber > 0) {
            pageNumber--;
            initModel();
        }
    }

    public void onItemsPerPageChanged(ActionEvent actionEvent) {
        pageSize = itemsPerPageDropdown.getValue();
        int totalFriends = (int) friendshipService.countFriends(IDUser);
        pageNumber = Math.min(pageNumber, Math.max(0, (totalFriends - 1) / pageSize));
        initModel();
    }

    public void handleAcceptRequest(MouseEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../accept-request.fxml"));

        AnchorPane root = (AnchorPane) loader.load();
        Scene scene = new Scene(root);
        Stage stage2 = new Stage();
        stage2.setScene(scene);
        stage2.setTitle("Gossip Go");

        AcceptRequest requestView = loader.getController();
        requestView.setService(requestService, userService, friendshipService, IDUser);
        stage2.show();

    }

    @Override
    public void update(FriendshipEntityChange friendshipEntityChange) {
        initModel();
    }

    public void handleRemoveFriend(MouseEvent actionEvent) {
        User request = (User) tableView.getSelectionModel().getSelectedItem();
        Friendship friendship = friendshipService.findOne(request.getID(), IDUser);
        FriendshipRequest friendshipRequest = requestService.findByIDs(IDUser, request.getID());
        requestService.delete(friendshipRequest.getID());
        friendshipService.delete(friendship.getID());
        pageNumber = Math.max(0, (int) Math.ceil((double) friendshipService.count() / pageSize) - 1);
        initModel();
    }

    public void handleRemoveUser(MouseEvent actionEvent) {
        friendshipService.removeAllByID(IDUser);
        requestService.removeAllByID(IDUser);
        messageService.removeByID(IDUser);
        userService.delete(IDUser);
        stage.close();
    }

    public void handleAccountSetting(MouseEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../settings-view.fxml"));

        AnchorPane root = (AnchorPane) loader.load();
        Scene scene = new Scene(root);
        Stage stage2 = new Stage();
        stage2.setScene(scene);
        stage2.setTitle("Gossip Go");
        SettingsView settingsView = loader.getController();
        settingsView.setService(userService, IDUser);
        stage2.show();
    }

    public void handleChat(MouseEvent actionEvent) throws IOException {
        User request = (User) tableView.getSelectionModel().getSelectedItem();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../chat-view.fxml"));

        AnchorPane root = (AnchorPane) loader.load();
        Scene scene = new Scene(root);
        Stage stage2 = new Stage();
        stage2.setScene(scene);
        stage2.setTitle("Yahoo Messenger");
        ChatView chatView = loader.getController();
        String name=request.getFirstName()+" "+request.getLastName();
        chatView.setService(messageService,userService ,request.getID(),IDUser,name);
        stage2.show();
    }
}
