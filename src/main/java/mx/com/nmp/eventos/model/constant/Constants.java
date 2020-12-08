package mx.com.nmp.eventos.model.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {

    public static String ELASTIC_HOST;
    public static String ELASTIC_USER;
    public static String ELASTIC_IN;
    public static String ELASTIC_PROTOCOL;
    public static int ELASTIC_PORT;

    public Constants(@Value("${elasticsearch.host}") String ELASTIC_HOST,
                     @Value("${elasticsearch.protocol}") String ELASTIC_PROTOCOL,
                     @Value("${elasticsearch.port}") int ELASTIC_PORT,
                     @Value("${elasticsearch.user}") String ELASTIC_USER,
                     @Value("${elasticsearch.password}") String ELASTIC_IN)

    {

        Constants.ELASTIC_HOST = ELASTIC_HOST;
        Constants.ELASTIC_PROTOCOL = ELASTIC_PROTOCOL;
        Constants.ELASTIC_PORT = ELASTIC_PORT;
        Constants.ELASTIC_USER = ELASTIC_USER;
        Constants.ELASTIC_IN = ELASTIC_IN;

    }

}
