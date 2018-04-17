package com.belosh.jdbctemplate.template

import com.belosh.jdbctemplate.entity.TestVO
import com.belosh.jdbctemplate.exception.EmptyResultDataAccessException
import com.belosh.jdbctemplate.exception.EntityBuilderException
import com.belosh.jdbctemplate.exception.IncorrectResultSizeDataAccessException
import com.belosh.jdbctemplate.rowmapper.TestVOMapper
import org.h2.jdbcx.JdbcDataSource
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

import java.time.LocalDateTime

import static org.junit.Assert.*

class NamedParameterJDBCTemplateITest {
    def static CREATE_DATATYPES_TABLE = "CREATE TABLE datatypes (" +
                                                    "fboolean boolean," +
                                                    "fint int," +
                                                    "fdouble double," +
                                                    "ffloat float," +
                                                    "ftext varchar(255)," +
                                                    "fdate timestamp)"

    def static SELECT_ROW_PLACEHOLDER_QUERY = "SELECT * FROM datatypes WHERE fboolean = ?"
    def static UPDATE_PLACEHOLDER_QUERY = "UPDATE datatypes SET fdouble = ? WHERE ftext = ?"
    def static INSERT_PLACEHOLDER_QUERY = "INSERT INTO datatypes (fboolean, fint, fdouble, ffloat, ftext, fdate) VALUES  (?, ?, ?, ?, ?, ?)"
    def static DELETE_PLACEHOLDER_QUERY = "DELETE FROM datatypes WHERE fboolean = ?"

    def static SELECT_ROW_BINDVAR_QUERY = "SELECT * FROM datatypes WHERE fboolean = :fboolean"
    def static UPDATE_BINDVAR_QUERY = "UPDATE datatypes SET fdouble = :fdouble WHERE ftext = :ftext"
    def static INSERT_BINDVAR_QUERY = "INSERT INTO datatypes (fboolean, fint, fdouble, ffloat, ftext, fdate) VALUES (:fboolean, :fint, :fdouble, :ffloat, :ftext, :fdate)"
    def static DELETE_BINDVAR_QUERY = "DELETE FROM datatypes WHERE fboolean = :fboolean"

    def static SELECT_ALL = "SELECT * FROM datatypes"
    def static DELETE_ALL = "DELETE FROM datatypes"

    def static namedParameterTemplate
    def static dataSourceH2 = new JdbcDataSource()
    def testVOMapper = new TestVOMapper()
    def localDateTime = LocalDateTime.now()

    @Rule
    public ExpectedException expectedEx = ExpectedException.none()

    @BeforeClass
    static void setUp() {
        // Configure Datasource
        dataSourceH2.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")

        namedParameterTemplate = new NamedParameterJDBCTemplate()
        namedParameterTemplate.setDataSource(dataSourceH2)
        namedParameterTemplate.update(CREATE_DATATYPES_TABLE)

        /* DEBUG H2 Console
        JdbcDataSource dataSourceH2 = new JdbcDataSource();
        dataSourceH2.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        Server.startWebServer(dataSourceH2.getConnection());
         */
    }

    @Before
    void prepareDatabase() {
        // Prepare Database
        namedParameterTemplate.update(DELETE_ALL)

        // Test Insert Mock Data
        def defaultParamMap = [fboolean: true, fint: 100, fdouble: 3.865d, ffloat: 3.865f, ftext: "test-row", fdate: localDateTime]
        namedParameterTemplate.update(INSERT_BINDVAR_QUERY, defaultParamMap)
        namedParameterTemplate.update(INSERT_PLACEHOLDER_QUERY, false, 5, 9.865d, 1.865f, "test-row", localDateTime)
    }

    @Test
    void testQueryByMap() {
        def paramMap = [ftext: "test-row"]
        List<TestVO> testVO = namedParameterTemplate.query(SELECT_ALL, testVOMapper, paramMap)
        def testVO0 = testVO.get(0)
        assertEquals(true, testVO0.isFieldBoolean())
        assertEquals(100, testVO0.getFieldInt())
        Assert.assertEquals(3.865d, testVO0.getFieldDouble(), 0)
        assertEquals(3.865f, testVO0.getFieldFloat(), 0)
        assertEquals("test-row", testVO0.getFieldText())
        assertEquals(localDateTime, testVO0.getFieldDate())

        def testVO1 = testVO.get(1)
        assertEquals(false, testVO1.isFieldBoolean())
        assertEquals(5, testVO1.getFieldInt())
        assertEquals(9.865d, testVO1.getFieldDouble(), 0)
        assertEquals(1.865f, testVO1.getFieldFloat(), 0)
        assertEquals("test-row", testVO1.getFieldText())
        assertEquals(localDateTime, testVO1.getFieldDate())

        assertEquals(2, testVO.size())
    }

    @Test
    void testQueryByVararrg() {
        List<TestVO> testVOList = namedParameterTemplate.query(SELECT_ALL, testVOMapper)

        def testVO0 = testVOList.get(0)
        assertEquals(true, testVO0.isFieldBoolean())
        assertEquals(100, testVO0.getFieldInt())
        assertEquals(3.865d, testVO0.getFieldDouble(), 0)
        assertEquals(3.865f, testVO0.getFieldFloat(), 0)
        assertEquals("test-row", testVO0.getFieldText())
        assertEquals(localDateTime, testVO0.getFieldDate())

        def testVO1 = testVOList.get(1)
        assertEquals(false, testVO1.isFieldBoolean())
        assertEquals(5, testVO1.getFieldInt())
        assertEquals(9.865d, testVO1.getFieldDouble(), 0)
        assertEquals(1.865f, testVO1.getFieldFloat(), 0)
        assertEquals("test-row", testVO1.getFieldText())
        assertEquals(localDateTime, testVO1.getFieldDate())

        assertEquals(2, testVOList.size())
    }

    @Test
    void testQueryForObjectByMap() {
        def queryParamMap = [fboolean: true]
        TestVO testVO = namedParameterTemplate.queryForObject(SELECT_ROW_BINDVAR_QUERY, testVOMapper, queryParamMap)
        assertEquals(true, testVO.isFieldBoolean())
        assertEquals(100, testVO.getFieldInt())
        assertEquals(3.865d, testVO.getFieldDouble(), 0)
        assertEquals(3.865f, testVO.getFieldFloat(), 0)
        assertEquals("test-row", testVO.getFieldText())
        assertEquals(localDateTime, testVO.getFieldDate())
    }

    @Test
    void testQueryForObjectByVarrarg() {
        TestVO testVO = namedParameterTemplate.queryForObject(SELECT_ROW_PLACEHOLDER_QUERY, testVOMapper, false)
        assertEquals(false, testVO.isFieldBoolean())
        assertEquals(5, testVO.getFieldInt())
        assertEquals(9.865d, testVO.getFieldDouble(), 0)
        assertEquals(1.865f, testVO.getFieldFloat(), 0)
        assertEquals("test-row", testVO.getFieldText())
        assertEquals(localDateTime, testVO.getFieldDate())
    }

    @Test
    void testUpdateByMap() {
        def updateParamMap = [fdouble: 100.001d, ftext: "test-row"]
        def deleteParamMap = [fboolean: true]
        def selectParamMap = [fboolean: true]

        // INSERT tested in setUp method

        // UPDATE
        def updatedRows = namedParameterTemplate.update(UPDATE_BINDVAR_QUERY, updateParamMap)
        TestVO testVO = namedParameterTemplate.queryForObject(SELECT_ROW_BINDVAR_QUERY, testVOMapper, selectParamMap)
        assertEquals(2, updatedRows)
        assertEquals(true, testVO.isFieldBoolean())
        assertEquals(100, testVO.getFieldInt())
        assertEquals(100.001d, testVO.getFieldDouble(), 0)
        assertEquals(3.865f, testVO.getFieldFloat(), 0)
        assertEquals("test-row", testVO.getFieldText())
        assertEquals(localDateTime, testVO.getFieldDate())

        // DELETE
        def deletedRows = namedParameterTemplate.update(DELETE_BINDVAR_QUERY, deleteParamMap)
        assertEquals(1, deletedRows)

        // Check if exception thrown
        expectedEx.expect(EntityBuilderException.class)
        expectedEx.expectMessage("Empty result set")
        namedParameterTemplate.queryForObject(SELECT_ROW_BINDVAR_QUERY, testVOMapper, selectParamMap)
    }

    @Test
    void testUpdateByVararrg() {
        // INSERT tested in setUp method

        // UPDATE
        def updatedRows = namedParameterTemplate.update(UPDATE_PLACEHOLDER_QUERY, 100.001d, "test-row")
        TestVO updatedTestVO = namedParameterTemplate.queryForObject(SELECT_ROW_PLACEHOLDER_QUERY, testVOMapper, true)
        assertEquals(2, updatedRows)
        assertEquals(true, updatedTestVO.isFieldBoolean())
        assertEquals(100, updatedTestVO.getFieldInt())
        assertEquals(100.001d, updatedTestVO.getFieldDouble(), 0)
        assertEquals(3.865f, updatedTestVO.getFieldFloat(),0)
        assertEquals("test-row", updatedTestVO.getFieldText())
        assertEquals(localDateTime, updatedTestVO.getFieldDate())

        // DELETE
        def deletedRows = namedParameterTemplate.update(DELETE_PLACEHOLDER_QUERY, true)
        assertEquals(1, deletedRows)


    }

    @Test
    void testForEmptyResultSet() {
        // Delete all data in table
        namedParameterTemplate.update(DELETE_ALL)

        expectedEx.expect(EntityBuilderException.class)
        expectedEx.expectMessage("Empty result set")
        namedParameterTemplate.queryForObject(SELECT_ROW_PLACEHOLDER_QUERY, testVOMapper, true)
    }

    @Test
    void testForEmptyResultSetForList() {
        // Delete all data in table
        namedParameterTemplate.update(DELETE_ALL)

        expectedEx.expect(EmptyResultDataAccessException.class)
        expectedEx.expectMessage("Empty result set")
        namedParameterTemplate.query(SELECT_ALL, testVOMapper)
    }

    @Test
    void testForMultipleRowsInResultSet() {
        expectedEx.expect(IncorrectResultSizeDataAccessException.class)
        expectedEx.expectMessage("More then one row in result set")
        namedParameterTemplate.queryForObject(SELECT_ALL, testVOMapper)
    }

    // Null
    @Test
    void testSetNull() {
        // Delete all data in table
        namedParameterTemplate.update(DELETE_ALL)
        namedParameterTemplate.update(INSERT_PLACEHOLDER_QUERY, true, 1, 9.865d, 1.865f, null, localDateTime)
        TestVO updatedTestVO = namedParameterTemplate.queryForObject(SELECT_ALL, testVOMapper)
        assertNull(updatedTestVO.getFieldText())
    }
}
