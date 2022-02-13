package it.unipi.lsdb.controllers;

import it.unipi.lsdb.Config;
import it.unipi.lsdb.Role;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class Rate {

    @FXML private Button r1;
    @FXML private Button r2;
    @FXML private Button r3;
    @FXML private Button r4;
    @FXML private Button r5;

    @FXML
    protected void initialize(){
        //TODO: retive if I have a rating and color the relative button
    }

    @FXML
    protected void rate(ActionEvent e){
        String text = ((Button)e.getSource()).getText();
        System.out.println(text);
        //TODO: check if book exists and rating also
        //TODO: rate
        /*
        Book.addBook(title.getText());
        Book.rateBook(Config.username, title.getText(),4);
         */
    }

}
