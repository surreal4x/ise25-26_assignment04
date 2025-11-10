package de.seuhd.campuscoffee.domain.impl;

import de.seuhd.campuscoffee.domain.services.OsmImportService;
import de.seuhd.campuscoffee.domain.model.Pos;
import de.seuhd.campuscoffee.domain.model.OsmNode;
import de.seuhd.campuscoffee.domain.ports.PosService;
import de.seuhd.campuscoffee.domain.ports.OsmDataService;
import de.seuhd.campuscoffee.domain.exceptions.OsmNodeNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the OSM import service that uses the domain ports
 * to fetch OSM data and create/update POS entities.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OsmImportServiceImpl implements OsmImportService {
    private final PosService posService;
    private final OsmDataService osmDataService;

    @Override
    public boolean importFromOsmNode(long nodeId) {
        try {
            log.info("Importing OSM node {} as POS", nodeId);
            OsmNode osmNode = osmDataService.fetchNode(nodeId);
            
            if (!osmNode.isValidPos()) {
                log.warn("OSM node {} does not have required fields for POS", nodeId);
                return false;
            }

            Pos pos = posService.importFromOsmNode(nodeId);
            log.info("Successfully imported OSM node {} as POS {}", nodeId, pos.name());
            return true;
        } catch (OsmNodeNotFoundException e) {
            log.warn("OSM node {} not found", nodeId);
            return false;
        } catch (Exception e) {
            log.error("Failed to import OSM node {}: {}", nodeId, e.getMessage());
            return false;
        }
    }

    @Override
    public List<Long> importFromOsmNodes(List<Long> nodeIds) {
        return nodeIds.stream()
                .filter(nodeId -> nodeId != null && importFromOsmNode(nodeId))
                .collect(Collectors.toList());
    }
}