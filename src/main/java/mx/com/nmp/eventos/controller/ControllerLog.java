package mx.com.nmp.eventos.controller;

import mx.com.nmp.eventos.model.logLevel.CountLevel;
import mx.com.nmp.eventos.model.nr.Evento;
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

    @GetMapping(value = "/level",params = "level")
    public List<Evento> getEventosPorLevel(@RequestParam("level") String level){
        List<Evento> respuesta = serviceLog.getEventosPorLevel(level);
        return respuesta;
    }

    @GetMapping(value = "/count")
    public CountLevel getEventosPorLevel(){
        return serviceLog.getCuentaTipoEventos();
    }

    @GetMapping(value = "/rate",params = {"fecha", "level"})
    public List<Evento> getEventosErrorFecha(@RequestParam("fecha") String fecha, @RequestParam("level") String level){
        List<Evento> eventos = serviceLog.getEventosPorFechaLevel(fecha, level);
        return eventos;
    }

    @GetMapping("/logsLastWeek")
    public List<Evento> logsLastWeek(){
        return serviceLog.getEventosUltimosSieteDias();
    }

}