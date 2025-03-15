package org.example.socialnetworkfx.socialnetworkfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.socialnetworkfx.socialnetworkfx.controller.LoginView;
import org.example.socialnetworkfx.socialnetworkfx.domain.User;
import org.example.socialnetworkfx.socialnetworkfx.domain.validation.FriendshipValidation;
import org.example.socialnetworkfx.socialnetworkfx.domain.validation.MessageValidation;
import org.example.socialnetworkfx.socialnetworkfx.domain.validation.RequestValidation;
import org.example.socialnetworkfx.socialnetworkfx.domain.validation.UserValidation;
import org.example.socialnetworkfx.socialnetworkfx.repository.*;
import org.example.socialnetworkfx.socialnetworkfx.repository.paging.UserDbPagingRepository;
import org.example.socialnetworkfx.socialnetworkfx.service.FriendshipRequestService;
import org.example.socialnetworkfx.socialnetworkfx.service.FriendshipService;
import org.example.socialnetworkfx.socialnetworkfx.service.MessageService;
import org.example.socialnetworkfx.socialnetworkfx.service.UserService;
import javafx.scene.image.Image;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class HelloApplication extends Application {
    UserDbRepository userRepository;
    FriendshipDbRepository friendshipRepository;
    MessageDbRepository messageRepository;
    UserService userService;
    FriendshipService friendshipService;
    FriendshipRequestDbRepository friendshipRequestDbRepository;
    FriendshipRequestService friendshipRequestService;
    MessageService messageService;

    @Override
    public void start(Stage primaryStage) throws IOException, SQLException {
        String username = "postgres";
        String password = "Bobby";
        String url = "jdbc:postgresql://localhost:5432/SocialNetwork";
        userRepository = new UserDbPagingRepository(url,username,password, new UserValidation());
        friendshipRepository = new FriendshipDbRepository(url,username, password, new FriendshipValidation());
        messageRepository=new MessageDbRepository(url,username,password,new MessageValidation());
        userService = new UserService((UserDbPagingRepository) userRepository);
        friendshipService=new FriendshipService(friendshipRepository, (UserDbPagingRepository) userRepository);
        friendshipRequestDbRepository=new FriendshipRequestDbRepository(url,username,password,new RequestValidation());
        friendshipRequestService=new FriendshipRequestService(friendshipRequestDbRepository);
        messageService=new MessageService(messageRepository);

        initView(primaryStage);
        primaryStage.setWidth(600);
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        AnchorPane userLayout = fxmlLoader.load();
        primaryStage.setScene(new Scene(userLayout));
        primaryStage.setTitle("Gossip Go");
        LoginView loginController = fxmlLoader.getController();
        loginController.setService(userService,friendshipService,friendshipRequestService,messageService);

    }

    public static void main(String[] args) {
        launch();
    }
}