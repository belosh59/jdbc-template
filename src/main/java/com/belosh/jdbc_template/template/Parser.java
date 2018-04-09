package com.belosh.jdbc_template.template;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    List<?> getOrderedParamList(String query, Map<String, ?> param) {
        int startKeyPosition = 0;
        List<Object> paramList = new ArrayList<>();
        Pattern pattern = Pattern.compile("[), ]");
        Matcher matcher = pattern.matcher(query);

        while ((startKeyPosition = query.indexOf(':', startKeyPosition)) > 0) {
            startKeyPosition++;
            String paramName;
            if (matcher.find(startKeyPosition)) {
                paramName = query.substring(startKeyPosition, matcher.start());
            } else {
                paramName = query.substring(startKeyPosition);
            }
            paramList.add(param.get(paramName));
        }

        return paramList;
    }

    List<?> getOrderedParamList(Object... args) {
        return new ArrayList<>(Arrays.asList(args));
    }

     String getPlaceholderQuery(String query, Map<String, ?> param) {
        for (String paramName : param.keySet()) {
            query = query.replaceFirst(":" + paramName, "?");
        }
        return query;
    }
}
