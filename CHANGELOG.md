# Changelog

All notable changes to this project will be documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## Added
- New database migration `V2__add_osm_node_id.sql` to track OpenStreetMap node IDs
- New domain model `OsmNode` with comprehensive mapping fields for OSM data
- OpenStreetMap data fetching implementation in `OsmDataServiceImpl`
- Smart mapping from OSM tags to POS types (cafe, bakery, vending machine)
- Automatic type inference based on OSM amenity tags
- Enhanced error handling with improved `OsmNodeNotFoundException`

## Changed
- Fix broken test case in `PosSystemTests` (assignment 3)
- Extend GitHub Actions triggers to include pushes to feature branches (assignment 3)
- Update `PosEntity` to include OSM node ID with unique constraint
- Enhance `PosService` with real OSM data conversion logic
- Refactor `OsmImportServiceImpl` to use proper domain ports
- Improve error messages for missing or invalid OSM data
- Add example of new OSM import endpoint to `README` file
- Strengthen architectural boundaries in import service implementation
- Enhance `OsmNode` model with immutable tags and comprehensive validation
- Add convenience methods for accessing OSM tag data
- Implement custom builder with strict validation for `OsmNode`
- Improve OSM to POS conversion with smart tag processing
- Add structured description building from multiple OSM tags
- Add extensible campus determination based on coordinates

## Fixed
- Replace stub implementation with actual OSM API integration
- Proper handling of missing or invalid postal codes
- Enhanced description generation from multiple OSM tags

## Security
- Added validation for OSM node data completeness
- Input sanitization for OSM tag values
- Added defensive copying of OSM tags to prevent modification
- Enhanced null safety checks for API responses
- Added immutable tag collections for thread safety

## [2025-11-10]
### Fixed
- Fixed JSpecify annotations in OsmNode record
- Resolved Lombok annotation processing issues
- Added explicit version management for Lombok
- Fixed builder pattern implementation for domain models
- Improved error handling in service implementations
- Enhanced documentation with detailed OSM feature usage guide

### Added
- Detailed documentation for OSM import feature requirements
- Example of all supported OSM tags in README
- Explicit Lombok version (1.18.30) for better build reproducibility
