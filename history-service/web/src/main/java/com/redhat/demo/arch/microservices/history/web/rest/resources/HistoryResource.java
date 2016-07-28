/*
 *
 */
package com.redhat.demo.arch.microservices.history.web.rest.resources;

import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;

import com.redhat.demo.arch.microservices.history.ejb.services.impl.HistoryCacheServiceBean;

/**
 * The Class HistoryResource.
 */
@Path("history")
@RequestScoped
public class HistoryResource {

    /** The log. */
    @Inject
    private Logger LOG;

    @Inject
    private HistoryCacheServiceBean historyService;

    /**
     * Simple test method.
     */
    @PUT
    @Path("/{payload}")
    public String sendMessage(@PathParam("payload") String payload) {
        if (payload == null) {
            LOG.error("payload is null");
            return "KO";
        }
        historyService.store(payload);
        return "OK";
    }

    /**
     * Simple test method.
     */
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Long> queryHistory(
            @QueryParam("minutes") Integer minutes) {
        if ((minutes != null) && (minutes < 0)) {
            String errString = "negative value not accetped: " + minutes;
            LOG.error(errString);
            throw new RuntimeException(errString);
        }
        return historyService.countHistoryByLastMins(minutes);
    }
}
