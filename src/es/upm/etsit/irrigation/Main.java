package es.upm.etsit.irrigation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
  
  private static final Logger logger = LogManager.getLogger(Main.class.getName());
  
  // 1 second
  private final static long WAITING_TIME = 1000;
  
  private Controller controller;
  
  public static void main(String[] args) {
    // Init connections

    // get location
    
    // load database
    loadDatabase();

    // loop
    while (true) {
      
      waiting();
    }
  }
  
  
  private static void loadDatabase() {
    
  }
  
  
  private static void waiting() {
    // Wait some time
    try {
      Thread.sleep(WAITING_TIME);
    }catch(Exception e) {
      logger.throwing(e);
    }
  }
  
  
  
}
