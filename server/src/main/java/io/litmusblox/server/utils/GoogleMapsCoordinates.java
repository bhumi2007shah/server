/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.utils;

/**
 * @author : sameer
 * Date : 15/10/19
 * Time : 12:31 PM
 * Class Name : GoogleMapsCoordinates
 * Project Name : server
 */

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class GoogleMapsCoordinates {
    protected static final Logger logger = LogManager.getLogger(GoogleMapsCoordinates.class);

    //using the RocketHire key for now
    //TODO: See if we need one for LB
    private static final String GOOGLE_API_KEY = "AIzaSyAmzlony_HlfEmqz7m-SWZXEhGAlAI641E";
    public static LatLng getCoordinates(String address) throws Exception{
        if(address == null){
            return null;
        }
        LatLng coordinates = null;
        try {
            logger.info("Finding coordinates for address: "+address);
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey(GOOGLE_API_KEY)
                    .build();

            GeocodingResult[] results =  GeocodingApi.geocode(context,address).await();
            if(results.length <= 0){
                logger.info("Could not find coordinates");
                return null;
            }else{
                coordinates = results[0].geometry.location;
            }
        } catch (Exception e) {
            logger.info("Error while finding coordinates:");
            e.printStackTrace();
        }
        return coordinates;
    }
}
