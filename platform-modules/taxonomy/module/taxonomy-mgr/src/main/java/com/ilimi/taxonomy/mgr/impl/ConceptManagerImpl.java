package com.ilimi.taxonomy.mgr.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.ilimi.graph.common.Request;
import com.ilimi.graph.common.Response;
import com.ilimi.graph.common.dto.BaseValueObjectList;
import com.ilimi.graph.common.dto.BooleanValue;
import com.ilimi.graph.common.dto.Identifier;
import com.ilimi.graph.common.dto.StringValue;
import com.ilimi.graph.common.exception.ClientException;
import com.ilimi.graph.dac.enums.GraphDACParams;
import com.ilimi.graph.dac.enums.RelationTypes;
import com.ilimi.graph.dac.model.Graph;
import com.ilimi.graph.dac.model.Node;
import com.ilimi.graph.engine.router.GraphEngineManagers;
import com.ilimi.taxonomy.enums.TaxonomyAPIParams;
import com.ilimi.taxonomy.enums.TaxonomyErrorCodes;
import com.ilimi.taxonomy.mgr.IConceptManager;

@Component
public class ConceptManagerImpl extends BaseManager implements IConceptManager {

    private static Logger LOGGER = LogManager.getLogger(IConceptManager.class.getName());

    @SuppressWarnings("unchecked")
    @Override
    public Response findAll(String taxonomyId) {
        if (StringUtils.isBlank(taxonomyId))
            throw new ClientException(TaxonomyErrorCodes.ERR_TAXONOMY_BLANK_TAXONOMY_ID.name(), "Taxonomy Id is blank");
        LOGGER.info("Find All Concepts : " + taxonomyId);
        Request request = getRequest(taxonomyId, GraphEngineManagers.SEARCH_MANAGER, "getNodesByObjectType",
                GraphDACParams.OBJECT_TYPE.name(), new StringValue("Concept"));
        request.put(GraphDACParams.GET_TAGS.name(), new BooleanValue(true));
        Response findRes = getResponse(request, LOGGER);
        Response response = copyResponse(findRes);
        if (checkError(response))
            return response;
        BaseValueObjectList<Node> nodes = (BaseValueObjectList<Node>) findRes.get(GraphDACParams.NODE_LIST.name());
        if (null != nodes && null != nodes.getValueObjectList() && !nodes.getValueObjectList().isEmpty()) {
            response.put(TaxonomyAPIParams.CONCEPTS.name(), nodes);
        }
        return response;
    }

    @Override
    public Response find(String id, String taxonomyId) {
        if (StringUtils.isBlank(taxonomyId))
            throw new ClientException(TaxonomyErrorCodes.ERR_TAXONOMY_BLANK_TAXONOMY_ID.name(), "Taxonomy Id is blank");
        if (StringUtils.isBlank(id))
            throw new ClientException(TaxonomyErrorCodes.ERR_TAXONOMY_BLANK_CONCEPT_ID.name(), "Concept Id is blank");
        Request request = getRequest(taxonomyId, GraphEngineManagers.SEARCH_MANAGER, "getDataNode", GraphDACParams.NODE_ID.name(),
                new StringValue(id));
        request.put(GraphDACParams.GET_TAGS.name(), new BooleanValue(true));
        Response getNodeRes = getResponse(request, LOGGER);
        Response response = copyResponse(getNodeRes);
        if (checkError(response)) {
            return response;
        }
        Node node = (Node) getNodeRes.get(GraphDACParams.NODE.name());
        if (null != node)
            response.put(TaxonomyAPIParams.CONCEPT.name(), node);
        return response;
    }

    @Override
    public Response create(String taxonomyId, Request request) {
        if (StringUtils.isBlank(taxonomyId))
            throw new ClientException(TaxonomyErrorCodes.ERR_TAXONOMY_BLANK_TAXONOMY_ID.name(), "Taxonomy Id is blank");
        Node concept = (Node) request.get(TaxonomyAPIParams.CONCEPT.name());
        if (null == concept)
            throw new ClientException(TaxonomyErrorCodes.ERR_TAXONOMY_BLANK_CONCEPT.name(), "Concept Object is blank");
        request.setManagerName(GraphEngineManagers.NODE_MANAGER);
        request.setOperation("createDataNode");
        return getResponse(request, LOGGER);
    }

    @Override
    public Response update(String id, String taxonomyId, Request request) {
        if (StringUtils.isBlank(taxonomyId))
            throw new ClientException(TaxonomyErrorCodes.ERR_TAXONOMY_BLANK_TAXONOMY_ID.name(), "Taxonomy Id is blank");
        if (StringUtils.isBlank(id))
            throw new ClientException(TaxonomyErrorCodes.ERR_TAXONOMY_BLANK_CONCEPT_ID.name(), "Concept Id is blank");
        Node concept = (Node) request.get(TaxonomyAPIParams.CONCEPT.name());
        if (null == concept)
            throw new ClientException(TaxonomyErrorCodes.ERR_TAXONOMY_BLANK_CONCEPT.name(), "Concept Object is blank");
        request.setManagerName(GraphEngineManagers.NODE_MANAGER);
        request.setOperation("updateDataNode");
        return getResponse(request, LOGGER);
    }

    @Override
    public Response delete(String id, String taxonomyId) {
        if (StringUtils.isBlank(taxonomyId))
            throw new ClientException(TaxonomyErrorCodes.ERR_TAXONOMY_BLANK_TAXONOMY_ID.name(), "Taxonomy Id is blank");
        if (StringUtils.isBlank(id))
            throw new ClientException(TaxonomyErrorCodes.ERR_TAXONOMY_BLANK_CONCEPT_ID.name(), "Concept Id is blank");
        Request request = getRequest(taxonomyId, GraphEngineManagers.NODE_MANAGER, "deleteDataNode", GraphDACParams.NODE_ID.name(),
                new StringValue(id));
        return getResponse(request, LOGGER);
    }

    @Override
    public Response deleteRelation(String startConceptId, String relationType, String endConceptId, String taxonomyId) {
        if (StringUtils.isBlank(taxonomyId))
            throw new ClientException(TaxonomyErrorCodes.ERR_TAXONOMY_BLANK_TAXONOMY_ID.name(), "Taxonomy Id is blank");
        if (StringUtils.isBlank(startConceptId) || StringUtils.isBlank(relationType) || StringUtils.isBlank(endConceptId))
            throw new ClientException(TaxonomyErrorCodes.ERR_TAXONOMY_UPDATE_CONCEPT.name(),
                    "Start Concept Id, Relation Type and End Concept Id are required to delete relation");
        Request request = getRequest(taxonomyId, GraphEngineManagers.GRAPH_MANAGER, "removeRelation");
        request.put(GraphDACParams.START_NODE_ID.name(), new StringValue(startConceptId));
        request.put(GraphDACParams.RELATION_TYPE.name(), new StringValue(relationType));
        request.put(GraphDACParams.END_NODE_ID.name(), new StringValue(endConceptId));
        return getResponse(request, LOGGER);
    }

    @Override
    public Response getConcepts(String id, String relationType, int depth, String taxonomyId) {
        if (StringUtils.isBlank(taxonomyId))
            throw new ClientException(TaxonomyErrorCodes.ERR_TAXONOMY_BLANK_TAXONOMY_ID.name(), "Taxonomy Id is blank");
        if (StringUtils.isBlank(id))
            throw new ClientException(TaxonomyErrorCodes.ERR_TAXONOMY_BLANK_CONCEPT_ID.name(), "Concept Id is blank");
        if (StringUtils.isBlank(relationType))
            relationType = RelationTypes.HIERARCHY.relationName();
        Request request = getRequest(taxonomyId, GraphEngineManagers.SEARCH_MANAGER, "getSubGraph");
        request.put(GraphDACParams.START_NODE_ID.name(), new StringValue(id));
        request.put(GraphDACParams.RELATION_TYPE.name(), new StringValue(relationType));
        if (depth > 0)
            request.put(GraphDACParams.DEPTH.name(), new Identifier(depth));
        Response findRes = getResponse(request, LOGGER);
        Response response = copyResponse(findRes);
        if (checkError(response))
            return response;
        Graph graph = (Graph) findRes.get(GraphDACParams.SUB_GRAPH.name());
        if (null != graph && null != graph.getNodes() && !graph.getNodes().isEmpty()) {
            BaseValueObjectList<Node> nodes = new BaseValueObjectList<Node>(graph.getNodes());
            response.put(TaxonomyAPIParams.CONCEPTS.name(), nodes);
        }
        return response;
    }

}
