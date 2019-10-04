package io.litmusblox.server.utils;

import io.litmusblox.server.model.Country;

/**
 * @author : sameer
 * Date : 18/09/19
 * Time : 11:53 AM
 * Class Name : Countries
 * Project Name : server
 */
public class CountryUtil {
    private static Country country = new Country("India", "+91", 10L, "in");

    public static Country getCountry(){return country;}
}
