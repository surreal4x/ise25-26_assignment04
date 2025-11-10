package de.seuhd.campuscoffee.domain.impl;

import de.seuhd.campuscoffee.domain.exceptions.DuplicatePosNameException;
import de.seuhd.campuscoffee.domain.exceptions.OsmNodeMissingFieldsException;
import de.seuhd.campuscoffee.domain.exceptions.OsmNodeNotFoundException;
import de.seuhd.campuscoffee.domain.model.CampusType;
import de.seuhd.campuscoffee.domain.model.OsmNode;
import de.seuhd.campuscoffee.domain.model.Pos;
import de.seuhd.campuscoffee.domain.exceptions.PosNotFoundException;
import de.seuhd.campuscoffee.domain.model.PosType;
import de.seuhd.campuscoffee.domain.ports.OsmDataService;
import de.seuhd.campuscoffee.domain.ports.PosDataService;
import de.seuhd.campuscoffee.domain.ports.PosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Implementation of the POS service that handles business logic related to POS entities.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PosServiceImpl implements PosService {
    private final PosDataService posDataService;
    private final OsmDataService osmDataService;

    @Override
    public void clear() {
        log.warn("Clearing all POS data");
        posDataService.clear();
    }

    @Override
    public @NonNull List<Pos> getAll() {
        log.debug("Retrieving all POS");
        return posDataService.getAll();
    }

    @Override
    public @NonNull Pos getById(@NonNull Long id) throws PosNotFoundException {
        log.debug("Retrieving POS with ID: {}", id);
        return posDataService.getById(id);
    }

    @Override
    public @NonNull Pos upsert(@NonNull Pos pos) throws PosNotFoundException {
        if (pos.id() == null) {
            // Create new POS
            log.info("Creating new POS: {}", pos.name());
            return performUpsert(pos);
        } else {
            // Update existing POS
            log.info("Updating POS with ID: {}", pos.id());
            // POS ID must be set
            Objects.requireNonNull(pos.id());
            // POS must exist in the database before the update
            posDataService.getById(pos.id());
            return performUpsert(pos);
        }
    }

    @Override
    public @NonNull Pos importFromOsmNode(@NonNull Long nodeId) throws OsmNodeNotFoundException {
        log.info("Importing POS from OpenStreetMap node {}...", nodeId);

        // Fetch the OSM node data using the port
        OsmNode osmNode = osmDataService.fetchNode(nodeId);

        // Convert OSM node to POS domain object and upsert it
        // TODO: Implement the actual conversion (the response is currently hard-coded).
        Pos savedPos = upsert(convertOsmNodeToPos(osmNode));
        log.info("Successfully imported POS '{}' from OSM node {}", savedPos.name(), nodeId);

        return savedPos;
    }

    /**
     * Converts an OSM node to a POS domain object.
     */
    private @NonNull Pos convertOsmNodeToPos(@NonNull OsmNode osmNode) {
        // First validate all required fields are present
        if (!osmNode.isValidPos()) {
            throw new OsmNodeMissingFieldsException(osmNode.nodeId());
        }

        // Extract all required fields using the helper methods
        String name = osmNode.getName();
        String street = osmNode.getStreet();
        String houseNumber = osmNode.getHouseNumber();
        String postalCodeStr = osmNode.getPostalCode();
        String city = osmNode.getCity();

        // Additional validation for name since it's crucial for our domain
        if (name == null || name.trim().isEmpty()) {
            log.warn("OSM node {} has an empty name tag", osmNode.nodeId());
            throw new OsmNodeMissingFieldsException(osmNode.nodeId());
        }
        
        int postalCode;
        try {
            postalCode = Integer.parseInt(postalCodeStr);
        } catch (NumberFormatException e) {
            throw new OsmNodeMissingFieldsException(osmNode.nodeId());
        }
        
        // Determine the POS type based on OSM tags
        PosType type = determinePosType(osmNode);
        
        // Determine campus based on coordinates
        CampusType campus = determineCampus(osmNode.latitude(), osmNode.longitude());
        
        // Build a comprehensive description from available tags
        String description = buildDescription(osmNode);
        
        if (description.isEmpty()) {
            description = name;
        }

        return Pos.builder()
                .name(name)
                .description(description)
                .type(type)
                .campus(campus)
                .street(street)
                .houseNumber(houseNumber)
                .postalCode(postalCode)
                .city(city)
                .osmNodeId(osmNode.nodeId())
                .build();
    }

    /**
     * Determines the POS type based on OSM tags.
     * @param osmNode The OSM node containing the tags
     * @return The determined POS type
     */
    private PosType determinePosType(OsmNode osmNode) {
        String vendingTag = osmNode.getTag("vending");
        String shopTag = osmNode.getTag("shop");
        String amenityTag = osmNode.getTag("amenity");

        if (vendingTag != null && vendingTag.contains("coffee")) {
            return PosType.VENDING_MACHINE;
        } else if ("bakery".equals(shopTag)) {
            return PosType.BAKERY;
        } else if ("cafeteria".equals(amenityTag)) {
            return PosType.CAFETERIA;
        }

        // Default to CAFE if no specific type could be determined
        return PosType.CAFE;
    }

    /**
     * Determines the campus type based on coordinates.
     * Current implementation defaults to ALTSTADT, but could be extended
     * to use actual coordinate boundaries for different campuses.
     * 
     * @param latitude The location's latitude
     * @param longitude The location's longitude
     * @return The determined campus type
     */
    private CampusType determineCampus(Double latitude, Double longitude) {
        // TODO: Implement actual coordinate-based campus determination
        // For now, default to ALTSTADT
        return CampusType.ALTSTADT;
    }

    /**
     * Builds a comprehensive description from available OSM tags.
     * @param osmNode The OSM node containing the tags
     * @return A description string built from relevant tags
     */
    private String buildDescription(OsmNode osmNode) {
        StringBuilder description = new StringBuilder();

        // Add operator information if available
        String operator = osmNode.getTag("operator");
        if (operator != null) {
            description.append(operator).append(" ");
        }

        // Add cuisine information if available
        String cuisine = osmNode.getTag("cuisine");
        if (cuisine != null) {
            description.append("serving ").append(cuisine).append(" ");
        }

        // Add opening hours if available
        String hours = osmNode.getTag("opening_hours");
        if (hours != null) {
            description.append("(Open: ").append(hours).append(") ");
        }

        // Use explicit description tag if available
        String explicitDesc = osmNode.getTag("description");
        if (explicitDesc != null) {
            if (description.length() > 0) {
                description.append("- ");
            }
            description.append(explicitDesc);
        }

        return description.toString().trim();
    }

    /**
     * Performs the actual upsert operation with consistent error handling and logging.
     * Database constraint enforces name uniqueness - data layer will throw DuplicatePosNameException if violated.
     * JPA lifecycle callbacks (@PrePersist/@PreUpdate) set timestamps automatically.
     *
     * @param pos the POS to upsert
     * @return the persisted POS with updated ID and timestamps
     * @throws DuplicatePosNameException if a POS with the same name already exists
     */
    private @NonNull Pos performUpsert(@NonNull Pos pos) throws DuplicatePosNameException {
        try {
            Pos upsertedPos = posDataService.upsert(pos);
            log.info("Successfully upserted POS with ID: {}", upsertedPos.id());
            return upsertedPos;
        } catch (DuplicatePosNameException e) {
            log.error("Error upserting POS '{}': {}", pos.name(), e.getMessage());
            throw e;
        }
    }
}
