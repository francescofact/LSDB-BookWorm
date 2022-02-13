package it.unipi.lsdb.controllers;

import it.unipi.lsdb.Config;
import it.unipi.lsdb.Role;
import it.unipi.lsdb.Utils;
import it.unipi.lsdb.models.Book;
import it.unipi.lsdb.models.Book_doc;
import it.unipi.lsdb.models.Mongo_comms;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Homepage {
    //UI
    @FXML private TextField searchbar;
    @FXML private Label hellouser;
    @FXML private Button searchbtn;
    @FXML private Button loginbtn;
    @FXML private MenuButton hamburger;
    @FXML private RadioButton radiobooks;
    @FXML private RadioButton radiousers;
    @FXML private MenuItem readinglist;

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


    @FXML
    protected void initialize(){
        if (Config.role == Role.ADMIN){
            hellouser.setText("Hello, " + Config.username);
            loginbtn.setVisible(false);
            hamburger.setVisible(true);
            readinglist.setVisible(false);
        } else {
            if (Utils.isLogged()){
                hellouser.setText("Hello, " + Config.username);
                loginbtn.setVisible(false);
                hamburger.setVisible(true);
                byml_pane.setVisible(true);
                loadBYML();
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

    //Utils functions
    private void loadMRB(){
        Book[] books = Book_doc.best_rated(6);
        ObservableList<Book> data = FXCollections.observableArrayList();
        data.addAll(books);

        mrb_img0.setImage(new Image(data.get(0).getImageURL()));
        mrb_title0.setText(data.get(0).getTitle());
        mrb_img1.setImage(new Image(data.get(1).getImageURL()));
        mrb_title1.setText(data.get(1).getTitle());
        mrb_img2.setImage(new Image(data.get(2).getImageURL()));
        mrb_title2.setText(data.get(2).getTitle());
        mrb_img3.setImage(new Image(data.get(3).getImageURL()));
        mrb_title3.setText(data.get(3).getTitle());
        mrb_img4.setImage(new Image(data.get(4).getImageURL()));
        mrb_title4.setText(data.get(4).getTitle());
        mrb_img5.setImage(new Image(data.get(5).getImageURL()));
        mrb_title5.setText(data.get(5).getTitle());

    }

    private void loadBYML(){
        //TEMPORARY DATA
        Book book = new Book("Laurence M. Hauptman","Hardcover","Reveals that several hundred thousand Indians were affected by the Civil War and that twenty thousand Indians enlisted on both sides in an attempt to gain legitimacy, autonomy, or simply land.","History,Military History,Civil War,American History,American Civil War,Nonfiction,North American Hi...,American History,Native Americans","https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1387738765l/1001053.jpg","002914180X","9.78E+12","https://goodreads.com/book/show/1001053.Between_Two_Fires",0, (float) 3.52,5,"Between Two Fires: American Indians in the Civil War",33);
        Book book1 = new Book("Charlotte Fiell,Emmanuelle Dirix","Paperback","Fashion Sourcebook - 1920s is the first book in a brand-new series by Fiell Publishing that documents comprehensively the seasonal fashion styles of the 20th century, decade by decade. Sumptuously illustrated with over 600 original photographs, drawings and prints, this title is a must-have reference work for not only students of fashion, but for all fashionistas. Fashion Sourcebook - 1920s focuses on the Art Deco period with its beautiful beaded dresses, cloche hats and t-bar shoes as worn by the fashionable flappers and the bright young things of the time. An accompanying introduction outlines the major themes within fashion during this period and introduces its most famous designers and assesses their creative contributions. Text in English, French & German. Also Available: Fashion Sourcebook - 1930s ISBN: 9781906863586 24.95","Couture,Fashion,Historical,Art,Nonfiction","https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1421011497l/10010552.jpg","1906863482","9.78E+12","https://goodreads.com/book/show/10010552-fashion-sourcebook-1920s",576, (float) 4.51,6,"Fashion Sourcebook 1920s",41);
        Book book2 = new Book("Andy Anderson","Paperback","The seminal history and analysis of the Hungarian Revolution and the workers' councils, perhaps the single most important revolutionary event ever, and this is simply the best book on it.","Politics,History","https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1348117708l/1001077.jpg","948984147","9.78E+12","https://goodreads.com/book/show/1001077.Hungary_56",124, 4,2,"Hungary 56",26);

        ObservableList<Book> data = FXCollections.observableArrayList();
        data.addAll(book1, book2, book);
        //

        byml_img0.setImage(new Image(data.get(0).getImageURL()));
        byml_title0.setText(data.get(0).getTitle());
        byml_img1.setImage(new Image(data.get(1).getImageURL()));
        byml_title1.setText(data.get(1).getTitle());
        byml_img2.setImage(new Image(data.get(2).getImageURL()));
        byml_title2.setText(data.get(2).getTitle());

    }


}
