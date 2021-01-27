package mx.com.nmp.eventos.model.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {

    private static String INDICE;
    private static String TIME_ZONE;

    public Constants(@Value("${index.elastic}") String INDICE,
                     @Value("${time.zone}") String TIME_ZONE)
    {

        Constants.INDICE = INDICE;
        Constants.TIME_ZONE = TIME_ZONE;

    }

    public static String getINDICE() {
        return INDICE;
    }

    public static String getTIME_ZONE() {
        return TIME_ZONE;
    }

}
