package it.unipi.lsdb.models;


public class Rating {
    String book;
    String user;
    double value;
    int id;
    public Rating(String b, String u , double v){
        this.book=b;
        this.user=u;
        this.value=v;
    }

}
