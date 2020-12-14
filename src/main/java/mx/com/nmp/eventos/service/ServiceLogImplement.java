package mx.com.nmp.eventos.service;

import com.google.gson.Gson;
import com.sun.media.jfxmediaimpl.platform.gstreamer.GSTPlatform;
import mx.com.nmp.eventos.model.indicelogs.LogDTO;
import mx.com.nmp.eventos.model.nr.LogIncidencia;
import mx.com.nmp.eventos.repository.RepositoryIncidencia;
import mx.com.nmp.eventos.repository.RepositoryLog;
import mx.com.nmp.eventos.repository.ServiceLog;
import org.elasticsearch.ElasticsearchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class ServiceLogImplement implements ServiceLog {

    private static final Logger LOGGER = Logger.getLogger(ServiceLogImplement.class.getName());

    @Autowired
    private RepositoryLog repositoryLog;

    @Autowired
    private RepositoryIncidencia repositoryIncidencia;

    @Override
    public List<LogDTO> saveAllLogs(List<LogDTO> logs) {
        repositoryLog.saveAll(logs);
        return null;
    }

    @Override
    public Iterable<LogDTO> getAllLogs() {
        return repositoryLog.findAll();
    }

    @Override
    public LogDTO saveLog(LogDTO log) {
        LOGGER.info("guarda evento en elastic");
        repositoryLog.save(log);
        return null;
    }

}
