package it.unipi.lsdb;

import it.unipi.lsdb.models.Book;
import it.unipi.lsdb.models.Mongo_comms;
import it.unipi.lsdb.models.User;
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

import java.io.IOException;

public class Utils {

    //JavaFX Utils
    public static Stage createWindow(String fxml, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Utils.class.getClassLoader().getResource(fxml+".fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
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

    public static void populateUserRatings(Stage stage, ObservableList<Book> data){
        final ListView<Book> listView = (ListView<Book>) stage.getScene().lookup("#ratings");
        listView.setItems(data);
        listView.setCellFactory(new Callback<ListView<Book>, ListCell<Book>>() {
            @Override
            public ListCell<Book> call(ListView<Book> listView) {
                return new CustomFX.UserRatings();
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

    //Window Utils
    public static void openBook(Book book){
        Stage stage = createWindow("book", "Book");

        String path = book.getImageURL();
        Image image = new Image(path);
        ImageView imageView= (ImageView) stage.getScene().lookup("#img");
        imageView.setImage(image);
        setTextFromID(stage,"title", book.getTitle());
        setTextFromID(stage,"author", book.getAuthor());
        setTextFromID(stage,"desc", book.getDesc());
    }

    public static void openUser(User user){
        Stage stage = createWindow("user", "User");
        setTextFromID(stage,"username", user.getUsername());
        //TEMPORARY DATA
        Book book = new Book("Laurence M. Hauptman","Hardcover","Reveals that several hundred thousand Indians were affected by the Civil War and that twenty thousand Indians enlisted on both sides in an attempt to gain legitimacy, autonomy, or simply land.","History,Military History,Civil War,American History,American Civil War,Nonfiction,North American Hi...,American History,Native Americans","https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1387738765l/1001053.jpg","002914180X","9.78E+12","https://goodreads.com/book/show/1001053.Between_Two_Fires",0, (float) 3.52,5,"Between Two Fires: American Indians in the Civil War",33);
        Book book1 = new Book("Charlotte Fiell,Emmanuelle Dirix","Paperback","Fashion Sourcebook - 1920s is the first book in a brand-new series by Fiell Publishing that documents comprehensively the seasonal fashion styles of the 20th century, decade by decade. Sumptuously illustrated with over 600 original photographs, drawings and prints, this title is a must-have reference work for not only students of fashion, but for all fashionistas. Fashion Sourcebook - 1920s focuses on the Art Deco period with its beautiful beaded dresses, cloche hats and t-bar shoes as worn by the fashionable flappers and the bright young things of the time. An accompanying introduction outlines the major themes within fashion during this period and introduces its most famous designers and assesses their creative contributions. Text in English, French & German. Also Available: Fashion Sourcebook - 1930s ISBN: 9781906863586 24.95","Couture,Fashion,Historical,Art,Nonfiction","https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1421011497l/10010552.jpg","1906863482","9.78E+12","https://goodreads.com/book/show/10010552-fashion-sourcebook-1920s",576, (float) 4.51,6,"Fashion Sourcebook 1920s",41);
        Book book2 = new Book("Andy Anderson","Paperback","The seminal history and analysis of the Hungarian Revolution and the workers' councils, perhaps the single most important revolutionary event ever, and this is simply the best book on it.","Politics,History","https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1348117708l/1001077.jpg","948984147","9.78E+12","https://goodreads.com/book/show/1001077.Hungary_56",124, 4,2,"Hungary 56",26);

        ObservableList<Book> data = FXCollections.observableArrayList();
        data.addAll(book1, book2, book);
        //
        populateUserRatings(stage, data);
    }

    public static void showSearch(String query, String type){
        Stage stage = createWindow("search", "Search Results");
        if (type.equals("books")){
            Book[] books = Mongo_comms.search_by_name(query);
            ObservableList<Book> data = FXCollections.observableArrayList();
            data.addAll(books);

            final Label searchlabel = (Label) stage.getScene().lookup("#terms");
            searchlabel.setText(query);
            populateSearchBooks(stage, data);
        } else if (type.equals("users")){
            //TEMPORARY DATA
            User user = new User("FrancescoFact", "1234");
            User user1 = new User("SteveJobs", "1234");
            User user2 = new User("Frank", "1234");
            ObservableList<User> data = FXCollections.observableArrayList();
            data.addAll(user, user1, user2);
            //
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
    }

    public static void openHomepage(Stage old) {
        if (Config.role == Role.ADMIN){
            createWindow("homepage_admin", "BooksWorm - Administrator", old);
        } else {
            createWindow("homepage", "BooksWorm", old);
        }
    }
}
