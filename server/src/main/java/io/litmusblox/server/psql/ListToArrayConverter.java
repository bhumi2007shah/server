/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.psql;

import org.postgresql.jdbc.PgArray;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Converter(autoApply = true)
public class ListToArrayConverter implements AttributeConverter<List<String>, Object> {
    @Override
    public PostgreSQLTextArray convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        String[] rst = new String[attribute.size()];
        return new PostgreSQLTextArray(attribute.toArray(rst));
    }

    @Override
    public List<String> convertToEntityAttribute(Object dbData) {

        if(null == dbData)
            return null;

        List<String> rst = new ArrayList<>();

        try {
            String[] elements = null;
            if(dbData.getClass().equals(io.litmusblox.server.psql.PostgreSQLTextArray.class)) {
                elements = (String[]) ((PostgreSQLTextArray) dbData).getArray();
            }
            else {
                elements = (String[]) (((PgArray) dbData).getArray());
            }
            for (String element : elements) {

                rst.add(element);
            }
        } catch (SQLException sqle) {

        }
        return rst;
    }
}
