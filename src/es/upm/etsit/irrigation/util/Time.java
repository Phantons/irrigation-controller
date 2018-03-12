package es.upm.etsit.irrigation.util;

import java.io.Serializable;
import java.time.LocalTime;

public class Time implements Serializable {
  private static final long serialVersionUID = 2L;
  
  private final LocalTime start;
  private final long timeoutInSeconds;
  
  
  public Time(LocalTime _start, long _timeout) {
    start = _start;
    timeoutInSeconds = _timeout;
  }
  
  public boolean isBetween(int hour, int minute) {
    LocalTime end = LocalTime.of(start.getHour(), start.getMinute());
    end = end.plusSeconds(timeoutInSeconds);
    
    return hour >= start.getHour() && hour <= end.getHour() && minute >= start.getMinute() && minute <= end.getMinute();
  }
  
  public long getTimeout() {
    return timeoutInSeconds;
  }
}
