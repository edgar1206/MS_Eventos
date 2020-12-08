package mx.com.nmp.eventos.repository;

import mx.com.nmp.eventos.model.nr.LogIncidencia;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryIncidencia extends ElasticsearchRepository <LogIncidencia, String> {
}
