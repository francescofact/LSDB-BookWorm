package it.unipi.lsdb.models;


import com.mongodb.*;
import com.mongodb.client.*;

import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.set;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import it.unipi.lsdb.Role;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;


public class User_doc {
    public static int number_results = 10;
    static String adress = "mongodb+srv://lsdb:lsdb@lsdb.hzdrg.mongodb.net";
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
        MongoClientSettings mcs = MongoClientSettings.builder()
                .applyConnectionString(uri)
                .readPreference(ReadPreference.nearest())
                .retryWrites(true)
                .writeConcern(WriteConcern.W1).build();
        myClient = MongoClients.create(mcs);
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
    
     public static boolean rate_book(User u, Book b, double rating){
        Rating rat = new Rating(b.getTitle(), rating);

        Document cursor = coll.find(eq("username", u.getUsername())).first();

        BasicDBObject query = new BasicDBObject();
        query.put("username", u.getUsername());
        BasicDBObject push_data = new BasicDBObject("$push", new BasicDBObject("Ratings", rat.create_doc()));
        coll.findOneAndUpdate(query, push_data);

        Book.rateBook(u.getUsername(), b.getTitle(), rating);
        Book_doc.rate_book_mg(b.getTitle(), rating);
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

    public static List<Document> getRatedBooks(String username){

        Document cursor = coll.find(eq("username", username)).first();

        Bson pipeline = lookup("Books", "Ratings.book", "title", "book");
        Document booksJoined = coll.aggregate(Arrays.asList(match(Filters.eq("username", username)),pipeline)).first();

        ArrayList<Document> ratings = (ArrayList<Document>) booksJoined.get("Ratings");
        return ratings;

    }

    public static boolean hasReviewed(String username, String title){
        User usr = findUser(username);
        ArrayList<Rating> ratings = usr.getRatings();
        for (Rating rat : ratings){
            if (rat.getBook().equals(title))
                return true;
        }
        return false;
    }
    
    public static ArrayList<String> most_active_users(){
            Bson unwind=unwind("$Ratings");
            Bson group= group("$username",sum("count",1L));
            Bson sort = sort(descending("count"));


            Bson limit = limit(10);

            List<Document> results=  coll.aggregate(Arrays.asList(unwind,group,sort,limit)).into(new ArrayList<>());
            ArrayList <String> author=new ArrayList<String>();

            int count=0;
            while(results.size()>count){
                Document doc = results.get(count);
                author.add(doc.getString("_id"));
                count++;
            }
        return author;


    }

    public static boolean userExists(String username){
        Document cursor = coll.find(eq("username", username)).first();
        return cursor != null;
    }
    
    
    public static ArrayList<Integer> star_range(){
        Bson unwind=unwind("$Ratings");
        Bson group= group("$Ratings.value",sum("count",1L));
        //Bson sort = sort(descending("$value"));

        List<Document> results=  coll.aggregate(Arrays.asList(unwind,group)).into(new ArrayList<>());
        double count=0;
        int z=0;
        ArrayList<Integer> n_value=new ArrayList<Integer>();
        for(int i=0;i<=5;i++) {
            n_value.add(0);
        }

        while(results.size()>z){
            Document doc = results.get((int)z);
            count=doc.getDouble("_id");
            n_value.set((int)count,Math.toIntExact(doc.getLong("count")));

          //  System.out.println(count+": "+n_value.get((int)count));
            z=z+1;
        }
        return n_value;
    }
    public static ArrayList<Integer> star_range_by_book(String b ){
        Bson unwind=unwind("$Ratings");                             //Unwinds ratings
        Bson match =match(eq("Ratings.book",b));                    //Matches ratings of a specific book
        Bson group= group("$Ratings.value",sum("count",1L));        //Counts ratings based on value
        //Bson sort = sort(descending("$value"));

        List<Document> results=  coll.aggregate(Arrays.asList(unwind,match,group)).into(new ArrayList<>());
        double count=0;
        int z=0;
        ArrayList<Integer> n_value=new ArrayList<Integer>();
        for(int i=0;i<=5;i++) {
            n_value.add(0);
        }

        while(results.size()>z){
            Document doc = results.get((int)z);
            count=doc.getDouble("_id");
            n_value.set((int)count,Math.toIntExact(doc.getLong("count")));

        //    System.out.println(count+": "+n_value.get((int)count));
            z=z+1;
        }
        return n_value;
    }



}
