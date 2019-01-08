package th.co.toyota.application.annotation;

import java.io.IOException;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.jdbc.JdbcTestUtils;

public class InjectDataTestExecutionListener extends AbstractTestExecutionListener  {
    final static Logger         logger   = LoggerFactory.getLogger(InjectDataTestExecutionListener.class);

    private static JdbcTemplate jdbcTemplate;

    private static DataSource   datasource;

    private static String       ENCODING = "UTF-8";

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        super.beforeTestClass(testContext);

        Method method = testContext.getTestMethod();
        SqlFileLocation dsLocation = method.getAnnotation(SqlFileLocation.class);
        if (dsLocation != null && dsLocation.enabled()) {
            logger.debug("dsLocation.value() " + dsLocation.value());
            executeSqlScript(testContext, dsLocation.value() + ".sql");
        }
    }
    
    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        super.beforeTestClass(testContext);

        Method method = testContext.getTestMethod();
        SqlFileLocation dsLocation = method.getAnnotation(SqlFileLocation.class);
        if (dsLocation != null && dsLocation.enabled()) {
            logger.debug("dsLocation.value() " + dsLocation.value());
            executeSqlScript(testContext, dsLocation.value() + "_Del.sql");
        }
    }
    
    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        super.beforeTestClass(testContext);

        Class<?> cls = testContext.getTestClass();
        SqlFileLocation dsLocation = cls.getAnnotation(SqlFileLocation.class);
        if (dsLocation != null && dsLocation.enabled()) {
            logger.debug("dsLocation.value() " + dsLocation.value());
            executeSqlScript(testContext, dsLocation.value() + ".sql");
        }
    }
    
    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        super.beforeTestClass(testContext);

        Class<?> cls = testContext.getTestClass();
        SqlFileLocation dsLocation = cls.getAnnotation(SqlFileLocation.class);
        if (dsLocation != null && dsLocation.enabled()) {
            logger.debug("dsLocation.value() " + dsLocation.value());
            executeSqlScript(testContext, dsLocation.value() + "_Del.sql");
        }
    }
    
    private void executeSqlScript(TestContext testContext, String sqlResourcePath) throws DataAccessException {
        JdbcTemplate jdbcTemplate = getJdbCTemplate(getDatasource(testContext));
        Resource resource = testContext.getApplicationContext().getResource(sqlResourcePath);
        executeSqlScript(jdbcTemplate, new EncodedResource(resource, ENCODING));
    }

    private DataSource getDatasource(TestContext testContext) {
        if (datasource == null) {
            datasource = (DataSource) testContext.getApplicationContext().getBean("dataSource");
        }
        return datasource;
    }

    private JdbcTemplate getJdbCTemplate(DataSource datasource) {
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(datasource);
        }
        return jdbcTemplate;
    }

    private static void executeSqlScript(JdbcTemplate simpleJdbcTemplate, EncodedResource resource)
            throws DataAccessException {
        logger.debug("executeSqlScript....");
        List<String> statements = new LinkedList<String>();
        try {
            LineNumberReader lnr = new LineNumberReader(resource.getReader());
            String script = JdbcTestUtils.readScript(lnr);
            char delimiter = ';';
            if (!JdbcTestUtils.containsSqlScriptDelimiters(script, delimiter)) {
                delimiter = '\n';
            }
            JdbcTestUtils.splitSqlScript(script, delimiter, statements);
            for (String statement : statements) {
                try {
                    //                    logger.debug("statement:" + statement.toString());
                    simpleJdbcTemplate.update(statement);
                }
                catch (DataAccessException ex) {
                    throw ex;
                }
            }
        }
        catch (IOException ex) {
            throw new DataAccessResourceFailureException("Impossible d'ouvrir le script depuis " + resource, ex);
        }
    }
}
