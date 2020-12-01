package mx.com.nmp.eventos.model.nr;

public class NewRelic {

    private String idTicket;
    private String error;
    private String errorDescripcion;
    private String stackTrace;
    private String statusCodeError;
    private String severidad;
    private String torreResolucion;
    private String horaOcurrencia;

    public String getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(String idTicket) {
        this.idTicket = idTicket;
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

    public String getHoraOcurrencia() {
        return horaOcurrencia;
    }

    public void setHoraOcurrencia(String horaOcurrencia) {
        this.horaOcurrencia = horaOcurrencia;
    }

}
