package it.unipi.lsdb;

import it.unipi.lsdb.models.Book;
import it.unipi.lsdb.models.User;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class CustomFX {

    //Custom JavaFX
    public static class SearchResultBooks extends ListCell<Book> {
        private HBox content;
        private Text name;
        private Label desc;
        private ImageView image;

        public SearchResultBooks() {
            super();
            name = new Text();
            name.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
            desc = new Label();
            desc.setMaxHeight(100);
            desc.setEllipsisString("...");
            desc.setWrapText(true);
            image = new ImageView();
            image.setFitWidth(80);
            image.setPreserveRatio(true);

            VBox vBox = new VBox(name, desc);

            content = new HBox(image, vBox);
            desc.setMaxWidth(950);
            content.setSpacing(10);
        }

        @Override
        protected void updateItem(Book item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) {
                name.setText(item.getTitle());
                desc.setText(item.getDesc());
                image.setImage(new Image(item.getImageURL(), true));
                setGraphic(content);
            } else {
                setGraphic(null);
            }
        }
    }

    public static class SearchResultUsers extends ListCell<User> {

        private Text username;

        public SearchResultUsers() {
            super();
            username = new Text();
            username.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        }

        @Override
        protected void updateItem(User item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) {
                username.setText(item.getUsername());
                setGraphic(username);
            } else {
                setGraphic(null);
            }
        }
    }

    public static class UserRatings extends ListCell<Book> {
        private HBox content;
        private Text name;
        private Text rating;
        private ImageView image;

        public UserRatings() {
            super();
            name = new Text();
            name.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
            rating = new Text();
            image = new ImageView();
            image.setFitWidth(40);
            image.setPreserveRatio(true);

            VBox vBox = new VBox(name, rating);

            content = new HBox(image, vBox);
            content.setSpacing(10);
        }

        @Override
        protected void updateItem(Book item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) {
                name.setText(item.getTitle());
                rating.setText("User Rating: "+"TODO"+"/10");
                image.setImage(new Image(item.getImageURL(), true));
                setGraphic(content);
            } else {
                setGraphic(null);
            }
        }
    }

}
