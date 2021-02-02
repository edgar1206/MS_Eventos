package mx.com.nmp.eventos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class EventosApplicationTest {
    @InjectMocks
    private EventosApplication eventosApplication;

    @Test
    void main() {
        String[] args = new String[0];
        eventosApplication.main(args);
    }
}