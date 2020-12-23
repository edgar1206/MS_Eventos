package mx.com.nmp.eventos.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import mx.com.nmp.eventos.dto.LogIndiceDTO;
import mx.com.nmp.eventos.model.indicelogs.HostIP;
import mx.com.nmp.eventos.model.indicelogs.HostName;
import mx.com.nmp.eventos.model.indicelogs.LogIndice;
import mx.com.nmp.eventos.model.indicelogs.Message;
import org.springframework.beans.factory.annotation.Autowired;

public class MessageMapper {

    @Autowired
    private static ObjectMapper objectMapper;

    public static LogIndice eventToMessage(LogIndiceDTO evento){
        Message message = new Message();
        message.setDescripcion(evento.getDescripcion());
        message.setAccion(evento.getAccion());
        message.setEstatus(evento.getEstatus());
        message.setFase(evento.getFase());
        message.setIdCliente(evento.getIdCliente());
        message.setIdSesion(evento.getIdSession());
        message.setNombreUsuario(evento.getNombreUsuario());
        message.setRecurso(evento.getRecurso());
        HostIP hostIP = new HostIP();
        hostIP.setIp(evento.getHostIP());
        message.setHost(hostIP);
        HostName hostName = new HostName();
        hostName.setHostname(evento.getHostName());
        LogIndice indice = new LogIndice();
        indice.setHost(hostName);
        indice.setMessage(message);
        indice.setCategoryName(evento.getCategoryName());
        indice.setGeoip(evento.getGeoIP());
        System.out.println(indice.getGeoip().getRegionCode());
        System.out.println(indice.getGeoip().getCountryCode());
        indice.setLevel(evento.getLevel());
        indice.setPid(evento.getPid());
        indice.setStartTime(evento.getStartTime());
        indice.setTimeStamp(evento.getTimeStamp());
        indice.setVersion(evento.getVersion());

        return indice;
    }

}
