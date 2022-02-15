package it.unipi.lsdb.controllers;

import it.unipi.lsdb.Config;
import it.unipi.lsdb.Role;
import it.unipi.lsdb.models.User_doc;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class User {

    @FXML private Button banuser;
    @FXML private Button follow;
    @FXML private Label username;

    private boolean isfollowing;
    private boolean isbanned = false;

    @FXML
    protected void initialize(){
        it.unipi.lsdb.models.User usr = User_doc.findUser(Config.openedUser);
        if (usr.getType() == Role.BANNED) {
            isbanned = true;
            banuser.setText("UnBan User");
        }

        if (Config.role == Role.ADMIN)
            banuser.setVisible(true);
        if (Config.role == Role.USER) {
            isfollowing = it.unipi.lsdb.models.User.checkFollow(Config.username, Config.openedUser);

            if (isfollowing)
                follow.setText("Unfollow User");
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

    @FXML
    protected void ban(){
        if (isbanned){
            //unban
            User_doc.editUserType(username.getText(), Role.USER);
            banuser.setText("Ban User");
            isbanned = false;
        } else {
            User_doc.editUserType(username.getText(), Role.BANNED);
            banuser.setText("UnBan User");
            isbanned = true;
        }
    }
}
