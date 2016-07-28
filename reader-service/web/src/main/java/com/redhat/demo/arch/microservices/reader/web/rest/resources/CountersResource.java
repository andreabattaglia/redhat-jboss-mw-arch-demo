/*
 *
 */
package com.redhat.demo.arch.microservices.reader.web.rest.resources;

import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;

import com.redhat.demo.arch.microservices.reader.ejb.services.impl.CountersServiceBean;

/**
 * The Class CountersResource.
 */
@Path("counters")
@RequestScoped
public class CountersResource {

    /** The log. */
    @Inject
    private Logger LOG;

    @Inject
    private CountersServiceBean countersService;

    /**
     * Simple test method.
     */
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Integer> getCounters() {
        return countersService.getCounters();
    }
}
