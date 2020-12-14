package mx.com.nmp.eventos.service;

import mx.com.nmp.eventos.model.nr.LogIncidencia;
import mx.com.nmp.eventos.repository.RepositoryIncidencia;
import mx.com.nmp.eventos.repository.ServiceIncidencia;
import org.elasticsearch.ElasticsearchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class ServiceIncidenciaImplement implements ServiceIncidencia {

    private static final Logger LOGGER = Logger.getLogger(ServiceIncidenciaImplement.class.getName());

    @Autowired
    private RepositoryIncidencia repositoryIncidencia;

    @Override
    public Iterable<LogIncidencia> getAllIncidencias() {
        return repositoryIncidencia.findAll();
    }

    @Override
    public LogIncidencia saveIncidencias(LogIncidencia incidencia) {
        LOGGER.info("guarda evento en elastic");
        return repositoryIncidencia.save(incidencia);
    }

}
