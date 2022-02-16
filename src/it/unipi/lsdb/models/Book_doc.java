package it.unipi.lsdb.models;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.ConnectionString;
import com.mongodb.client.model.*;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import com.mongodb.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.descending;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;

import java.util.function.Consumer;
import java.util.regex.Pattern;


public class Book_doc {
    public static int number_results=10;
    static String adress = "mongodb+srv://lsdb:lsdb@lsdb.hzdrg.mongodb.net";
    static String db_name = "db";
    static String collection = "Books";
    public static MongoDatabase database = null;
    public static MongoCollection<Document> coll = null;
    public static MongoClient myClient = null;
    static Consumer<Document> printDocuments = doc ->
    {System.out.println(doc.toJson());};

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
    public static void create_index(){
        coll.createIndex(Indexes.descending("totalratings"));

    }
    public static void create_index_v2(){
        coll.createIndex(Indexes.descending("rating"));

    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static void insert(Document doc) {
        coll.insertOne(doc);

    }

    public static void disconnect() {
        myClient.close();

    }

    public static void print_coll() {
        try (MongoCursor<Document> cursor = coll.find().iterator()) {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        }
    }
    public static void string_to_int() {
        try (MongoCursor<Document> cursor = coll.find().iterator()) {
            while (cursor.hasNext()) {

                Bson updates = Updates.set("totalratings", Integer.parseInt(cursor.next().getString("totalratings")));
                Document query = new Document().append("title", cursor.next().getString("title"));
                UpdateOptions options = new UpdateOptions().upsert(true);
                UpdateResult result = coll.updateOne(query, updates, options);

            }
        }
    }

    public static Book get_by_name(String name) {
        Bson projectionFields = Projections.fields(

                Projections.excludeId());
        Document doc = coll.find(Filters.eq("title", name))
                .projection(projectionFields)
                .first();

        if (doc == null){
            return null;
        } else {
            return new Book(doc);
        }

    }
    
    public static boolean rate_book_mg(String title, double rating){
        Book b= get_by_name(title);
      
        int old_tr=b.totalratings;
        double old_rt=b.rating;

        int new_tr=b.totalratings+1;
        double new_rt=((old_rt*old_tr)+rating)/new_tr;
        new_rt=round(new_rt, 2); // returns 200.35

        MongoCursor<Document> cursor = coll.find(eq("title",title)).iterator();
        if(!cursor.hasNext()) {
            return false;
        }
            while (cursor.hasNext()) {

                Bson updates = Updates.set("totalratings", new_tr);
                Bson updates2 = Updates.set("rating", new_rt);

                Document query = new Document().append("title", cursor.next().getString("title"));
                UpdateOptions options = new UpdateOptions().upsert(true);
                UpdateResult result = coll.updateOne(query, updates, options);
                UpdateResult result2 = coll.updateOne(query, updates2, options);


            }

            return true;
        }

    
    public static Book[] best_rated(int i){
        Book[] book_array= new Book[i];

        Bson projectionFields = Projections.fields(

                Projections.excludeId());
                MongoCursor<Document> cursor= coll.find(gt(
                        "totalratings",10000))
                .projection(projectionFields)
                .sort(Sorts.descending("rating")).limit(i)
                .iterator();
                int count=0;

                while (cursor.hasNext() && count<i){

                    Document doc = cursor.next();
                    book_array[count]= new Book(doc);
                    count++;
                }
                return book_array;
    }               //Returns a book array with best rated books
    
    public static boolean edit_book(String title, String img, String desc, String author){
        BasicDBObject query = new BasicDBObject();

        query.put("title",title);
        BasicDBObject update = new BasicDBObject();
        BasicDBObject update2 = new BasicDBObject();
        BasicDBObject update3 = new BasicDBObject();
        update.put("$set", new BasicDBObject("desc", desc));
        update2.put("$set", new BasicDBObject("img", img));
        update3.put("$set", new BasicDBObject("author", author));

        coll.updateOne(query,update);
        coll.updateOne(query,update2);
        coll.updateOne(query,update3);

        return true;
    }
    

    public static Book[] search_by_user(String name){
        String patternStr = name;
        Pattern pattern= Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
        Bson filter = Filters.regex("author",pattern);
        Bson projectionFields = Projections.fields(
                //Projections.include("author", "title"),
                Projections.excludeId());
        MongoCursor<Document> cursor = coll.find(filter).projection(projectionFields).sort(Sorts.descending("reviews")).iterator();
        int counter=0;
        Book book_array[]= new Book[number_results];
        while(cursor.hasNext() && counter<number_results){
            Document doc = cursor.next();
            book_array[counter]=new Book(doc);
            counter++;}
        return book_array;

    }     //Returns a book array with n= number_results where name is contained in author
    
     public static ArrayList <String> most_famous_authors(){

        Bson group = group("$author", sum("totalrat", "$totalratings"));
        Bson sort = sort(descending("totalrat"));
        Bson limit = limit(10);

        List <Document> results=  coll.aggregate(Arrays.asList(group, sort,limit)).into(new ArrayList<>());
        ArrayList <String> author=new ArrayList<String>();

        int count=0;
        while(!results.isEmpty() && count<number_results){
            Document doc = results.get(count);
            author.add(doc.getString("_id"));
            //System.out.println(results.get(count));
            count++;
        }
        return author;
    }
    
    public static ArrayList <String> most_published() {
        Bson group = group("$author", sum("count", 1L));
        Bson sort = sort(descending("count"));
        Bson limit = limit(10);

        List <Document> results=  coll.aggregate(Arrays.asList(group, sort,limit)).into(new ArrayList<>());
        ArrayList <String> author=new ArrayList<String>();
        int count=0;
        while(!results.isEmpty() && count<number_results){
            Document doc = results.get(count);
            author.add(doc.getString("_id"));
            count++;
        }
        return author;
    }
    
    public static ArrayList <String> best_rated_authors(){

        Bson group = group("$author", avg("avgrating", "$rating"));
        Bson group1 = group("$author", sum("count", 1L));



        Bson match = match(gte("totalratings", 10000));

        Bson sort = sort(descending("avgrating"));
        Bson limit = limit(10);
        ArrayList <String> author=new ArrayList<String>();

        List <Document> results=  coll.aggregate(Arrays.asList(match,group, sort,limit)).into(new ArrayList<>());
        int count=0;
        while(!results.isEmpty() && count<number_results){
            Document doc = results.get(count);
            author.add(doc.getString("_id"));
            count++;
        }
        return author;
    }
    public static Book[] search_by_name(String name){
        String patternStr = name;
        Pattern pattern= Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
        Bson filter = Filters.regex("title",pattern);
        Bson projectionFields = Projections.fields(
                Projections.excludeId());
        MongoCursor<Document> cursor = coll.find(filter).projection(projectionFields).sort(Sorts.descending("reviews")).iterator();
        int count =0;
        Book[] book_array=new Book[number_results];

        while(cursor.hasNext() && count<number_results){
            Document doc = cursor.next();
            book_array[count]=new Book(doc);
            count++;
        }
        return book_array;
    }     //Returns a book array with n=number_reults where name is contained in the title

}
