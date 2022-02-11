package it.unipi.lsdb.models;

public class Mongo_comms {
  public static void setup(){
        Book_doc.connect();
        User_doc.connect();
    }

    public static void close(){
        Book_doc.disconnect();
        User_doc.disconnect();
    }
}
