
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.time.LocalDate;

//import javax.xml.bind.DatatypeConverter;


import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
//import org.bson.Document;
//import org.bson.conversions.Bson;

/*
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import it.unipi.giar.GiarSession;
import it.unipi.giar.MongoDriver;
import it.unipi.giar.Neo4jDriver;

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
    private String firstName;
    private String lastName;
    private int age;
    private LocalDate joinDate;
    private String email;
    private String country;

    /*
    private ArrayList<Document> wishlist;
    private ArrayList<Document> myBooks;
    private ArrayList<Document> ratings;
    */

    public User(String type, String username, String password, String email, String country,
                String firstName, String lastName, int age) {
        this.type = type;
        this.username = username;
        this.password = password;
        this.email = email;
        this.country = country;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        /*

        this.wishlist = new ArrayList<Document>();
        this.myGames = new ArrayList<Document>();
        this.ratings = new ArrayList<Document>();
        */
    }
    public static void addBook(String bookName, String username) {
        Neo4jDriver nd = Neo4jDriver.getInstance();
        try (Session session = nd.getDriver().session()) {
            session.writeTransaction(
                    new TransactionWork<Boolean>() {
                        @Override
                        public Boolean execute(Transaction tx) {
                            Result result = tx.run(
                                    "MATCH (n:Book) "
                                            + "WHERE n.name = $name "
                                            + "RETURN n"
                                    ,parameters ("name", bookName));

                            if(!result.hasNext()) {
                                tx.run("CREATE (n:Book {name: $name})"
                                        ,parameters ("name", bookName));
                            }

                            tx.run("MATCH (p:Person) "
                                            + "WHERE p.name = $username "
                                            + "MATCH (b:Book) "
                                            + "WHERE b.name = $bookName "
                                            + "CREATE (p)-[:READ]->(b)"
                                    ,parameters("username", username, "bookName", bookName));
                            return true;
                        };
                    }
            );
        }
    }

    public static void deleteBook(final String book, String username) {
        Neo4jDriver nd = Neo4jDriver.getInstance();
        try (Session session = nd.getDriver().session()) {
            session.writeTransaction(
                    new TransactionWork<Boolean>() {
                        @Override
                        public Boolean execute(Transaction tx) {
                            tx.run("MATCH (p: Person{name: $username})-[r:READ]->(b: Book{name:$name}) "
                                            + "DELETE r"
                                    ,parameters("username", username, "name", book));

                            //Delete the node if it has no relationships.
                            tx.run("MATCH (b:Book{name: $name}) "
                                            + "WHERE NOT ()-[:READ]->(b) "
                                            + "DELETE b"
                                    ,parameters("name", book));
                            return true;
                        }
                    }
            );
        }
    }

    //Takes inn User object and creates a node containing all information (does not account for already existing username
    public static void createUser(User user) {
        Neo4jDriver nd = Neo4jDriver.getInstance();
        try (Session session = nd.getDriver().session()) {
            session.run("CREATE (a:Person {name:$name, password:$password, email:$email, country:$country, firstName:$firstName, lastName:$lastName, age:$age})"
                        , parameters( "name", user.username, "password", user.password, "email", user.email, "country", user.country,
                                      "firstName", user.firstName, "lastName", user.lastName, "age", user.age));
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
                                    + "WHERE NOT ()-[:READ]->(b) "
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

    //Books is selected as CURRENTLY_READING. Can have more than one book.
    public static void currentlyReading(String user, String book){
        Neo4jDriver nd = Neo4jDriver.getInstance();
        try (Session session = nd.getDriver().session()) {
            session.writeTransaction(
                    new TransactionWork<Boolean>() {
                        @Override
                        public Boolean execute(Transaction tx) {
                            tx.run("MATCH (p:Person) "
                                            + "WHERE p.name = $user "
                                            + "MATCH (b:Book) "
                                            + "WHERE b.name = $book "
                                            + "MERGE (p)-[:CURRENTLY_READING]->(b)"
                                    ,parameters("user", user, "book", book));
                            return true;
                        };
                    }
            );
        }
    }

    //RATED book, removes CURRENTLY_READING, and addes READ. Does to add READ if book is not CURRENTLY_READ (problem maybe?)
    public static void rateBook(String user, String book, int rating){
        Neo4jDriver nd = Neo4jDriver.getInstance();
        try (Session session = nd.getDriver().session()) {
            session.writeTransaction(
                    new TransactionWork<Boolean>() {
                        @Override
                        public Boolean execute(Transaction tx) {
                            tx.run("MATCH (p:Person) "
                                            + "WHERE p.name = $user "
                                            + "MATCH (b:Book) "
                                            + "WHERE b.name = $book "
                                            + "MERGE (p)-[:RATED {rating: $rating}]->(b)"
                                    ,parameters("user", user, "book", book, "rating", rating));
                            tx.run("MATCH (p:Person)-[r:CURRENTLY_READING]->(b) "
                                        + "WHERE p.name = $user AND b.name= $book "
                                        + "DELETE r "
                                        + "MERGE (p)-[:READ]->(b)"
                                    ,parameters("user", user, "book", book));
                            return true;
                        }
                    }
            );
        }
    }

    //Will search for a username and return a username if it contains the username you searched for
    public static void searchUser(String user){
        Neo4jDriver nd = Neo4jDriver.getInstance();
            try (Session session = nd.getDriver().session()) {
                session.run("MATCH(p:Person) "
                                + "WHERE p.name CONTAINS $user "
                                + "RETURN p.type, p.name, p.password, p.email, p.country, p.firstName, p.lastName, p.age"
                                + "ORDER BY "
                                , parameters("user", user));
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}