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

    public static SearchRequest getByActionWeekResource(String action, String phase, String index, String timeZone){
        if(action.equalsIgnoreCase("Solicitar Pagos")){
            action = "Solicitar";
        }
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventAction",action));
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventPhase",phase));
        //boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventResource",resource));
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
    public static CountRequest getActionLevelLastDayDashboard(String action ,String level, String index, String timeZone){
        CountRequest countRequest = new CountRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventAction", action));
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventLevel", level));
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
    public static SearchRequest getByActionLevelDayLogs(String action, String phase,String level, String index, String timeZone, String fecha1, String fecha2){
        if(action==null && phase==null && level==null){
            SearchRequest searchRequest = new SearchRequest();
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("timeGenerated").gte(fecha1+"T00:00:00").lte(fecha2+"T23:59:59").timeZone(ElasticQuery.getUtc(timeZone)));
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
            if(phase==null || phase.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error, fase es nulo o vacio.");
            if(action==null || action.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error, accion es nulo o vacio.");
            if(level==null || level.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error, nivel es nulo o vacio");

            SearchRequest searchRequest = new SearchRequest();
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventAction",action));
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventPhase",phase));
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("eventLevel",level));
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("timeGenerated").gte(fecha1+"T00:00:00").lte(fecha2+"T23:59:59").timeZone(ElasticQuery.getUtc(timeZone)));
            sourceBuilder.query(boolQueryBuilder);
            sourceBuilder.from(0);
            sourceBuilder.size(10000);
            searchRequest.source(sourceBuilder);
            searchRequest.scroll(TimeValue.timeValueMinutes(1L));
            searchRequest.indices(index);
            return searchRequest;
        }

    }
    public static SearchRequest groupbyActionandPhase(String index) {

            TermsAggregationBuilder subAggregation = AggregationBuilders.terms("phase")
                .field("eventPhase.keyword");
            TermsAggregationBuilder aggregation = AggregationBuilders.terms("action")
                .field("eventAction.keyword")
                .subAggregation(subAggregation);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().aggregation(aggregation);
        //System.out.println(sourceBuilder);
       // SearchRequest searchRequest =
                //new SearchRequest().indices(index).types("article").source(sourceBuilder);
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
