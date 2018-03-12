package es.upm.etsit.irrigation;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import es.upm.etsit.irrigation.shared.Mode;
import es.upm.etsit.irrigation.shared.Zone;

public class Controller {
  private final long MILLISECONDS = 1000;
  
  private Mode mode;  
  final GpioController gpio = GpioFactory.getInstance();
  
  private Map<Zone, GpioPinDigitalOutput> zonesPin = new HashMap<Zone, GpioPinDigitalOutput>();
  
  
  public void checkAndStartIrrigationCycles() {
    Calendar now = GregorianCalendar.getInstance();
    now.setTimeInMillis(System.currentTimeMillis());
    
    for (Zone zone : mode.getZones()) {
      long timeout = 0;
      if (!zone.isWatering() && (timeout = zone.getSchedule().isTimeForIrrigation(now)) > 0) {
        if (zonesPin.get(zone) == null) {
          makePin(zone);
        }
        
        activeElectrovalve(zone, timeout*MILLISECONDS);
      }
    }
  }
  
  public void checkInactivePorts() {
    for (Zone zone : zonesPin.keySet()) {
      if (zonesPin.get(zone).isLow()) {
        zone.setWatering(false);
      }
    }
  }
  
  private void makePin(Zone zone) {
    GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(zone.getPinAddress()));
    pin.setShutdownOptions(true, PinState.LOW);
    zonesPin.put(zone, pin);
  }
  
  private void activeElectrovalve(Zone zone, long timeout) {
    zonesPin.get(zone).pulse(timeout);
    zone.setWatering(true);
  }
  
  
  public void setNewActiveMode(Mode newMode) {
    for (Zone zone : zonesPin.keySet()) {
      zone.setWatering(false);
      zonesPin.get(zone).low();
    }
    
    zonesPin.clear();
    
    mode = newMode;
  }
  
  
}
