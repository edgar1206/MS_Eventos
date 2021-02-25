package mx.com.nmp.eventos.controller;

import mx.com.nmp.eventos.model.nr.Evento;
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
    public ResponseEntity<?> addEvent(@RequestBody Evento evento){
        try{
            serviceLog.addEvent(evento);
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

    @GetMapping(value = "/third_level",params = "fase")
    public List<DashBoard> getThirdLevel(@RequestParam("fase")String fase){
        if(fase.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error, parametro nulo o vacio.");
        if(Validator.validateCodigoPhase(fase)){
            return serviceLog.getThirdLevel(Validator.getPhase(fase));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error, fase no válida.");
    }

    @GetMapping(value = "/fourth_level")
    public ResponseEntity getFourthLevel(@RequestParam(value="fase", required=false)String fase, @RequestParam(value="accion", required=false)String accion, @RequestParam(value="nivel", required=false)String nivel,@RequestParam(value="fechaDesde", required=true)String fechaDesde,@RequestParam(value="fechaHasta", required=true)String fechaHasta){
        return new ResponseEntity(serviceLog.getFourthLevel(accion, fase,nivel,fechaDesde,fechaHasta),HttpStatus.OK);


    }

}