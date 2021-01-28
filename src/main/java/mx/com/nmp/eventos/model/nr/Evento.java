package mx.com.nmp.eventos.model.nr;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import java.util.Date;

@Document(indexName = "#{@environment.getProperty('index.elastic')}", type = "_doc")
public class Evento {

    @Id
    private String idEvent;
    private String eventType;
    private String eventLevel;
    private String eventCategory;
    private String eventAction;
    private String eventDescription;
    private String severity;
    private String eventResource;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ",timezone = "America/Mexico_City")
    private Date timeGenerated;
    private String eventPhase;
    private String resolutionTower;
    private String applicationName;
    private String configurationElement;

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventLevel() {
        return eventLevel;
    }

    public void setEventLevel(String eventLevel) {
        this.eventLevel = eventLevel;
    }

    public String getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }

    public String getEventAction() {
        return eventAction;
    }

    public void setEventAction(String eventAction) {
        this.eventAction = eventAction;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getEventResource() {
        return eventResource;
    }

    public void setEventResource(String eventResource) {
        this.eventResource = eventResource;
    }

    public Date getTimeGenerated() {
        return timeGenerated;
    }

    public void setTimeGenerated(Date timeGenerated) {
        this.timeGenerated = timeGenerated;
    }

    public String getEventPhase() {
        return eventPhase;
    }

    public void setEventPhase(String eventPhase) {
        this.eventPhase = eventPhase;
    }

    public String getResolutionTower() {
        return resolutionTower;
    }

    public void setResolutionTower(String resolutionTower) {
        this.resolutionTower = resolutionTower;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getConfigurationElement() {
        return configurationElement;
    }

    public void setConfigurationElement(String configurationElement) {
        this.configurationElement = configurationElement;
    }

}
