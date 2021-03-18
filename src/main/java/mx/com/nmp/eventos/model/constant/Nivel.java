package mx.com.nmp.eventos.model.constant;

import java.util.ArrayList;
import java.util.List;

public class Nivel {

    public static final String[] name = {"Info", "Error", "Debug", "Fatal", "Trace"};

    private List<String> levels = new ArrayList<>();

    public List<String> getLevels() {
        return levels;
    }

    public void setLevels(List<String> levels) {
        this.levels = levels;
    }

}
