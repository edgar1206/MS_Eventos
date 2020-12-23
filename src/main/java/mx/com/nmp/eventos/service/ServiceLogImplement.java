package mx.com.nmp.eventos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import mx.com.nmp.eventos.dto.LogIndiceDTO;
import mx.com.nmp.eventos.model.constant.Constants;
import mx.com.nmp.eventos.model.indicelogs.LogIndice;
import mx.com.nmp.eventos.model.logLevel.CountLevel;
import mx.com.nmp.eventos.model.nr.Evento;
import mx.com.nmp.eventos.repository.RepositoryLog;
import mx.com.nmp.eventos.utils.ElasticQuery;
import mx.com.nmp.eventos.utils.MessageMapper;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    public List<LogIndice> getAllLogs() {

        List<LogIndice> logs = new ArrayList<>();
        try {
            final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
            SearchRequest searchRequest = ElasticQuery.getLogs(constants.getINDICE());
            searchRequest.scroll(scroll);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            String scrollId = searchResponse.getScrollId();
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            addLog(searchHits, logs);

            while (searchHits != null && searchHits.length > 1) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(scroll);
                searchResponse = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
                scrollId = searchResponse.getScrollId();
                searchHits = searchResponse.getHits().getHits();
                addLog(searchHits, logs);
            }

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

        return logs;
    }
/*
    public List<LogIndice> getEventosPorLevel(String level){
        List<LogIndice> logs = new ArrayList<>();
        try {
            final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
            SearchRequest searchRequest = ElasticQuery.getLogsByLevel(level, constants.getINDICE());
            searchRequest.scroll(scroll);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            addLog(searchHits, logs);
        } catch (ElasticsearchStatusException | ActionRequestValidationException | IOException ess) {
            LOGGER.info("Error: " + ess.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ess.getMessage());
        }
        if(logs.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron eventos");
        return logs;
    }

    public List<LogIndice> getEventosPorFechaLevel(String fecha, String level){
        List<LogIndice> logs = new ArrayList<>();
        try {
            final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
            SearchRequest searchRequest = ElasticQuery.getLogsByLevelAndDate(fecha, level, constants.getINDICE());
            searchRequest.scroll(scroll);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            addLog(searchHits, logs);
        } catch (ElasticsearchStatusException | ActionRequestValidationException | IOException ess) {
            LOGGER.info("ERROR: " + ess.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ess.getMessage());
        }
        if(logs.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron eventos");
        return logs;
    }
*/
    public CountLevel getCuentaTipoEventos(){
        CountLevel countLevel = new CountLevel();
        countLevel.setInfo(countEventosLevel("info"));
        countLevel.setError(countEventosLevel("error"));
        countLevel.setFatal(countEventosLevel("fatal"));
        /*countLevel.setTrace(countEventosLevel("trace"));*/
        return countLevel;
    }

    @Async
    public long countEventosLevel(String level){
        CountRequest countRequest = new CountRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("level",level));
        countRequest.indices(constants.getINDICE());
        countRequest.source(searchSourceBuilder);
        try {
            CountResponse countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            return countResponse.getCount();
        } catch (IOException e) {
            LOGGER.info("Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public void saveLog(Evento evento) {
        try{
           // LogIndice indice = MessageMapper.eventToMessage(evento);
            //LogIndice logIndice = repositoryLog.save(indice);
            Evento eventoIndice = repositoryLog.save(evento);
            Gson gson = new Gson();
            System.out.println("---------------------  " + gson.toJson(eventoIndice));
        }catch (Exception e){
            LOGGER.info( "Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private void addLog(SearchHit[] results, List<LogIndice> logs) throws JsonProcessingException {
        if (results != null && results.length > 0) {
            for(SearchHit result:results){
                System.out.println();
                System.out.println(result.getSourceAsString());
                LogIndice log = new ObjectMapper().readValue(result.getSourceAsString(), LogIndice.class);
                logs.add(log);
            }
        }
    }

}
