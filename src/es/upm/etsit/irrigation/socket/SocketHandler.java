package es.upm.etsit.irrigation.socket;
import javax.xml.bind.DatatypeConverter;

public class SocketHandler {

  
  public static byte[] askMode(String identificador) {
    String respuesta = Comunicaciones.consultarAlServidor("/ActMd/" + identificador + "/", 1)[0];
    if(respuesta != null){
    	if(respuesta.equals("IdFalso")){
    		return new byte[0];
    	}
    	byte[] modoSerializado = DatatypeConverter.parseHexBinary(respuesta);
    	return modoSerializado;
    }
    return null;
  }
  
  public static Integer[] shouldIrrigateNow(String identificador) {
    String respuesta = Comunicaciones.consultarAlServidor("/ShReg/" + identificador + "/", 1)[0];
    if(respuesta != null){
    	if(respuesta.equals("IdFalso")){
    		return new Integer[0];
    	}
    	Integer[] salida = new Integer[32];
    	String[] separados = respuesta.substring(1, respuesta.length() - 1).split("/");
    	for(int j1 = 0; j1 < 32; j1 = j1 + 1){
    		if(separados[j1].equals("null") == false){
    			salida[j1] = Integer.parseInt(separados[j1]);
    		}
    		else{
    			salida[j1] = 0;
    		}
    	}
    	return salida;
    }
    return null;
  }
  
  public static void sendPortStatus(Boolean[] status, String identificador) {
    String peticion = "/InfRg/" + identificador + "/";
    for(int j1 = 0; j1 < 32; j1 = j1 + 1){
    	if(status[j1] != null){
    		peticion = peticion + Boolean.toString(status[j1]) + "/";
    	}
    	else{
    		peticion = peticion + "null/";
    	}
    }
    Comunicaciones.consultarAlServidor(peticion, 0);
  }
}
