package es.upm.etsit.irrigation.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.upm.etsit.irrigation.util.DayOfWeek;
import es.upm.etsit.irrigation.util.Time;

public class Schedule implements Serializable {
  private static final long serialVersionUID = 2L;
  
  private List<DayOfWeek> days = new ArrayList<DayOfWeek>();
  private List<Time> irrigationCycles = new ArrayList<Time>();
  
  
  public Schedule(List<DayOfWeek> _days, List<Time> _irrigationCycles) {
    days = _days;
    irrigationCycles = _irrigationCycles;
  }
  
  public long isTimeForIrrigation(Calendar now) {
    int today = now.get(Calendar.DAY_OF_WEEK);
    int hour = now.get(Calendar.HOUR);
    int minute = now.get(Calendar.MINUTE);
    
    for (Time irrigationCycle : irrigationCycles) {
      if (irrigationCycle.isBetween(hour, minute)) {
        for (DayOfWeek day : days) {
          if (day.getID() == today)
            return irrigationCycle.getTimeout();
        }
      }
    }
    
    return 0;
  }
}
