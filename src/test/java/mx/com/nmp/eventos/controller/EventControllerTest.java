package mx.com.nmp.eventos.controller;

import mx.com.nmp.eventos.model.nr.Evento;
import mx.com.nmp.eventos.model.response.SecondLevel;
import mx.com.nmp.eventos.service.EventService;
import mx.com.nmp.eventos.utils.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @InjectMocks
    private EventController eventController;

    @Mock
    private EventService eventService;
    @Mock
    private Validator validator;
    //private AccionFase accionFase;

    private EventService eventServiceAcciones;

    @BeforeEach
    void setUp(){
        eventService.loadActions();
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
       /* Accion accion= new Accion();
        accion.setNombre("Registro");
        Fase fase = new Fase();
        fase.setNombre("Validar datos");
        List<Accion> accionList= new ArrayList<>();
        List<Fase> faseList= new ArrayList<>();
        accion.setFases(faseList);
        accionList.add(accion);*/
        SecondLevel secondLevel = new SecondLevel();
        Mockito.when(eventService.getSecondLevel("Registro")).thenReturn(secondLevel);
        Mockito.when(validator.validateAction("Registro")).thenReturn(true);
        Assertions.assertEquals(secondLevel,eventController.getSecondLevel("Registro"));
    }
    @Test
    void getSecondLevelException() {
        boolean thrown = false;
        try {
            eventController.getSecondLevel("Registroo");
        } catch (ResponseStatusException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    void getThirdLevel() {
        Assertions.assertNotNull(eventController.getThirdLevel("Login","Initiate auth"));

    }
    @Test
    void getThirdLevelException() {
        boolean thrown = false;
        try {
            eventController.getThirdLevel("Login","Initiate authh");
        } catch (ResponseStatusException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    void getFourthLevel() {
        eventController.getFourthLevel(null,null, null,"2020-12-01");
    }

}