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

@Service
public class ServiceLogImplement implements ServiceLog {

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
    public LogDTO saveLog(LogDTO log) {
        repositoryLog.save(log);
        return null;
    }

}
