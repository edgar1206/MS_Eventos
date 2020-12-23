package mx.com.nmp.eventos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import mx.com.nmp.eventos.dto.LogIndiceDTO;
import mx.com.nmp.eventos.model.indicelogs.LogIndice;
import mx.com.nmp.eventos.model.indicelogs.Message;
import mx.com.nmp.eventos.model.logLevel.CountLevel;
import mx.com.nmp.eventos.service.ServiceLogImplement;
import mx.com.nmp.eventos.utils.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.GenericSignatureFormatError;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/nmp/monitoreo/v1")
public class ControllerLog {

    private static final Logger LOGGER = Logger.getLogger(ControllerLog.class.getName());

    @Autowired
    private ServiceLogImplement serviceLog;

    @PostMapping("/addLog")
    public ResponseEntity<?> agregaLog(@RequestBody LogIndiceDTO evento){
        try{
            serviceLog.saveLog(evento);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/logs")
    public ResponseEntity<?> getLogs(){
        return ResponseEntity.ok(serviceLog.getAllLogs());
    }
/*
    @GetMapping(value = "/level",params = "level")
    public List<LogIndice> getEventosPorLevel(@RequestParam("level") String level){
        List<LogIndice> respuesta = serviceLog.getEventosPorLevel(level);
        return respuesta;
    }
*/
    @GetMapping(value = "/count")
    public CountLevel getEventosPorLevel(){
        return serviceLog.getCuentaTipoEventos();
    }
/*
    @GetMapping(value = "/rate",params = {"fecha", "level"})
    public List<LogIndice> getEventosErrorFecha(@RequestParam("fecha") String fecha, @RequestParam("level") String level){
        List<LogIndice> eventos = serviceLog.getEventosPorFechaLevel(fecha, level);
        return eventos;
    }
*/
}
