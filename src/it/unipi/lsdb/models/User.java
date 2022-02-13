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

    private Role type;
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

    public User(Role type, String username, String password, String email, String country,
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
   public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.type = Role.USER;
    }


    public User(Document doc){
        User aux=new User(doc.getString("username"),doc.getString("password"));
        this.username=aux.username;
        this.password=aux.password;
        if (doc.getString("type").equals("admin")){
            this.type = Role.ADMIN;
        } else {
            this.type = Role.USER;
        }
    }

    //Constructor when inserting Record object into User()

    public Document create_doc(){
        Document doc = null;
        // System.out.println(this.author);
        doc = new Document().append("username",this.username).append("password",this.password);

        if(type!=null)
            doc=doc.append("type",this.type);

        if(firstName!=null)
            doc=doc.append("firstName",this.firstName);

        if(lastName!=null)
            doc=doc.append("lastName",this.lastName);

        if(age!=0)
            doc=doc.append("age",this.age);

        if(joinDate!=null)
            doc=doc.append("joinDate",this.joinDate);

        if(email!=null)
            doc=doc.append("email",this.email);

        if(country!=null)
            doc=doc.append("country",this.country);


        return doc;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Role getType(){ return type; }

    //Adds a book to the Neo4jDB if it does not exist. Will create relationship READ between user and book.
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
                                tx.run("MERGE (n:Book {name: $name})"
                                        ,parameters ("name", bookName));
                            }

                            /*tx.run("MATCH (p:Person) "
                                            + "WHERE p.name = $username "
                                            + "MATCH (b:Book) "
                                            + "WHERE b.name = $bookName "
                                            + "CREATE (p)-[:READ]->(b)"
                                    ,parameters("username", username, "bookName", bookName));

                             */
                            return true;
                        };
                    }
            );
        }
    }

    //Deletes book from graph. Might be useful
    public static void deleteBook(final String book) {
        Neo4jDriver nd = Neo4jDriver.getInstance();
        try (Session session = nd.getDriver().session()) {
            session.writeTransaction(
                    new TransactionWork<Boolean>() {
                        @Override
                        public Boolean execute(Transaction tx) {
                            tx.run("MATCH (b: Book{name: $book})"
                                            + "DETACH DELETE b"
                                    ,parameters("book", book));
                            return true;
                        }
                    }
            );
        }
    }

    //Takes inn User object and creates a node containing all information (does not account for already existing username)
    //That should be done elsewhere
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

    //Books is selected as CURRENTLY_READING. Can have more than one book.
    public static void currentlyReading(String user, String book){
        Neo4jDriver nd = Neo4jDriver.getInstance();
        try (Session session = nd.getDriver().session()) {
            session.writeTransaction(
                    new TransactionWork<Boolean>() {
                        @Override
                        public Boolean execute(Transaction tx) {
                            Result result = tx.run("MATCH (b:Book) "
                                            + "WHERE b.name = $book "
                                            + "RETURN b"
                                    ,parameters ("book", book));
                            if(!result.hasNext()) {
                                tx.run("MERGE (b:Book {name: $book})"
                                        ,parameters ("book", book));
                            }
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

    //RATED book, removes CURRENTLY_READING.
    public static void rateBook(String user, String book, int rating) {
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
                                    , parameters("user", user, "book", book, "rating", rating));
                            tx.run("MATCH (p:Person)-[r:CURRENTLY_READING]->(b) "
                                            + "WHERE p.name = $user AND b.name= $book "
                                            + "DELETE r "
                                    , parameters("user", user, "book", book));
                            return true;
                        }
                    }
            );
        }
    }

    public static ArrayList<String> getRatedBooks(String username) {
        ArrayList<String> rated = new ArrayList<String>();
        Neo4jDriver nd = Neo4jDriver.getInstance();
        try (Session session = nd.getDriver().session()) {
            session.readTransaction(
                    new TransactionWork<Boolean>() {
                        @Override
                        public Boolean execute(Transaction tx) {
                            Result result = tx.run("MATCH (p: Person)-[:RATED]->(b:Book) "
                                    + "WHERE p.name = $username "
                                    + "RETURN b.name AS book",parameters ("username", username));

                            while(result.hasNext()) {
                                Record rec = result.next();
                                rated.add((rec.get("book").asString()));
                            }
                            return true;
                        };
                    }
            );
        }
        return rated;
    }
}

