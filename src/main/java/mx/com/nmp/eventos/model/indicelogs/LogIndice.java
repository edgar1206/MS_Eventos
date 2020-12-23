package mx.com.nmp.eventos.model.indicelogs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Document(indexName = "#{@environment.getProperty('index.elastic')}", type = "doc")
public class LogIndice {

    @Id
    private String _id;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date startTime;
    @JsonProperty("@timestamp")
    private Date timeStamp;
    private Message message;
    @JsonProperty("@version")
    private String version;
    private GeoIP geoip;
    private String pid;
    private String categoryName;
    private String level;
    private HostName host;

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public GeoIP getGeoip() {
        return geoip;
    }

    public void setGeoip(GeoIP geoip) {
        this.geoip = geoip;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public HostName getHost() {
        return host;
    }

    public void setHost(HostName host) {
        this.host = host;
    }
}
