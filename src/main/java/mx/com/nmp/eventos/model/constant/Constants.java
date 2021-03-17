package mx.com.nmp.eventos.model.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {

    private String INDICE;
    private String TIME_ZONE;
    private String MONTH;

    public Constants(@Value("${index.elastic}") String INDICE,
                     @Value("${time.zone}") String TIME_ZONE,
                     @Value("${time.month}") String MONTH)
    {

        this.INDICE = INDICE;
        this.TIME_ZONE = TIME_ZONE;
        this.MONTH = MONTH;

    }

    public String getINDICE() {
        return INDICE;
    }

    public String getTIME_ZONE() {
        return TIME_ZONE;
    }

    public String getMONTH() {
        return MONTH;
    }
}
