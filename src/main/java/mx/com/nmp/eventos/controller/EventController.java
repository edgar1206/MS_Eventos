package mx.com.nmp.eventos.controller;

import mx.com.nmp.eventos.model.nr.Evento;
import mx.com.nmp.eventos.model.nr.EventoDto;
import mx.com.nmp.eventos.model.response.Acciones;
import mx.com.nmp.eventos.model.response.DashBoard;
import mx.com.nmp.eventos.model.response.SecondLevel;
import mx.com.nmp.eventos.service.EventService;
import mx.com.nmp.eventos.utils.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET})
@RequestMapping("/event/trace/v1")
public class EventController {

    @Autowired
    private EventService serviceLog;

    @PostMapping("/event")
    public ResponseEntity<?> addEvent(@RequestBody EventoDto evento){
        try{
            Evento eventoPersistencia = new Evento();
            eventoPersistencia.setIdEvent(evento.getIdEvent());
            eventoPersistencia.setEventType(evento.getEventType());
            eventoPersistencia.setEventLevel(evento.getEventLevel());
            eventoPersistencia.setEventCategory(evento.getEventCategory());
            eventoPersistencia.setEventAction(evento.getEventAction());
            eventoPersistencia.setEventDescription(evento.getEventDescription());
            eventoPersistencia.setSeverity(evento.getSeverity());
            eventoPersistencia.setEventResource(evento.getEventResource());
            eventoPersistencia.setTimeGenerated(evento.getTimeGenerated());
            eventoPersistencia.setEventPhase(evento.getEventPhase());
            eventoPersistencia.setResolutionTower(evento.getResolutionTower());
            eventoPersistencia.setApplicationName(evento.getApplicationName());
            eventoPersistencia.setConfigurationElement(evento.getConfigurationElement());
            serviceLog.addEvent(eventoPersistencia);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    //////----------

    @GetMapping("/dashboard")
    public List<DashBoard> getDashboard(){
        return serviceLog.getDashboard();
    }

    @GetMapping(value = "/second_level",params = "action")
    public SecondLevel getSecondLevel(@RequestParam("action")String action){
        if(action.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error, parametro nulo o vacio.");
        if(Validator.validateAction(action)){
            return serviceLog.getSecondLevel(action);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error, acción no válida.");
    }

    @GetMapping(value = "/third_level",params = {"action","phase"})
    public List<DashBoard> getThirdLevel(@RequestParam("action")String accion, @RequestParam("phase")String fase){
        if(fase.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error, parametro nulo o vacio.");
        if(accion.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error, parametro nulo o vacio.");
        if(Validator.validateActionPhase(accion,fase)){
            return serviceLog.getThirdLevel(accion,fase);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error, parametros no validos.");
    }

    @GetMapping(value = "/fourth_level")
    public List<Evento> getFourthLevel(@RequestParam(value="phase", required=false)String fase, @RequestParam(value="action", required=false)String accion, @RequestParam(value="level", required=false)String nivel, @RequestParam(value="date", required=true)String fecha){
        if(accion != null && fase != null && !Validator.validateActionPhase(accion,fase)){

            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error, parametros no validos.");

        }
        return serviceLog.getFourthLevel(accion,fase,nivel,fecha,fecha);
    }

    @GetMapping(value = "/actions")
    public Acciones getActionPhase(){
        return serviceLog.getActionsPhases();
    }

}