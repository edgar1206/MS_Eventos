package mx.com.nmp.eventos.controller;

import com.google.gson.Gson;
import mx.com.nmp.eventos.model.indicelogs.LogDTO;
import mx.com.nmp.eventos.model.nr.LogIncidencia;
import mx.com.nmp.eventos.service.ServiceLogImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/nmp/monitoreo/v1")
public class ControllerLog {

    private static final Logger LOGGER = Logger.getLogger(ControllerLog.class.getName());

    @Autowired
    private ServiceLogImplement serviceLog;

    @PostMapping("/addLogs")
    public ResponseEntity<?> agregaLogs(@RequestBody List<LogDTO> logs){
        serviceLog.saveAllLogs(logs);
        return null;
    }

    @PostMapping("/addLog")
    public ResponseEntity<?> agregaLog(@RequestBody LogDTO log){
        LOGGER.info("recibe evento");
        try{
            serviceLog.saveLog(log);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/logs")
    public ResponseEntity<?> getLogs(){
        return ResponseEntity.ok(serviceLog.getAllLogs());
    }

}
