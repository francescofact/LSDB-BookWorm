package it.unipi.lsdb.controllers;

import it.unipi.lsdb.Config;
import it.unipi.lsdb.Role;
import it.unipi.lsdb.models.Book;
import it.unipi.lsdb.models.Book_doc;
import it.unipi.lsdb.models.User;
import it.unipi.lsdb.models.User_doc;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
        String rat = ((Button)e.getSource()).getText();

        //TODO: check if rating exists
        User usr = User_doc.findUser(Config.username);
        Book book = Book_doc.get_by_name(Config.editBook);
        User_doc.rate_book(usr, book, Integer.parseInt(rat));
        Alert a = new Alert(Alert.AlertType.INFORMATION, "Rating inserted.");
        a.show();
    }

}
