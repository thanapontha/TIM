package th.co.toyota.bw0.batch.main;

import static org.junit.Assert.assertNotNull;

import java.sql.Timestamp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.toyota.bw0.batch.service.ExampleConvertExcelToStageService;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.config.AppTestConfig;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {AppTestConfig.class})
public class ExampleConvertExcelToStageTest {
	
	Timestamp sysdate;
	
	@Autowired
	private ExampleConvertExcelToStageService service;

	@Before
	public void setUp() throws Exception {
		
		
		sysdate = FormatUtil.currentTimestampToOracleDB();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getAppIdWithAppIdNullAppIdShouldHaveData() {
//		String appId = service.getAppId(null, sysdate, "tester");
//		assertNotNull(appId);
	}
}
