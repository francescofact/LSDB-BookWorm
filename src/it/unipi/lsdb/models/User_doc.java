import com.mongodb.ConnectionString;
import com.mongodb.client.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;

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

    }

    public static boolean login(String username, String password) {

        MongoCursor<Document> cursor = coll.find(eq("username", username)).iterator();
        int counter = 0;
        if (cursor.hasNext()) {

            Document doc = cursor.next();
            if (password.equals(doc.getString("password"))) {
                return true;

            } else return false;
        } else
            return false;
    }

    public static User findUser(String username) {
        MongoCursor<Document> cursor = coll.find(eq("username", username)).iterator();
        int counter = 0;
        if (cursor.hasNext()) {

            Document doc = cursor.next();
            User u = new User(doc);
            return u;
        }
        else return null;
    }
   
}