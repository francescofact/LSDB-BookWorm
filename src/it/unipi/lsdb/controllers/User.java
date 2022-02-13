package it.unipi.lsdb.controllers;

import it.unipi.lsdb.Config;
import it.unipi.lsdb.Role;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class User {

    @FXML private Button banuser;
    @FXML private Button follow;
    @FXML private Label username;

    private boolean isfollowing = false;
    @FXML
    protected void initialize(){
        if (Config.role == Role.ADMIN)
            banuser.setVisible(true);
        if (Config.role == Role.USER) {
            //TODO: check if relation exists between two user
            isfollowing = false;
            follow.setVisible(true);
        }
    }

    @FXML
    protected void follow(){
        if (isfollowing){
            //need to unfollow
            it.unipi.lsdb.models.User.unFollowUser(Config.username, username.getText());
            follow.setText("Follow User");
            isfollowing = false;
        } else {
            //need to follow
            it.unipi.lsdb.models.User.followUser(Config.username, username.getText());
            follow.setText("Unfollow User");
            isfollowing = true;
        }

    }

    //TODO: ban user
}
