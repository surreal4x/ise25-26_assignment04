package de.seuhd.campuscoffee.data.impl;

import de.seuhd.campuscoffee.domain.exceptions.OsmNodeNotFoundException;
import de.seuhd.campuscoffee.domain.model.OsmNode;
import de.seuhd.campuscoffee.domain.ports.OsmDataService;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Collections;

/**
 * OSM import service.
 */
@Service
@Slf4j
class OsmDataServiceImpl implements OsmDataService {
    private static final String OSM_API_URL = "https://api.openstreetmap.org/api/0.6/node/%d.json";
    private final RestTemplate restTemplate;

    public OsmDataServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Fetches node data from OpenStreetMap API.
     *
     * @param nodeId The OpenStreetMap node ID to fetch
     * @return An OsmNode containing the node's data
     * @throws OsmNodeNotFoundException if the node doesn't exist or cannot be fetched
     */
    @Override
    public @NonNull OsmNode fetchNode(@NonNull Long nodeId) throws OsmNodeNotFoundException {
        Objects.requireNonNull(nodeId, "Node ID cannot be null");
        log.info("Fetching OSM node {} from API", nodeId);
        
        try {
            String url = String.format(OSM_API_URL, nodeId);
            OsmApiResponse response = restTemplate.getForObject(url, OsmApiResponse.class);
            
            if (response == null || response.elements == null || response.elements.isEmpty()) {
                log.warn("No data found for OSM node {}", nodeId);
                throw new OsmNodeNotFoundException(nodeId);
            }

            OsmApiNode node = response.elements.get(0);
            
            // Validate required node data
            if (node.id == null || node.lat == null || node.lon == null || node.tags == null) {
                log.error("Invalid data structure received for OSM node {}", nodeId);
                throw new OsmNodeNotFoundException(nodeId);
            }
            
            // Create a defensive copy of the tags map
            Map<String, String> tags = Collections.unmodifiableMap(new HashMap<>(node.tags));
            
            return OsmNode.builder()
                    .nodeId(node.id)
                    .latitude(node.lat)
                    .longitude(node.lon)
                    .tags(tags)
                    .build();
        } catch (Exception e) {
            log.error("Failed to fetch OSM node {}: {}", nodeId, e.getMessage());
            throw new OsmNodeNotFoundException(nodeId, e);
        }
    }

    private record OsmApiResponse(
        List<OsmApiNode> elements
    ) {}

    private record OsmApiNode(
        Long id,
        Double lat,
        Double lon,
        Map<String, String> tags
    ) {}
}
