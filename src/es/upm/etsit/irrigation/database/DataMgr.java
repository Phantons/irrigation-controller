package es.upm.etsit.irrigation.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import es.upm.etsit.irrigation.shared.Mode;
import es.upm.etsit.irrigation.shared.Schedule;
import es.upm.etsit.irrigation.shared.Zone;
import es.upm.etsit.irrigation.util.DayOfWeek;
import es.upm.etsit.irrigation.util.Time;
import main.java.com.telecobets.analizer.Arbitrage;
import main.java.com.telecobets.analizer.settings.filter.Filter;
import main.java.com.telecobets.analizer.stats.StatsType;
import main.java.com.telecobets.database.Account;
import main.java.com.telecobets.database.DBStatements;
import main.java.com.telecobets.database.Database;

public class DataMgr {
  private static final Logger logger = LogManager.getLogger(DataMgr.class.getName());
  
  public static void addModeToDB(final Mode mode) {
    PreparedStatement stmt;
    Connection conn = null;
    try {
      conn = Database.getConnection();
      stmt = conn.prepareStatement(Database.getPreparedStatement(DBStatements.MAIN_INS_MODE),
          Statement.RETURN_GENERATED_KEYS);
      int i = 1;
      stmt.setString(i++, mode.getName());
      stmt.executeUpdate();
      ResultSet tableKeys = stmt.getGeneratedKeys();
      tableKeys.next();
      int modeID = tableKeys.getInt(1);
      mode.setID(modeID);
      
      for (Zone zone : mode.getZones()) {
        stmt = conn.prepareStatement(Database.getPreparedStatement(DBStatements.MAIN_INS_ZONE),
            Statement.RETURN_GENERATED_KEYS);
        i = 1;
        stmt.setInt(i++, zone.getPinAddress());
        stmt.setInt(i++, modeID);
        stmt.setString(i++, zone.getName());
        stmt.setBoolean(i++, zone.shouldTakeWeather());
        
        stmt.executeUpdate();
        
        tableKeys = stmt.getGeneratedKeys();
        tableKeys.next();
        int zoneID = tableKeys.getInt(1);
        
        Schedule schedule = zone.getSchedule();
        
        stmt = conn.prepareStatement(Database.getPreparedStatement(DBStatements.MAIN_INS_DAYS));
        i = 1;
        stmt.setInt(i++, zoneID);
        for (DayOfWeek day : DayOfWeek.values()) {
          stmt.setBoolean(i++, schedule.isDaySelected(day));
        }
        stmt.executeUpdate();
        
        for (Time time : schedule.getIrrigationCycles()) {
          stmt = conn.prepareStatement(Database.getPreparedStatement(DBStatements.MAIN_INS_SCHEDULES));
          i = 1;
          stmt.setInt(i++, zoneID);
          stmt.setInt(i++, time.getStart().getHour());
          stmt.setInt(i++, time.getStart().getMinute());
          stmt.setLong(i++, time.getTimeout());
          stmt.executeUpdate();
        }
      }
    } catch (SQLException e) {
      logger.error("Error adding account to db");
      logger.throwing(e);
    } finally {
      Database.closeConnection(conn);
    }
  }
  
  public static void removeMode(final Mode mode) {
    Thread dbController = new Thread("DBController") {
      public void run() {
        Connection conn = null;
        try {
          conn = Database.getConnection();
          PreparedStatement stmt = null;
          // First delete schedules and days for each zone id of the mode
          for (Zone zone : mode.getZones()) {
            stmt = conn.prepareStatement(Database.getPreparedStatement(DBStatements.MAIN_DEL_DAYS_BY_ZONE_ID));
            stmt.setInt(1, zone.getPinAddress());
            stmt.executeUpdate();
            
            stmt = conn.prepareStatement(Database.getPreparedStatement(DBStatements.MAIN_DEL_SCHEDULES_BY_ZONE_ID));
            stmt.setInt(1, zone.getPinAddress());
            stmt.executeUpdate(); 
          }
          
          // Then delete all zones for the mode.
          stmt = conn.prepareStatement(Database.getPreparedStatement(DBStatements.MAIN_DEL_ZONES_BY_MODE_ID));
          stmt.setInt(1, mode.getID());
          stmt.executeUpdate(); 
          
          // Finally delete the mode
          stmt = conn.prepareStatement(Database.getPreparedStatement(DBStatements.MAIN_DEL_MODE));
          stmt.setInt(1, mode.getID());
          stmt.executeUpdate();
          
        } catch (SQLException e) {
          logger.error("Error removing settings to DB");
          logger.throwing(e);
        } finally {
          Database.closeConnection(conn);
        }
      }
    };
    dbController.start();
  }
}
