package mx.com.nmp.eventos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import mx.com.nmp.eventos.model.indicelogs.GeoIP;

import java.util.Date;

public class LogIndiceDTO {
    @JsonProperty("message.idCliente")
    private String idCliente;
    @JsonProperty("message.idSesion")
    private String idSession;
    private Date startTime;
    @JsonProperty("message.descripcion")
    private String descripcion;
    private String pid;
    private String nombreUsuario;
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
    private String fase;
    @JsonProperty("message.estatus")
    private String estatus;

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdSession() {
        return idSession;
    }

    public void setIdSession(String idSession) {
        this.idSession = idSession;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public GeoIP getGeoIP() {
        return geoIP;
    }

    public void setGeoIP(GeoIP geoIP) {
        this.geoIP = geoIP;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getRecurso() {
        return recurso;
    }

    public void setRecurso(String recurso) {
        this.recurso = recurso;
    }

    public String getHostIP() {
        return hostIP;
    }

    public void setHostIP(String hostIP) {
        this.hostIP = hostIP;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @JsonProperty("message.fase")
    public String getFase() {
        return fase;
    }

    @JsonProperty("message.fase")
    public void setFase(String fase) {
        this.fase = fase;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }
}
