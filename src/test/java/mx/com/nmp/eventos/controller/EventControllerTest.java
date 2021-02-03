package mx.com.nmp.eventos.controller;

import mx.com.nmp.eventos.model.nr.Evento;
import mx.com.nmp.eventos.model.response.SecondLevel;
import mx.com.nmp.eventos.service.EventService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @InjectMocks
    private EventController eventController;

    @Mock
    private EventService eventService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void addEvent() {
        Evento evento = new Evento();
        evento.setIdEvent("1");
        eventController.addEvent(evento);
    }

    @Test
    void getDashboard() {
        Assertions.assertNotNull(eventController.getDashboard());

    }

    @Test
    void getSecondLevel() {
        SecondLevel secondLevel = new SecondLevel();
        Mockito.when(eventService.getSecondLevel("Registro")).thenReturn(secondLevel);
        Assertions.assertEquals(secondLevel,eventController.getSecondLevel("Registro"));
    }

    @Test
    void getThirdLevel() {
        Assertions.assertNotNull(eventController.getThirdLevel("Autenticar"));

    }
}