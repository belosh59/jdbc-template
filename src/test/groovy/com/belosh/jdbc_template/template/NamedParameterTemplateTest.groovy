package com.belosh.jdbc_template.template
import com.belosh.jdbc_template.rowmapper.TestVOMapper
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.postgresql.ds.PGPoolingDataSource

import javax.sql.DataSource

import static org.junit.Assert.*
import java.sql.Date
import java.text.SimpleDateFormat

class NamedParameterTemplateTest {
    def static SELECT_ALL_PLACEHOLDER_QUERY = "SELECT * FROM jdbc_template"
    def static SELECT_ROW_PLACEHOLDER_QUERY = "SELECT * FROM jdbc_template WHERE fboolean = ?"
    def static UPDATE_PLACEHOLDER_QUERY = "UPDATE jdbc_template SET fdouble = ? WHERE ftext = ?"
    def static INSERT_PLACEHOLDER_QUERY = "INSERT INTO jdbc_template (fboolean, fint, fdouble, ffloat, ftext, fdate) VALUES  (?, ?, ?, ?, ?, ?)"
    def static DELETE_PLACEHOLDER_QUERY = "DELETE FROM jdbc_template WHERE fboolean = ?"

    def static SELECT_ALL_BINDVAR_QUERY = "SELECT * FROM jdbc_template"
    def static SELECT_ROW_BINDVAR_QUERY = "SELECT * FROM jdbc_template WHERE fboolean = :fboolean"
    def static UPDATE_BINDVAR_QUERY = "UPDATE jdbc_template SET fdouble = :fdouble WHERE ftext = :ftext"
    def static INSERT_BINDVAR_QUERY = "INSERT INTO jdbc_template (fboolean, fint, fdouble, ffloat, ftext, fdate) VALUES (:fboolean, :fint, :fdouble, :ffloat, :ftext, :fdate)"
    def static DELETE_BINDVAR_QUERY = "DELETE FROM jdbc_template WHERE fboolean = :fboolean"

    def static DELETE_ALL = "DELETE FROM jdbc_template"

    def static namedParameterTemplate
    def testVOMapper = new TestVOMapper()
    def static sqlDate

    @BeforeClass
    static void setUp() {
        // Configure Datasource
        DataSource dataSourcePG = new PGPoolingDataSource()
        dataSourcePG.setServerName("ec2-54-247-125-137.eu-west-1.compute.amazonaws.com")
        dataSourcePG.setDatabaseName("d3n2i90vr7qpeg")
        dataSourcePG.setUser("ojzdpxsxypoket")
        dataSourcePG.setPassword("f5bfab965fda8984c533f4a543a183d1530bfc7a3ec341b79a8fb106c746c9c8")
        dataSourcePG.setPortNumber(5432)
        dataSourcePG.setSsl(true)
        dataSourcePG.setSslfactory("org.postgresql.ssl.NonValidatingFactory")
        namedParameterTemplate = new NamedParameterTemplate(dataSourcePG)
    }

    @Before
    void prepareDatabase() {
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

    @Test
    void testQueryByMap() {
        def paramMap = [ftext: "test-row"]
        def testVO = namedParameterTemplate.query(SELECT_ALL_BINDVAR_QUERY, testVOMapper, paramMap)
        def testVO0 = testVO.get(0)
        assertEquals(true, testVO0.isFieldBoolean())
        assertEquals(100, testVO0.getFieldInt())
        assertEquals(3.865d, testVO0.getFieldDouble(), 0)
        assertEquals(3.865f, testVO0.getFieldFloat(), 0)
        assertEquals("test-row", testVO0.getFieldText())
        assertEquals(sqlDate, testVO0.getFieldDate())

        def testVO1 = testVO.get(1)
        assertEquals(false, testVO1.isFieldBoolean())
        assertEquals(5, testVO1.getFieldInt())
        assertEquals(9.865d, testVO1.getFieldDouble(), 0)
        assertEquals(1.865f, testVO1.getFieldFloat(), 0)
        assertEquals("test-row", testVO1.getFieldText())
        assertEquals(sqlDate, testVO1.getFieldDate())

        assertEquals(2, testVO.size())
    }

    @Test
    void testQueryByVararrg() {
        def testVOList = namedParameterTemplate.query(SELECT_ALL_PLACEHOLDER_QUERY, testVOMapper)

        def testVO0 = testVOList.get(0)
        assertEquals(true, testVO0.isFieldBoolean())
        assertEquals(100, testVO0.getFieldInt())
        assertEquals(3.865d, testVO0.getFieldDouble(), 0)
        assertEquals(3.865f, testVO0.getFieldFloat(), 0)
        assertEquals("test-row", testVO0.getFieldText())
        assertEquals(sqlDate, testVO0.getFieldDate())

        def testVO1 = testVOList.get(1)
        assertEquals(false, testVO1.isFieldBoolean())
        assertEquals(5, testVO1.getFieldInt())
        assertEquals(9.865d, testVO1.getFieldDouble(), 0)
        assertEquals(1.865f, testVO1.getFieldFloat(), 0)
        assertEquals("test-row", testVO1.getFieldText())
        assertEquals(sqlDate, testVO1.getFieldDate())

        assertEquals(2, testVOList.size())
    }

    @Test
    void testQueryForObjectByMap() {
        def queryParamMap = [fboolean: true]
        def testVO = namedParameterTemplate.queryForObject(SELECT_ROW_BINDVAR_QUERY, testVOMapper, queryParamMap)
        assertEquals(true, testVO.isFieldBoolean())
        assertEquals(100, testVO.getFieldInt())
        assertEquals(3.865d, testVO.getFieldDouble(), 0)
        assertEquals(3.865f, testVO.getFieldFloat(), 0)
        assertEquals("test-row", testVO.getFieldText())
        assertEquals(sqlDate, testVO.getFieldDate())
    }

    @Test
    void testQueryForObjectByVarrarg() {
        def testVO = namedParameterTemplate.queryForObject(SELECT_ROW_PLACEHOLDER_QUERY, testVOMapper, false)
        assertEquals(false, testVO.isFieldBoolean())
        assertEquals(5, testVO.getFieldInt())
        assertEquals(9.865d, testVO.getFieldDouble(), 0)
        assertEquals(1.865f, testVO.getFieldFloat(), 0)
        assertEquals("test-row", testVO.getFieldText())
        assertEquals(sqlDate, testVO.getFieldDate())
    }

    @Test
    void testUpdateByMap() {
        def updateParamMap = [fdouble: 100.001d, ftext: "test-row"]
        def deleteParamMap = [fboolean: true]
        def selectParamMap = [fboolean: true]

        // INSERT tested in setUp method

        // UPDATE
        def updatedRows = namedParameterTemplate.update(UPDATE_BINDVAR_QUERY, updateParamMap)
        def testVO = namedParameterTemplate.queryForObject(SELECT_ROW_BINDVAR_QUERY, testVOMapper, selectParamMap)
        assertEquals(2, updatedRows)
        assertEquals(true, testVO.isFieldBoolean())
        assertEquals(100, testVO.getFieldInt())
        assertEquals(100.001d, testVO.getFieldDouble(), 0)
        assertEquals(3.865f, testVO.getFieldFloat(), 0)
        assertEquals("test-row", testVO.getFieldText())
        assertEquals(sqlDate, testVO.getFieldDate())

        // DELETE

        def deletedRows = namedParameterTemplate.update(DELETE_BINDVAR_QUERY, deleteParamMap)
        def deletedTestVO = namedParameterTemplate.queryForObject(SELECT_ROW_BINDVAR_QUERY, testVOMapper, selectParamMap)
        assertEquals(1, deletedRows)
        assertNull(deletedTestVO)
    }

    @Test
    void testUpdateByVararrg() {
        // INSERT tested in setUp method

        // UPDATE
        def updatedRows = namedParameterTemplate.update(UPDATE_PLACEHOLDER_QUERY, 100.001d, "test-row")
        def updatedTestVO = namedParameterTemplate.queryForObject(SELECT_ROW_PLACEHOLDER_QUERY, testVOMapper, true)
        assertEquals(2, updatedRows)
        assertEquals(true, updatedTestVO.isFieldBoolean())
        assertEquals(100, updatedTestVO.getFieldInt())
        assertEquals(100.001d, updatedTestVO.getFieldDouble(), 0)
        assertEquals(3.865f, updatedTestVO.getFieldFloat(),0)
        assertEquals("test-row", updatedTestVO.getFieldText())
        assertEquals(sqlDate, updatedTestVO.getFieldDate())

        // DELETE
        def deletedRows = namedParameterTemplate.update(DELETE_PLACEHOLDER_QUERY,true)
        def deletedTestVO = namedParameterTemplate.queryForObject(SELECT_ROW_PLACEHOLDER_QUERY, testVOMapper, true)
        assertEquals(1, deletedRows)
        assertNull(deletedTestVO)
    }

    //TODO: Require test for null value
}
