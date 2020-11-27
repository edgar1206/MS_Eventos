package mx.com.nmp.eventos.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig extends AbstractFactoryBean<RestHighLevelClient> {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchConfig.class);
    private RestHighLevelClient restHighLevelClient;
    @Value("${elasticsearch.host}")
    private String host;
    @Value("${elasticsearch.protocol}")
    private String protocol;
    @Value("${elasticsearch.port}")
    private int port;
    @Value("${elasticsearch.user}")
    private String user;
    @Value("${elasticsearch.password}")
    private String password;

    @Override
    public Class<RestHighLevelClient> getObjectType() {
        return RestHighLevelClient.class;
    }

    @Override
    protected RestHighLevelClient createInstance() throws Exception {
        return buildClient();
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    private RestHighLevelClient buildClient() {
        log.info("conectando con elastic");
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));
        try {
            restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, protocol))
                    .setHttpClientConfigCallback((HttpAsyncClientBuilder httpClientBuilder) -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)));
            log.info("conexion establecida");
        } catch (Exception e) {
            log.info("no se pudo establecer conexion");
        }
        return restHighLevelClient;
    }

    @Override
    public void destroy() {
        try {
            if (restHighLevelClient != null) {
                restHighLevelClient.close();
            }
        } catch (final Exception e) { logger.error("Error closing ElasticSearch client: ", e); }
    }

}