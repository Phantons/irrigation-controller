package es.upm.etsit.irrigation.util;

public enum DayOfWeek {
  MONDAY(0),
  TUESDAY(1),
  WEDNESDAY(2),
  THURSDAY(3),
  FRIDAY(4),
  SATURDAY(5),
  SUNDAY(6);
  
  private int ID;
  
  DayOfWeek(int _id) {
    ID = _id;
  }
  
  public int getID() {
    return ID;
  }
  
  public static DayOfWeek getFromID(int id) {
    for (DayOfWeek day : DayOfWeek.values())
      if (day.getID() == id)
        return day;
    return null;
  }
}
