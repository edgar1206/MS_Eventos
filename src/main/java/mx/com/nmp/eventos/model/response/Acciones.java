package mx.com.nmp.eventos.model.response;

import java.io.Serializable;
import java.util.List;

public class Acciones implements Serializable {

    private List<Accion> acciones;

    public List<Accion> getAcciones() {
        return acciones;
    }

    public void setAcciones(List<Accion> acciones) {
        this.acciones = acciones;
    }
}
