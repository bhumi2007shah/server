/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.Data;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * @author : Sumit
 * Date : 25/7/19
 * Time : 12:57 PM
 * Class Name : DateDeserializer
 * Project Name : server
 */
@Data
public class DateDeserializer extends StdDeserializer {

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String date = p.getText();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(date);
        } catch (Exception e) {
            return null;
        }
    }

    public DateDeserializer(Class vc) {
        super(vc);
    }

    public DateDeserializer() {
        this(null);
    }
}
