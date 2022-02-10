package it.unipi.lsdb.models;

public class Mongo_comms {
  public static void setup(){
        Mongo_comms.connect();
        User_comms.connect();
        return;
    }
   public static void close(){
        Mongo_comms.disconnect();
        User_comms.disconnect();
        return;
    }
}
