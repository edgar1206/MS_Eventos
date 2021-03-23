package mx.com.nmp.eventos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import mx.com.nmp.eventos.model.constant.AccionFaseApp;
import mx.com.nmp.eventos.model.constant.Constants;
import mx.com.nmp.eventos.model.nr.Evento;
import mx.com.nmp.eventos.model.response.Acciones;
import mx.com.nmp.eventos.repository.RepositoryLog;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @InjectMocks
    private EventService eventService;


    private RestHighLevelClient restHighLevelClient;
    @Mock
    private RepositoryLog repositoryLog;

    @Mock
    private CountResponse countResponse;
    private String index;
    /**
     * The constant CLUSTER.
     */
    public static final String CLUSTER = "monte";
    /**
     * ElasticServer version.
     */
    public static final String EMBEDDED_ELASTIC_VERSION = "6.5.3";
    private static EmbeddedElastic embeddedElastic = null;


    @Mock
    private Constants constants;

    @BeforeEach
    void setUp() throws Exception {
        index="smnr_mimonte_eventos";
        try {
            embeddedElastic = EmbeddedElastic.builder()
                    .withIndex(index)
                    .withElasticVersion(EMBEDDED_ELASTIC_VERSION)
                    .withSetting(PopularProperties.HTTP_PORT, 21121)
                    .withSetting(PopularProperties.CLUSTER_NAME, CLUSTER)
                    .withEsJavaOpts("-Xms512m -Xmx1024m")
                    .withStartTimeout(3, MINUTES)
                    .build();
            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("localhost", 21121, "http")
                    ));
            embeddedElastic.start();
            insertGeneric(getEvento("1", "MiMonte", "Microservicios-L2","ERROR","Usuario Monte","https://rsi.montepiedad.com.mx//NMP/GestionClientes/Cliente/v2/usuarioMonte:8089", "Usuario Monte", "Login","Elemento configuracion","prueba","prueba","prueba"), restHighLevelClient, "1");
            insertGeneric(getEvento("2","MiMonte", "Microservicios-L2" ,"INFO","Usuario Monte","https://rsi.montepiedad.com.mx//NMP/GestionClientes/Cliente/v2/usuarioMonte:8089","usuario Monte", "Login","Elemento configuracion","prueba","prueba","prueba"), restHighLevelClient, "2");
        }catch(final Exception e) {
            throw new RuntimeException("EmbeddedElastic, can not initialize");
        }

        eventService.setRestHighLevelClient(restHighLevelClient);
    }
    @AfterEach
    public void stopEmbeddedElastic(){
        embeddedElastic.stop();
        embeddedElastic = null;
    }
    private Evento getEvento(String id, String applicationName, String resolutionTower, String level, String phase, String resource, String category, String action, String configurationElement, String severity, String eventDescription, String eventType){
        Evento eventoTest = new Evento();
        eventoTest.setIdEvent(id);
        eventoTest.setApplicationName(applicationName);
        eventoTest.setResolutionTower(resolutionTower);
        eventoTest.setTimeGenerated(new Date());
        eventoTest.setEventLevel(level);
        eventoTest.setEventPhase(phase);
        eventoTest.setEventCategory(category);
        eventoTest.setEventResource(resource);
        eventoTest.setEventAction(action);
        eventoTest.setConfigurationElement(configurationElement);
        eventoTest.setSeverity(severity);
        eventoTest.setEventDescription(eventDescription);
        eventoTest.setEventType(eventType);
        return eventoTest;
    }

    @Test
    void getDashboard(){
        Mockito.when(constants.getTIME_ZONE()).thenReturn("America/Mexico_City");
        Mockito.when(constants.getINDICE()).thenReturn("smnr_mimonte_eventos");
        Assertions.assertNotNull( eventService.getDashboard("MiMonte"));
    }

    @Test
    void getSecondLevel()  {
        cargaAccionFase();
        Mockito.when(constants.getTIME_ZONE()).thenReturn("America/Mexico_City");
        Mockito.when(constants.getINDICE()).thenReturn("smnr_mimonte_eventos");
        Assertions.assertNotNull(eventService.getSecondLevel("Login","MiMonte"));
    }
    @Test
    void getThirdLevel()  {
        Mockito.when(constants.getTIME_ZONE()).thenReturn("America/Mexico_City");
        Mockito.when(constants.getINDICE()).thenReturn("smnr_mimonte_eventos");
        Assertions.assertNotNull(eventService.getThirdLevel("Login", "Usuario Monte","MiMonte"));
    }

    @Test
    void getFourthLevelParametros() {
        Mockito.when(constants.getTIME_ZONE()).thenReturn("America/Mexico_City");
        Mockito.when(constants.getINDICE()).thenReturn("smnr_mimonte_eventos");
        Assertions.assertEquals(eventService.getFourthLevel("Autenticar","Token refresh", "error","2020-12-01", "2021-01-25","MiMonte").size(),0);
    }
    @Test
    void getFourthLevel() {
        Mockito.when(constants.getTIME_ZONE()).thenReturn("America/Mexico_City");
        Mockito.when(constants.getINDICE()).thenReturn("smnr_mimonte_eventos");
        Assertions.assertEquals(eventService.getFourthLevel(null,null, null,"2020-12-01", "2021-01-25","MiMonte").size(),0);
    }
    @Test
    void getFourthLevelException() {
        boolean thrown = false;
        try {
            eventService.getFourthLevel("Autenticar",null, "error","2020-12-01", "2021-01-25","MiMonte");;
        } catch (ResponseStatusException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    void addEventException(){
        eventService.addEvent(getEvento("1", "MiMonte", "Microservicios-L2","ERROR","Usuario Monte","https://rsi.montepiedad.com.mx//NMP/GestionClientes/Cliente/v2/usuarioMonte:8089", "Usuario Monte", "Login","Elemento configuracion","prueba","prueba","prueba"));
        verify(repositoryLog).save(any(Evento.class));
    }
    @Test
    void getfaseAction(){
        Mockito.when(constants.getTIME_ZONE()).thenReturn("America/Mexico_City");
        Mockito.when(constants.getINDICE()).thenReturn("smnr_mimonte_eventos");
        Mockito.when(constants.getMONTH()).thenReturn("4");
        eventService.getFaseAction("MiMonte");
    }
    private static <T> Map<String, Object> transformerObject(T clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(clazz, Map.class);
    }
    private static <T> void insertGeneric(T clazz, RestHighLevelClient client, String id) throws IOException{
        IndexRequest indexRequest = new IndexRequest("smnr_mimonte_eventos", "doc", id).source(transformerObject(clazz));
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