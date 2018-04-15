package com.belosh.jdbctemplate.template;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private static final Pattern placeHolder = Pattern.compile(":\\s*(\\w+)");

    static List<?> getOrderedParamList(String query, Map<String, ?> param) {
        List<Object> paramList = new ArrayList<>();
        Matcher matcher = placeHolder.matcher(query);

        while (matcher.find()) {
            String paramName = matcher.group(1);
            paramList.add(param.get(paramName));
        }
        return paramList;
    }

    static List<?> getOrderedParamList(Object... args) {
        List<Object> paramList = new ArrayList<>(args.length);
        Collections.addAll(paramList, args);
        return paramList;
    }

    static String getPlaceholderQuery(String query) {
        Matcher matcher = placeHolder.matcher(query);
        return matcher.replaceAll("?");
    }
}
