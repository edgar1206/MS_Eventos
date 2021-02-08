package mx.com.nmp.eventos.model.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class TableTest {
    @InjectMocks
    private Table table;
    @Test
    void getFase() {
        table.getFase();
    }

    @Test
    void setFase() {
        table.setFase("prueba");
    }

    @Test
    void getRecurso() {
        table.getRecurso();
    }

    @Test
    void setRecurso() {
        table.setRecurso("prueba");
    }

    @Test
    void getInfo() {
        table.getInfo();
    }


    @Test
    void setInfo() {
        table.setInfo(5435);
    }

    @Test
    void getError() {
        table.getError();
    }

    @Test
    void setError() {
        table.setError(234);
    }

    @Test
    void getDebug() {
        table.getDebug();
    }

    @Test
    void setDebug() {
        table.setDebug(4324);
    }

    @Test
    void getFatal() {
        table.getFatal();
    }

    @Test
    void setFatal() {
        table.setFatal(434);
    }

    @Test
    void getTrace() {
        table.getTrace();
    }

    @Test
    void setTrace() {
        table.setTrace(34534);
    }
}