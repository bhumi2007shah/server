/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Class for holding Utility methods to be used across the application
 *
 * @author : Shital Raval
 * Date : 12/7/19
 * Time : 10:00 AM
 * Class Name : Util
 * Project Name : server
 */
@Log4j2
public class Util {


    /**
     * Utility method to convert only relevant information into json
     *
     * @param responseBean the response bean to be converted to json
     * @param serializeMap map with key = filterclassname and value as a list of all bean properties required to be serialized
     * @param serializeExceptMap map with key = filterclassname and value as a list of bean properties that shouldn't be serialized
     * @return
     */
    public static String stripExtraInfoFromResponseBean(Object responseBean, Map<String, List<String>> serializeMap, Map<String, List<String>> serializeExceptMap) {

        ObjectMapper mapper = new ObjectMapper();

        String json="";
        try {

            SimpleFilterProvider filter = new SimpleFilterProvider();
            if (null != serializeMap)
                serializeMap.forEach((key, value) ->
                        filter.addFilter(key, SimpleBeanPropertyFilter.filterOutAllExcept(new HashSet<String>(value)))
                );

            if(null != serializeExceptMap)
                serializeExceptMap.forEach((key, value) ->
                        filter.addFilter(key, SimpleBeanPropertyFilter.serializeAllExcept(new HashSet<String>(value)))
                );

            json = mapper.writer(filter).writeValueAsString(responseBean);

        } catch (JsonGenerationException e) {
            log.error("error generating JSON string from response object: " + e.getMessage());
            e.printStackTrace();
        } catch (JsonMappingException e) {
            log.error("error generating JSON string from response object: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            log.error("error generating JSON string from response object: " + e.getMessage());
            e.printStackTrace();
        }

        return json;
    }
}
