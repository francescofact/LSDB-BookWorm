package it.unipi.lsdb.models;


import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.*;
import org.bson.Document;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;

import static org.neo4j.driver.Values.parameters;


public class Book {
    String author;
    String bookformat;
    String desc;
    String genre;
    String img;
    String isbn;
    String isbn13;
    String link;
    int pages;
    double rating;
    int reviews;
    String title;
    int totalratings;


    public Book(String author_d, String bookformat_d, String desc_d,String genre_d,
        String img_d, String isbn_d, String isbn13_d, String link_d, int pages_d,
        double rating_d, int reviews_d, String title_d, int totalratings_d){
        author=author_d;
        bookformat=bookformat_d;
        desc=desc_d;
        genre=genre_d;
        img=img_d;
        isbn=isbn_d;
        isbn13=isbn13_d;
        link=link_d;
        pages=pages_d;
        rating=rating_d;
        reviews=reviews_d;
        title=title_d;
        totalratings=totalratings_d;

    }
    
    public Book(Document doc){
        Book b = new Book(doc.getString("author"),doc.getString("bookformat"),doc.getString("desc"),doc.getString("genre"),
                doc.getString("img"),doc.getString("isbn"),doc.getString("isbn13"),doc.getString("link"),doc.getInteger("pages"),
                doc.getDouble("rating"),doc.getInteger("reviews"),doc.getString("title"), doc.getInteger("totalratings"));
        author=b.author;
        bookformat=b.bookformat;
        desc=b.desc;
        genre=b.genre;
        img=b.img;
        isbn=b.isbn;
        isbn13=b.isbn13;
        link=b.link;
        pages=b.pages;
        rating=b.rating;
        reviews=b.reviews;
        title=b.title;
        totalratings=b.totalratings;
    }
    
    

    public String getTitle(){
        return title;
    }

    public String getDesc(){
        return desc;
    }

    public String getImageURL(){
        return img;
    }

    public String getAuthor() { return author; }
    
    
    public Document create_doc(){
        Document doc = null;
       // System.out.println(this.author);
        doc = new Document().append("author",this.author).append("bookformat",this.bookformat).append("desc",this.desc).append("genre",this.genre)
                .append("img",this.img).append("isbn",this.isbn).append("isbn13",this.isbn13).append("link",this.link).append("pages",this.pages)
                .append("rating",this.rating).append("reviews",this.reviews).append("title",this.title).append("totalratings",this.totalratings);

        return doc;
    }


    //Adds a book to the Neo4jDB if it does not exist
    public static void addBook(String bookName) {
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

    //RATE book
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
                            return true;
                        }
                    }
            );
        }
    }
    
    public static ArrayList<String> suggestedBooks(String user) {
        ArrayList<String> suggestedBooks = new ArrayList<String>();
        Neo4jDriver nd = Neo4jDriver.getInstance();
        try (Session session = nd.getDriver().session()) {
            session.readTransaction(
                    new TransactionWork<Boolean>() {
                        @Override
                        public Boolean execute(Transaction tx) {
                            Result result = tx.run("MATCH (p:Person{name:$user})-[:FOLLOW]->(n)-[r:RATED]->(b:Book) "
                                            + "WHERE NOT (p)-[r]->(b) "
                                            + "RETURN b.name, count(r), sum(r.rating) AS total"
                                            + "ORDER BY total/count(r) "
                                            + "LIMIT 10"
                                    , parameters("user", user));

                            while (result.hasNext()) {
                                Record rec = result.peek();
                                result.next();
                                suggestedBooks.add((rec.get(0).asString()));
                            }
                            return true;
                        }
                    }
            );
        }
        return suggestedBooks;
    }

}
