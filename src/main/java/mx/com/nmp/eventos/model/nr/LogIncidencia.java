package mx.com.nmp.eventos.model.nr;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@TypeAlias("Incidencia")
@Document(indexName = "indice-incidencia")
public class LogIncidencia {

    @Id
    private String id;
    private String idEvento;
    private String error;
    private String errorDescripcion;
    private String stackTrace;
    private String statusCodeError;
    private String severidad;
    private String torreResolucion;
    private Date horaOcurrencia;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(String idEvento) {
        this.idEvento = idEvento;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorDescripcion() {
        return errorDescripcion;
    }

    public void setErrorDescripcion(String errorDescripcion) {
        this.errorDescripcion = errorDescripcion;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getStatusCodeError() {
        return statusCodeError;
    }

    public void setStatusCodeError(String statusCodeError) {
        this.statusCodeError = statusCodeError;
    }

    public String getSeveridad() {
        return severidad;
    }

    public void setSeveridad(String severidad) {
        this.severidad = severidad;
    }

    public String getTorreResolucion() {
        return torreResolucion;
    }

    public void setTorreResolucion(String torreResolucion) {
        this.torreResolucion = torreResolucion;
    }

    public Date getHoraOcurrencia() {
        return horaOcurrencia;
    }

    public void setHoraOcurrencia(Date horaOcurrencia) {
        this.horaOcurrencia = horaOcurrencia;
    }
}
