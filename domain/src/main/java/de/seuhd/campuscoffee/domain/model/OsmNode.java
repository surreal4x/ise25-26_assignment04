package de.seuhd.campuscoffee.domain.model;

import org.jspecify.annotations.NonNull;

/**
 * Represents an OpenStreetMap node with relevant Point of Sale information.
 * This is the domain model for OSM data before it is converted to a POS object.
 */
public record OsmNode(
    Long nodeId,
    Double latitude,
    Double longitude,
    java.util.Map<String, String> tags
) {
    /**
     * Creates a new OSM node builder.
     * @return A new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for OsmNode.
     */
    public static class Builder {
        private Long nodeId;
        private Double latitude;
        private Double longitude;
        private java.util.Map<String, String> tags = new java.util.HashMap<>();

        public Builder nodeId(Long nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public Builder latitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder tags(java.util.Map<String, String> tags) {
            this.tags = new java.util.HashMap<>(tags);
            return this;
        }

        public OsmNode build() {
            if (nodeId == null || latitude == null || longitude == null || tags == null) {
                throw new IllegalStateException("All fields must be non-null");
            }
            return new OsmNode(nodeId, latitude, longitude, java.util.Collections.unmodifiableMap(tags));
        }
    }
    /**
     * Checks if this node has all required fields to create a valid POS.
     */
    public boolean isValidPos() {
        return hasValidName() &&
               hasValidAddress();
    }

    /**
     * Checks if the node has a valid name.
     */
    private boolean hasValidName() {
        String name = tags.get("name");
        return name != null && !name.trim().isEmpty();
    }

    /**
     * Checks if the node has all required address fields.
     */
    private boolean hasValidAddress() {
        return tags.containsKey("addr:street") &&
               tags.containsKey("addr:housenumber") &&
               tags.containsKey("addr:postcode") &&
               tags.containsKey("addr:city");
    }

    /**
     * Gets a tag value or returns null if not present.
     */
    /**
     * Gets a tag value or returns null if not present.
     */
    public String getTag(String key) {
        return tags.get(key);
    }

    /**
     * Gets the name of the node from the tags.
     */
    public String getName() {
        return tags.get("name");
    }

    /**
     * Gets the street address from the tags.
     */
    public String getStreet() {
        return tags.get("addr:street");
    }

    /**
     * Gets the house number from the tags.
     */
    public String getHouseNumber() {
        return tags.get("addr:housenumber");
    }

    /**
     * Gets the postal code from the tags.
     */
    public String getPostalCode() {
        return tags.get("addr:postcode");
    }

    /**
     * Gets the city from the tags.
     */
    public String getCity() {
        return tags.get("addr:city");
    }
}
