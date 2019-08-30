/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Util class to make REST api calls to 3rd party apis
 *
 * @author : Shital Raval
 * Date : 21/8/19
 * Time : 12:58 PM
 * Class Name : RestClient
 * Project Name : server
 */
@Log4j2
public class RestClient {

    //singleton instance
    private static RestClient restObj = null;

    @Value("${restConnectionTimeout}")
    private int connectionTimeout;

    @Value("${restReadTimeout}")
    private int readTimeout;

    //private constructor
    private RestClient() {
    }

    //get instance
    public static RestClient getInstance() {
        if (null == restObj)
            restObj = new RestClient();
        return restObj;
    }

    /**
     * Method that connects to the server as mentioned in the url and performs GET/POST operation
     *
     * @param requestObj the payload to send in the request in json format
     * @param apiUrl the url to connect to
     * @param requestType GET / POST
     * @param authToken authorization information
     * @return JSON representation of the response
     * @throws Exception
     */
    public String consumeRestApi(String requestObj, String apiUrl, HttpMethod requestType, String authToken) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        SimpleClientHttpRequestFactory requestFactory = (SimpleClientHttpRequestFactory)restTemplate.getRequestFactory();
        requestFactory.setConnectTimeout(connectionTimeout);
        requestFactory.setReadTimeout(readTimeout);

        //log.info("Request object sent: " + requestObj);

        HttpEntity<String> entity;
        if (null != requestObj)
            entity = new HttpEntity<String>(requestObj, getHttpHeader(authToken, true));
        else
            entity = new HttpEntity<String>(getHttpHeader(authToken, false));
        try {
            long startTime = System.currentTimeMillis();
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, requestType, entity, String.class);
            log.info("Time taken to retrieve response from REST api: " + (System.currentTimeMillis() - startTime) + "ms.");
            return response.getBody();
        } catch(HttpStatusCodeException e ) {
            List<String> customHeader = e.getResponseHeaders().get("x-app-err-id");
            String svcErrorMessageID = "";
            if (customHeader != null) {
                svcErrorMessageID = customHeader.get(0);
            }
            log.error("Error response from REST call: " + svcErrorMessageID + " :: " + e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Exception while making a REST call: " + e.getMessage());
            throw e;
        }

    }

    /**
     * Method to generate HTTP Header to be used by the REST API
     * @param authToken authorization information
     * @return
     */
    private HttpHeaders getHttpHeader(String authToken, boolean setContentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",authToken);
        if(setContentType) {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        }
        return headers;
    }
}
