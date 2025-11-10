# Project Requirement Proposal (PRP)
<!-- Adapted from https://github.com/Wirasm/PRPs-agentic-eng/tree/development/PRPs -->

You are a senior software engineer.
Use the information below to implement a new feature or improvement in this software project.

## Context

**Situation**: The project "CampusCoffee" needs an automatic way of importing POS-Data based on an existing https://www.openstreetmap.org entry. You will plan, engineer and implement the feature based on the environment data given so that it works with the already existent structure.

## Goal

**Feature Goal**: [import feature for POS-Data read from specified openstreetmap entrys to update or expand the database]

**Deliverable**: [e.g. https://www.openstreetmap.org/api/0.6/node/5589879349]

**Success Definition**: [adding a fitting link to a curl command enables the user to automatically add all the important info without doing that manually. In short automization of e.g. the command: curl --header "Content-Type: application/json" --request POST --data '{"name":"New Café","description":"Description","type":"CAFE","campus":"ALTSTADT","street":"Hauptstraße","houseNumber":"100","postalCode":69117,"city":"Heidelberg"}' http://localhost:8080/api/pos]

## User Persona (if applicable)

**Target User**: [Specific user type: end user, user expanding the data base of the app e.g. student at the local university]

**Use Case**: [When somebody wants to add a new POS e.g. new Café that is currently not in the Database or the Data about an already existent POS has to be updated]

**User Journey**: [the user copies an openstreetmap link then connects to the application selects update / add information and pastes the link. Then executes and the data is automatically saved in the predetermined structure]

**Pain Points Addressed**: [the high expense of time needed to manually create an entry]

## Why

- [better accessibility and faster addition of data]
- [Integration with existing features]
- [easier user handling of the tool]

## What

[requirements stated ahead]

### Success Criteria

- [new entry] [added to database]
- [existing entry] [replaced with current information]
- [invalid entry] [ignored and returning an error message including example usage]

## Documentation & References

MUST READ - Include the following information in your context window.

The `README.md` file at the root of the project contains setup instructions and example API calls.

This Java Spring Boot application is structured as a multi-module Maven project following the ports-and-adapters architectural pattern.
There are the following submodules:

`api` - Maven submodule for controller adapter.

`application` - Maven submodule for Spring Boot application, test data import, and system tests.

`data` - Maven submodule for data adapter.

`domain` - Maven submodule for domain model, main business logic, and ports.
