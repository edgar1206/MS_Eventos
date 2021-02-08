package mx.com.nmp.eventos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mx.com.nmp.eventos.model.constant.*;
import mx.com.nmp.eventos.model.nr.Evento;
import mx.com.nmp.eventos.model.response.DashBoard;
import mx.com.nmp.eventos.model.response.SecondLevel;
import mx.com.nmp.eventos.model.response.Table;
import mx.com.nmp.eventos.repository.RepositoryLog;
import mx.com.nmp.eventos.utils.ElasticQuery;
import mx.com.nmp.eventos.utils.Validator;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
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

    public void setRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    //// Dashboard

    public List<DashBoard> getDashboard(){
        List<DashBoard> boards = new ArrayList<>();
        boards.addAll(getEventAction());
        boards.addAll(getEventLevel());
        boards.addAll(getEventWeek());
        boards.addAll(getEventMonth());
        boards.addAll(getEventYear());
        return boards;
    }

    private List<DashBoard> getEventAction(){
        List<DashBoard> boards = new ArrayList<>();
        DashBoard eventAction = new DashBoard();
        Long[][] data = new Long[1][Accion.name.length];
        List<String> labels = new ArrayList<>();
        for(int i = 0; i < Accion.name.length; i ++){
            data[0][i]=countEventActionLastDay(Key.eventAction,Accion.name[i],labels);
        }
        eventAction.setData(data);
        eventAction.setLabels(labels);
        eventAction.setKey(Key.eventAction);
        long total = 0;
        for (int i = 0; i < Accion.name.length ; i++){
            total += data[0][i];
        }
        eventAction.setTotal(total);
        boards.add(eventAction);
        return boards;
    }

    private List<DashBoard> getEventLevel(){
        List<DashBoard> boards = new ArrayList<>();
        DashBoard eventLevel = new DashBoard();
        List<String> labels = new ArrayList<>();
        Long[][] data = new Long[1][5];
        data[0][0]=countEventActionLastDay(Key.eventLevel, Nivel.info,labels);
        data[0][1]=countEventActionLastDay(Key.eventLevel, Nivel.error,labels);
        data[0][2]=countEventActionLastDay(Key.eventLevel, Nivel.debug,labels);
        data[0][3]=countEventActionLastDay(Key.eventLevel, Nivel.fatal,labels);
        data[0][4]=countEventActionLastDay(Key.eventLevel, Nivel.trace,labels);
        eventLevel.setData(data);
        eventLevel.setLabels(labels);
        eventLevel.setKey(Key.eventLevel);
        long total = 0;
        for (int i = 0; i < 5 ; i++){
            total += data[0][i];
        }
        eventLevel.setTotal(total);
        boards.add(eventLevel);
        return boards;
    }

    private List<DashBoard> getEventWeek(){
        List<DashBoard> boards = new ArrayList<>();
        DashBoard eventWeek = new DashBoard();
        List<String> labels = new ArrayList<>();
        Long[][] data = new Long[1][7];
        for(int i = 0; i < 7 ; i ++){
            data[0][i]=countByDay(String.valueOf(i),labels);
        }
        eventWeek.setData(data);
        eventWeek.setLabels(labels);
        eventWeek.setKey(Key.eventWeek);
        long total = 0;
        for (int i = 0; i < 7 ; i++){
            total += data[0][i];
        }
        eventWeek.setTotal(total);
        boards.add(eventWeek);
        return boards;
    }

    private List<DashBoard> getEventMonth(){
        List<DashBoard> boards = new ArrayList<>();
        DashBoard eventMonth = new DashBoard();
        List<String> labels = new ArrayList<>();
        Long[][] data = new Long[1][4];
        for(int i = 0; i < 4 ; i ++){
            data[0][i]=countByWeek(String.valueOf(i),labels);
        }
        eventMonth.setData(data);
        eventMonth.setLabels(labels);
        eventMonth.setKey(Key.eventMonth);
        long total = 0;
        for (int i = 0; i < 4 ; i++){
            total += data[0][i];
        }
        eventMonth.setTotal(total);
        boards.add(eventMonth);
        return boards;
    }

    private List<DashBoard> getEventYear(){
        List<DashBoard> boards = new ArrayList<>();
        DashBoard eventYear = new DashBoard();
        List<String> events = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        Long[][] data = new Long[Accion.name.length][12];
        long total = 0;
        for (int i = 0; i < Accion.name.length; i ++){
            for(int j = 0; j < 12 ; j ++){
                data[i][j] = countActionByMonth(Accion.name[i],j);
                total += data[i][j];
            }
            events.add(Accion.name[i]);
        }
        for(int m = 0; m < 12 ; m ++){
           setMonth(labels,m);
        }
        eventYear.setLabels(labels);
        eventYear.setEvents(events);
        eventYear.setData(data);
        eventYear.setTotal(total);
        eventYear.setKey(Key.eventYear);
        boards.add(eventYear);
        return boards;
    }

    ///  Segundo Nivel

    public SecondLevel getSecondLevel(String action){
        SecondLevel secondLevel = new SecondLevel();
        secondLevel.setTable(getTable(action));
        secondLevel.setChart(getChart(action));
        return secondLevel;
    }

    private List<Table> getTable(String action){
        List<Table> lista = new ArrayList<>();
        int posAction = 0;
        String[] phase = new String[0]; int tam = 0;
        for(int i = 0; i < Accion.name.length; i++){
            if(Accion.name[i].equalsIgnoreCase(action)){
                phase = Accion.fases[i];
                tam = phase.length;
                posAction = i;
                break;
            }
        }

        for(int i = 0; i < tam; i++){
            for(int j=0; j < Accion.recurso[posAction][i].length; j++){
                Table table = new Table();
                table.setFase(phase[i]);
                table.setRecurso(Accion.recursos[Accion.recurso[posAction][i][j]]);
                getPhaseByAction(action, phase[i], Accion.recursos[Accion.recurso[posAction][i][j]], table);
                lista.add(table);
            }
        }
        long total = lista.stream().mapToLong(table -> table.getInfo() + table.getDebug() + table.getError()).sum();
        return lista;
    }

    private DashBoard getChart(String action){
        DashBoard chart = new DashBoard();
        Long[][] data = new Long[5][7];
        List<String> events = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        long total = 0;
        for (int i = 0; i < 5; i ++){
            events.add(Nivel.name[i]);
            for(int j = 0; j < 7 ; j ++){
                data[i][j] = countByActionLevelDay(j,action,Nivel.name[i]);
                total += data[i][j];
            }
        }
        for(int d = 0; d < 7 ; d ++){
            labels.add(getDayOfWeek(String.valueOf(d)));
        }
        chart.setData(data);
        chart.setLabels(labels);
        chart.setEvents(events);
        chart.setTotal(total);
        return chart;
    }

    ///  Tercer Nivel

    public List<DashBoard> getThirdLevel(String fase){
        String action = "";
        if(fase.contains(",")){
            action = Validator.getAccion(fase);
            fase = Validator.getFase(fase);
        }else{
            for (int i = 0; i < Accion.name.length; i++){
                for(int j = 0; j < Accion.fases[i].length; j ++){
                    if (fase.equalsIgnoreCase(Accion.fases[i][j])) {
                        action = Accion.name[i];
                        fase = Accion.fases[i][j];
                        break;
                    }
                }
            }
        }
        List<DashBoard> boards = new ArrayList<>();
        for(int i =0; i < Nivel.name.length; i++ ){
            boards.addAll(getByLevel(action, fase, Nivel.name[i]));
        }
        boards.addAll(getPhaseEventWeek(action, fase));
        return boards;
    }

    private List<DashBoard> getByLevel(String accion ,String fase, String lvl){
        List<DashBoard> boards = new ArrayList<>();
        DashBoard level = new DashBoard();
        List<String> labels = new ArrayList<>();
        Long[][] data = new Long[1][7];
        long total = 0;
        for(int i=0; i < 7; i++){
            data[0][i] = countByNameDay(String.valueOf(i), labels, lvl, accion, fase);
            total += data[0][i];
        }
        level.setKey(lvl);
        level.setTotal(total);
        level.setData(data);
        level.setLabels(labels);
        boards.add(level);
        return boards;
    }

    private List<DashBoard> getPhaseEventWeek(String action,String phase){
        List<DashBoard> boards = new ArrayList<>();
        DashBoard eventWeek = new DashBoard();
        List<String> events = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        Long[][] data = new Long[5][7];
        long total = 0;
        for (int i = 0; i < 5; i ++){
            events.add(Nivel.name[i]);
            for(int j = 0; j < 7 ; j ++){
                data[i][j] = countByDayPhaseLevel(j,Nivel.name[i],action,phase);
                total += data[i][j];
            }
        }
        for(int d = 0; d < 7 ; d ++){
            labels.add(getDayOfWeek(String.valueOf(d)));
        }
        eventWeek.setLabels(labels);
        eventWeek.setEvents(events);
        eventWeek.setData(data);
        eventWeek.setTotal(total);
        eventWeek.setKey(Key.eventWeek);
        boards.add(eventWeek);
        return boards;
    }

//////////-----

    @Async
    private void getPhaseByAction(String action, String phase, String resource, Table table){
        List<Evento> eventos = new ArrayList<>();
        try {
            final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
            SearchRequest searchRequest = ElasticQuery.getByActionWeekResource(action,phase,resource,constants.getINDICE(),constants.getTIME_ZONE());
            searchRequest.scroll(scroll);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            String scrollId = searchResponse.getScrollId();
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            addLog(searchHits, eventos);
            while (searchHits != null && searchHits.length > 1) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(scroll);
                searchResponse = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
                scrollId = searchResponse.getScrollId();
                searchHits = searchResponse.getHits().getHits();
                addLog(searchHits, eventos);
            }

        } catch (ElasticsearchStatusException | ActionRequestValidationException | IOException ess) {
            LOGGER.info("Error: " + ess.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ess.getMessage());
        }
        final Map<String, Map<String, Long>> result = eventos.stream()
                .collect(Collectors.groupingBy(Evento::getEventPhase,
                                Collectors.groupingBy(Evento::getEventLevel,
                                        Collectors.counting())));

        table.setFase(phase);
        result.forEach((fase, mapLevel) -> {
            mapLevel.forEach((level, count) -> {
                level = level.toUpperCase();
                switch (level){
                    case Nivel.INFO:
                        table.setInfo(count);
                        break;
                    case Nivel.ERROR:
                        table.setError(count);
                        break;
                    case Nivel.DEBUG:
                        table.setDebug(count);
                        break;
                    case Nivel.TRACE:
                        table.setTrace(count);
                        break;
                    case Nivel.FATAL:
                        table.setFatal(count);
                        break;
                }
            });
        });
    }

    @Async
    private long countByActionLevelDay(int dia, String action, String level){
        try {
            CountRequest countRequest = ElasticQuery.getByActionLevelDay(dia , action, level, constants.getINDICE(),constants.getTIME_ZONE());
            CountResponse countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return countResponse.getCount();
        } catch (IOException e) {
            LOGGER.info("Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Async
    private long countByDayPhaseLevel(int dia, String level, String action ,String phase){
        try {
            CountRequest countRequest = ElasticQuery.getByNameDay(String.valueOf(dia) , level, action, phase , constants.getINDICE(),constants.getTIME_ZONE());
            CountResponse countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return countResponse.getCount();
        } catch (IOException e) {
            LOGGER.info("Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Async
    public long countByNameDay(String dia, List<String> labels, String level, String action, String phase){
        labels.add(getNameDayOfWeek(dia));
        try {
            CountRequest countRequest = ElasticQuery.getByNameDay(dia,level,action,phase,constants.getINDICE(),constants.getTIME_ZONE());
            CountResponse countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return countResponse.getCount();
        } catch (IOException e) {
            LOGGER.info("Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Async
    public long countByDay(String dia, List<String> labels){
        labels.add(getDayOfWeek(dia));
        try {
            CountRequest countRequest = ElasticQuery.getByDay(dia,constants.getINDICE(),constants.getTIME_ZONE());
            CountResponse countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return countResponse.getCount();
        } catch (IOException e) {
            LOGGER.info("Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Async
    public long countByWeek(String week, List<String> labels){
        labels.add(getWeekOfMonth(week));
        try {
            CountRequest countRequest = ElasticQuery.getByWeek(week,constants.getINDICE(),constants.getTIME_ZONE());
            CountResponse countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return countResponse.getCount();
        } catch (IOException e) {
            LOGGER.info("Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Async
    private long countEventActionLastDay(String field, String value, List<String> evento){
        evento.add(value);
        try {
            CountRequest countRequest = ElasticQuery.getActionLevelLastDay(field ,value ,constants.getINDICE(),constants.getTIME_ZONE());
            CountResponse countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return countResponse.getCount();
        } catch (IOException e) {
            LOGGER.info("Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Async
    private long countActionByMonth(String accion, int mes){
        try {
            CountRequest countRequest = ElasticQuery.getByMonth(accion , mes , constants.getINDICE(),constants.getTIME_ZONE());
            CountResponse countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return countResponse.getCount();
        } catch (IOException e) {
            LOGGER.info("Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    //---------

    public void addEvent(Evento evento) {
        try{
            repositoryLog.save(evento);
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

}
