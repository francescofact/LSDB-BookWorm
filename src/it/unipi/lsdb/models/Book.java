package it.unipi.lsdb.models;


import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.*;
import org.bson.Document;


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
    float rating;
    int reviews;
    String title;
    int totalratings;


    public Book(String author_d, String bookformat_d, String desc_d,String genre_d,
        String img_d, String isbn_d, String isbn13_d, String link_d, int pages_d,
        float rating_d, int reviews_d, String title_d, int totalratings_d){
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

}
