package it.unipi.lsdb.controllers;

import it.unipi.lsdb.Utils;
import it.unipi.lsdb.models.User;
import it.unipi.lsdb.Config;
import it.unipi.lsdb.Role;
import it.unipi.lsdb.models.Mongo_comms;
import it.unipi.lsdb.models.User_doc;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class Book {

    @FXML private Button rate;
    @FXML private Button edit;
    @FXML private Label title;

    @FXML
    protected void initialize(){
        if (Config.role == Role.GUEST) {
            rate.setDisable(true);
        } else if (Config.role == Role.ADMIN){
            rate.setVisible(false);
            edit.setVisible(true);
        }
    }

    @FXML
    private void rate(){
        Config.editBook = title.getText();
        Utils.createWindow("rate", "Rate this book");
    }

    @FXML
    private void edit(){
        Config.editBook = title.getText();
        Utils.createWindow("newbook", "Edit book");
    }
}
