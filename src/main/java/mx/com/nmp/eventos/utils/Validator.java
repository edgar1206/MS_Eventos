package mx.com.nmp.eventos.utils;

import mx.com.nmp.eventos.model.constant.AccionFase;
import mx.com.nmp.eventos.model.constant.Nivel;
import mx.com.nmp.eventos.model.response.Accion;
import mx.com.nmp.eventos.model.response.Fase;

import java.util.ArrayList;
import java.util.List;

public class Validator {
    public static Boolean validateLevel(String level){
        for(String nivel:Nivel.name){
            if(level.equalsIgnoreCase(nivel)){
                return true;
            }
        }
        return false;
    }

    public static Boolean validateAction(String action){
        for(int i = 0; i < AccionFase.accionFase.getAcciones().size(); i++){
            if(action.equals(AccionFase.accionFase.getAcciones().get(i).getNombre())){
                return true;
            }
        }
        return false;
    }

    public static Boolean validateActionPhase(String action, String phase) {
        for(int i = 0; i < AccionFase.accionFase.getAcciones().size(); i++){
            if(action.equalsIgnoreCase(AccionFase.accionFase.getAcciones().get(i).getNombre())){
                List<Fase> fases = AccionFase.accionFase.getAcciones().get(i).getFases();
                for (int j = 0; j < fases.size(); j++) {
                    if(fases.get(j).getNombre().equals(phase)){
                        return true;
                    }
                }
            }
        }
        return false;
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
