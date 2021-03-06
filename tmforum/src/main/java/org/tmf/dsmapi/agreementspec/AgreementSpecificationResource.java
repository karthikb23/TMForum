package org.tmf.dsmapi.agreementspec;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.tmf.dsmapi.event.AgreementEventEnum;
import org.tmf.dsmapi.event.EventPublisher;
import org.tmf.dsmapi.agreement.model.AgreementSpecification;
import org.tmf.dsmapi.commons.exceptions.BadUsageException;
import org.tmf.dsmapi.commons.exceptions.UnknownResourceException;
import org.tmf.dsmapi.commons.jaxrs.PATCH;
import org.tmf.dsmapi.commons.utils.TMFFilter;
import org.tmf.dsmapi.commons.utils.URIParser;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by atinsingh on 4/6/17.
 */

@Stateless
@Path("/agreementManagement/agreementSpecification")
public class AgreementSpecificationResource {

    public static Logger logger = Logger.getLogger(AgreementSpecificationResource.class.getName());
    @EJB
    AgreementSpecificationFacade agreementSpecificationFacade;
    @EJB
    EventPublisher<AgreementSpecification> eventPublisher;

    /**
     * Default constructor
     */

    public AgreementSpecificationResource() {
    }

    /**
     * Method Maps to url  -  POST http://host:port:/DSAgreement/agreementManagement/agreementSpecification
     *
     * @param specification
     * @param uriInfo
     * @return
     * @throws BadUsageException
     * @throws UnknownResourceException
     */

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response create(AgreementSpecification specification, @Context UriInfo uriInfo) throws BadUsageException {
        //Create entity
        Response response = null;
        try {
            // Check if all mandatory items are present in incoming JSON load, if mandatory fields are missing this
            // method  will throw a BadUsageException exception, which should be reported back to the user
            agreementSpecificationFacade.checkCreation(specification);

            //This line will only be called if checkCreation has not thrown any exception, method will go and create a
            //new entiry in the database.
            agreementSpecificationFacade.create(specification);

            //In-case where ID is not sent in the JSON payload, Persistence layer will create a new ID based on the
            //sequence, enity HREF should be updated with latest ID.
            //below method will set attribute href and persist in database.
            specification.setHref(uriInfo.getAbsolutePath() + "/" + specification.getId());
            agreementSpecificationFacade.edit(specification);

            //Publish event for agreementspec creation;
            eventPublisher.generateEventNotification(specification, new Date(),
                    AgreementEventEnum.AgreementSpecCreationNotification);

            //Construct back the response with 204 and return the entity created by the facade.
            response = Response.status(Response.Status.CREATED).entity(specification).build();

        } catch (BadUsageException ex) {
            response = Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception ex) {
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }


    /**
     * This function will return the complete collection and will match the end point
     * http://host:port:/DSAgreement/agreementManagement/agreementSpecification?fields=field1,field2
     *
     * @param uriInfo
     * @return
     * @throws BadUsageException
     * @throws Exception
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response find(@Context UriInfo uriInfo) throws BadUsageException, UnknownResourceException, Exception {
        //get all queryparams
        MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();

        Map<String, List<String>> mutableMap = new HashMap();
        for (Map.Entry<String, List<String>> e : queryParameters.entrySet()) {
            mutableMap.put(e.getKey(), e.getValue());
        }

        //System.out.println(mutableMap.toString());

        // Retrieve fields to filter view
        Set<String> fieldSet = URIParser.getFieldsSelection(mutableMap);

        //Declare the entity object;
        Object entity;

        System.out.println("Query Param Field Set" + fieldSet.toString());
        if (fieldSet.isEmpty()) {
            System.out.println("Query param is empty");
        }

        // construct filter as id,name, serviceCategory, serviceCategory.id, serviceCategory.name...
        if (fieldSet.isEmpty() || fieldSet.contains(URIParser.ALL_FIELDS)) {
            // construct filter as id,name, serviceCategory, serviceCategory.id, serviceCategory.name...
            List<AgreementSpecification> specifications = agreementSpecificationFacade.findAll();
            entity = specifications;
        } else {
            // Retrieve entity
            Set<AgreementSpecification> agreementSpecifications = findByCriteria(mutableMap);
            //Apply filter on the entities
            entity = TMFFilter.applyFilter(agreementSpecifications, fieldSet);
        }

        //Construct the response.
        Response response;
        if (entity != null) {
            response = Response.ok(entity).build();
        } else {
            response = Response.status(Response.Status.NOT_FOUND).build();
        }
        return response;
    }

    /**
     * This function will match the end point
     * https://host:port/DSAgreement/agreementManagement/agreementSpecification/{id}?fields=field1,field2
     * {id} is mandatory, if {id} is not passed, URL will return not found 404
     *
     * @param id
     * @param uriInfo
     * @return
     * @throws BadUsageException
     * @throws UnknownResourceException
     * @throws Exception
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response find(@PathParam("id") String id, @Context UriInfo uriInfo) throws BadUsageException, UnknownResourceException, Exception {

        //Get Query Param will return the multimap with query as par values
        MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();

        Map<String, List<String>> mutableMap = new HashMap();
        for (Map.Entry<String, List<String>> e : queryParameters.entrySet()) {
            mutableMap.put(e.getKey(), e.getValue());
        }

        //System.out.println(mutableMap.toString());

        // fields to filter view

        Set<String> fieldSet = URIParser.getFieldsSelection(mutableMap);

        AgreementSpecification specification = agreementSpecificationFacade.find(id);

        Object entity;


        if (fieldSet.isEmpty() || fieldSet.contains(URIParser.ALL_FIELDS)) {
            // construct filter as id,name, serviceCategory, serviceCategory.id, serviceCategory.name...
            entity = specification;
        } else {


            //Apply filter on the entities
            entity = TMFFilter.applyFilter(specification, fieldSet);
        }

        Response response;
        if (specification != null) {
            response = Response.ok(entity).build();
        } else {
            response = Response.status(Response.Status.NOT_FOUND).build();
        }
        return response;
    }

    /**
     * This function will match the url http://host:port/DSAgreement/agreementManagement/agreementSpecification/{id}
     * function will find the entity associated with it and delete that entity
     *
     * @param id
     * @return
     */
    @DELETE
    @Path("{id}")
    public Response deleteByID(@PathParam("id") String id) throws UnknownResourceException {
        AgreementSpecification specification = agreementSpecificationFacade.find(id);
        agreementSpecificationFacade.remove(id);
        eventPublisher.generateEventNotification(specification, new Date(), AgreementEventEnum.AgreementRemoveNotification);
        return Response.accepted().build();
    }

    /**
     * Function will match the URL
     * PATCH http://host:port/DSAgreement/agreementManagement/agreementSpecification/[id}
     * function will take AgeementSpecification Object JSOn to patch the existing object.
     * to update/patch particular path, please see jsonpatch verion of the function.
     *
     * @param id
     * @param patchObject
     * @return
     * @throws BadUsageException
     * @throws UnknownResourceException
     */
    @PATCH
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response patch(@PathParam("id") String id, AgreementSpecification patchObject) throws BadUsageException, UnknownResourceException {

        logger.log(Level.INFO, "Object patch request is called for the id " + id);

        AgreementSpecification specification = agreementSpecificationFacade.patchObject(id, patchObject);


        Response response;
        if (specification != null) {
            response = Response.ok(specification).build();
        } else {
            logger.log(Level.INFO, "No existing object with Id " + id + "found in database");
            response = Response.status(Response.Status.NOT_FOUND).entity("No object Available for Patching").build();
        }

        return response;
    }


    @PATCH
    @Path("{id}")
    @Consumes("application/json-patch+json")
    public Response patch(@PathParam("id") String id, JsonPatch patch) throws BadUsageException, UnknownResourceException, JsonPatchException {

        //Grab the entity to be patched.
        AgreementSpecification specification = agreementSpecificationFacade.find(id);

        //Define an Object Mapper to convert object into JSON object.
        ObjectMapper mapper = new ObjectMapper();

        logger.log(Level.INFO, "JSON patch request is called for the id " + id);

        //Convert AgreementSpecification to JSON object
        JsonNode node = mapper.convertValue(specification, JsonNode.class);

        final JsonNode patchedNode = patch.apply(node);
        //now use JSONPatch library to do the diff and apply patch
        AgreementSpecification patchObject = mapper.convertValue(patchedNode, AgreementSpecification.class);

        //remove the ID as it's not patchable
        patchObject.setId(null);


        // now check if the object is patchable and apply patch.
        AgreementSpecification entity = agreementSpecificationFacade.patchObject(id, patchObject);

        return Response.status(Response.Status.ACCEPTED).entity(entity).build();


    }


    // return Set of unique elements to avoid List with same elements in case of join
    private Set<AgreementSpecification> findByCriteria(Map<String, List<String>> criteria) throws BadUsageException {

        List<AgreementSpecification> resultList = null;
        if (criteria != null && !criteria.isEmpty()) {
            resultList = agreementSpecificationFacade.findByCriteria(criteria, AgreementSpecification.class);
        } else {
            resultList = agreementSpecificationFacade.findAll();
        }
        if (resultList == null) {
            return new LinkedHashSet<AgreementSpecification>();
        } else {
            return new LinkedHashSet<AgreementSpecification>(resultList);
        }
    }

}
