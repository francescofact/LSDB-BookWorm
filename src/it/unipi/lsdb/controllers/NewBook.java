package it.unipi.lsdb.controllers;

import it.unipi.lsdb.Config;
import it.unipi.lsdb.Role;
import it.unipi.lsdb.Utils;
import it.unipi.lsdb.models.Book;
import it.unipi.lsdb.models.Book_doc;
import it.unipi.lsdb.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class NewBook {

    @FXML private Button save;
    @FXML private TextField title;
    @FXML private TextField author;
    @FXML private TextField img;
    @FXML private TextArea desc;

    private String bookName;

    @FXML
    protected void initialize(){
        if (Config.editBook != null) {
            //edit book
            bookName = Config.editBook;
            Config.editBook = null;
            //loading info
            Book book = Book_doc.get_by_name(bookName);
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            desc.setText(book.getDesc());
            img.setText(book.getImageURL());
        }
    }

    @FXML
    private void save(){
        if (bookName == null){
            //creating on neo4j
            Book.addBook(title.getText());

        } else {
            //TODO: save edited.
        }
    }
}
