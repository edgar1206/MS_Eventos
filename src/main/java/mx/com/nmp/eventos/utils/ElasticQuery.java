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

    public static SearchRequest getByActionWeek(String action, String phase, String index, String timeZone){
        if(action.equalsIgnoreCase("Solicitar Pagos")){
            action = "Solicitar";
        }
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventAction",action));
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventPhase",phase));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("timeGenerated")
                .gte("now-" + 6 + "d/d")
                .lte("now-" + 0 + "d/d")
                .timeZone(getUtc(timeZone)));
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.from(0);
        sourceBuilder.size(10000);
        searchRequest.source(sourceBuilder);
        searchRequest.scroll(TimeValue.timeValueMinutes(1L));
        searchRequest.indices(index);
        return searchRequest;
    }

    public static CountRequest getActionLevelLastDay(String field ,String value, String index, String timeZone){
        CountRequest countRequest = new CountRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery(field,value));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("timeGenerated").gte("now-1d").lte("now").timeZone(ElasticQuery.getUtc(timeZone)));
        searchSourceBuilder.query(boolQueryBuilder);
        countRequest.indices(index);
        countRequest.source(searchSourceBuilder);
        return countRequest;
    }

    public static CountRequest getByNameDay(String dia,String level,String action, String phase,String index, String timeZone){
        CountRequest countRequest = new CountRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventAction", action));
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventLevel", level));
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventPhase", phase));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("timeGenerated")
                .gte("now-" + dia + "d/d")
                .lte("now-" + dia + "d/d")
                .timeZone(getUtc(timeZone)));
        sourceBuilder.query(boolQueryBuilder);
        countRequest.indices(index);
        countRequest.source(sourceBuilder);
        return countRequest;
    }

    public static CountRequest getByDay(String dia,String index, String timeZone){
        CountRequest countRequest = new CountRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.rangeQuery("timeGenerated")
                .gte("now-" + dia + "d/d")
                .lte("now-" + dia + "d/d")
                .timeZone(getUtc(timeZone)));
        countRequest.indices(index);
        countRequest.source(sourceBuilder);
        return countRequest;
    }

    public static CountRequest getByWeek(String week,String index, String timeZone){
        CountRequest countRequest = new CountRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.rangeQuery("timeGenerated")
                .gte("now-" + week + "w/w")
                .lte("now-" + week + "w/w")
                .timeZone(getUtc(timeZone)));
        countRequest.indices(index);
        countRequest.source(sourceBuilder);
        return countRequest;
    }

    public static CountRequest getByMonth(String action, int mes,String index, String timeZone){
        CountRequest countRequest = new CountRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventAction",action));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("timeGenerated")
                .gte("now-" + mes + "M/M")
                .lte("now-" + mes + "M/M")
                .timeZone(getUtc(timeZone)));
        sourceBuilder.query(boolQueryBuilder);
        countRequest.indices(index);
        countRequest.source(sourceBuilder);
        return countRequest;
    }

    public static CountRequest getByActionLevelDay(int dia, String action, String level, String index, String timeZone){
        if(action.equalsIgnoreCase("Solicitar Pagos")){
            action = "Solicitar";
        }
        CountRequest countRequest = new CountRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventAction", action));
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventLevel", level));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("timeGenerated")
                .gte("now-" + dia + "d/d")
                .lte("now-" + dia + "d/d")
                .timeZone(getUtc(timeZone)));
        sourceBuilder.query(boolQueryBuilder);
        countRequest.source(sourceBuilder);
        countRequest.indices(index);
        return countRequest;
    }

    public static String getUtc(String timeZone){
        TimeZone zone = TimeZone.getTimeZone(timeZone);
        int horas =  zone.getOffset(Calendar.ZONE_OFFSET)/36000;
        String zoneId = String.valueOf(horas);
        zoneId = zoneId.replace("-","");
        String utc = "-0";
        return utc.concat(zoneId);
    }

}
