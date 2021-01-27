package mx.com.nmp.eventos.utils;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.Calendar;
import java.util.TimeZone;

public class ElasticQuery {

    public static CountRequest getActionLevelLastDay(String field ,String value, String index){
        CountRequest countRequest = new CountRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchQuery(field,value));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("timeGenerated").gte("now-1d").lte("now").timeZone(ElasticQuery.getUtc()));
        searchSourceBuilder.query(boolQueryBuilder);
        countRequest.indices(index);
        countRequest.source(searchSourceBuilder);
        return countRequest;
    }

    public static CountRequest getByNameDay(String dia,String level,String phase,String index){
        CountRequest countRequest = new CountRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchQuery("eventLevel", level));
        boolQueryBuilder.must(QueryBuilders.matchQuery("eventPhase", phase));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("timeGenerated")
                .gte("now-" + dia + "d/d")
                .lte("now-" + dia + "d/d")
                .timeZone(getUtc()));
        sourceBuilder.query(boolQueryBuilder);
        countRequest.indices(index);
        countRequest.source(sourceBuilder);
        return countRequest;
    }

    public static CountRequest getByDay(String dia,String index){
        CountRequest countRequest = new CountRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.rangeQuery("timeGenerated")
                .gte("now-" + dia + "d/d")
                .lte("now-" + dia + "d/d")
                .timeZone(getUtc()));
        countRequest.indices(index);
        countRequest.source(sourceBuilder);
        return countRequest;
    }

    public static CountRequest getByWeek(String week,String index){
        CountRequest countRequest = new CountRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.rangeQuery("timeGenerated")
                .gte("now-" + week + "w/w")
                .lte("now-" + week + "w/w")
                .timeZone(getUtc()));
        countRequest.indices(index);
        countRequest.source(sourceBuilder);
        return countRequest;
    }

    public static CountRequest getByMonth(String accion, int mes,String index){
        CountRequest countRequest = new CountRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchQuery("eventAction",accion));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("timeGenerated")
                .gte("now-" + mes + "M/M")
                .lte("now-" + mes + "M/M")
                .timeZone(getUtc()));
        sourceBuilder.query(boolQueryBuilder);
        countRequest.indices(index);
        countRequest.source(sourceBuilder);
        return countRequest;
    }

/////

    public static SearchRequest getLogs(String index){
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        sourceBuilder.from(0);
        sourceBuilder.size(100);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.source(sourceBuilder);
        searchRequest.scroll(TimeValue.timeValueMinutes(1L));
        return searchRequest;
    }

    public static String getUtc(){
        TimeZone zone = TimeZone.getTimeZone("America/Mexico_City");
        int horas =  zone.getOffset(Calendar.ZONE_OFFSET)/36000;
        String zoneId = String.valueOf(horas);
        zoneId = zoneId.replace("-","");
        String utc = "-0";
        return utc.concat(zoneId);
    }

}
