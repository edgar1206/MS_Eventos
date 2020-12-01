package mx.com.nmp.eventos.repository;

import mx.com.nmp.eventos.model.indicelogs.LogDTO;

import java.util.List;

public interface ServiceLog {

    List<LogDTO> saveAllLogs(List<LogDTO> logs);

    LogDTO saveLog(LogDTO logDTO);

}
