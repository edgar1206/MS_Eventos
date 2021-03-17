package mx.com.nmp.eventos.model.response;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SecondLevel implements Serializable {

    //private List<Table> table;
    private List<Map<String, String>> table;
    private DashBoard chart;

    public List<Map<String, String>> getTable() {
        return table;
    }

    public void setTable(List<Map<String, String>> table) {
        this.table = table;
    }

   /* public List<Table> getTable() {
        return table;
    }

    public void setTable(List<Table> table) {
        this.table = table;
    }*/

    public DashBoard getChart() {
        return chart;
    }

    public void setChart(DashBoard chart) {
        this.chart = chart;
    }

}
