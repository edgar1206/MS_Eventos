package mx.com.nmp.eventos.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class LogDTO {
    @JsonProperty("message.descripcion")
    private String descripcion;
    private Date startTime;
    private String pid;
    @JsonProperty("geoip")
    private GeoIP geoIP;
    private String categoryName;
    @JsonProperty("host.hostname")
    private String hostName;
    @JsonProperty("message.recurso")
    private String recurso;
    @JsonProperty("message.host.ip")
    private String hostIP;
    @JsonProperty("message.accion")
    private String accion;
    @JsonProperty("@version")
    private String version;
    @JsonProperty("@timestamp")
    private Date timeStamp;
    private String level;
    @JsonProperty("message.fase")
    private String fase;
    @JsonProperty("message.estatus")
    private String estatus;
}
