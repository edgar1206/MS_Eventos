package mx.com.nmp.eventos.controller;

import mx.com.nmp.eventos.model.nr.LogIncidencia;
import mx.com.nmp.eventos.service.ServiceIncidenciaImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nmp/monitoreo/v1")
public class ControllerIncidencias {

    @Autowired
    private ServiceIncidenciaImplement serviceIncidenciaImplement;

    @GetMapping("/incidencias")
    public ResponseEntity<?> getAllIncidencias(){
        try{
            return ResponseEntity.ok().body(serviceIncidenciaImplement.getAllIncidencias());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/incidencia")
    public ResponseEntity<?> agregaNotificacion(@RequestBody LogIncidencia log){
        try{
            serviceIncidenciaImplement.saveIncidencias(log);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

}
