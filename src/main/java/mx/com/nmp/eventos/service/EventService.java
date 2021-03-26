package mx.com.nmp.eventos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mx.com.nmp.eventos.model.constant.*;
import mx.com.nmp.eventos.model.nr.Evento;
import mx.com.nmp.eventos.model.response.*;
import mx.com.nmp.eventos.repository.RepositoryLog;
import mx.com.nmp.eventos.utils.ElasticQuery;
import mx.com.nmp.eventos.utils.Validator;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class EventService {

    private static final Logger LOGGER = Logger.getLogger(EventService.class.getName());

    @Autowired
    private RepositoryLog repositoryLog;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private Constants constants;

    public EventService() {
    }

    public void setRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    //// Dashboard

    public List<DashBoard> getDashboard(String appName){
        //getFaseAction(appName);
        if(AccionFaseApp.app.get(appName) == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre de app no valido o no se encontraron acciones.");
        }
        List<DashBoard> boards = new ArrayList<>();
        getEventActionLevel(appName, boards);
        boards.add(getEventWeek(appName));
        boards.add(getEventMonth(appName));
        boards.add(getEventYear(appName));
        boards.add(getEventDayActionLevel(appName));
        return boards;
    }

    @Async
    private void getEventActionLevel(String appName, List<DashBoard> boards){
        countEventActionLevelLastDay(boards, appName);
    }

    @Async
    private DashBoard getEventWeek(String appName){
        DashBoard eventWeek = new DashBoard();
        List<String> labels = new ArrayList<>();
        Long[] data = new Long[7];
        for(int i = 0; i < 7 ; i ++){
            data[i]=countByDay(String.valueOf(i),labels, appName);
        }
        eventWeek.setData(data);
        eventWeek.setLabels(labels);
        eventWeek.setKey(Key.eventWeek);
        long total = 0;
        for (int i = 0; i < 7 ; i++){
            total += data[i];
        }
        eventWeek.setTotal(total);
        return eventWeek;
    }

    @Async
    private DashBoard getEventMonth(String appName){
        DashBoard eventMonth = new DashBoard();
        List<String> labels = new ArrayList<>();
        Long[] data = new Long[4];
        for(int i = 0; i < 4 ; i ++){
            data[i]=countByWeek(String.valueOf(i),labels, appName);
        }
        eventMonth.setData(data);
        eventMonth.setLabels(labels);
        eventMonth.setKey(Key.eventMonth);
        long total = 0;
        for (int i = 0; i < 4 ; i++){
            total += data[i];
        }
        eventMonth.setTotal(total);
        return eventMonth;
    }

    @Async
    private DashBoard getEventYear(String appName){
        DashBoard eventYear = new DashBoard();
        List<String> events = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        Acciones acciones = AccionFaseApp.app.get(appName);
        int actionSize = acciones.getAcciones().size();
        for (Accion accion : acciones.getAcciones()) {
            events.add(accion.getNombre());
        }
        Long[][] data = new Long[actionSize][3];
        for (Long[] datum : data) {
            Arrays.fill(datum, 0L);
        }
        for(int j = 0; j < 3 ; j ++){
            countActionByMonth(data, events, j, appName);
        }
        for(int m = 0; m < 3 ; m ++){
           setMonth(labels,m);
        }
        long total = 0;
        for (Long[] datum : data) {
            for (Long aLong : datum) {
                total += aLong;
            }
        }
        eventYear.setLabels(labels);
        eventYear.setEvents(events);
        eventYear.setData(data);
        eventYear.setTotal(total);
        eventYear.setKey(Key.eventYear);
        return eventYear;
    }

    @Async
    private DashBoard getEventDayActionLevel(String appName){
        DashBoard eventDayActionLevel = new DashBoard();
        List<String> labels = new ArrayList<>();
        Acciones acciones = AccionFaseApp.app.get(appName);
        int actionLength = acciones.getAcciones().size();
        Long[][] data = new Long[Nivel.name.length][actionLength];
        for (Long[] datum : data) {
            Arrays.fill(datum, 0L);
        }
        List<String> levels = new ArrayList<>(Arrays.asList(Nivel.name));
        countByLevelActionDay(data, appName);
        for(int m = 0; m < actionLength ; m ++){
            labels.add(acciones.getAcciones().get(m).getNombre());
        }
        long total = 0;
        for (Long[] datum : data) {
            for (Long aLong : datum) {
                total += aLong;
            }
        }
        eventDayActionLevel.setLabels(labels);
        eventDayActionLevel.setLevels(levels);
        eventDayActionLevel.setData(data);
        eventDayActionLevel.setTotal(total);
        eventDayActionLevel.setKey(Key.eventActionLastDay);
        return eventDayActionLevel;
    }

    ///  Segundo Nivel

    public SecondLevel getSecondLevel(String action, String appName){
        SecondLevel secondLevel = new SecondLevel();
        secondLevel.setTable(getTable(action,appName));
        secondLevel.setChart(getChart(action, appName));
        return secondLevel;
    }

    private List<Map<String, String>> getTable(String action, String appName){
        List<Map<String, String>> mapList = new ArrayList<>();
        Accion accion = new Accion();
        Acciones acciones = AccionFaseApp.app.get(appName);
        for (Accion nodoAccion : acciones.getAcciones()) {
            if(nodoAccion.getNombre().equals(action))
                accion = nodoAccion;
        }
        accion.getFases().stream().parallel().forEach(fase -> {
            Map<String,String> map = new HashMap<>();
            map.put("fase",fase.getNombre());
            for (String nivel : Nivel.name) {
                map.put(nivel,"0");
            }
            getPhaseByAction(action,appName,map);
            mapList.add(map);
        });
        return mapList;
    }

    private DashBoard getChart(String action, String appName){
        DashBoard chart = new DashBoard();
        Long[][] data = new Long[Nivel.name.length][7];
        for (Long[] datum : data) {
            Arrays.fill(datum, 0L);
        }
        List<String> events = new ArrayList<>(Arrays.asList(Nivel.name));
        List<String> labels = new ArrayList<>();
        for(int i = 0; i < 7 ; i ++){
            countByDaySecondLevel(action, data, labels, events, i,appName);
        }
        long total = 0;
        for (Long[] datum : data) {
            for (Long aLong : datum) {
                total += aLong;
            }
        }
        chart.setData(data);
        chart.setLabels(labels);
        chart.setEvents(events);
        chart.setTotal(total);
        return chart;
    }

    ///  Tercer Nivel

    public List<DashBoard> getThirdLevel(String accion, String fase, String appName){
        List<DashBoard> boards = new ArrayList<>();
        getByLevel(accion, fase, appName, boards);
        return boards;
    }

    private void getByLevel(String accion ,String fase, String appName, List<DashBoard> boards){
        List<String> niveles = new ArrayList<>(Arrays.asList(Nivel.name));
        DashBoard eventWeek = new DashBoard();
        Long[][] dataWeek = new Long[niveles.size()][7];
        for (Long[] longs : dataWeek) {
            Arrays.fill(longs, 0L);
        }
        List<String> labels = new ArrayList<>();
        List<SearchResponse> searchResponses = new ArrayList<>();
        for (int i = 0; i < 7; i++)
            searchResponses.add(countByNameDay(String.valueOf(i), labels, accion, fase, appName));
        int posLevel = 0;
        for (String nivel : niveles) {
            DashBoard eventLevel = new DashBoard();
            long total = 0L;
            eventLevel.setKey(nivel);
            Long[] data = new Long[7];
            Arrays.fill(data, 0L);
            int dia = 0;
            for (SearchResponse response : searchResponses) {
                Map<String, Aggregation> results = response.getAggregations().getAsMap();
                ParsedStringTerms levels = (ParsedStringTerms) results.get("level");
                for (Terms.Bucket level : levels.getBuckets()){
                    if(nivel.equalsIgnoreCase(level.getKeyAsString().trim())){
                        data[dia] += level.getDocCount();
                        dataWeek[posLevel][dia] += level.getDocCount();
                    }
                }
                dia ++;
            }
            for (Long datum : data) {
                total += datum;
            }
            eventLevel.setTotal(total);
            eventLevel.setLabels(labels);
            eventLevel.setData(data);
            boards.add(eventLevel);
            posLevel++;
        }
        long total = 0L;
        for (Long[] longs : dataWeek) {
            for (Long aLong : longs) {
                total += aLong;
            }
        }
        eventWeek.setTotal(total);
        eventWeek.setLabels(labels);
        eventWeek.setData(dataWeek);
        eventWeek.setEvents(niveles);
        eventWeek.setKey(Key.eventWeek);
        boards.add(eventWeek);
    }

    //  Cuarto Nivel

    public List<Evento> getFourthLevel(String accion, String fase, String nivel, String desde, String hasta, String appName){
        List<Evento> eventos = new ArrayList<>();
        List<String> horas = new ArrayList<>();
        horas.add("00");
        horas.add("02");
        horas.add("04");
        horas.add("06");
        horas.add("08");
        horas.add("10");
        horas.add("12");
        horas.add("14");
        horas.add("16");
        horas.add("18");
        horas.add("20");
        horas.add("22");
        horas.stream().parallel().forEach(hora -> {
            String horaFin = String.format("%02d",Integer.parseInt(hora) + 1);
            eventos.addAll(getEventsByDate(accion, fase, nivel, desde, hasta, hora, horaFin, appName));
        });
        return eventos;
    }

//////////-----

    private List<Evento> getEventsByDate(String accion, String fase, String nivel, String desde, String hasta, String inicio, String fin, String appName){
        List<Evento> eventos = new ArrayList<>();
        try {
            final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
            SearchRequest searchRequest = ElasticQuery.getByActionLevelDayLogs(accion, fase, nivel, constants.getINDICE(),constants.getTIME_ZONE(),desde, hasta, inicio, fin, appName);
            searchRequest.scroll(scroll);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            addLog(searchHits, eventos);
        } catch (ElasticsearchStatusException | ActionRequestValidationException | IOException ess) {
            LOGGER.info("Error: " + ess.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ess.getMessage());
        }
        return eventos;
    }

    @Async
    private void getPhaseByAction(String action,String appName, Map<String,String> map){
        SearchRequest countRequest = ElasticQuery.getByActionWeek(action,constants.getINDICE(),constants.getTIME_ZONE(), appName);
        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(countRequest, RequestOptions.DEFAULT);
        } catch (ElasticsearchStatusException | ActionRequestValidationException | IOException ess) {
            LOGGER.info("Error: " + ess.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ess.getMessage());
        }
        Map<String, Aggregation> results = response.getAggregations().getAsMap();
        ParsedStringTerms phases = (ParsedStringTerms) results.get("phase");
        for (Terms.Bucket phase : phases.getBuckets()) {
            if(map.get("fase").equalsIgnoreCase(phase.getKeyAsString().trim())){
                ParsedStringTerms levels = (ParsedStringTerms) phase.getAggregations().getAsMap().get("level");
                for (Terms.Bucket level : levels.getBuckets()) {
                    Arrays.stream(Nivel.name).forEach( nivel -> {
                        if(level.getKeyAsString().trim().equalsIgnoreCase(nivel)){
                            map.put(nivel,String.valueOf(level.getDocCount()));
                        }
                    });
                }
            }
        }
    }

    @Async
    public SearchResponse countByNameDay(String dia, List<String> labels, String action, String phase, String appName){
        SearchResponse response = null;
        labels.add(getNameDayOfWeek(dia));
        try {
            SearchRequest searchRequest = ElasticQuery.getByNameDay(dia,action,phase,constants.getINDICE(),constants.getTIME_ZONE(), appName);
            return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (ElasticsearchStatusException | ActionRequestValidationException | IOException ess) {
            LOGGER.info("Error: " + ess.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ess.getMessage());
        }
    }

    @Async
    public long countByDay(String dia, List<String> labels, String appName){
        labels.add(getDayOfWeek(dia));
        try {
            CountRequest countRequest = ElasticQuery.getByDay(dia,constants.getINDICE(),constants.getTIME_ZONE(), appName);
            CountResponse countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return countResponse.getCount();
        } catch (IOException e) {
            LOGGER.info("Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Async
    public void countByDaySecondLevel(String action, Long[][] data, List<String> labels, List<String> events, int dia, String appName){
        labels.add(getDayOfWeek(String.valueOf(dia)));
        SearchResponse response = null;
        try {
            SearchRequest searchRequest = ElasticQuery.getByDaySecondLevel(action,dia,constants.getINDICE(),constants.getTIME_ZONE(), appName);
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (ElasticsearchStatusException | ActionRequestValidationException | IOException ess) {
            LOGGER.info("Error: " + ess.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ess.getMessage());
        }
        Map<String, Aggregation> results = response.getAggregations().getAsMap();
        ParsedStringTerms levels = (ParsedStringTerms) results.get("level");
        int posLevel = 0;
        for (Terms.Bucket level : levels.getBuckets()){
            for (String nivel : events) {
                if(nivel.equalsIgnoreCase(level.getKeyAsString().trim())){
                    data[posLevel][dia] = level.getDocCount();
                    posLevel ++;
                    if(posLevel == events.size()) posLevel = 0;
                }
            }
        }
    }

    @Async
    public long countByWeek(String week, List<String> labels, String appName){
        labels.add(getWeekOfMonth(week));
        try {
            CountRequest countRequest = ElasticQuery.getByWeek(week,constants.getINDICE(),constants.getTIME_ZONE(), appName);
            CountResponse countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return countResponse.getCount();
        } catch (IOException e) {
            LOGGER.info("Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Async
    public void countByLevelActionDay(Long[][] data, String appName){
        SearchRequest searchRequest = null;
        SearchResponse searchResponse = null;
        Acciones acciones = AccionFaseApp.app.get(appName);
        int actionLength = acciones.getAcciones().size();
        try {
            searchRequest = ElasticQuery.getActionLevelLastDay(constants.getINDICE(),constants.getTIME_ZONE(), appName);
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.info("Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        DashBoard eventAction = new DashBoard();

        List<String> labelsAction = new ArrayList<>();
        List<String> labelsLevel = new ArrayList<>(Arrays.asList(Nivel.name));
        Long[] dataAction = new Long[actionLength];
        Arrays.fill(dataAction, 0L);

        Map<String, Aggregation> results = searchResponse.getAggregations().getAsMap();
        ParsedStringTerms actions = (ParsedStringTerms) results.get("action");
        for (Terms.Bucket action : actions.getBuckets()) {
            int pos = 0;
            for (Accion accion : acciones.getAcciones()) {
                if(action.getKeyAsString().trim().equals(accion.getNombre())){
                    labelsAction.add(accion.getNombre());
                    ParsedStringTerms levels = (ParsedStringTerms) action.getAggregations().getAsMap().get("level");
                    for (Terms.Bucket level : levels.getBuckets()) {
                        int posLevel = 0;
                        for (String nivel : labelsLevel) {
                            if(nivel.equalsIgnoreCase(level.getKeyAsString().trim())){
                                data[posLevel][pos] += level.getDocCount();
                            }
                            posLevel ++;
                            if(posLevel == data.length) posLevel = 0;
                        }
                    }
                }
                pos ++;
            }
        }
        eventAction.setData(dataAction);
        eventAction.setLabels(labelsAction);
        long totalAction = 0;
        for (Long aLong : dataAction) {
            totalAction += aLong;
        }
        eventAction.setTotal(totalAction);
        eventAction.setKey(Key.eventAction);
    }

    @Async
    private void countEventActionLevelLastDay(List<DashBoard> boards, String appName){
        SearchRequest searchRequest = null;
        SearchResponse searchResponse = null;
        Acciones acciones = AccionFaseApp.app.get(appName);
        int actionLength = acciones.getAcciones().size();
        try {
            searchRequest = ElasticQuery.getActionLevelLastDay(constants.getINDICE(),constants.getTIME_ZONE(), appName);
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.info("Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        DashBoard eventAction = new DashBoard();
        DashBoard eventActionLevel = new DashBoard();

        List<String> labelsAction = new ArrayList<>();
        for (Accion accione : acciones.getAcciones()) {
            labelsAction.add(accione.getNombre());
        }
        List<String> labelsLevel = new ArrayList<>(Arrays.asList(Nivel.name));
        Long[] dataAction = new Long[actionLength];
        Arrays.fill(dataAction, 0L);
        Long[] dataLevel = new Long[Nivel.name.length];
        Arrays.fill(dataLevel, 0L);

        Map<String, Aggregation> results = searchResponse.getAggregations().getAsMap();
        ParsedStringTerms actions = (ParsedStringTerms) results.get("action");
        int pos = 0;
        for (Terms.Bucket action : actions.getBuckets()) {
            for (String accion : labelsAction) {
                if(action.getKeyAsString().trim().equals(accion)){
                    dataAction[pos] = action.getDocCount();
                    pos ++;

                    ParsedStringTerms levels = (ParsedStringTerms) action.getAggregations().getAsMap().get("level");
                    int posLevel = 0;
                    for (Terms.Bucket level : levels.getBuckets()) {
                        for (String nivel : labelsLevel) {
                            if(nivel.equalsIgnoreCase(level.getKeyAsString().trim())){
                                dataLevel[posLevel] += level.getDocCount();
                            }
                        }
                        posLevel ++;
                        if(posLevel == dataLevel.length) posLevel = 0;
                    }
                }
            }
        }
        eventAction.setData(dataAction);
        eventAction.setLabels(labelsAction);
        long totalAction = 0;
        for (Long aLong : dataAction) {
            totalAction += aLong;
        }
        eventAction.setTotal(totalAction);
        eventAction.setKey(Key.eventAction);
        boards.add(eventAction);

        long totalLevel = 0;
        for (Long aLong : dataLevel) {
            totalLevel += aLong;
        }
        eventActionLevel.setTotal(totalLevel);
        eventActionLevel.setLabels(labelsLevel);
        eventActionLevel.setData(dataLevel);
        eventActionLevel.setKey(Key.eventLevel);
        boards.add(eventActionLevel);
    }

    @Async
    private void countActionByMonth(Long[][] data, List<String> labels, int mes, String appName){
        SearchResponse searchResponse = null;
        try {
            SearchRequest searchRequest = ElasticQuery.getByMonth(mes, constants.getINDICE(), constants.getTIME_ZONE(), appName);
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.info("Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        Map<String, Aggregation> results = searchResponse.getAggregations().getAsMap();
        ParsedStringTerms actions = (ParsedStringTerms) results.get("action");
        int posAction = 0;
        for (String accion : labels) {
            for (Terms.Bucket action : actions.getBuckets()) {
                if(accion.equals(action.getKeyAsString().trim())){
                    data[posAction][mes] = action.getDocCount();
                }
            }
            posAction ++;
        }
    }

    //---------

    public void addEvent(Evento evento) {
        try{
            this.repositoryLog.save(evento);
        }catch (Exception e){
            LOGGER.info( "Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private void addLog(SearchHit[] results, List<Evento> eventos) throws JsonProcessingException {
        if (results != null && results.length > 0) {
            for(SearchHit result:results){
                Evento evento = new ObjectMapper().readValue(result.getSourceAsString(), Evento.class);
                eventos.add(evento);
            }
        }
    }

    private String getDayOfWeek(String dia){
        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime zt = now.atZone(ZoneId.of(ElasticQuery.getUtc(constants.getTIME_ZONE())));
        LocalDate date = zt.toLocalDate();
        date = date.minusDays(Integer.parseInt(dia));
        DayOfWeek day = date.getDayOfWeek();
        return Dia.DayOfWeek[day.getValue() - 1];
    }

    private String getNameDayOfWeek(String dia){
        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime zt = now.atZone(ZoneId.of(ElasticQuery.getUtc(constants.getTIME_ZONE())));
        LocalDate date = zt.toLocalDate();
        date = date.minusDays(Integer.parseInt(dia));
        DayOfWeek day = date.getDayOfWeek();
        return Dia.name[day.getValue() - 1];
    }

    private String getWeekOfMonth(String week){
        return Mes.weekOfMonth[Integer.parseInt(week)];
    }

    public void setMonth(List<String> labels, int m){
        Calendar ca1 = Calendar.getInstance(TimeZone.getTimeZone(constants.getTIME_ZONE()));
        ca1.add(Calendar.MONTH,- m);
        ca1.setMinimalDaysInFirstWeek(1);
        int mes = ca1.get(Calendar.MONTH);
        labels.add(Mes.name[mes]);
    }

    public Acciones getFaseAction(String appName){
        SearchResponse response = null;
        SearchRequest searchRequest = ElasticQuery.groupbyActionandPhase(constants.getINDICE(), constants.getTIME_ZONE(), constants.getMONTH(), appName);
        try {
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.info("Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        Acciones accionesApp = new Acciones();
        Nivel nivelesApp = new Nivel();
        List<Accion> accionList = new ArrayList<>();
        List<String> niveles = new ArrayList<>();
        Map<String, Aggregation> results = response.getAggregations().getAsMap();
        ParsedStringTerms actions = (ParsedStringTerms) results.get("action");
        for (Terms.Bucket action : actions.getBuckets()) {
            Accion accion = new Accion();
            accion.setNombre(action.getKeyAsString().trim());//--
            List<Fase> fases = new ArrayList<>();
            ParsedStringTerms phases = (ParsedStringTerms) action.getAggregations().getAsMap().get("phase");
            for (Terms.Bucket phase : phases.getBuckets()) {
                Fase fase = new Fase();
                fase.setNombre(phase.getKeyAsString().trim());//--
                fases.add(fase);

                ParsedStringTerms levels = (ParsedStringTerms) phase.getAggregations().getAsMap().get("level");
                for (Terms.Bucket level : levels.getBuckets()) {
                    niveles.add(level.getKeyAsString().toUpperCase().trim());
                }
            }
            accion.setFases(fases);
            accionList.add(accion);
        }
        niveles = niveles.stream().distinct().collect(Collectors.toList());
        accionList = Validator.validateActionPhase(accionList);//
        accionesApp.setAcciones(accionList);
        nivelesApp.setLevels(niveles);

        AccionFaseApp.app.put(appName,accionesApp);
        AccionFaseApp.levels.put(appName,nivelesApp);

        return accionesApp;
    }

}
