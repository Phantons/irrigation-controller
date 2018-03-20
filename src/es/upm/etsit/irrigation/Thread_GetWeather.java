package es.upm.etsit.irrigation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Thread_GetWeather extends Thread {
  private final Logger logger = LogManager.getLogger(getClass().getName());
  
  private final int location;
  private final String WEATHER_ADDRESS = "https://opendata.aemet.es/opendata/api/prediccion/especifica/municipio/horaria/";
  private final String API_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYXJxdWl0b3MubWNtQGdtYWlsLmNvbSIs"
      + "Imp0aSI6IjNkZDA0Y2Q4LTk4MmYtNGU5ZS04NTU1LTIwZGI2OTIyYjVkZiIsImlzcyI6IkFFTUVUIiwi"
      + "aWF0IjoxNTE5OTIyNDg3LCJ1c2VySWQiOiIzZGQwNGNkOC05ODJmLTRlOWUtODU1NS0yMGRiNjkyMmI1"
      + "ZGYiLCJyb2xlIjoiIn0.OJQ7Y9ywbaFGkYlbk9LEv8xllS_A0buf3-TXDIALpJI";
  
  
  public Thread_GetWeather(int _location) {
    location = _location;
  }
  
  public void run() {
    
    try {
      HttpResponse<JsonNode> response = Unirest.get(WEATHER_ADDRESS + location + "/?api_key=" + API_KEY)
          .header("cache-control", "no-cache")
          .asJson();
      
      JSONArray array = response.getBody().getArray();
      
      
    } catch (UnirestException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }
  
  private String getURLOfData(String url) {
    try {
      JsonNode data = fetchURL(WEATHER_ADDRESS + location + "/?api_key=" + API_KEY);
      
      if (data != null) {
        JSONArray response = data.getArray();
        
        int status = response.getJSONObject(0).getInt("estado");
        
        if (status == 200) {
          return response.getJSONObject(0).getString("datos");
        }
      }
      
    } catch (IllegalArgumentException | UnirestException | InterruptedException e) {
      logger.error("Couldn't fetch URL", e);
    }
    
    return null;
  }
  
  
  private JsonNode fetchURL(String url) throws UnirestException, InterruptedException,
    IllegalArgumentException {
    
    HttpResponse<JsonNode> response = Unirest.get(url)
        .header("cache-control", "no-cache")
        .asJson();
    
    String retryHeader = response.getHeaders().getFirst("Retry-After");

    if (response.getStatus() == 200) {
      return response.getBody();
    } else if (response.getStatus() == 429 && retryHeader != null) {
      Long waitSeconds = Long.valueOf(retryHeader);
      Thread.sleep(waitSeconds * 1000);
      return fetchURL(url);
    } else {
      throw new IllegalArgumentException("No data at " + url);
    }
  }
}
