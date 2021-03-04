package mx.com.nmp.eventos.controller;

import mx.com.nmp.eventos.model.constant.AccionFase;
import mx.com.nmp.eventos.model.constant.Constants;
import mx.com.nmp.eventos.model.nr.Evento;
import mx.com.nmp.eventos.model.response.Acciones;
import mx.com.nmp.eventos.model.response.SecondLevel;
import mx.com.nmp.eventos.service.EventService;
import mx.com.nmp.eventos.utils.Validator;
import org.junit.jupiter.api.Assertions;
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
    private Constants constants;

    private Validator validator;
    @Mock
    private AccionFase accionFase;


    private EventService eventServiceAcciones;
    @Mock
    private Acciones acciones;


    /*@BeforeEach
    void setUp(){
        eventService.loadActions();
    }*/
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

      /*  Accion accion= new Accion();
        accion.setNombre("Registro");
        Fase fase = new Fase();
        fase.setNombre("Validar datos");
        List<Accion> accionList= new ArrayList<>();
        List<Fase> faseList= new ArrayList<>();
        accion.setFases(faseList);
        accionList.add(accion);*/ //esto no se usa

        SecondLevel secondLevel = new SecondLevel();
        Mockito.when(eventService.getSecondLevel("Registro")).thenReturn(secondLevel);
       // Mockito.when(validator.validateAction("Registro")).thenReturn(true);
        Mockito.when(constants.getTIME_ZONE()).thenReturn("America/Mexico_City");
        Mockito.when(constants.getINDICE()).thenReturn("smnr_mimonte_eventos");
       // Mockito.when(accionFase.accionFase.getAcciones()).thenReturn(accionList);
       //Mockito.when(acciones.getAcciones()).thenReturn(accionList);
     /*  try(MockedStatic<Acciones> mockedStatic=Mockito.mockStatic(Acciones.class)){
           mockedStatic.when(Acciones::getAcciones).thenReturn(accionList);
       }*/
      /*  try(MockedStatic<AccionFase> mockedStatic=Mockito.mockStatic(AccionFase.class)){
           mockedStatic.when(AccionFase::accionFase).thenReturn(accionList);
       }*/
      /*   try(MockedStatic<Validator> mockedStatic=Mockito.mockStatic(Validator.class)){
           mockedStatic.when(Validator::validateAction("434")).thenReturn(true);
       }*/
        //eventService.loadActions();

        Assertions.assertEquals(secondLevel,eventController.getSecondLevel("Login"));
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
        Assertions.assertNotNull(eventController.getThirdLevel("Login","Usuario Monte"));

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