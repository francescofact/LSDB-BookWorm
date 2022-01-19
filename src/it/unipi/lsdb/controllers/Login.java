package it.unipi.lsdb.controllers;

import it.unipi.lsdb.Config;
import it.unipi.lsdb.Role;
import it.unipi.lsdb.Utils;
import it.unipi.lsdb.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Login {
    // Will be injected by FXMLLoader
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Button loginbtn;
    @FXML private Button newbtn;
    @FXML private Label wrong;

    @FXML
    private void login(ActionEvent event) {
        event.consume();
        /*if (username.getText().equals("user")) {
            Config.username = "user";
            Config.role = Role.USER;
        } else if (username.getText().equals("admin")){
            Config.username = "admin";
            Config.role = Role.ADMIN;
        }*/
        login((Stage)((Node)event.getTarget()).getScene().getWindow());
    }

    @FXML
    private void register(ActionEvent event){
        event.consume();
        User newuser = new User(username.getText(), password.getText());
        User.createUser(newuser);
        login((Stage)((Node)event.getTarget()).getScene().getWindow());
    }

    private void login(Stage stage){
        if (User.checkUserAndPassword(username.getText(), password.getText())){
            Config.username = username.getText();
            Config.role = Role.USER;
            Utils.openHomepage(stage);
        }
    }

}
