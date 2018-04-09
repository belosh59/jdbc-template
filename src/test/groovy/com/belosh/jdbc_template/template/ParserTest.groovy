package com.belosh.jdbc_template.template

class ParserTest extends GroovyTestCase {
    def BIND_VARIABLE_QUERY = "SELECT * FROM jdbc_template WHERE ftext = :ftext and fint = :fint"
    def parser = new Parser()

    void testGetIndexValueMap() {
        Map<String, Object> namedParameterMap = new HashMap<>()
        namedParameterMap.put("ftext", "text")
        namedParameterMap.put("fint", 100)

        Map<Integer, ?> generatedIndexParam = parser.getOrderedParamList(BIND_VARIABLE_QUERY, namedParameterMap)
        assertEquals("text", generatedIndexParam.get(1))
        assertEquals(100, generatedIndexParam.get(2))
    }

    void testGetIndexValueMap1() {
        Map<Integer, ?> generatedIndexParam = parser.getOrderedParamList( "text", 100)
        assertEquals("text", generatedIndexParam.get(1))
        assertEquals(100, generatedIndexParam.get(2))
    }
}
