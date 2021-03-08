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
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.*;
import java.util.*;
import java.util.logging.Logger;

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

    public Acciones getActionsPhases(){
        return AccionFase.accionFase;
    }

    @Bean
    public void loadActions(){
        AccionFase.accionFase = getFaseAction();
    }

    //// Dashboard

    public List<DashBoard> getDashboard(){
        AccionFase.accionFase = getFaseAction();
        List<DashBoard> boards = new ArrayList<>();
        boards.add(getEventAction());
        boards.add(getEventLevel());
        boards.add(getEventWeek());
        boards.add(getEventMonth());
        boards.add(getEventYear());
        boards.add(getEventDayActionLevel());
        return boards;
    }

    @Async
    private DashBoard getEventAction(){
        DashBoard eventAction = new DashBoard();
        int actionLength = AccionFase.accionFase.getAcciones().size();
        Long[] data = new Long[actionLength];
        List<String> labels = new ArrayList<>();
        for(int i = 0; i < actionLength; i ++){
            data[i]=countEventActionLastDay(Key.eventAction, AccionFase.accionFase.getAcciones().get(i).getNombre(),labels);
        }
        eventAction.setData(data);
        eventAction.setLabels(labels);
        eventAction.setKey(Key.eventAction);
        long total = 0;
        for (int i = 0; i < actionLength ; i++){
            total += data[i];
        }
        eventAction.setTotal(total);
        return eventAction;
    }

    @Async
    private DashBoard getEventLevel(){
        DashBoard eventLevel = new DashBoard();
        List<String> labels = new ArrayList<>();
        Long[] data = new Long[5];
        data[0]=countEventActionLastDay(Key.eventLevel, Nivel.info,labels);
        data[1]=countEventActionLastDay(Key.eventLevel, Nivel.error,labels);
        data[2]=countEventActionLastDay(Key.eventLevel, Nivel.debug,labels);
        data[3]=countEventActionLastDay(Key.eventLevel, Nivel.fatal,labels);
        data[4]=countEventActionLastDay(Key.eventLevel, Nivel.trace,labels);
        eventLevel.setData(data);
        eventLevel.setLabels(labels);
        eventLevel.setKey(Key.eventLevel);
        long total = 0;
        for (int i = 0; i < 5 ; i++){
            total += data[i];
        }
        eventLevel.setTotal(total);
        return eventLevel;
    }

    @Async
    private DashBoard getEventWeek(){
        DashBoard eventWeek = new DashBoard();
        List<String> labels = new ArrayList<>();
        Long[] data = new Long[7];
        for(int i = 0; i < 7 ; i ++){
            data[i]=countByDay(String.valueOf(i),labels);
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
    private DashBoard getEventMonth(){
        DashBoard eventMonth = new DashBoard();
        List<String> labels = new ArrayList<>();
        Long[] data = new Long[4];
        for(int i = 0; i < 4 ; i ++){
            data[i]=countByWeek(String.valueOf(i),labels);
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
    private DashBoard getEventYear(){
        DashBoard eventYear = new DashBoard();
        List<String> events = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int actionSize = AccionFase.accionFase.getAcciones().size();
        Long[][] data = new Long[actionSize][3];
        long total = 0;
        for (int i = 0; i < actionSize; i ++){
            for(int j = 0; j < 3 ; j ++){
                data[i][j] = countActionByMonth(AccionFase.accionFase.getAcciones().get(i).getNombre(),j);
                total += data[i][j];
            }
            events.add(AccionFase.accionFase.getAcciones().get(i).getNombre());
        }
        for(int m = 0; m < 3 ; m ++){
           setMonth(labels,m);
        }
        eventYear.setLabels(labels);
        eventYear.setEvents(events);
        eventYear.setData(data);
        eventYear.setTotal(total);
        eventYear.setKey(Key.eventYear);
        return eventYear;
    }

    @Async
    private DashBoard getEventDayActionLevel(){
        DashBoard eventDayActionLevel = new DashBoard();
        List<String> levels = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int actionLength = AccionFase.accionFase.getAcciones().size();
        Long[][] data = new Long[Nivel.name.length][actionLength];
        long total = 0;
        for (int i = 0; i < Nivel.name.length; i ++){
            for(int j = 0; j < actionLength ; j ++){
                data[i][j] = countByLevelActionDay(AccionFase.accionFase.getAcciones().get(i).getNombre(),Nivel.name[i]);
                total += data[i][j];
            }
            levels.add(Nivel.name[i]);
        }
        for(int m = 0; m < actionLength ; m ++){
            labels.add(AccionFase.accionFase.getAcciones().get(m).getNombre());
        }
        eventDayActionLevel.setLabels(labels);
        eventDayActionLevel.setLevels(levels);
        eventDayActionLevel.setData(data);
        eventDayActionLevel.setTotal(total);
        eventDayActionLevel.setKey(Key.eventActionLastDay);
        return eventDayActionLevel;
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
        Accion accion = new Accion();
        for (Accion nodoAccion : AccionFase.accionFase.getAcciones()) {
            if(nodoAccion.getNombre().equals(action))
                accion = nodoAccion;
        }
        accion.getFases().stream().parallel().forEach(fase -> {
                Table table = new Table();
                table.setFase(fase.getNombre());
                for (int i = 0; i < 7; i ++){
                    table.setInfo(getPhaseByAction(action,fase.getNombre(),Nivel.INFO,String.valueOf(i)));
                    table.setError(getPhaseByAction(action,fase.getNombre(),Nivel.ERROR,String.valueOf(i)));
                    table.setDebug(getPhaseByAction(action,fase.getNombre(),Nivel.DEBUG,String.valueOf(i)));
                    table.setTrace(getPhaseByAction(action,fase.getNombre(),Nivel.TRACE,String.valueOf(i)));
                    table.setFatal(getPhaseByAction(action,fase.getNombre(),Nivel.FATAL,String.valueOf(i)));
                }
                lista.add(table);
            });
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

    public List<DashBoard> getThirdLevel(String accion, String fase){
        List<DashBoard> boards = new ArrayList<>();
        for(int i =0; i < Nivel.name.length; i++ ){
            boards.add(getByLevel(accion, fase, Nivel.name[i]));
        }
        boards.add(getPhaseEventWeek(accion, fase));
        return boards;
    }

    private DashBoard getByLevel(String accion ,String fase, String lvl){
        DashBoard level = new DashBoard();
        List<String> labels = new ArrayList<>();
        Long[] data = new Long[7];
        long total = 0;
        for(int i=0; i < 7; i++){
            data[i] = countByNameDay(String.valueOf(i), labels, lvl, accion, fase);
            total += data[i];
        }
        level.setKey(lvl);
        level.setTotal(total);
        level.setData(data);
        level.setLabels(labels);
        return level;
    }

    private DashBoard getPhaseEventWeek(String action,String phase){
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
        return eventWeek;
    }

    //  Cuarto Nivel

    public List<Evento> getFourthLevel(String accion, String fase, String nivel, String desde, String hasta){
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
            eventos.addAll(getEventsByDate(accion, fase, nivel, desde, hasta, hora, horaFin));
        });
        return eventos;
    }

//////////-----

    private List<Evento> getEventsByDate(String accion, String fase, String nivel, String desde, String hasta, String inicio, String fin){
        List<Evento> eventos = new ArrayList<>();
        try {
            final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
            SearchRequest searchRequest = ElasticQuery.getByActionLevelDayLogs(accion, fase, nivel, constants.getINDICE(),constants.getTIME_ZONE(),desde, hasta, inicio, fin);
            searchRequest.scroll(scroll);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            String scrollId = searchResponse.getScrollId();
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            addLog(searchHits, eventos);
        } catch (ElasticsearchStatusException | ActionRequestValidationException | IOException ess) {
            LOGGER.info("Error: " + ess.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ess.getMessage());
        }
        return eventos;
    }

    @Async
    private long getPhaseByAction(String action,String phase,String level,String day){
        try {
            CountRequest countRequest = ElasticQuery.getByActionWeek(action,phase,level,day,constants.getINDICE(),constants.getTIME_ZONE());
            CountResponse countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return countResponse.getCount();
        } catch (ElasticsearchStatusException | ActionRequestValidationException | IOException ess) {
            LOGGER.info("Error: " + ess.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ess.getMessage());
        }
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
    public long countByLevelActionDay(String action, String level){
        try {
            CountRequest countRequest = ElasticQuery.getActionLevelLastDayDashboard(action, level,constants.getINDICE(),constants.getTIME_ZONE());
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

    @Async
    public Acciones getFaseAction(){
        Acciones acciones = new Acciones();
        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(ElasticQuery.groupbyActionandPhase(constants.getINDICE()), RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.info("Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        List<Accion> accionList = new ArrayList<>();
        Map<String, Aggregation> results = response.getAggregations().getAsMap();
        ParsedStringTerms actions = (ParsedStringTerms) results.get("action");
        for (Terms.Bucket action : actions.getBuckets()) {
            Accion accion = new Accion();
            accion.setNombre(action.getKeyAsString().toUpperCase());//--
            List<Fase> fases = new ArrayList<>();
            ParsedStringTerms phases = (ParsedStringTerms) action.getAggregations().getAsMap().get("phase");
            for (Terms.Bucket phase : phases.getBuckets()) {
                Fase fase = new Fase();
                fase.setNombre(phase.getKeyAsString().toUpperCase());//--
                fases.add(fase);
            }
            accion.setFases(fases);
            accionList.add(accion);
        }

        accionList = Validator.validateActionPhase(accionList);//

        acciones.setAcciones(accionList);

        return acciones;
    }


}
