package mx.com.nmp.eventos.utils;

import mx.com.nmp.eventos.model.constant.AccionFaseApp;
import mx.com.nmp.eventos.model.constant.Nivel;
import mx.com.nmp.eventos.model.response.Accion;
import mx.com.nmp.eventos.model.response.Fase;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

public class Validator {

    public static Boolean validateLevel(String level){
        for(String nivel:Nivel.name){
            if(level.equalsIgnoreCase(nivel)){
                return true;
            }
        }
        return false;
    }

    public static Boolean validateAction(String action, String appName){
        try{
            for(int i = 0; i < AccionFaseApp.app.get(appName).getAcciones().size(); i++){
                if(action.equals(AccionFaseApp.app.get(appName).getAcciones().get(i).getNombre())){
                    return true;
                }
            }
            return false;
        }catch (NullPointerException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre de app no valido o no se encontraron acciones.");
        }

    }

    public static Boolean validateActionPhase(String action, String phase, String appName) {
        try{
            for(int i = 0; i < AccionFaseApp.app.get(appName).getAcciones().size(); i++){
                if(action.equalsIgnoreCase(AccionFaseApp.app.get(appName).getAcciones().get(i).getNombre())){
                    List<Fase> fases = AccionFaseApp.app.get(appName).getAcciones().get(i).getFases();
                    for (Fase fase : fases) {
                        if (fase.getNombre().equals(phase)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }catch (NullPointerException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre de app no valido o no se encontraron acciones.");
        }
    }

    public static List<Accion> validateActionPhase(List<Accion> actions){
        List<Accion> resultado = new ArrayList<>();
        for (Accion action : actions) {
            if(!action.getNombre().equalsIgnoreCase(""))
                resultado.add(action);
        }
        return resultado;
    }

}
