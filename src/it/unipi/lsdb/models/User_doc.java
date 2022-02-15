package it.unipi.lsdb.models;


import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Updates.set;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.regex.Pattern;



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
                Rating r = new Rating(list.get(i).getString("book"), username, list.get(i).getDouble("value"));
                ratings.add(r);
                i++;
            }

            Document doc = cursor;
            System.out.println("Documento:");
            System.out.println(doc);
            u = new User(doc, ratings);
        }
            else
                u = new User(cursor);

            return u;

    }
    
     public static boolean rate_book(User u, Book b, double rating){
        Document cursor = coll.find(eq("username", u.getUsername())).first();
        int counter = 0;
        ArrayList<Document> list;
        list = new ArrayList<>();
        list = (ArrayList<Document>)cursor.get("Ratings");
        int i=0;
        if(list!=null)
            i = list.size();

        int ii = i+1;
        BasicDBObject query = new BasicDBObject();
        query.put("username",u.getUsername());

        BasicDBObject update = new BasicDBObject();
        BasicDBObject update2 = new BasicDBObject();
        BasicDBObject update3 = new BasicDBObject();


        update.put("$set", new BasicDBObject("Ratings."+i+".id", ii));
        update2.put("$set", new BasicDBObject("Ratings."+i+".book", b.isbn));
        update3.put("$set", new BasicDBObject("Ratings."+i+".value", rating));


        coll.updateOne(
                query,update);

        coll.updateOne(
                query,update2);

        coll.updateOne(
                query,update3);

        //Here we will have to call on the graph db
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
   
}
