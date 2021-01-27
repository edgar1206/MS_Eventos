package mx.com.nmp.eventos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mx.com.nmp.eventos.model.constant.*;
import mx.com.nmp.eventos.model.nr.Evento;
import mx.com.nmp.eventos.model.response.DashBoard;
import mx.com.nmp.eventos.repository.RepositoryLog;
import mx.com.nmp.eventos.utils.ElasticQuery;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

@Service
public class ServiceLogImplement{

    private static final Logger LOGGER = Logger.getLogger(ServiceLogImplement.class.getName());

    @Autowired
    private RepositoryLog repositoryLog;

    @Autowired
    private Constants constants;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

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
        Long[][] data = new Long[1][8];
        List<String> labels = new ArrayList<>();
        for(int i=0; i<8; i++){
            data[0][i]=countEventActionLastDay(Key.eventAction,Accion.name[i],labels);
        }
        eventAction.setData(data);
        eventAction.setLabels(labels);
        eventAction.setKey(Key.eventAction);
        long total = 0;
        for (int i = 0; i < 8 ; i++){
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
        for (int i = 0; i < 4 ; i++){
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
        Long[][] data = new Long[8][12];
        long total = 0;
        for (int i = 0; i < 8; i ++){
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

    /////

    ///  Segundo Nivel




    ///

    ///  Tercer Nivel

    public List<DashBoard> getThirdLevel(String fase){
        List<DashBoard> boards = new ArrayList<>();
        for(int i =0; i < Nivel.name.length; i++ ){
            boards.addAll(getByLevel(fase, Nivel.name[i]));
        }
        boards.addAll(getPhaseEventWeek(fase));
        return boards;
    }

    private List<DashBoard> getByLevel(String fase, String lvl){
        List<DashBoard> boards = new ArrayList<>();
        DashBoard level = new DashBoard();
        List<String> labels = new ArrayList<>();
        Long[][] data = new Long[1][7];
        long total = 0;
        for(int i=0; i < 7; i++){
            data[0][i] = countByNameDay(String.valueOf(i), labels, lvl, fase);
            total += data[0][i];
        }
        level.setKey(lvl);
        level.setTotal(total);
        level.setData(data);
        level.setLabels(labels);
        boards.add(level);
        return boards;
    }

    private List<DashBoard> getPhaseEventWeek(String phase){
        List<DashBoard> boards = new ArrayList<>();
        DashBoard eventWeek = new DashBoard();
        List<String> events = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        Long[][] data = new Long[5][7];
        long total = 0;
        for (int i = 0; i < 5; i ++){
            events.add(Nivel.name[i]);
            for(int j = 0; j < 7 ; j ++){
                data[i][j] = countByDayPhaseLevel(j,Nivel.name[i],phase);
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
    ///

    public List<Evento> getAllLogs() {
        List<Evento> eventos = new ArrayList<>();
        try {
            final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
            SearchRequest searchRequest = ElasticQuery.getLogs(constants.getINDICE());
            searchRequest.scroll(scroll);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            String scrollId = searchResponse.getScrollId();
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            addLog(searchHits, eventos);
/*
            while (searchHits != null && searchHits.length > 1) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(scroll);
                searchResponse = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
                scrollId = searchResponse.getScrollId();
                searchHits = searchResponse.getHits().getHits();
                addLog(searchHits, eventos);
            }*/
        } catch (ElasticsearchStatusException | ActionRequestValidationException | IOException ess) {
            LOGGER.info("Error: " + ess.getMessage());
        }

        /*List<String> estatus = new ArrayList<String>();
        logs.forEach(logIndice -> {
            estatus.add(logIndice.getLevel());
        });
        List<String> valoresUnicos = estatus
                .stream()
                .distinct()
                .collect(Collectors.toList());
        System.out.println(valoresUnicos);*/
        if(eventos.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se encontraron eventos");
        }
        return eventos;
    }

/*
    public EventYear getCuentaEventosPorMes(String year){
        EventMonth mes = new EventMonth();
        EventYear eventYear = new EventYear();
        mes.setEnero(countEventoMes(Mes.ENERO,year));
        mes.setFebrero(countEventoMes(Mes.FEBRERO,year));
        mes.setMarzo(countEventoMes(Mes.MARZO,year));
        mes.setAbril(countEventoMes(Mes.ABRIL,year));
        mes.setMayo(countEventoMes(Mes.MAYO,year));
        mes.setJunio(countEventoMes(Mes.JUNIO,year));
        mes.setJulio(countEventoMes(Mes.JULIO,year));
        mes.setAgosto(countEventoMes(Mes.AGOSTO,year));
        mes.setSeptiembre(countEventoMes(Mes.SEPTIEMBRE,year));
        mes.setOctubre(countEventoMes(Mes.OCTUBRE,year));
        mes.setNoviembre(countEventoMes(Mes.NOVIEMBRE,year));
        mes.setDiciembre(countEventoMes(Mes.DICIEMBRE,year));
        long total = mes.getEnero() + mes.getFebrero() + mes.getMarzo() + mes.getAbril() + mes.getMayo()
                + mes.getJunio() + mes.getJulio() + mes.getAgosto() + mes.getAgosto() + mes.getSeptiembre()
                + mes.getOctubre() + mes.getNoviembre() + mes.getDiciembre();
        eventYear.setAnio(year);
        eventYear.setMes(mes);
        eventYear.setTotalEventos(total);
        return eventYear;
    }
*/

//////////-----

    @Async
    private long countByDayPhaseLevel(int dia, String level, String phase){
        try {
            CountRequest countRequest = ElasticQuery.getByNameDay(String.valueOf(dia) , level, phase , constants.getINDICE());
            CountResponse countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return countResponse.getCount();
        } catch (IOException e) {
            LOGGER.info("Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Async
    public long countByNameDay(String dia, List<String> labels, String level, String phase){
        labels.add(getNameDayOfWeek(dia));
        try {
            CountRequest countRequest = ElasticQuery.getByNameDay(dia,level,phase,constants.getINDICE());
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
            CountRequest countRequest = ElasticQuery.getByDay(dia,constants.getINDICE());
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
            CountRequest countRequest = ElasticQuery.getByWeek(week,constants.getINDICE());
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
            CountRequest countRequest = ElasticQuery.getActionLevelLastDay(field ,value , constants.getINDICE());
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
            CountRequest countRequest = ElasticQuery.getByMonth(accion , mes , constants.getINDICE());
            CountResponse countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return countResponse.getCount();
        } catch (IOException e) {
            LOGGER.info("Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    //-*------

    public void saveLog(Evento evento) {
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
        ZonedDateTime zt = now.atZone(ZoneId.of(ElasticQuery.getUtc()));
        LocalDate date = zt.toLocalDate();
        date = date.minusDays(Integer.parseInt(dia));
        DayOfWeek day = date.getDayOfWeek();
        return Dia.DayOfWeek[day.getValue() - 1];
    }

    private String getNameDayOfWeek(String dia){
        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime zt = now.atZone(ZoneId.of(ElasticQuery.getUtc()));
        LocalDate date = zt.toLocalDate();
        date = date.minusDays(Integer.parseInt(dia));
        DayOfWeek day = date.getDayOfWeek();
        return Dia.name[day.getValue() - 1];
    }

    private String getWeekOfMonth(String week){
        return Mes.weekOfMonth[Integer.parseInt(week)];
    }

    public void setMonth(List<String> labels, int m){
        Calendar ca1 = Calendar.getInstance(TimeZone.getTimeZone("America/Mexico_City"));
        ca1.add(Calendar.MONTH,- m);
        ca1.setMinimalDaysInFirstWeek(1);
        int mes = ca1.get(Calendar.MONTH);
        labels.add(Mes.name[mes]);
    }

}
