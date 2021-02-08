package mx.com.nmp.eventos.model.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
@ExtendWith(MockitoExtension.class)
class SecondLevelTest {
    @InjectMocks
    private SecondLevel secondLevel;
    @Test
    void getTable() {
        secondLevel.getTable();
    }

    @Test
    void setTable() {
        List<Table> tables = new ArrayList<>();
        secondLevel.setTable(tables);
    }

    @Test
    void getChart() {
        secondLevel.getChart();
    }

    @Test
    void setChart() {
        DashBoard chart= new DashBoard();
        secondLevel.setChart(chart);
    }
}