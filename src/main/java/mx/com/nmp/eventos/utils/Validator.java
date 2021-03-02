package mx.com.nmp.eventos.utils;

import mx.com.nmp.eventos.model.constant.AccionFase;
import mx.com.nmp.eventos.model.response.Accion;
import mx.com.nmp.eventos.model.response.Fase;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Validator {

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

        List<String> acciones = new ArrayList<>();
        List<Accion> resultado = new ArrayList<>();

        for (Accion action : actions) {
            acciones.add(action.getNombre());
        }
        List<String> valoresUnicos = acciones
                .stream()
                .distinct()
                .collect(Collectors.toList());

        Iterator<Accion> iteratorActions = actions.iterator();
        while (iteratorActions.hasNext()) {
            Accion accion = iteratorActions.next();
            if(valoresUnicos.contains(accion.getNombre())){
                valoresUnicos.remove(accion.getNombre());
                iteratorActions.remove();
                resultado.add(accion);
            }
        }

        for (Accion accionResultado : resultado) {
            for (Accion action : actions) {
                if(accionResultado.getNombre().equals(action.getNombre())){
                    if(accionResultado.getFases().size() > 0 && action.getFases().size() > 0 ) {
                        accionResultado.setFases(unifyPhases(accionResultado.getFases(), action.getFases()));
                    }
                }
            }
        }
        return resultado;
    }

    private static List<Fase> unifyPhases(List<Fase> unique, List<Fase> duplicates){
        return Stream.concat(unique.stream(), duplicates.stream())
                .distinct()
                .collect(Collectors.toList());
    }

}
