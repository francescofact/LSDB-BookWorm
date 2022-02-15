package it.unipi.lsdb.controllers;

import it.unipi.lsdb.Config;
import it.unipi.lsdb.CustomFX;
import it.unipi.lsdb.Role;
import it.unipi.lsdb.Utils;
import it.unipi.lsdb.models.*;
import it.unipi.lsdb.models.Book;
import it.unipi.lsdb.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static it.unipi.lsdb.Utils.openUser;
import static it.unipi.lsdb.models.Book_doc.most_published;

public class Homepage {
    //UI
    @FXML private TextField searchbar;
    @FXML private Label hellouser;
    @FXML private Button searchbtn;
    @FXML private Button loginbtn;
    @FXML private Button hamburger;
    @FXML private RadioButton radiobooks;
    @FXML private RadioButton radiousers;

    //MostRatedBooks
    @FXML private ImageView mrb_img0;
    @FXML private Label mrb_title0;
    @FXML private ImageView mrb_img1;
    @FXML private Label mrb_title1;
    @FXML private ImageView mrb_img2;
    @FXML private Label mrb_title2;
    @FXML private ImageView mrb_img3;
    @FXML private Label mrb_title3;
    @FXML private ImageView mrb_img4;
    @FXML private Label mrb_title4;
    @FXML private ImageView mrb_img5;
    @FXML private Label mrb_title5;

    //BookYouMayLike
    @FXML private Pane byml_pane;
    @FXML private ImageView byml_img0;
    @FXML private Label byml_title0;
    @FXML private ImageView byml_img1;
    @FXML private Label byml_title1;
    @FXML private ImageView byml_img2;
    @FXML private Label byml_title2;
    @FXML private ImageView byml_img3;
    @FXML private Label byml_title3;
    @FXML private ImageView byml_img4;
    @FXML private Label byml_title4;
    @FXML private ImageView byml_img5;
    @FXML private Label byml_title5;


    @FXML private ListView<User> sugusers;
    @FXML private Label suguserlabel;

    @FXML
    protected void initialize(){
        if (Config.role == Role.ADMIN){
            hellouser.setText("Hello, " + Config.username);
            loginbtn.setVisible(false);
            hamburger.setVisible(true);
        } else {
            if (Utils.isLogged()){
                hellouser.setText("Hello, " + Config.username);
                loginbtn.setVisible(false);
                hamburger.setVisible(true);
                byml_pane.setVisible(true);
                sugusers.setVisible(true);
                suguserlabel.setVisible(true);
                loadBYML();
                loadSF();
            }
            loadMRB();
        }
    }

    @FXML
    private void search(ActionEvent event) {
        event.consume();
        if (radiobooks.isSelected()){
            Utils.showSearch(searchbar.getText(), "books");
        } else if (radiousers.isSelected()){
            Utils.showSearch(searchbar.getText(), "users");
        }

    }

    @FXML
    private void login(ActionEvent event){
        event.consume();
        Utils.createWindow("login", "Login or Create Account", (Stage)loginbtn.getScene().getWindow());
    }

    @FXML
    protected void newbook(){
        Utils.createWindow("newbook", "Insert new book");
    }

    @FXML
    protected void openBook(InputEvent e){
        ImageView cover = (ImageView)e.getSource();
        String obj = cover.getId();
        String prefix = (obj.contains("mrb")) ? "mrb" : "byml";
        obj = obj.substring(obj.length() - 1);
        Label title = (Label) cover.getScene().lookup("#"+prefix+"_title"+obj);
        Book book = Book_doc.get_by_name(title.getText());
        Utils.openBook(book);

    }

    @FXML
    protected void logout(InputEvent e){
        Config.username = null;
        Config.role = Role.GUEST;
        Utils.openHomepage((Stage)((Node)e.getTarget()).getScene().getWindow());
    }

    @FXML
    protected void mostpub(){
        ArrayList<String> authors = Book_doc.most_published();
        String msg = "Most Published Authors:\n\n";
        for (String author : authors) {
            msg += (author+"\n");
        }
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.show();
    }
    @FXML
    protected void mostrated(){
        ArrayList<String> authors = Book_doc.best_rated_authors();
        String msg = "Best Rated Authors:\n\n";
        for (String author : authors) {
            msg += (author+"\n");
        }
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.show();
    }
    @FXML
    protected void mostfam(){
        ArrayList<String> authors = Book_doc.most_famous_authors();
        String msg = "Most Famouse Authors:\n\n";
        for (String author : authors) {
            msg += (author+"\n");
        }
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.show();
    }

    //Utils functions
    private void loadMRB(){
        Book[] books = Book_doc.best_rated(6);
        ObservableList<Book> data = FXCollections.observableArrayList();
        data.addAll(books);

        mrb_img0.setImage(new Image(data.get(0).getImageURL(), true));
        mrb_title0.setText(data.get(0).getTitle());
        mrb_img1.setImage(new Image(data.get(1).getImageURL(), true));
        mrb_title1.setText(data.get(1).getTitle());
        mrb_img2.setImage(new Image(data.get(2).getImageURL(), true));
        mrb_title2.setText(data.get(2).getTitle());
        mrb_img3.setImage(new Image(data.get(3).getImageURL(), true));
        mrb_title3.setText(data.get(3).getTitle());
        mrb_img4.setImage(new Image(data.get(4).getImageURL(), true));
        mrb_title4.setText(data.get(4).getTitle());
        mrb_img5.setImage(new Image(data.get(5).getImageURL(), true));
        mrb_title5.setText(data.get(5).getTitle());

    }

    private void loadBYML(){
        ArrayList<String> books_str = Book.suggestedBooks(Config.username);
        List<Book> books = books_str
                .stream()
                .map(Book_doc::get_by_name)
                .collect(Collectors.toList());
        int size = books.size();
        if (size > 0){
            byml_title0.setText(books.get(0).getTitle());
            byml_img0.setImage(new Image(books.get(0).getImageURL(), true));
            byml_title0.setVisible(true);
            byml_img0.setVisible(true);
        }
        if (size > 1){
            byml_title1.setText(books.get(1).getTitle());
            byml_img1.setImage(new Image(books.get(1).getImageURL(), true));
            byml_title1.setVisible(true);
            byml_img1.setVisible(true);
        }
        if (size > 2){
            byml_title2.setText(books.get(2).getTitle());
            byml_img2.setImage(new Image(books.get(2).getImageURL(), true));
            byml_title2.setVisible(true);
            byml_img2.setVisible(true);
        }
        if (size > 3){
            byml_title3.setText(books.get(3).getTitle());
            byml_img3.setImage(new Image(books.get(3).getImageURL(), true));
            byml_title3.setVisible(true);
            byml_img3.setVisible(true);
        }
        if (size > 4){
            byml_title4.setText(books.get(4).getTitle());
            byml_img4.setImage(new Image(books.get(4).getImageURL(), true));
            byml_title4.setVisible(true);
            byml_img4.setVisible(true);
        }
        if (size > 5){
            byml_title5.setText(books.get(5).getTitle());
            byml_img5.setImage(new Image(books.get(5).getImageURL(), true));
            byml_title5.setVisible(true);
            byml_img5.setVisible(true);
        }

    }

    private void loadSF(){
        ArrayList<String> users = User.suggestedUsers(Config.username);
        List<User> realusers = users
                .stream()
                .map(User_doc::findUser)
                .collect(Collectors.toList());
        ObservableList<User> data = FXCollections.observableArrayList();
        data.addAll(realusers);

        sugusers.setItems(data);
        sugusers.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> listView) {
                return new CustomFX.SearchResultUsers();
            }
        });
        sugusers.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    //Use ListView's getSelected Item
                    User item = sugusers.getSelectionModel().getSelectedItem();
                    openUser(item);
                }
            }
        });
    }

}
