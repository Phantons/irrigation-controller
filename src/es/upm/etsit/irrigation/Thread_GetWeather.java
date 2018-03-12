package es.upm.etsit.irrigation;

import java.net.InetAddress;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Thread_GetWeather extends Thread {
  
  private final InetAddress myAddress;
  private final String WEATHER_ADDRESS = "https://opendata.aemet.es/opendata/api/prediccion/especifica/municipio/horaria/";
  private final String API_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYXJxdWl0b3MubWNtQGdtYWlsLmNvbSIs"
      + "Imp0aSI6IjNkZDA0Y2Q4LTk4MmYtNGU5ZS04NTU1LTIwZGI2OTIyYjVkZiIsImlzcyI6IkFFTUVUIiwi"
      + "aWF0IjoxNTE5OTIyNDg3LCJ1c2VySWQiOiIzZGQwNGNkOC05ODJmLTRlOWUtODU1NS0yMGRiNjkyMmI1"
      + "ZGYiLCJyb2xlIjoiIn0.OJQ7Y9ywbaFGkYlbk9LEv8xllS_A0buf3-TXDIALpJI";
  
  
  public Thread_GetWeather(InetAddress _myAddress) {
    myAddress = _myAddress;
  }
  
  public void run() {
    
    try {
      HttpResponse response = Unirest.get(WEATHER_ADDRESS + "")
          .header("cache-control", "no-cache")
          .asJson();
    } catch (UnirestException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }

}
