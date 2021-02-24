package mx.com.nmp.eventos.model.response;

public class Table {

    private String fase;
    private long info;
    private long error;
    private long debug;
    private long fatal;
    private long trace;

    public String getFase() {
        return fase;
    }

    public void setFase(String fase) {
        this.fase = fase;
    }

    public long getInfo() {
        return info;
    }

    public void setInfo(long info) {
        this.info = info;
    }

    public long getError() {
        return error;
    }

    public void setError(long error) {
        this.error = error;
    }

    public long getDebug() {
        return debug;
    }

    public void setDebug(long debug) {
        this.debug = debug;
    }

    public long getFatal() {
        return fatal;
    }

    public void setFatal(long fatal) {
        this.fatal = fatal;
    }

    public long getTrace() {
        return trace;
    }

    public void setTrace(long trace) {
        this.trace = trace;
    }
}
