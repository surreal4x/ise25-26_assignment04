package de.seuhd.campuscoffee.domain.services;

import java.util.List;
import java.util.Optional;

public interface OsmImportService {
    /**
     * Imports a POS from an OSM node
     * @param nodeId The OSM node ID
     * @return true if import was successful
     */
    boolean importFromOsmNode(long nodeId);

    /**
     * Batch imports multiple POS from OSM nodes
     * @param nodeIds List of OSM node IDs
     * @return List of successfully imported node IDs
     */
    List<Long> importFromOsmNodes(List<Long> nodeIds);
}