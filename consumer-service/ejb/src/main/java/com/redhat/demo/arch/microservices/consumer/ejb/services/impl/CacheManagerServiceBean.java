/*
 *
 */
package com.redhat.demo.arch.microservices.consumer.ejb.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.slf4j.Logger;

@Singleton
@Startup
@LocalBean
@Lock(LockType.READ)
public class CacheManagerServiceBean {

    @Inject
    private Logger LOG;

    private RemoteCacheManager remoteCacheManager;

    @PostConstruct
    private void init() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("init() - start");
        }

        Properties properties = new Properties();
        try (InputStream is = getClass()
                .getResourceAsStream("/hotrod-client.properties")) {
            properties.load(is);
        } catch (IOException e) {
            LOG.error("", e);
        }
        properties.put("infinispan.client.hotrod.server_list",
                System.getProperty("jboss.datagrid.topology"));

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.withProperties(properties);
        configurationBuilder//
                .security()//
                .authentication()//
                .enable()//
                .serverName("securedhotrodserver")//
                .saslMechanism("DIGEST-MD5")//
                .callbackHandler(new HotRodCallbackHandler());
        remoteCacheManager = new RemoteCacheManager(
                configurationBuilder.build(), true);
        // remoteCacheManager.start();

        if (LOG.isDebugEnabled()) {
            LOG.debug("init() - end");
        }
    }

    @PreDestroy
    private void destroy() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("destroy() - start");
        }

        remoteCacheManager.stop();

        if (LOG.isDebugEnabled()) {
            LOG.debug("destroy() - end");
        }
    }

    public <K, V> RemoteCache<K, V> getCache(final String cacheName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getCache(String cacheName={}) - start", cacheName);
        }

        RemoteCache<K, V> returnCache = remoteCacheManager.getCache(cacheName,
                true);

        LOG.info("RemoteCache {} initializiation completed.", cacheName);

        if (LOG.isDebugEnabled()) {
            LOG.debug("getCache(String cacheName={}) - end - return value={}",
                    cacheName, returnCache);
        }
        return returnCache;
    }

}
