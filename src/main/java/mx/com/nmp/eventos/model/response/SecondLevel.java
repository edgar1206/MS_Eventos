package mx.com.nmp.eventos.model.response;

import java.io.Serializable;
import java.util.List;

public class SecondLevel implements Serializable {

    private List<Table> table;
    private DashBoard chart;

    public List<Table> getTable() {
        return table;
    }

    public void setTable(List<Table> table) {
        this.table = table;
    }

    public DashBoard getChart() {
        return chart;
    }

    public void setChart(DashBoard chart) {
        this.chart = chart;
    }
}
