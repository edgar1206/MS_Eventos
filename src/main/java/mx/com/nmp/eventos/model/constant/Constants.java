package mx.com.nmp.eventos.model.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {

    private String ELASTIC_HOST;
    private String ELASTIC_USER;
    private String ELASTIC_IN;
    private String ELASTIC_PROTOCOL;
    private int ELASTIC_PORT;
    private String INDICE;

    public Constants(@Value("${elasticsearch.host}") String ELASTIC_HOST,
                     @Value("${elasticsearch.protocol}") String ELASTIC_PROTOCOL,
                     @Value("${elasticsearch.port}") int ELASTIC_PORT,
                     @Value("${elasticsearch.user}") String ELASTIC_USER,
                     @Value("${elasticsearch.password}") String ELASTIC_IN,
                     @Value("${index.elastic}") String INDICE)

    {

        this.ELASTIC_HOST = ELASTIC_HOST;
        this.ELASTIC_USER = ELASTIC_USER;
        this.ELASTIC_IN = ELASTIC_IN;
        this.INDICE = INDICE;

    }

    public String getELASTIC_HOST() {
        return ELASTIC_HOST;
    }

    public String getELASTIC_USER() {
        return ELASTIC_USER;
    }

    public String getELASTIC_IN() {
        return ELASTIC_IN;
    }

    public String getELASTIC_PROTOCOL() {
        return ELASTIC_PROTOCOL;
    }

    public int getELASTIC_PORT() {
        return ELASTIC_PORT;
    }

    public String getINDICE() {
        return INDICE;
    }
}
