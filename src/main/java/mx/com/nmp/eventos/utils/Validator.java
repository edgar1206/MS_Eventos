package mx.com.nmp.eventos.utils;

import mx.com.nmp.eventos.model.constant.Accion;

public class Validator {

    public static Boolean validateAction(String action){
        for(int i = 0; i < Accion.name.length; i++){
            if(action.equalsIgnoreCase(Accion.name[i])){
                return true;
            }
        }
        return false;
    }

    public static Boolean validatePhase(String phase) {
        for (int i = 0; i < Accion.codigoFase.length; i++) {
            if (phase.equalsIgnoreCase(Accion.codigoFase[i])) {
                return true;
            }
        }
        return false;
    }

    public static String getPhase(String phase){
        for(int i = 0; i < Accion.accionFase.length; i++){
            if(phase.equalsIgnoreCase(Accion.accionFase[i])){
                if (phase.contains("Login")){
                    phase = "Login".concat(",Autenticar");
                }
                if (phase.contains("MS")){
                    phase = "Registro tarjeta MS".concat(",Registro tarjeta");
                }
                if (phase.contains("Delete")){
                    phase = "Delete token".concat(",Autenticar");
                }
                if (phase.contains("Refresh")){
                    phase = "Token refresh".concat(",Autenticar");
                }
                if (phase.contains("CC")){
                    phase = "Associating the card to the customer".concat(",Registro Tarjeta");
                }
                if (phase.contains("OpenPay")){
                    phase = "OpenPay read customer list from merchant - Lista vacia".concat(",Registro Tarjeta");
                }
            }
        }
        return phase;
    }

    public static String getAccion(String accionFase){
        return accionFase.split(",")[0];
    }

    public static String getFase(String accionFase){
        return accionFase.split(",")[1];
    }

}
