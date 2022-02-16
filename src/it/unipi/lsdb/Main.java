package it.unipi.lsdb;

import it.unipi.lsdb.models.Mongo_comms;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Mongo_comms.setup();
        Utils.openHomepage(null);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
