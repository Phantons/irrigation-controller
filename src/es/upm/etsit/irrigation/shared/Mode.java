package es.upm.etsit.irrigation.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Mode implements Serializable {
  private static final long serialVersionUID = 2L;
  
  
  private transient int ID;
  private final String name;
  private List<Zone> zones = new ArrayList<Zone>();
  
  
  public Mode(int _ID, String _name) {
    ID = _ID;
    name = _name;
  }
  
  public int getID() {
    return ID;
  }
  
  public void setID(int _ID) {
    ID = _ID;
  }
  
  public String getName() {
    return name;
  }


  /**
   * @return the zones
   */
  public List<Zone> getZones() {
    return zones;
  }


  /**
   * @param zones the zones to set
   */
  public void setZones(List<Zone> zones) {
    this.zones = zones;
  }
}
