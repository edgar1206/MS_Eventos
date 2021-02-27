package mx.com.nmp.eventos.model.response;

import java.util.List;

public class Accion {

    private String nombre;
    private List<Fase> fases;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Fase> getFases() {
        return fases;
    }

    public void setFases(List<Fase> fases) {
        this.fases = fases;
    }

}
