package mx.com.nmp.eventos.repository;

import mx.com.nmp.eventos.model.nr.Evento;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryLog extends ElasticsearchRepository <Evento, String> {

}
