package it.unipi.lsdb.models;


import org.bson.Document;

import java.util.ArrayList;

public class Rating {
    String book;
    double value;
    int id;
    public Rating(String b, double v){
        this.book=b;
        this.value=v;
    }

    public Document create_doc(){
        Document doc = null;
        doc = new Document();
        doc=doc.append("book",this.book);
        doc=doc.append("value",this.value);

        return doc;
    }

    public String getBook(){ return book; }

}
