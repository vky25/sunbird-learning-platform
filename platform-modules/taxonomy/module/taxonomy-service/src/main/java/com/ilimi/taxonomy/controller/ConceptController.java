package com.ilimi.taxonomy.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ilimi.graph.common.Request;
import com.ilimi.graph.common.Response;
import com.ilimi.taxonomy.mgr.IConceptManager;

@Controller
@RequestMapping("/concept")
public class ConceptController extends BaseController {

    private static Logger LOGGER = LogManager.getLogger(ConceptController.class.getName());

    @Autowired
    private IConceptManager conceptManager;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Response> findAll(@RequestParam(value = "taxonomyId", required = true) String taxonomyId) {
        LOGGER.info("FindAll | TaxonomyId: " + taxonomyId);
        try {
            Response response = conceptManager.findAll(taxonomyId);
            LOGGER.info("FindAll | Response: " + response);
            return getResponseEntity(response);
        } catch (Exception e) {
            LOGGER.error("FindAll | Exception: " + e.getMessage(), e);
            return getExceptionResponseEntity(e);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Response> find(@PathVariable(value = "id") String id,
            @RequestParam(value = "taxonomyId", required = true) String taxonomyId) {
        LOGGER.info("Find | TaxonomyId: " + taxonomyId + " | Id: " + id);
        try {
            Response response = conceptManager.find(id, taxonomyId);
            LOGGER.info("Find | Response: " + response);
            return getResponseEntity(response);
        } catch (Exception e) {
            LOGGER.error("Find | Exception: " + e.getMessage(), e);
            return getExceptionResponseEntity(e);
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Response> create(@RequestParam(value = "taxonomyId", required = true) String taxonomyId,
            @RequestBody Request request) {
        LOGGER.info("Create | TaxonomyId: " + taxonomyId + " | Request: " + request);
        try {
            Response response = conceptManager.create(taxonomyId, request);
            LOGGER.info("Create | Response: " + response);
            return getResponseEntity(response);
        } catch (Exception e) {
            LOGGER.error("Create | Exception: " + e.getMessage(), e);
            return getExceptionResponseEntity(e);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    @ResponseBody
    public ResponseEntity<Response> update(@PathVariable(value = "id") String id,
            @RequestParam(value = "taxonomyId", required = true) String taxonomyId, @RequestBody Request request) {
        LOGGER.info("Update | TaxonomyId: " + taxonomyId + " | Id: " + id + " | Request: " + request);
        try {
            Response response = conceptManager.update(id, taxonomyId, request);
            LOGGER.info("Update | Response: " + response);
            return getResponseEntity(response);
        } catch (Exception e) {
            LOGGER.error("Update | Exception: " + e.getMessage(), e);
            return getExceptionResponseEntity(e);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Response> delete(@PathVariable(value = "id") String id,
            @RequestParam(value = "taxonomyId", required = true) String taxonomyId) {
        LOGGER.info("Delete | TaxonomyId: " + taxonomyId + " | Id: " + id);
        try {
            Response response = conceptManager.delete(id, taxonomyId);
            LOGGER.info("Delete | Response: " + response);
            return getResponseEntity(response);
        } catch (Exception e) {
            LOGGER.error("Delete | Exception: " + e.getMessage(), e);
            return getExceptionResponseEntity(e);
        }
    }

    @RequestMapping(value = "/{id1}/{rel}/{id2}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Response> deleteRelation(@PathVariable(value = "id1") String fromConcept,
            @PathVariable(value = "rel") String relationType, @PathVariable(value = "id2") String toConcept,
            @RequestParam(value = "taxonomyId", required = true) String taxonomyId) {
        LOGGER.info("Delete Relation | TaxonomyId: " + taxonomyId + " | StartId: " + fromConcept + " | Relation: " + relationType
                + " | EndId: " + toConcept);
        try {
            Response response = conceptManager.deleteRelation(fromConcept, relationType, toConcept, taxonomyId);
            LOGGER.info("Delete Relation | Response: " + response);
            return getResponseEntity(response);
        } catch (Exception e) {
            LOGGER.error("Delete Relation | Exception: " + e.getMessage(), e);
            return getExceptionResponseEntity(e);
        }
    }

    @RequestMapping(value = "/{id}/{rel}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Response> getConcepts(@PathVariable(value = "id") String id, @PathVariable(value = "rel") String relationType,
            @RequestParam(value = "taxonomyId", required = true) String taxonomyId,
            @RequestParam(value = "depth", required = false, defaultValue = "0") int depth) {
        LOGGER.info("Get Concepts | TaxonomyId: " + taxonomyId + " | Id: " + id + " | Relation: " + relationType + " | Depth: " + depth);
        try {
            Response response = conceptManager.getConcepts(id, relationType, depth, taxonomyId);
            LOGGER.info("Get Concepts | Response: " + response);
            return getResponseEntity(response);
        } catch (Exception e) {
            LOGGER.error("Get Concepts | Exception: " + e.getMessage(), e);
            return getExceptionResponseEntity(e);
        }
    }
}
