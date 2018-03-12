package es.upm.etsit.irrigation.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import es.upm.etsit.irrigation.shared.Mode;

public class DataMgr {
  private static final Logger logger = LogManager.getLogger(DataMgr.class.getName());
  
  private List<Mode> allModes;
  
  
  public void addMode(Mode mode) {
    allModes.add(mode);
  }
  
}
