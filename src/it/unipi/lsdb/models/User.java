package it.unipi.lsdb.models;

import java.time.LocalDate;
import java.util.ArrayList;

//import javax.xml.bind.DatatypeConverter;

import it.unipi.lsdb.Role;
import org.bson.Document;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import org.neo4j.driver.Record;
//import org.bson.Document;
//import org.bson.conversions.Bson;

/*
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.unwind;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Sorts.descending;

 */

import static org.neo4j.driver.Values.parameters;




public class User {

    private String type;
    private String username;
    private String password;
    ArrayList<Rating> ratings;


    public User(Role type, String username, String password) {
        setType(type);
        this.username = username;
        this.password = password;
        this.ratings=new ArrayList<Rating>();
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.type = "basic";
        this.ratings=new ArrayList<Rating>();
    }


    public User(Document doc){
        User aux=new User(doc.getString("username"),doc.getString("password"));
        this.username=aux.username;
        this.password=aux.password;
        this.type=doc.getString("type");
        this.ratings = new ArrayList<Rating>();

    }

    //Constructor when inserting Record object into User()
    public User(Document doc, ArrayList<Rating> ratings){
           User aux=new User(doc.getString("username"),doc.getString("password"));
        this.username=aux.username;
        this.password=aux.password;
        this.type=doc.getString("type");
        this.ratings = ratings;
    }
    
     public Document create_doc(){
        Document doc = null;
        // System.out.println(this.author);
        doc = new Document().append("username",this.username).append("password",this.password);

        if(type!=null)
            doc=doc.append("type",this.type);

        ArrayList <Document> aux = new ArrayList<Document>();
        int i =0;
        while(this.ratings.size()>i){

                aux.add(new Document("id",i+1).append("book",this.ratings.get(i).book).append("value",this.ratings.get(i).value));
                i++;
            }

         doc=doc.append("Ratings",aux);

        return doc;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getType(){
        if (this.type.equals("banned")){
            return Role.BANNED;
        } else if (this.type.equals("admin")){
            return Role.ADMIN;
        } else {
            return Role.USER;
        }
    }

    public void setType(Role type){
        if (type == Role.BANNED){
            this.type = "banned";
        } else if (type == Role.ADMIN){
            this.type = "admin";
        } else {
            this.type = "basic";
        }
    }

    public ArrayList<Rating> getRatings() {
        return ratings;
    }

    //Takes inn User object and creates a node containing all information (does not account for already existing username)
    //That should be done elsewhere
    public static void createUser(User user) {
        Neo4jDriver nd = Neo4jDriver.getInstance();
        try (Session session = nd.getDriver().session()) {
            session.run("CREATE (a:Person{name:$name})"
                        , parameters( "name", user.username));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Removes all relationships of user and deletes it. If a book is left w/o relationships, it will be deleted.
    public static void deleteUser(String user) {
        Neo4jDriver nd = Neo4jDriver.getInstance();
        try (Session session = nd.getDriver().session()) {
            session.writeTransaction(
                    new TransactionWork<Boolean>() {
                        @Override
                        public Boolean execute(Transaction tx) {
                            tx.run("MATCH (n:Person {name: $user}) "
                                            + "DETACH DELETE n"
                                    , parameters("user", user));
                            //Delete the node if it has no relationships.
                            tx.run("MATCH (b:Book) "
                                    + "WHERE size((b)--())=0 "
                                    + "DELETE b");
                            return true;
                        }
                    }
            );
        }
    }

    //Follows user. Only possible to have one such relationship between two specific users.
    public static void followUser(String follower, String toFollow) {
        Neo4jDriver nd = Neo4jDriver.getInstance();
        try (Session session = nd.getDriver().session()) {
            session.writeTransaction(
                    new TransactionWork<Boolean>() {
                        @Override
                        public Boolean execute(Transaction tx) {
                            tx.run("MATCH (p:Person) "
                                            + "WHERE p.name = $follower "
                                            + "MATCH (n:Person) "
                                            + "WHERE n.name = $toFollow "
                                            + "MERGE (p)-[:FOLLOW]->(n)"
                                    ,parameters("follower", follower, "toFollow", toFollow));
                            return true;
                        };
                    }
            );
        }
    }

    //Unfollows a user by deleting the relationship.
    public static void unFollowUser(String unFollower, String toUnFollow){
        Neo4jDriver nd = Neo4jDriver.getInstance();
        try (Session session = nd.getDriver().session()) {
            session.writeTransaction(
                    new TransactionWork<Boolean>() {
                        @Override
                        public Boolean execute(Transaction tx) {
                            tx.run("MATCH (p:Person {name: $unFollower })-[r:FOLLOW]->(n:Person {name: $toUnFollow }) "
                                    + "DELETE r"
                                    , parameters("unFollower", unFollower, "toUnFollow", toUnFollow));
                            return true;
                        }
                    }
            );
        }
    }
    
        public static ArrayList<String> suggestedUsers(String user) {
        ArrayList<String> suggestedUsers = new ArrayList<String>();
        Neo4jDriver nd = Neo4jDriver.getInstance();
        try (Session session = nd.getDriver().session()) {
            session.readTransaction(
                    new TransactionWork<Boolean>() {
                        @Override
                        public Boolean execute(Transaction tx) {
                            Result result = tx.run("MATCH (p:Person{name:$user}), (n:Person), ()-[r:FOLLOW]->(n) "
                                            + "WHERE NOT (p)-->(n) AND NOT p.name = n.name "
                                            + "RETURN n.name, count(r) "
                                            + "ORDER BY count(r) "
                                            + "LIMIT 10"
                                    , parameters("user", user));

                            while (result.hasNext()) {
                                Record rec = result.peek();
                                result.next();
                                suggestedUsers.add((rec.get(0).asString()));
                            }
                            return true;
                        }
                    }
            );
        }
        return suggestedUsers;
    }
    
        public static Boolean checkFollow(String username1, String username2) {
        Neo4jDriver nd = Neo4jDriver.getInstance();
        try (Session session = nd.getDriver().session()) {
            return session.readTransaction(
                    new TransactionWork<Boolean>() {
                        @Override
                        public Boolean execute(Transaction tx) {
                            Result result = tx.run("MATCH  (p1:Person {name:$username1}), (p2:Person {name:$username2}) "
                                            + "RETURN EXISTS ((p1)-[:FOLLOW]->(p2)) "
                                    ,parameters("username1", username1, "username2", username2));
                            return result.peek().get(0).asBoolean();
                        };
                    }
            );
        }
    }


}

