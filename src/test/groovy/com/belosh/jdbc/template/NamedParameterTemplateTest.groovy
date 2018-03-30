package com.belosh.jdbc.template
import com.belosh.jdbc.rowmapper.JdbcTemplateMapper
import org.postgresql.ds.PGPoolingDataSource

import java.sql.Date
import java.text.SimpleDateFormat

class NamedParameterTemplateTest extends GroovyTestCase {
    def SELECT_ALL_PLACEHOLDER_QUERY = "SELECT * FROM jdbc_template"
    def SELECT_ROW_PLACEHOLDER_QUERY = "SELECT * FROM jdbc_template WHERE fboolean = ?"
    def UPDATE_PLACEHOLDER_QUERY = "UPDATE jdbc_template SET fdouble = ? WHERE ftext = ?"
    def INSERT_PLACEHOLDER_QUERY = "INSERT INTO jdbc_template (fboolean, fint, fdouble, ffloat, ftext, fdate) VALUES  (?, ?, ?, ?, ?, ?)"
    def DELETE_PLACEHOLDER_QUERY = "DELETE FROM jdbc_template WHERE fboolean = ?"

    def SELECT_ALL_BINDVAR_QUERY = "SELECT * FROM jdbc_template"
    def SELECT_ROW_BINDVAR_QUERY = "SELECT * FROM jdbc_template WHERE fboolean = :fboolean"
    def UPDATE_BINDVAR_QUERY = "UPDATE jdbc_template SET fdouble = :fdouble WHERE ftext = :ftext"
    def INSERT_BINDVAR_QUERY = "INSERT INTO jdbc_template (fboolean, fint, fdouble, ffloat, ftext, fdate) VALUES (:fboolean, :fint, :fdouble, :ffloat, :ftext, :fdate)"
    def DELETE_BINDVAR_QUERY = "DELETE FROM jdbc_template WHERE fboolean = :fboolean"

    def DELETE_ALL = "DELETE FROM jdbc_template"

    def namedParameterTemplate = new NamedParameterTemplate()
    def jdbcTemplateMapper = new JdbcTemplateMapper()
    def sqlDate

    void setUp() {
        // Configure Datasource
        PGPoolingDataSource dataSourcePG = new PGPoolingDataSource()
        dataSourcePG.setServerName("ec2-54-247-125-137.eu-west-1.compute.amazonaws.com")
        dataSourcePG.setDatabaseName("d3n2i90vr7qpeg")
        dataSourcePG.setUser("ojzdpxsxypoket")
        dataSourcePG.setPassword("f5bfab965fda8984c533f4a543a183d1530bfc7a3ec341b79a8fb106c746c9c8")
        dataSourcePG.setPortNumber(5432)
        dataSourcePG.setSsl(true)
        dataSourcePG.setSslfactory("org.postgresql.ssl.NonValidatingFactory")
        namedParameterTemplate.setDataSource(dataSourcePG)

        // Prepare Database
        namedParameterTemplate.update(DELETE_ALL)

        // Prepare Mock Data
        def parsedDate = new SimpleDateFormat("dd-MM-yyyy").parse("29-03-2018")
        sqlDate = new Date(parsedDate.getTime())

        // Test Insert Mock Data
        def defaultParamMap = [fboolean: true, fint: 100, fdouble: 3.865d, ffloat: 3.865f, ftext: "test-row", fdate: sqlDate]
        namedParameterTemplate.update(INSERT_BINDVAR_QUERY, defaultParamMap)
        namedParameterTemplate.update(INSERT_PLACEHOLDER_QUERY, false, 5, 9.865d, 1.865f, "test-row", sqlDate)
    }

    void testQueryByMap() {
        def newParamMap = [ftext: "test-row"]
        def jdbcTemplates = namedParameterTemplate.query(SELECT_ALL_BINDVAR_QUERY, jdbcTemplateMapper, newParamMap)
        def jdbcTemplate0 = jdbcTemplates.get(0)
        assertEquals(true, jdbcTemplate0.isFieldBoolean())
        assertEquals(100, jdbcTemplate0.getFieldInt())
        assertEquals(3.865d, jdbcTemplate0.getFieldDouble())
        assertEquals(3.865f, jdbcTemplate0.getFieldFloat())
        assertEquals("test-row", jdbcTemplate0.getFieldText())
        assertEquals(sqlDate, jdbcTemplate0.getFieldDate())

        def jdbcTemplate1 = jdbcTemplates.get(1)
        assertEquals(false, jdbcTemplate1.isFieldBoolean())
        assertEquals(5, jdbcTemplate1.getFieldInt())
        assertEquals(9.865d, jdbcTemplate1.getFieldDouble())
        assertEquals(1.865f, jdbcTemplate1.getFieldFloat())
        assertEquals("test-row", jdbcTemplate1.getFieldText())
        assertEquals(sqlDate, jdbcTemplate1.getFieldDate())

        assertEquals(2, jdbcTemplates.size())
    }

    void testQueryByVararrg() {
        def jdbcTemplates = namedParameterTemplate.query(SELECT_ALL_PLACEHOLDER_QUERY, jdbcTemplateMapper)

        def jdbcTemplate0 = jdbcTemplates.get(0)
        assertEquals(true, jdbcTemplate0.isFieldBoolean())
        assertEquals(100, jdbcTemplate0.getFieldInt())
        assertEquals(3.865d, jdbcTemplate0.getFieldDouble())
        assertEquals(3.865f, jdbcTemplate0.getFieldFloat())
        assertEquals("test-row", jdbcTemplate0.getFieldText())
        assertEquals(sqlDate, jdbcTemplate0.getFieldDate())

        def jdbcTemplate1 = jdbcTemplates.get(1)
        assertEquals(false, jdbcTemplate1.isFieldBoolean())
        assertEquals(5, jdbcTemplate1.getFieldInt())
        assertEquals(9.865d, jdbcTemplate1.getFieldDouble())
        assertEquals(1.865f, jdbcTemplate1.getFieldFloat())
        assertEquals("test-row", jdbcTemplate1.getFieldText())
        assertEquals(sqlDate, jdbcTemplate1.getFieldDate())

        assertEquals(2, jdbcTemplates.size())
    }



    void testQueryForObjectByMap() {
        def queryParamMap = [fboolean: true]
        def jdbcTemplate = namedParameterTemplate.queryForObject(SELECT_ROW_BINDVAR_QUERY, jdbcTemplateMapper, queryParamMap)
        assertEquals(true, jdbcTemplate.isFieldBoolean())
        assertEquals(100, jdbcTemplate.getFieldInt())
        assertEquals(3.865d, jdbcTemplate.getFieldDouble())
        assertEquals(3.865f, jdbcTemplate.getFieldFloat())
        assertEquals("test-row", jdbcTemplate.getFieldText())
        assertEquals(sqlDate, jdbcTemplate.getFieldDate())
    }

    void testQueryForObjectByVarrarg() {
        def jdbcTemplate = namedParameterTemplate.queryForObject(SELECT_ROW_PLACEHOLDER_QUERY, jdbcTemplateMapper, false)
        assertEquals(false, jdbcTemplate.isFieldBoolean())
        assertEquals(5, jdbcTemplate.getFieldInt())
        assertEquals(9.865d, jdbcTemplate.getFieldDouble())
        assertEquals(1.865f, jdbcTemplate.getFieldFloat())
        assertEquals("test-row", jdbcTemplate.getFieldText())
        assertEquals(sqlDate, jdbcTemplate.getFieldDate())
    }

    void testUpdateByMap() {
        def updateParamMap = [fdouble: 100.001d, ftext: "test-row"]
        def deleteParamMap = [fboolean: true]
        def selectParamMap = [fboolean: true]

        // INSERT tested in setUp method

        // UPDATE
        def updatedRows = namedParameterTemplate.update(UPDATE_BINDVAR_QUERY, updateParamMap)
        def updatedJdbcTemplate = namedParameterTemplate.queryForObject(SELECT_ROW_BINDVAR_QUERY, jdbcTemplateMapper, selectParamMap)
        assertEquals(2, updatedRows)
        assertEquals(true, updatedJdbcTemplate.isFieldBoolean())
        assertEquals(100, updatedJdbcTemplate.getFieldInt())
        assertEquals(100.001d, updatedJdbcTemplate.getFieldDouble())
        assertEquals(3.865f, updatedJdbcTemplate.getFieldFloat())
        assertEquals("test-row", updatedJdbcTemplate.getFieldText())
        assertEquals(sqlDate, updatedJdbcTemplate.getFieldDate())

        // DELETE

        def deletedRows = namedParameterTemplate.update(DELETE_BINDVAR_QUERY, deleteParamMap)
        def deletedJdbcTemplate = namedParameterTemplate.queryForObject(SELECT_ROW_BINDVAR_QUERY, jdbcTemplateMapper, selectParamMap)
        assertEquals(1, deletedRows)
        assertNull(deletedJdbcTemplate)
    }

    void testUpdateByVararrg() {
        // INSERT tested in setUp method

        // UPDATE
        def updatedRows = namedParameterTemplate.update(UPDATE_PLACEHOLDER_QUERY, 100.001d, "test-row")
        def updatedJdbcTemplate = namedParameterTemplate.queryForObject(SELECT_ROW_PLACEHOLDER_QUERY, jdbcTemplateMapper, true)
        assertEquals(2, updatedRows)
        assertEquals(true, updatedJdbcTemplate.isFieldBoolean())
        assertEquals(100, updatedJdbcTemplate.getFieldInt())
        assertEquals(100.001d, updatedJdbcTemplate.getFieldDouble())
        assertEquals(3.865f, updatedJdbcTemplate.getFieldFloat())
        assertEquals("test-row", updatedJdbcTemplate.getFieldText())
        assertEquals(sqlDate, updatedJdbcTemplate.getFieldDate())

        // DELETE
        def deletedRows = namedParameterTemplate.update(DELETE_PLACEHOLDER_QUERY,true)
        def deletedJdbcTemplate = namedParameterTemplate.queryForObject(SELECT_ROW_PLACEHOLDER_QUERY, jdbcTemplateMapper, true)
        assertEquals(1, deletedRows)
        assertNull(deletedJdbcTemplate)
    }
}
