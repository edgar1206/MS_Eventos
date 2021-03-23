package mx.com.nmp.eventos.controller;

import com.google.gson.Gson;
import mx.com.nmp.eventos.model.constant.AccionFaseApp;
import mx.com.nmp.eventos.model.nr.Evento;
import mx.com.nmp.eventos.model.nr.EventoDto;
import mx.com.nmp.eventos.model.response.Acciones;
import mx.com.nmp.eventos.model.response.DashBoard;
import mx.com.nmp.eventos.model.response.SecondLevel;
import mx.com.nmp.eventos.service.EventService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventControllerTest {

    @InjectMocks
    private EventController eventController;

    @Mock
    private EventService serviceLog;

    @Before
    public void setUp(){
        serviceLog.getFaseAction("MiMonte");
        cargaAccionFase();

    }

    @Test
    public void getDashboard() {
        List<DashBoard> dashBoards = new ArrayList<>();
        when(serviceLog.getDashboard("MiMonte")).thenReturn(dashBoards);
        Assert.assertEquals(eventController.getDashboard("MiMonte"),dashBoards);
    }

    @Test
    public void getSecondLevel(){
        SecondLevel secondLevel = new SecondLevel();
        when(serviceLog.getSecondLevel("Login","MiMonte")).thenReturn(secondLevel);
        Assert.assertEquals(eventController.getSecondLevel("Login","MiMonte"),secondLevel);
    }
    @Test
    public void getSecondLevelException(){
        boolean thrown = false;
        try {
            eventController.getSecondLevel("Usuario","MiMonte");
        } catch (ResponseStatusException e) {
            thrown = true;
        }
    }

    @Test
    public void getThirdLevel(){
        List<DashBoard> dashBoards = new ArrayList<>();
        when( serviceLog.getThirdLevel("Login", "Autenticar","MiMonte")).thenReturn(dashBoards);
        Assert.assertEquals(eventController.getThirdLevel("Login","Autenticar","MiMonte"),dashBoards);
    }
    @Test
    public void getThirdLevelException(){
        boolean thrown = false;
        try {
            eventController.getThirdLevel("Login","Autenticarr","MiMonte");
        } catch (ResponseStatusException e) {
            thrown = true;
        }
    }
    @Test
    public void getFourthLevel(){
        List<Evento> eventos = new ArrayList<>();
//      when(serviceLog.getFourthLevel("Login", "Autenticarr","error", "2021-01-22","2021-01-22","MiMonte")).thenReturn(eventos);
        Assert.assertEquals(eventController.getFourthLevel("MiMonte","Autenticar","Login","error", "2021-01-22"),eventos);
    }
    @Test
    public void getFourthLevelException(){
        boolean thrown = false;
        try {
            eventController.getFourthLevel("MiMonte","Autenticar","Login","error", "2021-01-22");
        } catch (ResponseStatusException e) {
            thrown = true;
        }

    }
    @Test
    public void getActions(){
        Acciones acciones = new Acciones();
        when(serviceLog.getFaseAction("MiMonte")).thenReturn(acciones);
        Assert.assertEquals(eventController.getActionPhase("MiMonte"),acciones);
    }
    @Test
    public void addEvento(){
        EventoDto evento = new EventoDto();
        eventController.addEvent(evento);
        verify(serviceLog).addEvent(any(Evento.class));
    }

    private void cargaAccionFase(){
        String json = "{\n" +
                "    \"acciones\": [\n" +
                "        {\n" +
                "            \"nombre\": \"Login\",\n" +
                "            \"fases\": [\n" +
                "                {\n" +
                "                    \"nombre\": \"Initiate auth\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"nombre\": \"Usuario monte\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"nombre\": \"Autenticar\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"nombre\": \"Boletas\",\n" +
                "            \"fases\": [\n" +
                "                {\n" +
                "                    \"nombre\": \"Obtener créditos\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"nombre\": \"Contratos por folio\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"nombre\": \"Desacarga ticket\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"nombre\": \"Registro\",\n" +
                "            \"fases\": [\n" +
                "                {\n" +
                "                    \"nombre\": \"Validar datos\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"nombre\": \"Solicitar reinicio contraseña\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"nombre\": \"Registrar nueva contraseña\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"nombre\": \"Solicitar activación token\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"nombre\": \"Solicitar alta cuenta\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"nombre\": \"Validar medio contacto\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"nombre\": \"Solicitar reinicio contraseña oauth\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"nombre\": \"Registrar nueva contraseña oauth\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"nombre\": \"Token oauth\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"nombre\": \"Crud tarjetas\",\n" +
                "            \"fases\": [\n" +
                "                {\n" +
                "                    \"nombre\": \"Consulta tarjeta\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"nombre\": \"Eliminar tarjeta\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"nombre\": \"Edición tarjeta\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"nombre\": \"Movimientos\",\n" +
                "            \"fases\": [\n" +
                "                {\n" +
                "                    \"nombre\": \"Descarga\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"nombre\": \"Consulta\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"nombre\": \"Token refresh\",\n" +
                "            \"fases\": [\n" +
                "                {\n" +
                "                    \"nombre\": \"Autenticar\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"nombre\": \"Solicitar pagos\",\n" +
                "            \"fases\": [\n" +
                "                {\n" +
                "                    \"nombre\": \"Realizar pago\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"nombre\": \"Finalizar transacción\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"nombre\": \"Openpay read customer list from merchant - lista vacia\",\n" +
                "            \"fases\": [\n" +
                "                {\n" +
                "                    \"nombre\": \"Registro tarjeta\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"nombre\": \"Campaña\",\n" +
                "            \"fases\": [\n" +
                "                {\n" +
                "                    \"nombre\": \"Getcampaign\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        Gson gson = new Gson();
        Acciones acciones = gson.fromJson(json, Acciones.class);
        Map<String, Acciones> mapa = new HashMap<>();
        mapa.put("MiMonte",acciones);
        AccionFaseApp.app=mapa;
    }

}