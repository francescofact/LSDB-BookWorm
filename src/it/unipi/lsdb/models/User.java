package it.unipi.lsdb.models;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String username;
    private String password;
    private Map<Book, Float> ratings;

    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.ratings =  new HashMap<Book, Float>();
    }

    public String getUsername(){
        return username;
    }
}
