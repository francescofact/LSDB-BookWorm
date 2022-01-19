package it.unipi.lsdb;

import it.unipi.lsdb.models.Mongo_comms;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Mongo_comms.connect();
        Utils.openHomepage(null);
        //Utils.showSearch(data);
        //Utils.openBook(book);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
