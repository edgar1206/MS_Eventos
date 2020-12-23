package mx.com.nmp.eventos.utils;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.HashSet;

public class ElasticQuery {

    public static SearchRequest getLogs(String index){
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        sourceBuilder.from(0);
        sourceBuilder.size(5000);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.source(sourceBuilder);
        searchRequest.scroll(TimeValue.timeValueMinutes(1L));
        return searchRequest;
    }

    public static SearchRequest getLogsByLevel(String level, String index){
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("level",level));
        sourceBuilder.from(0);
        sourceBuilder.size(4000);
        searchRequest.indices(index);
        searchRequest.source(sourceBuilder);
        searchRequest.scroll(TimeValue.timeValueMinutes(1L));
        return searchRequest;
    }

    public static SearchRequest getLogsByLevelAndDate(String fecha, String level, String index){
        String lte = fecha.concat("T23:59:59.999Z");
        String gte = fecha.concat("T00:00:00.000Z");
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        HashSet<String> valores = new HashSet<>();
        valores.add(level);
        if(!level.isEmpty())boolQueryBuilder.must(QueryBuilders.termsQuery("level",valores));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("startTime").gte(gte).lte(lte));
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.from(0);
        sourceBuilder.size(4000);
        searchRequest.indices(index);
        searchRequest.source(sourceBuilder);
        searchRequest.scroll(TimeValue.timeValueMinutes(1L));
        return searchRequest;
    }

}
