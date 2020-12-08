package mx.com.nmp.eventos.service;

import mx.com.nmp.eventos.model.nr.LogIncidencia;
import mx.com.nmp.eventos.repository.RepositoryIncidencia;
import mx.com.nmp.eventos.repository.ServiceIncidencia;
import org.elasticsearch.ElasticsearchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ServiceIncidenciaImplement implements ServiceIncidencia {

    @Autowired
    private RepositoryIncidencia repositoryIncidencia;

    @Override
    public Iterable<LogIncidencia> getAllIncidencias() {
        return repositoryIncidencia.findAll();
    }

    @Override
    public LogIncidencia saveIncidencias(LogIncidencia incidencia) {
        return repositoryIncidencia.save(incidencia);
    }

}
