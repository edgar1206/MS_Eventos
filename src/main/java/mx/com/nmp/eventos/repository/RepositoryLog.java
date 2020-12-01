package mx.com.nmp.eventos.repository;

import mx.com.nmp.eventos.model.indicelogs.LogDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryLog extends ElasticsearchRepository <LogDTO, String> {

}
