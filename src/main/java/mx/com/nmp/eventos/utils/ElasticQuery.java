package mx.com.nmp.eventos.utils;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Calendar;
import java.util.TimeZone;

public class ElasticQuery {

    public static SearchRequest getByActionWeek(String action, String index, String timeZone, String appName){
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        TermsAggregationBuilder subSubAggregation = AggregationBuilders.terms("level")
                .field("eventLevel.keyword").size(100);
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("phase")
                .field("eventPhase.keyword").size(100).subAggregation(subSubAggregation);
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventAction.keyword",action));
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("applicationName",appName));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("timeGenerated").gte("now-6d/d").lte("now").timeZone(ElasticQuery.getUtc(timeZone)));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.aggregation(aggregation);
        searchRequest.source(searchSourceBuilder);
        searchRequest.indices(index);
        return searchRequest;
    }

    public static SearchRequest getActionLevelLastDay(String index, String timeZone, String appName){
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        SearchRequest searchRequest = new SearchRequest();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("applicationName", appName));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("timeGenerated").gte("now-1d").lte("now").timeZone(ElasticQuery.getUtc(timeZone)));
        TermsAggregationBuilder subAggregation = AggregationBuilders.terms("level")
                .field("eventLevel.keyword").size(100);
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("action")
                .field("eventAction.keyword").size(100).subAggregation(subAggregation);
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.aggregation(aggregation);
        searchRequest.indices(index);
        searchRequest.source(sourceBuilder);
        return searchRequest;
    }

    public static SearchRequest getByNameDay(String dia, String action, String phase,String index, String timeZone, String appName){
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("applicationName",appName));
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventAction.keyword",action));
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventPhase.keyword",phase));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("timeGenerated")
                .gte("now-" + dia + "d/d")
                .lte("now-" + dia + "d/d")
                .timeZone(getUtc(timeZone)));
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("level")
                .field("eventLevel.keyword").size(100);
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.aggregation(aggregation);
        searchRequest.indices(index);
        searchRequest.source(sourceBuilder);
        return searchRequest;
    }

    public static CountRequest getByDay(String dia,String index, String timeZone, String appName){
        CountRequest countRequest = new CountRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("applicationName",appName));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("timeGenerated")
                .gte("now-" + dia + "d/d")
                .lte("now-" + dia + "d/d")
                .timeZone(getUtc(timeZone)));
        sourceBuilder.query(boolQueryBuilder);
        countRequest.indices(index);
        countRequest.source(sourceBuilder);
        return countRequest;
    }

    public static SearchRequest getByDaySecondLevel(String action, int dia,String index, String timeZone, String appName){
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        SearchRequest searchRequest = new SearchRequest();
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("level")
                .field("eventLevel.keyword").size(100);
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("applicationName", appName));
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventAction.keyword", action));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("timeGenerated")
                .gte("now-" + dia + "d/d")
                .lte("now-" + dia + "d/d")
                .timeZone(getUtc(timeZone)));
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.aggregation(aggregation);
        searchRequest.indices(index);
        searchRequest.source(sourceBuilder);
        return searchRequest;
    }

    public static CountRequest getByWeek(String week,String index, String timeZone, String appName){
        CountRequest countRequest = new CountRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("applicationName",appName));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("timeGenerated")
                .gte("now-" + week + "w/w")
                .lte("now-" + week + "w/w")
                .timeZone(getUtc(timeZone)));
        sourceBuilder.query(boolQueryBuilder);
        countRequest.indices(index);
        countRequest.source(sourceBuilder);
        return countRequest;
    }

    public static SearchRequest getByMonth(int mes,String index, String timeZone, String appName){
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("applicationName", appName));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("timeGenerated")
                .gte("now-" + mes + "M/M")
                .lte("now-" + mes + "M/M")
                .timeZone(getUtc(timeZone)));
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("action")
                .field("eventAction.keyword").size(100);
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.aggregation(aggregation);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.source(sourceBuilder);
        return searchRequest;
    }

    public static SearchRequest getByActionLevelDayLogs(String action, String phase, String level, String index, String timeZone, String fecha1, String fecha2, String inicio, String fin, String appName){

        if(action==null && phase==null && level==null){
            SearchRequest searchRequest = new SearchRequest();
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("applicationName",appName));
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("timeGenerated")
                    .gte(fecha1+"T"+inicio+":00:00")
                    .lte(fecha2+"T"+fin+":59:59")
                    .timeZone(ElasticQuery.getUtc(timeZone)));
            sourceBuilder.query(boolQueryBuilder);
            sourceBuilder.from(0);
            sourceBuilder.size(10000);
            searchRequest.source(sourceBuilder);
            searchRequest.scroll(TimeValue.timeValueMinutes(1L));
            searchRequest.indices(index);
            return searchRequest;

        }
        else
        {
            if(phase==null || phase.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error, phase es nulo o vacio.");
            if(action==null || action.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error, action es nulo o vacio.");
            if(level==null || level.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error, level es nulo o vacio");

            SearchRequest searchRequest = new SearchRequest();
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("applicationName",appName));
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventAction.keyword",action));
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventPhase.keyword",phase));
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventLevel",level));
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("timeGenerated").gte(fecha1+"T"+inicio+":00:00").lte(fecha2+"T"+fin+":59:59").timeZone(ElasticQuery.getUtc(timeZone)));
            sourceBuilder.query(boolQueryBuilder);
            sourceBuilder.from(0);
            sourceBuilder.size(10000);
            searchRequest.source(sourceBuilder);
            searchRequest.scroll(TimeValue.timeValueMinutes(1L));
            searchRequest.indices(index);
            return searchRequest;
        }
    }

    public static SearchRequest groupbyActionandPhase(String index, String timeZone, String month, String appName) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("applicationName", appName));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("timeGenerated")
                .gte("now-" + month + "M/M")
                .lte("now")
                .timeZone(getUtc(timeZone)));
        TermsAggregationBuilder levelAggregation = AggregationBuilders.terms("level")
                .field("eventLevel.keyword").size(100);
        TermsAggregationBuilder phaseAggregation = AggregationBuilders.terms("phase")
                .field("eventPhase.keyword").subAggregation(levelAggregation).size(100);
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("action")
                .field("eventAction.keyword")
                .subAggregation(phaseAggregation).size(100);
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.aggregation(aggregation);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.source(sourceBuilder);
        return searchRequest;
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
