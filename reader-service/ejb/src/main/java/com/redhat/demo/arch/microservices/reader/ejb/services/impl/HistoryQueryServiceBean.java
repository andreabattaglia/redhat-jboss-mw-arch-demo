/*
 *
 */
package com.redhat.demo.arch.microservices.reader.ejb.services.impl;

import java.io.IOException;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;

@Singleton
@LocalBean
public class HistoryQueryServiceBean {
    @Inject
    private Logger LOG;

    private HttpClient httpClient;

    @PostConstruct
    private void init() {
        httpClient = new DefaultHttpClient();
    }

    @PreDestroy
    private void destroy() {
        httpClient.getConnectionManager().shutdown();

    }

    public String getCountersByMinutes(int minutes) throws IOException {
        String output = null;
        String loadBalancerHost = System.getProperty("jboss.loadbalancer.host");
        int loadBalancerPort = 80;
        try {
            loadBalancerPort = Integer
                    .parseInt(System.getProperty("jboss.loadbalancer.port"));
        } catch (NumberFormatException e) {
        }
        try {
            // HttpGet request = new HttpGet("http://" + loadBalancerAddress
            // + "history/rest/");
            URIBuilder builder = new URIBuilder();
            builder.setScheme("http").setHost(loadBalancerHost)
                    .setPort(loadBalancerPort).setPath("/history/rest/history")
                    .setParameter("minutes", Integer.toString(minutes));
            HttpGet request = new HttpGet(builder.build());

            HttpResponse response = httpClient.execute(request);

            int responseCode = response.getStatusLine().getStatusCode();
            if ((response.getStatusLine().getStatusCode() == 200)
                    || (response.getStatusLine().getStatusCode() == 204)) {

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    try (Scanner s = new Scanner(entity.getContent())
                            .useDelimiter("\\A")) {
                        output = s.hasNext() ? s.next() : "";
                    } catch (IOException e) {

                    }
                }
            } else {
                LOG.warn(
                        "Cannot query history: History service unavailable: {}",
                        responseCode);
            }
            request.releaseConnection();
        } catch (Exception ex) {
            LOG.error("Exception caught contacting \"history\" service: ", ex);
            // LOG.error("url:" + url);
            // LOG.error("payload:" + payload);
        }
        return output;
    }

}
