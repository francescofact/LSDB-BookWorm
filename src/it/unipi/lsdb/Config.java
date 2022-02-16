package it.unipi.lsdb;

import it.unipi.lsdb.models.User;
import javafx.scene.image.Image;

public class Config {

    public static String username;
    public static Role role = Role.GUEST;
    public static String editBook = null;
    public static String openedUser = null;
    public static Image appIcon = new Image("/icon.png");
}
