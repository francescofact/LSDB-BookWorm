package it.unipi.lsdb.models;

public class Mongo_comms {
  public static void setup(){
        Book_doc.connect();
        User_doc.connect();
        Book_doc.create_index();
        Book_doc.create_index_v2();
    }

    public static void close(){
        Book_doc.disconnect();
        User_doc.disconnect();
    }
}
