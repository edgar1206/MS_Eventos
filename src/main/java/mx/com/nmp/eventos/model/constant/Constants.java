package mx.com.nmp.eventos.model.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {

    private static String HOST;
    private static String USERNAME;
    private static String PASSWORD;
    private static String EXCHANGE;
    private static String QUEUE;
    private static String ROUTING;

    public Constants(@Value("${spring.rabbitmq.host}") String HOST,
                     @Value("${spring.rabbitmq.username}") String USERNAME,
                     @Value("${spring.rabbitmq.password}") String PASSWORD,
                     @Value("${spring.rabbitmq.exchange}") String EXCHANGE,
                     @Value("${spring.rabbitmq.queue}") String QUEUE,
                     @Value("${spring.rabbitmq.routingkey}") String ROUTING)
    {

        Constants.HOST = HOST;
        Constants.USERNAME = USERNAME;
        Constants.PASSWORD = PASSWORD;
        Constants.EXCHANGE = EXCHANGE;
        Constants.QUEUE = QUEUE;
        Constants.ROUTING = ROUTING;

    }

    public static String getHOST() {
        return HOST;
    }

    public static String getUSERNAME() {
        return USERNAME;
    }

    public static String getPASSWORD() {
        return PASSWORD;
    }

    public static String getEXCHANGE() {
        return EXCHANGE;
    }

    public static String getQUEUE() {
        return QUEUE;
    }

    public static String getROUTING() {
        return ROUTING;
    }

}
