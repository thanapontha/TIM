/******************************************************
 * Program History
 * 
 * Project Name	            :  GWRDS : 
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.batch.preprocess.main
 * Program ID 	            :  ExampleFileUploadPreprocess.java
 * Program Description	    :  Example File Upload PreProcess
 * Environment	 	    	:  Java 7
 * Author		    		:  Thanapon T.
 * Version		    		:  1.0
 * Creation Date            :  January, 11 2018
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.bw0.batch.preprocess.main;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.common.base.Strings;

import th.co.toyota.config.AppConfig;
import th.co.toyota.bw0.api.common.CommonUtility;
import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.constants.MessagesConstants;
import th.co.toyota.bw0.api.repository.common.CommonAPIRepository;
import th.co.toyota.bw0.batch.preprocess.service.ExampleFileUploadPreprocessService;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.util.IST30000LoggerDb;
import th.co.toyota.st3.batch.receiving.CST31250FileReceivingCmdOptions;
import th.co.toyota.st3.batch.receiving.IST31250PreProcessClass;

public class ExampleFileUploadPreprocess implements IST31250PreProcessClass {

	final Logger logger = LoggerFactory.getLogger(ExampleFileUploadPreprocess.class);

	@Autowired
	private IST30000LoggerDb loggerBBW02130;

	@Autowired
	protected MessageSource messageSource;
	
	@Autowired
	private ExampleFileUploadPreprocessService serviceKompo;

	@Autowired
	private CommonAPIRepository repository;
	private String batchName = "Upload KOMPO preprocess";
	
	public Timestamp sysdate;

	public int unitModelCount = 0;
	public boolean errConcurrentcy = false;
	
	//for test on your computer
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ApplicationContext appContext = new AnnotationConfigApplicationContext(AppConfig.class);
		ExampleFileUploadPreprocess main = appContext.getBean(ExampleFileUploadPreprocess.class);
		CST31250FileReceivingCmdOptions arg0 = new CST31250FileReceivingCmdOptions();
		arg0.setModuleId(AppConstants.MODULE_ID_BW02);
		arg0.setFunctionId("BBW02130");
		arg0.setApplicationId("1800000672");
		arg0.setUser("gwrds01");
		
		List<String> additionalPrameters = new ArrayList<>();
		additionalPrameters.add("TDEM");
		additionalPrameters.add("K");
		additionalPrameters.add("Jan-18");
		additionalPrameters.add("D-14");
		additionalPrameters.add("IMC");
		additionalPrameters.add("IMV3");//5
		additionalPrameters.add("TMT-IH");
		additionalPrameters.add("Other");
		additionalPrameters.add("IH");
		additionalPrameters.add("Kompo_OK_Sep_11_20171016061037.xlsx");
		additionalPrameters.add("gwrds01");
		additionalPrameters.add("1800000721");

		//TDEM K Sep-17 D-11 TMT^##^Test Hiace TMT_IH Engine 1GD Kompo_OK_Sep_11_20171016061037.xlsx suthida 001811
		
		arg0.setAdditionalPrameters(additionalPrameters);
		main.preProcessInterfaceFileData(arg0);
	}

	@Override
	public int preProcessInterfaceFileData(CST31250FileReceivingCmdOptions arg0) {
		String appId = arg0.getApplicationId();
		String createBy = arg0.getUser();
		Connection conn = null;
		StringBuilder paramStr1 = new StringBuilder();
		StringBuilder paramStr2 = new StringBuilder();
		StringBuilder paramStr3 = new StringBuilder();
		try {
			conn = repository.getConnection();
			
			int result = CST30000Constants.SUCCESS;
			// step 1 : write start log
			String msg = messageSource.getMessage(CST30000Messages.INFO_PROCESS_START, new String[] { batchName }, Locale.getDefault());
			logger.info(msg);
			loggerBBW02130.start(appId, CST30000Messages.INFO_PROCESS_START, msg, createBy);
			
			serviceKompo.sysdate = this.sysdate;

			int resultKompo = serviceKompo.validate(conn, arg0);
			
			//Save data to result table
			if ( (resultKompo == CST30000Constants.SUCCESS || resultKompo == CST30000Constants.WARNING) 
					&& serviceKompo.totalRead > 0) {

				// Manage Transaction
				Object[] resultChk = serviceKompo.manageTransaction(conn, resultKompo);
				
				resultKompo =(int)resultChk[0];
				String errcode = Strings.nullToEmpty((String)resultChk[2]);
				if(MessagesConstants.B_ERROR_CONCURRENTCY.equals(errcode)){
					errConcurrentcy = true;
				}
			}
				
			paramStr1.append("TOTAL SUMMARY INPUT: TB_S_PAMS_RUNDOWN : Total read records=").append("");
			paramStr1.append(" TB_S_KOMPO : Total read records=").append(serviceKompo.totalRead);
			
			paramStr2.append(" OUTPUT:");
			paramStr2.append(" TB_R_RUNDOWN_KOMPO_STS : Total inserted records = " + unitModelCount);
			paramStr2.append(" TB_R_PAMS_RUNDOWN : Total inserted records = ").append(serviceKompo.insertedPamsCnt);
			paramStr2.append(" TB_R_KOMPO : Total inserted records = ").append(serviceKompo.insertedKompoCnt);
			paramStr2.append(", Total warning records = ").append(serviceKompo.warningCnt);
			paramStr2.append(", Total error records = ").append(serviceKompo.errorCnt);
			
			paramStr3.append("Upload file: "+ serviceKompo.fileName);
			
			if (resultKompo == CST30000Constants.SUCCESS) {
				msg = messageSource.getMessage(
						CST30000Messages.INFO_PROCESS_END_SUCCESS,
						new String[] { batchName, paramStr1.toString(), paramStr2.toString(), paramStr3.toString() },
						Locale.getDefault());
				logger.info(msg);
				loggerBBW02130.end(appId, CST30000Messages.INFO_PROCESS_END_SUCCESS, msg, createBy);
				
				result = CST30000Constants.SUCCESS;
			}else if (resultKompo == CST30000Constants.ERROR) {				
				msg = messageSource.getMessage(
						CST30000Messages.INFO_PROCESS_END_ERROR,
						new String[] { batchName, paramStr1.toString(), paramStr2.toString(), paramStr3.toString() },
						Locale.getDefault());
				logger.error(msg);
				loggerBBW02130.endError(appId, CST30000Messages.INFO_PROCESS_END_ERROR, msg, createBy);
				
				result = CST30000Constants.ERROR;
			}
			return result;
		} catch (Exception e) {		
			String errMsg = messageSource
					.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR,
							new String[] { CommonUtility.genMessageOfException(e) },
							Locale.getDefault());
			logger.error(errMsg);
			loggerBBW02130.error(appId, CST30000Messages.ERROR_UNDEFINED_ERROR,
					errMsg, createBy);
			
			errMsg = messageSource.getMessage(
					CST30000Messages.INFO_PROCESS_END_ERROR,
					new String[] { batchName, paramStr1.toString(), paramStr2.toString(), paramStr3.toString() },
					Locale.getDefault());
			logger.error(errMsg);
			loggerBBW02130.endError(appId, CST30000Messages.INFO_PROCESS_END_ERROR, errMsg, createBy);

			return CST30000Constants.ERROR;
		}finally{
			try {
				if(conn!=null && !conn.isClosed()){
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
