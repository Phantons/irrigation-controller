package es.upm.etsit.irrigation.shared;

import java.io.Serializable;

public class Zone implements Serializable {
  private static final long serialVersionUID = 2L;
  
  private final int pinAddress;
  private Schedule schedule;
  private boolean shouldTakeWeather;
  private boolean isWatering;
  
  public Zone(int _pinAddress) {
    pinAddress = _pinAddress;
  }

  public int getPinAddress() {
    return pinAddress;
  }

  public Schedule getSchedule() {
    return schedule;
  }

  public void setSchedule(Schedule schedule) {
    this.schedule = schedule;
  }

  public boolean shouldTakeWeather() {
    return shouldTakeWeather;
  }

  public void setShouldTakeWeather(boolean shouldTakeWeather) {
    this.shouldTakeWeather = shouldTakeWeather;
  }

  /**
   * @return the isActive
   */
  public boolean isWatering() {
    return isWatering;
  }

  /**
   * @param isActive the isActive to set
   */
  public void setWatering(boolean isActive) {
    this.isWatering = isActive;
  }
}
