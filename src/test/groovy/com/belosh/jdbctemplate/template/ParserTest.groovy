package com.belosh.jdbctemplate.template

import org.junit.Assert
import org.junit.Test

class ParserTest {
    def BIND_VARIABLE_QUERY = "SELECT * FROM jdbctemplate WHERE ftext = :ftext and fint = :fint"
    def namedParameterMap = ["ftext":"text", "fint":100]
    def parser = new Parser()

    @Test
    void testGetIndexValueMap() {
        List<Object> generatedIndexParam = parser.getOrderedParamList(BIND_VARIABLE_QUERY, namedParameterMap)
        Assert.assertEquals("text", generatedIndexParam.get(0))
        Assert.assertEquals(100, generatedIndexParam.get(1))
    }

    @Test
    void testGetIndexValueMap1() {
        Map<Integer, ?> generatedIndexParam = parser.getOrderedParamList( "text", 100)
        Assert.assertEquals("text", generatedIndexParam.get(0))
        Assert.assertEquals(100, generatedIndexParam.get(1))
    }

    @Test
    void getPlaceholderQuery() {
        String placeholderQuery = parser.getPlaceholderQuery(BIND_VARIABLE_QUERY)
        Assert.assertEquals("SELECT * FROM jdbctemplate WHERE ftext = ? and fint = ?", placeholderQuery);
    }
}
