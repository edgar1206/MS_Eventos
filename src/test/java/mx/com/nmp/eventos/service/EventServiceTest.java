package mx.com.nmp.eventos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import mx.com.nmp.eventos.model.constant.Constants;
import mx.com.nmp.eventos.model.nr.Evento;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Assert;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static java.util.concurrent.TimeUnit.MINUTES;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @InjectMocks
    private EventService eventService;


    private RestHighLevelClient restHighLevelClient;

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
        Mockito.when(constants.getTIME_ZONE()).thenReturn("America/Mexico_City");
        Mockito.when(constants.getINDICE()).thenReturn("smnr_mimonte_eventos");
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
            insertGeneric(getEvento("1", "MiMonte", "Microservicios-L2","ERROR","Usuario Monte","https://iamdr.montepiedad.com.mx//NMP/GestionClientes/Cliente/v2/usuarioMonte:4444"), restHighLevelClient, "1");
            insertGeneric(getEvento("2","MiMonte", "Microservicios-L2" ,"INFO","Usuario Monte","https://iamdr.montepiedad.com.mx//NMP/GestionClientes/Cliente/v2/usuarioMonte:4444"), restHighLevelClient, "2");
    }catch(final Exception e) {
        throw new RuntimeException("EMbeddedElastic, can not initialize");
    }

        eventService.setRestHighLevelClient(restHighLevelClient);
    }
    @AfterEach
    public void stopEmbeddedElastic(){
        embeddedElastic.stop();
        embeddedElastic = null;
    }
    private Evento getEvento(String id, String applicationName, String resolutionTower, String level, String phase, String resource){
        Evento eventoTest = new Evento();
        eventoTest.setIdEvent(id);
        eventoTest.setApplicationName(applicationName);
        eventoTest.setResolutionTower(resolutionTower);
        eventoTest.setTimeGenerated(new Date());
        eventoTest.setEventLevel(level);
        eventoTest.setEventPhase(phase);
        eventoTest.setEventResource(resource);
        return eventoTest;
    }

    @Test
    void getDashboard() throws IOException {
       // CountRequest countRequest = new CountRequest();
        //Mockito.when(restHighLevelClient.count(countRequest, RequestOptions.DEFAULT)).thenReturn(mock(CountResponse.class));
        //Mockito.when(restHighLevelClient.count(countRequest, RequestOptions.DEFAULT)).thenReturn(mock(CountResponse.class));
       // Mockito.when(countResponse.getCount()).thenReturn(3L);
        Assert.notNull( eventService.getDashboard());
    }

    @Test
    void getSecondLevel() throws IOException {
        eventService.getSecondLevel("Registro");
    }

    @Test
    void getThirdLevel() {
        eventService.getThirdLevel("Autenticar");
    }

    private static <T> Map<String, Object> transformerObject(T clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(clazz, Map.class);
    }
     private static <T> void insertGeneric(T clazz, RestHighLevelClient client, String id) throws IOException{
        IndexRequest indexRequest = new IndexRequest("smnr_mimonte_eventos", "doc", id).source(transformerObject(clazz));
       // LOGGER.info("prueba {​​​​​​​​}​​​​​​​​", client.index(indexRequest, RequestOptions.DEFAULT));
    }
}