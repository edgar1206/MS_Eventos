package mx.com.nmp.eventos.model.indicelogs;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GeoIP {

    @JsonProperty("country_code2")
    private String countryCode;
    @JsonProperty("region_code")
    private String regionCode;
    private Location location;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
