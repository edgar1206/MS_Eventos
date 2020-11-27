package mx.com.nmp.eventos.controller;

import mx.com.nmp.eventos.model.LogDTO;
import mx.com.nmp.eventos.repository.ServiceLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/nmp/monitoreo/v1")
public class ControllerLog {

    @Autowired
    private ServiceLog serviceLog;

    @PostMapping("/addLogs")
    public ResponseEntity<?> agregaLogs(@RequestBody List<LogDTO> logs){
        serviceLog.saveAllLogs(logs);
        return null;
    }

    @PostMapping("/addLog")
    public ResponseEntity<?> agregaLog(@RequestBody LogDTO log){
        serviceLog.saveLog(log);
        return null;
    }

}
