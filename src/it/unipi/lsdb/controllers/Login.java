package it.unipi.lsdb.controllers;

import it.unipi.lsdb.Config;
import it.unipi.lsdb.Role;
import it.unipi.lsdb.Utils;
import it.unipi.lsdb.models.User;
import it.unipi.lsdb.models.User_doc;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
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
        login((Stage)((Node)event.getTarget()).getScene().getWindow());
    }

    @FXML
    private void register(ActionEvent event){
        event.consume();
        User newuser = new User(username.getText(), password.getText());
        //TODO: aggiungere altri dettagli
        User_doc.insert(newuser.create_doc());
        login((Stage)((Node)event.getTarget()).getScene().getWindow());
    }

    private void login(Stage stage){
        if (User_doc.login(username.getText(), password.getText())){
            User usr = User_doc.findUser(username.getText());
            if (usr.getType() == Role.BANNED){
                Alert a = new Alert(Alert.AlertType.ERROR, "You are banned.");
                a.show();
                return;
            }
            Config.username = usr.getUsername();
            Config.role = usr.getType();
            System.out.println(Config.role);
            Utils.openHomepage(stage);
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR, "Incorrect login details.");
            a.show();
        }
    }

}
