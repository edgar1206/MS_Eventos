package mx.com.nmp.eventos.model.constant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class ConstantsTest {
    @InjectMocks
    private Constants constants;

    @Test
    void getINDICE() {
        constants.getINDICE();
    }

    @Test
    void getTIME_ZONE() {
        constants.getTIME_ZONE();
    }
}