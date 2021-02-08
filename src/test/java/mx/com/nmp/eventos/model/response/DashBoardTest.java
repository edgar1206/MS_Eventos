package mx.com.nmp.eventos.model.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
@ExtendWith(MockitoExtension.class)
class DashBoardTest {
    @InjectMocks
    private DashBoard dashBoard;
    @Test
    void getData() {
        dashBoard.getData();
    }


    @Test
    void setData() {
        Long[][] data= new Long[1][1];
        dashBoard.setData(data);
    }

    @Test
    void getLabels() {
        dashBoard.getLabels();
    }

    @Test
    void setLabels() {
        List<String> labels = new ArrayList<>();
        dashBoard.setLabels(labels);
    }

    @Test
    void getEvents() {
        dashBoard.getEvents();
    }

    @Test
    void setEvents() {
        List<String> eventos= new ArrayList<>();
        dashBoard.setEvents(eventos);
    }

    @Test
    void getTotal() {
        dashBoard.getTotal();
    }

    @Test
    void setTotal() {
        dashBoard.setTotal(5354);
    }

    @Test
    void getKey() {
        dashBoard.getKey();
    }

    @Test
    void setKey() {
        dashBoard.setKey("4353");
    }
}