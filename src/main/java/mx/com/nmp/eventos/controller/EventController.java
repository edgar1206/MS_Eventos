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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<DashBoard> getDashboard(@RequestHeader String appName){
        return serviceLog.getDashboard(appName);
    }

    @GetMapping(value = "/second_level",params = "action")
    public SecondLevel getSecondLevel(@RequestParam("action")String action, @RequestHeader String appName){
        if(action.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error, parametro nulo o vacio.");
        if(Validator.validateAction(action, appName)){
            Map mapa=concatenar(action,null);
            return serviceLog.getSecondLevel(mapa.get("accion").toString(),appName);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error, acción no válida.");
    }

    @GetMapping(value = "/third_level",params = {"action","phase"})
    public List<DashBoard> getThirdLevel(@RequestParam("action")String accion, @RequestParam("phase")String fase, @RequestHeader String appName){
        if(fase.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error, parametro nulo o vacio.");
        if(accion.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error, parametro nulo o vacio.");
        if(Validator.validateActionPhase(accion, fase, appName)){
            Map mapa=concatenar(accion,fase);
            return serviceLog.getThirdLevel(mapa.get("accion").toString(), mapa.get("fase").toString(), appName);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error, parametros no validos.");
    }

    @GetMapping(value = "/fourth_level")
    public List<Evento> getFourthLevel(@RequestHeader String appName, @RequestParam(value="phase", required=false)String fase, @RequestParam(value="action", required=false)String accion, @RequestParam(value="level", required=false)String nivel, @RequestParam(value="date", required=true)String fecha){
        if(accion != null && fase != null && nivel!=null && (!Validator.validateActionPhase(accion, fase, appName) || !Validator.validateLevel(nivel))){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error, parametros no validos.");

        }
        Map mapa=concatenar(accion,fase);
        return serviceLog.getFourthLevel(mapa.get("accion").toString(), mapa.get("fase").toString(),nivel,fecha,fecha,appName);
    }

    @GetMapping(value = "/actions")
    public Acciones getActionPhase(@RequestHeader String appName){
        return serviceLog.getFaseAction(appName);
    }

    private Map concatenar(String accion, String fase){
        if(accion.equals("OpenPay read customer list from merchant - Lista vacia")){
            accion=accion+" ";
        }
        if(fase!=null &&fase.equals("Registro Tarjeta")){
            fase=fase+" ";
        }
        Map mapa = new HashMap();
        mapa.put("accion",accion);
        mapa.put("fase",fase);
        return mapa;
    }

}