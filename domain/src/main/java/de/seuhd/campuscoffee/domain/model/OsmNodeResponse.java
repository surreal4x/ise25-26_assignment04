package de.seuhd.campuscoffee.domain.model;

import lombok.Data;
import java.util.Map;

@Data
public class OsmNodeResponse {
    private long id;
    private double lat;
    private double lon;
    private Map<String, String> tags;

    public boolean isValidPOS() {
        return tags != null && tags.containsKey("name") && 
               tags.containsKey("addr:street") && tags.containsKey("addr:housenumber") &&
               tags.containsKey("addr:postcode") && tags.containsKey("addr:city");
    }
}