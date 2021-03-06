package it.unipi.lsdb;

import it.unipi.lsdb.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static it.unipi.lsdb.models.User_doc.star_range_by_book;

public class Utils {

    //JavaFX Utils
    public static Stage createWindow(String fxml, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Utils.class.getClassLoader().getResource(fxml+".fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.getIcons().add(Config.appIcon);
            stage.setScene(new Scene(root1));
            stage.show();
            return stage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("I shouldn't be here");
        return null;
    }

    public static Stage createWindow(String fxml, String title, Stage oldwindow){
        if (oldwindow != null)
            oldwindow.close();
        return createWindow(fxml, title);
    }

    public static void setTextFromID(Stage stage, String fxid, String text){
        Label label= (Label) stage.getScene().lookup("#"+fxid);
        label.setText(text);
    }

    public static void populateSearchBooks(Stage stage, ObservableList<Book> data){
        final ListView<Book> listView = (ListView<Book>) stage.getScene().lookup("#results");
        listView.setItems(data);
        listView.setCellFactory(new Callback<ListView<Book>, ListCell<Book>>() {
            @Override
            public ListCell<Book> call(ListView<Book> listView) {
                return new CustomFX.SearchResultBooks();
            }
        });
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    //Use ListView's getSelected Item
                    Book item = listView.getSelectionModel().getSelectedItem();
                    openBook(item);
                }
            }
        });
    }

    public static void populateSearchUsers(Stage stage, ObservableList<User> data){
        final ListView<User> listView = (ListView<User>) stage.getScene().lookup("#results");
        listView.setItems(data);
        listView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> listView) {
                return new CustomFX.SearchResultUsers();
            }
        });
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    //Use ListView's getSelected Item
                    User item = listView.getSelectionModel().getSelectedItem();
                    openUser(item);
                }
            }
        });
    }

    public static void populateUserRatings(Stage stage, ObservableList<Rating> data){
        final ListView<Rating> listView = (ListView<Rating>) stage.getScene().lookup("#ratings");
        listView.setItems(data);
        listView.setCellFactory(new Callback<ListView<Rating>, ListCell<Rating>>() {
            @Override
            public ListCell<Rating> call(ListView<Rating> listView) {
                return new CustomFX.UserRatings();
            }
        });
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    //Use ListView's getSelected Item
                    Rating itemdoc = listView.getSelectionModel().getSelectedItem();
                    Book item = Book_doc.get_by_name(itemdoc.getBook());
                    openBook(item);
                }
            }
        });
    }

    //Window Utils
    public static void openBook(Book book){
        Stage stage = createWindow("book", "Book");

        String path = book.getImageURL();
        Image image = new Image(path, true);
        ImageView imageView= (ImageView) stage.getScene().lookup("#img");
        imageView.setImage(image);
        setTextFromID(stage,"title", book.getTitle());
        setTextFromID(stage,"author", book.getAuthor());
        setTextFromID(stage,"desc", book.getDesc());
        ArrayList<Integer> stars = star_range_by_book(book.getTitle());
        setTextFromID(stage,"ratinglabel", "Rating: "+book.getRating().toString()+"/5         Distribution of ratings (in our platform): " + stars.toString());
    }

    public static void openUser(User user){
        Config.openedUser = user.getUsername();
        Stage stage = createWindow("user", "User");
        setTextFromID(stage,"username", user.getUsername());


        ArrayList<Rating> books = User_doc.getRatedBooks(user.getUsername());
        ObservableList<Rating> data = FXCollections.observableArrayList();
        data.addAll(books);
        populateUserRatings(stage, data);
    }

    public static void showSearch(String query, String type){
        Stage stage = createWindow("search", "Search Results");
        if (type.equals("books")){
            Book[] books = Book_doc.search_by_name(query);
            ObservableList<Book> data = FXCollections.observableArrayList();
            data.addAll(books);

            populateSearchBooks(stage, data);
        } else if (type.equals("users")){
            ArrayList<User> users = User_doc.findUsers(query);
            ObservableList<User> data = FXCollections.observableArrayList();
            data.addAll(users);
            populateSearchUsers(stage, data);
        }
    }


    // Utility Functions
    public static boolean isLogged(){
        return !(Config.username == null);
    }

    public static void logout(){
        Config.username = null;
        Config.role = Role.GUEST;
        Config.editBook = null;
        Config.openedUser = null;
    }

    public static void openHomepage(Stage old) {
        if (Config.role == Role.ADMIN){
             createWindow("homepage_admin", "BooksWorm - Administrator", old);
        } else {
            Stage stage = createWindow("homepage", "BooksWorm", old);
            if (Config.role != Role.USER)
                stage.setHeight(500);
        }
    }
}
