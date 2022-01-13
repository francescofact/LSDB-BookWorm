package it.unipi.lsdb.controllers;

import it.unipi.lsdb.Config;
import it.unipi.lsdb.Role;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class User {

    @FXML private Button banuser;

    @FXML
    protected void initialize(){
        if (Config.role == Role.ADMIN)
            banuser.setVisible(true);
    }
}
