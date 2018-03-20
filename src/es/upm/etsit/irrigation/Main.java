package es.upm.etsit.irrigation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import es.upm.etsit.irrigation.database.DBStatements;
import es.upm.etsit.irrigation.database.Database;
import es.upm.etsit.irrigation.exceptions.ConnectionException;
import es.upm.etsit.irrigation.shared.Mode;
import es.upm.etsit.irrigation.shared.Schedule;
import es.upm.etsit.irrigation.shared.Zone;
import es.upm.etsit.irrigation.socket.SocketHandler;
import es.upm.etsit.irrigation.util.DayOfWeek;
import es.upm.etsit.irrigation.util.LocalTime;
import es.upm.etsit.irrigation.util.Time;

public class Main {
  
  private static final Logger logger = LogManager.getLogger(Main.class.getName());
  
  // 1 second
  private final static long WAITING_TIME = 1000;
  
  // 1 min
  private final static long UPDATE_TIME = 60000;
  private static long updateTime = 0;
  
  // 1 hour
  private final static long WEATHER_TIME = 60000*60;
  private static long weatherTime = 0;
  
  private static Controller controller;
  
  private static final String USER = "ELCO";
  private static final String PASSWORD = "ELCO";
  
  // TODO: Move this to a external file
  private static final String RASPI_ID = "ELCORASPI";
  
  public static void main(String[] args) {
    // load database
    try {
      Database.init(USER, PASSWORD);
    } catch (ConnectionException e) {
      logger.throwing(e);
    }
    
    
    Connection conn = null;
    
    try {
      conn = Database.getConnection();
    } catch (SQLException e) {
      logger.throwing(e);
    }
    
    if (conn == null)
      System.exit(0);
    
    Database.checkDatabase(conn);
    Database.closeConnection(conn);
    
    Mode mode = null;
    try {
      mode = loadDatabase();
    } catch (SQLException e) {
      logger.throwing(e);
    }
    
    controller = new Controller(mode);
    
    // get location
    //controller.setLocation(SocketHandler.askLocation(RASPI_ID));
    
    // loop
    while (true) {
      if (System.currentTimeMillis() > updateTime) {
        // Ask new mode if exists.
        Mode newMode = SocketHandler.askMode(RASPI_ID);
        if (newMode != null) {
          controller.setNewActiveMode(newMode);
        }
        
        // Ask if some zone should irrigate now
        Integer[] portIrrigationTimes = SocketHandler.shouldIrrigateNow(RASPI_ID);
        for (int i = 0; i < portIrrigationTimes.length; i++) {
          Integer time = portIrrigationTimes[i];
          if (time != null && time > 0) {
            controller.activeElectrovalve(controller.getZoneByPinAddress(i), time);
          }
        }
        
        // Send current port status
        SocketHandler.sendPortStatus(RASPI_ID, controller.getCurrentZoneStatus());
        
        updateTime = System.currentTimeMillis() + UPDATE_TIME;
      }
      
      if (System.currentTimeMillis() > weatherTime) {
        Thread thread = new Thread_GetWeather(controller.getLocation());
        thread.start();
        
        weatherTime = System.currentTimeMillis() + WEATHER_TIME;
      }
      
      
      controller.checkAndStartIrrigationCycles();
      controller.checkInactivePorts();
      
      waiting();
    }
  }
  
  private static Mode loadDatabase() throws SQLException {
    Connection conn = Database.getConnection();
    PreparedStatement stmt = null;
    ResultSet result = null;
    
    // Load accounts info
    stmt = conn.prepareStatement(Database.getPreparedStatement(DBStatements.MAIN_SEL_MODES));
    result = stmt.executeQuery();
    result.next();
    
    int ID = result.getInt("ID");
    String name = result.getString("name");
    
    Mode mode = new Mode(ID, name);
    
    stmt = conn.prepareStatement(Database.getPreparedStatement(DBStatements.MAIN_SEL_ZONES_BY_MODE_ID));
    stmt.setInt(1, mode.getID());
    result = stmt.executeQuery();
    
    while(result.next()) {
      int pinAddress = result.getInt("pinAddress");
      String zoneName = result.getString("name");
      boolean shouldTakeWeather = result.getBoolean("shouldTakeWeather");
      
      Zone zone = new Zone(zoneName, pinAddress);
      zone.setShouldTakeWeather(shouldTakeWeather);
      mode.getZones().add(zone);
      
      PreparedStatement stmt2 = conn.prepareStatement(Database.getPreparedStatement(DBStatements.MAIN_SEL_DAYS_BY_ZONE_ID));
      stmt2.setInt(1, zone.getPinAddress());
      ResultSet result2 = stmt2.executeQuery();
      result2.next();
      
      boolean[] days = new boolean[DayOfWeek.DAYS_OF_WEEK];
      for (int i = 0; i < DayOfWeek.DAYS_OF_WEEK; i++) {
        days[i] = result2.getBoolean(i);
      }
      
      stmt2 = conn.prepareStatement(Database.getPreparedStatement(DBStatements.MAIN_SEL_SCHEDULES_BY_ZONE_ID));
      stmt2.setInt(1, zone.getPinAddress());
      result2 = stmt2.executeQuery();
      
      List<Time> irrigationCycle = new ArrayList<Time>();
      while(result2.next()) {
        int startHour = result2.getInt("startHour");
        int startMinute = result2.getInt("startMinute");
        long timeout = result2.getLong("timeout");
        
        Time time = new Time(LocalTime.of(startHour, startMinute), timeout);
        irrigationCycle.add(time);
      }
      
      Schedule schedule = new Schedule(days, irrigationCycle);
      zone.setSchedule(schedule);
      
    }
    
    return mode;
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
