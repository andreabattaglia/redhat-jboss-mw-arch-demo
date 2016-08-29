/*
 *
 */
package com.redhat.demo.arch.microservices.reader.ejb.services.impl;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.infinispan.client.hotrod.RemoteCache;

/**
 * The Class CountersServiceBean.
 *
 * @author Andrea Battaglia (Red Hat)
 */
@Singleton
@Startup
@LocalBean
@Lock(LockType.READ)
public class CountersServiceBean {
    // /** The log. */
    // @Inject
    // private Logger LOG;

    @EJB
    private CacheManagerServiceBean cacheManagerService;
    private RemoteCache<String, Integer> countersCache;

    /**
     * Inits the.
     */
    @PostConstruct
    private void init() {

        countersCache = cacheManagerService.getCache("Demo_CountersCache");
    }

    public Map<String, Integer> getCounters() {
        return new TreeMap<>(countersCache.getAll(countersCache.keySet()));
    }
}
