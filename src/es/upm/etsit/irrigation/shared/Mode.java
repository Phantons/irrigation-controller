package es.upm.etsit.irrigation.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Mode implements Serializable {
  private static final long serialVersionUID = 2L;
  
  
  private final int ID;
  private List<Zone> zones = new ArrayList<Zone>();
  private boolean isActive = false;
  
  public Mode(int _ID) {
    ID = _ID;
  }
  
  
  public int getID() {
    return ID;
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


  /**
   * @return the isActive
   */
  public boolean isActive() {
    return isActive;
  }


  /**
   * @param isActive the isActive to set
   */
  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }
  
}
