/*
 *
 */
package com.redhat.demo.arch.microservices.history.ejb.services.impl;

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
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;
import org.infinispan.protostream.annotations.ProtoSchemaBuilderException;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.slf4j.Logger;

import com.redhat.demo.arch.microservices.history.common.dto.PayloadHistory;

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
                configurationBuilder.build());
        configureProtobufMarshaller();

        if (LOG.isDebugEnabled()) {
            LOG.debug("init() - end");
        }
    }

    private void configureProtobufMarshaller() {
        SerializationContext serCtx = ProtoStreamMarshaller
                .getSerializationContext(remoteCacheManager);

        // generate and register a Protobuf schema and marshallers based
        // on PayloadHistory class and the referenced classes (if any)
        ProtoSchemaBuilder protoSchemaBuilder = new ProtoSchemaBuilder();
        String generatedSchema;
        try {
            // generate the 'history.proto' schema file based on the
            // annotations on
            // Memo class and register it with the SerializationContext of the
            // client
            generatedSchema = protoSchemaBuilder
                    .fileName("historypayload.proto")//
                    .packageName(PayloadHistory.class.getPackage().getName())//
                    .addClass(PayloadHistory.class)//
                    .build(serCtx);

            // register the schemas with the server too
            RemoteCache<String, String> metadataCache = remoteCacheManager
                    .getCache(
                            ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
            metadataCache.put("historypayload.proto", generatedSchema);

            String errors = metadataCache
                    .get(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX);
            if (errors != null) {
                throw new IllegalStateException(
                        "Some Protobuf schema files contain errors:\n"
                                + errors);
            }

        } catch (ProtoSchemaBuilderException | IOException e) {
            throw new RuntimeException(
                    "An error occurred initializing HotRod protobuf marshaller:",
                    e);
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
