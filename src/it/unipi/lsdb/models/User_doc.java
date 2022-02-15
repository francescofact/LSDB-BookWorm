package it.unipi.lsdb.models;


import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.client.*;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Updates.set;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import it.unipi.lsdb.Role;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static com.mongodb.client.model.Aggregates.lookup;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;


public class User_doc {
    public static int number_results = 10;
    static String adress = "mongodb://localhost";
    static String db_name = "db";
    static String collection = "Users";
    public static MongoDatabase database = null;
    public static MongoCollection<Document> coll = null;
    public static MongoClient myClient = null;
    static Consumer<Document> printDocuments = doc ->
    {
        System.out.println(doc.toJson());
    };

    public static void connect() {
        ConnectionString uri = new ConnectionString(adress);
        myClient = MongoClients.create(uri);
        database = myClient.getDatabase(db_name + "");
        coll = database.getCollection(collection);
    }

    public static void disconnect() {
        myClient.close();

    }

    public static void insert(Document doc) {
        coll.insertOne(doc);
        User.createUser(new User(doc.getString("username"), ""));
    }

    public static boolean login(String username, String password) {
        MongoCursor<Document> cursor = coll.find(eq("username", username)).iterator();
        if (cursor.hasNext()) {
            Document doc = cursor.next();
            return password.equals(doc.getString("password"));
        } else
            return false;
    }

    public static User findUser(String username) {
        Document cursor = coll.find(eq("username", username)).first();
        int counter = 0;
        ArrayList<Document> list;
        list = new ArrayList<>();
        list = (ArrayList<Document>)cursor.get("Ratings");
        ArrayList<Rating> ratings=new ArrayList<Rating>();
        int i =0;
        User u;
        if(list!=null) {
            while (list.size() > i) {
                Rating r = new Rating(list.get(i).getString("book"), list.get(i).getDouble("value"));
                ratings.add(r);
                i++;
            }

            Document doc = cursor;
            u = new User(doc, ratings);
        }
            else
                u = new User(cursor);

        return u;

    }
    
     public static boolean rate_book(User u, Book b, int rating){
        Rating rat = new Rating(b.getTitle(), rating);

        Document cursor = coll.find(eq("username", u.getUsername())).first();

        BasicDBObject query = new BasicDBObject();
        query.put("username", u.getUsername());
        BasicDBObject push_data = new BasicDBObject("$push", new BasicDBObject("Ratings", rat.create_doc()));
        coll.findOneAndUpdate(query, push_data);

        Book.rateBook(u.getUsername(), b.getTitle(), rating);
        return true;

    }
    
    
    public static ArrayList<User> findUsers(String query) {
        MongoCursor<Document> cursor = coll.find(regex("username", ".*" + Pattern.quote(query) + ".*")).iterator();
        ArrayList<User> users = new ArrayList<User>();
        if (cursor.hasNext()) {
            Document doc = cursor.next();
            users.add(new User(doc));
        }
        return users;
    }

    public static void editUserType(String username, Role role){
        String type;
        if (role == Role.BANNED){
            type = "banned";
        } else {
            type = "basic";
        }
        coll.updateOne(Filters.eq("username", username), Updates.set("type", type));

    }
    private static Consumer<Document> printDocuments() {
        return doc -> System.out.println(doc.toJson(JsonWriterSettings.builder().indent(true).build()));
    }

    public static List<Book> getRatedBooks(String username){

        Document cursor = coll.find(eq("username", username)).first();

        Bson pipeline = lookup("Books", "Ratings.book", "title", "Ratings.title");
        Document booksJoined = coll.aggregate(Arrays.asList(match(Filters.eq("username", username)),pipeline)).first();

        ArrayList<Document> ratings = (ArrayList<Document>) booksJoined.get("rated_books");
        System.out.println(ratings);
        List<Book> books = ratings
                .stream()
                .map(Book::mapper)
                .collect(Collectors.toList());
        System.out.println(books);
        return books;

    }


}
