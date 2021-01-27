package mx.com.nmp.eventos.controller;

import mx.com.nmp.eventos.model.nr.Evento;
import mx.com.nmp.eventos.model.response.DashBoard;
import mx.com.nmp.eventos.service.ServiceLogImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/event/trace/v1")
public class ControllerLog {

    private static final Logger LOGGER = Logger.getLogger(ControllerLog.class.getName());

    @Autowired
    private ServiceLogImplement serviceLog;

    @PostMapping("/addLog")
    public ResponseEntity<?> agregaLog(@RequestBody Evento evento){
        try{
            serviceLog.saveLog(evento);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/logs")
    public List<Evento> getLogs(){
        return serviceLog.getAllLogs();
    }

    //////----------

    @GetMapping("/dashboard")
    public List<DashBoard> getDashboard(){
        return serviceLog.getDashboard();
    }


    @GetMapping(value = "/third_level",params = "fase")
    public List<DashBoard> getThirdLevel(@RequestParam("fase")String fase){
        return serviceLog.getThirdLevel(fase);
    }

}