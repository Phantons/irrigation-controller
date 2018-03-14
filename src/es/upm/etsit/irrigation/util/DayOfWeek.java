package es.upm.etsit.irrigation.util;

public enum DayOfWeek {
  SUNDAY(1),
  MONDAY(2),
  TUESDAY(3),
  WEDNESDAY(4),
  THURSDAY(5),
  FRIDAY(6),
  SATURDAY(7);
  
  private int ID;
  public static final int DAYS_OF_WEEK = 7;
  
  DayOfWeek(int _id) {
    ID = _id;
  }
  
  public int getID() {
    return ID;
  }
}
