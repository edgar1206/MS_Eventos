package mx.com.nmp.eventos.model.nr;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class EventoDto {
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

    public String getEventType() {
        return eventType;
    }

    public String getEventLevel() {
        return eventLevel;
    }

    public String getEventCategory() {
        return eventCategory;
    }

    public String getEventAction() {
        return eventAction;
    }


    public String getEventDescription() {
        return eventDescription;
    }

    public String getSeverity() {
        return severity;
    }

    public String getEventResource() {
        return eventResource;
    }

    public Date getTimeGenerated() {
        return timeGenerated;
    }

    public String getEventPhase() {
        return eventPhase;
    }

    public String getResolutionTower() {
        return resolutionTower;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getConfigurationElement() {
        return configurationElement;
    }

}
