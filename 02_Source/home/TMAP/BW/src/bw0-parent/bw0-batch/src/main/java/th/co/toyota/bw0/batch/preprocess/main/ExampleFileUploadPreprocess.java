/******************************************************
 * Program History
 * 
 * Project Name	            :  
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.batch.preprocess.main
 * Program ID 	            :  ExampleFileUploadPreprocess.java
 * Program Description	    :  Example File Upload PreProcess
 * Environment	 	    	:  Java 7
 * Author		    		:  Thanapon T.
 * Version		    		:  1.0
 * Creation Date            :  January, 14 2018
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.bw0.batch.preprocess.main;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import th.co.toyota.bw0.api.common.CommonUtility;
import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.repository.common.CommonAPIRepository;
import th.co.toyota.bw0.batch.preprocess.service.ExampleFileUploadPreprocessService;
import th.co.toyota.config.AppConfig;
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
	private ExampleFileUploadPreprocessService preprocessService;

	@Autowired
	private CommonAPIRepository commonRepository;
	
	private String batchName = "Example File Upload preprocess";

	//for test on your computer
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ApplicationContext appContext = new AnnotationConfigApplicationContext(AppConfig.class);
		ExampleFileUploadPreprocess main = appContext.getBean(ExampleFileUploadPreprocess.class);
		CST31250FileReceivingCmdOptions arg0 = new CST31250FileReceivingCmdOptions();
		arg0.setModuleId(AppConstants.MODULE_ID_BW02);
		arg0.setFunctionId("BBW02130");
		arg0.setApplicationId("1");
		arg0.setUser("timuser01");
		
		List<String> additionalPrameters = new ArrayList<>();
		additionalPrameters.add("timuser01");//user id
		additionalPrameters.add("1");//application id
		additionalPrameters.add("Example_data.xlsx");//file name
		
		arg0.setAdditionalPrameters(additionalPrameters);
		main.preProcessInterfaceFileData(arg0);
	}

	@Override
	public int preProcessInterfaceFileData(CST31250FileReceivingCmdOptions arg0) {
		String appId = arg0.getApplicationId();
		String createBy = arg0.getUser();
		StringBuilder paramStr1 = new StringBuilder();
		StringBuilder paramStr2 = new StringBuilder();
		StringBuilder paramStr3 = new StringBuilder();
		int result = CST30000Constants.SUCCESS;
		try(Connection conn = commonRepository.getConnection()) {
			String msg = messageSource.getMessage(CST30000Messages.INFO_PROCESS_START, new String[] { batchName }, Locale.getDefault());
			logger.info(msg);
			loggerBBW02130.start(appId, CST30000Messages.INFO_PROCESS_START, msg, createBy);

			result = preprocessService.validate(conn, arg0);

			if ( (result == CST30000Constants.SUCCESS || result == CST30000Constants.WARNING) && preprocessService.totalRead > 0) {
				// Manage Transaction
				Object[] resultChk = preprocessService.manageTransaction(conn);
				result =(int)resultChk[0];
			}
				
			paramStr1.append("TOTAL SUMMARY INPUT: TB_S_KOMPO : Total read records=").append(preprocessService.totalRead);
			
			paramStr2.append(" OUTPUT:");
			paramStr2.append(" TB_R_KOMPO : Total inserted records = ").append(preprocessService.insertedCnt);
			paramStr2.append(", Total warning records = ").append(preprocessService.warningCnt);
			paramStr2.append(", Total error records = ").append(preprocessService.errorCnt);
			
			paramStr3.append("Upload file: "+ preprocessService.getParamVo().getFileName());
			
			if (result == CST30000Constants.SUCCESS) {
				msg = messageSource.getMessage(
						CST30000Messages.INFO_PROCESS_END_SUCCESS,
						new String[] { batchName, paramStr1.toString(), paramStr2.toString(), paramStr3.toString() },
						Locale.getDefault());
				logger.info(msg);
				loggerBBW02130.end(appId, CST30000Messages.INFO_PROCESS_END_SUCCESS, msg, createBy);
			}else if (result == CST30000Constants.ERROR) {				
				msg = messageSource.getMessage(
						CST30000Messages.INFO_PROCESS_END_ERROR,
						new String[] { batchName, paramStr1.toString(), paramStr2.toString(), paramStr3.toString() },
						Locale.getDefault());
				logger.error(msg);
				loggerBBW02130.endError(appId, CST30000Messages.INFO_PROCESS_END_ERROR, msg, createBy);
			}else if (result == CST30000Constants.WARNING) {				
				msg = messageSource.getMessage(
						CST30000Messages.INFO_PROCESS_END_WARNING,
						new String[] { batchName, paramStr1.toString(), paramStr2.toString(), paramStr3.toString() },
						Locale.getDefault());
				logger.warn(msg);
				loggerBBW02130.endError(appId, CST30000Messages.INFO_PROCESS_END_WARNING, msg, createBy);
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
		}
	}
}
