/*
 *
 */
package com.redhat.demo.arch.microservices.reader.web.rest.resources;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;

import com.redhat.demo.arch.microservices.reader.ejb.services.impl.HistoryQueryServiceBean;

/**
 * The Class CountersResource.
 */
@Path("query")
@RequestScoped
public class QueryHistoryResource {

    /** The log. */
    @Inject
    private Logger LOG;

    @Inject
    private HistoryQueryServiceBean historyQueryService;

    /**
     * Simple test method.
     */
    @GET
    @Path("")
    public String queryHistory(@QueryParam("minutes") Integer minutes) {
        if ((minutes != null) && (minutes < 0)) {
            String errString = "negative value not accetped: " + minutes;
            LOG.error(errString);
            return errString;
        }
        try {
            return historyQueryService.getCountersByMinutes(minutes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
