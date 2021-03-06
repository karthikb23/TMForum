/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tmf.dsmapi.commons.jaxrs;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.tmf.dsmapi.commons.exceptions.ExceptionType;

@Provider
public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {
    // @Override
    public Response toResponse(JsonMappingException ex) {
        JsonFault error = new JsonFault(ExceptionType.BAD_USAGE_UNKNOWN_VALUE.getInfo(), ex.getMessage());
        return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).entity(error).build();
    }
}
