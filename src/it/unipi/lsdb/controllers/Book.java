package it.unipi.lsdb.controllers;

import it.unipi.lsdb.models.User;
import it.unipi.lsdb.Config;
import it.unipi.lsdb.Role;
import it.unipi.lsdb.models.Mongo_comms;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class Book {

    @FXML private Button rate;
    @FXML private Button addtoreading;
    @FXML private Label title;

    @FXML
    protected void initialize(){
        if (Config.role == Role.GUEST) {
            rate.setDisable(true);
            addtoreading.setDisable(true);
        }
    }

    @FXML
    private void rate(){
        User.addBook(title.getText(), Config.username);
        User.rateBook(Config.username, title.getText(),4);
    }
}
