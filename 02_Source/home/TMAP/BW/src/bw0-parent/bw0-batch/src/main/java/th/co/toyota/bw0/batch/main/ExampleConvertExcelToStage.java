/******************************************************
 * Program History
 * 
 * Project Name	            :  TIM : Toyota Insurance Management
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.batch.main
 * Program ID 	            :  ExampleConvertExcelToStage.java
 * Program Description	    :  Example Upload
 * Environment	 	    	:  Java 7
 * Author		    		:  Thanawut T.
 * Version		    		:  1.0
 * Creation Date            :  10 January 2019
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2019-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.bw0.batch.main;

import java.sql.Timestamp;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import th.co.toyota.bw0.api.common.CommonUtility;
import th.co.toyota.bw0.batch.common.CommonBatchUtil;
import th.co.toyota.bw0.batch.service.ExampleConvertExcelToStageService;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.config.AppConfig;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.util.IST30000LoggerDb;

import com.google.common.base.Strings;

public class ExampleConvertExcelToStage {

	final Logger logger = LoggerFactory.getLogger(ExampleConvertExcelToStage.class);

	@Autowired
	private IST30000LoggerDb loggerBBW02130;

	@Autowired
	protected MessageSource messageSource;

	@Autowired
	protected CommonBatchUtil batchUtil;

	@Autowired
	private ExampleConvertExcelToStageService service;

	public static final Integer IDX_VALIDATE_STATUS = 0;
	public static final Integer IDX_ROWS_EFFECTED = 1;

	public static final Integer IDX_PARAM_USER_LOGIN = 0;
	public static final Integer IDX_PARAM_APP_ID = 1;
	public static final Integer IDX_PARAM_FILE_NAME = 2;

	public static void main(String[] args) {
		String[] params = { "timuser01", // param 1: User Login
				"1", // param 2: Application ID
				"Example_data.xlsx" // param 3: file name
				// timuser01::1::Example_data.xlsx
		};

		if (args.length != 0) {
			params = args;
		}

		String batchName = "Example Upload Batch";
		String fileId = "BW02130";
		String tableName = "TB_S_KOMPO";

		String createBy = "SYSTEM";
		String appId = null;
		String fileName = "blankFile";
		Timestamp sysdate = FormatUtil.currentTimestampToOracleDB();

		int lengthParamCheck = 3;
		if (params.length == lengthParamCheck) {
			if (!Strings.isNullOrEmpty(params[IDX_PARAM_USER_LOGIN]))
				createBy = CommonUtility.convertBatchParam(params[IDX_PARAM_USER_LOGIN]);
			if (!Strings.isNullOrEmpty(params[IDX_PARAM_APP_ID]))
				appId = CommonUtility.convertBatchParam(params[IDX_PARAM_APP_ID]);
			fileName = CommonUtility.convertBatchParam(params[IDX_PARAM_FILE_NAME]);

		}

		// Start Spring
		ApplicationContext appContext = new AnnotationConfigApplicationContext(AppConfig.class);
		ExampleConvertExcelToStage exampleConvertExcelToStage = appContext.getBean(ExampleConvertExcelToStage.class);

		//Get App ID
		appId = exampleConvertExcelToStage.service.getAppId(appId, sysdate, createBy);
		
		int status = CST30000Constants.SUCCESS;
		try {
			int[] result = new int[2];
			String msg = "";
			boolean archiveFlag = false;

			// Log start process
			msg = exampleConvertExcelToStage.messageSource.getMessage(CST30000Messages.INFO_PROCESS_START,
					new String[] { batchName }, Locale.getDefault());
			exampleConvertExcelToStage.logger.info(msg);
			exampleConvertExcelToStage.loggerBBW02130.start(appId, CST30000Messages.INFO_PROCESS_START, msg, createBy);

			// Process : Convert Excel To Staging table
			if (exampleConvertExcelToStage.service
					.validateParameters(params, lengthParamCheck, appId, createBy, fileName, fileId)) {
				result = exampleConvertExcelToStage.convertExcelToStaging(tableName, exampleConvertExcelToStage.loggerBBW02130);
			} else {
				exampleConvertExcelToStage.logger.debug("error when validate parameters");
				result[IDX_VALIDATE_STATUS] = CST30000Constants.ERROR;
				result[IDX_ROWS_EFFECTED] = 0;
			}

			// process result management
			if (result[IDX_VALIDATE_STATUS] == CST30000Constants.ERROR) {
				status = CST30000Constants.ERROR;
				archiveFlag = true;
			} else {
				status = CST30000Constants.SUCCESS;
				archiveFlag = false;
			}

			// Archive File
			exampleConvertExcelToStage.batchUtil.archiveFile(fileName, "", archiveFlag);

			// Log end process
			if (result[IDX_VALIDATE_STATUS] == CST30000Constants.ERROR) {
				msg = exampleConvertExcelToStage.messageSource.getMessage(CST30000Messages.INFO_PROCESS_END_ERROR,
						new String[] { batchName, "(Please see details on above log)", "Upload file:" + fileName, "" },
						Locale.getDefault());
				exampleConvertExcelToStage.logger.info(msg);
				exampleConvertExcelToStage.loggerBBW02130.endError(appId, CST30000Messages.INFO_PROCESS_END_ERROR, msg, createBy);
			} else {
				msg = exampleConvertExcelToStage.messageSource.getMessage(CST30000Messages.INFO_PROCESS_END_SUCCESS,
						new String[] { batchName, "(Total convert Example data " + result[IDX_ROWS_EFFECTED] + " rows)",
								"Upload file:" + fileName, "" },
						Locale.getDefault());
				exampleConvertExcelToStage.logger.info(msg);
				exampleConvertExcelToStage.loggerBBW02130.end(appId, CST30000Messages.INFO_PROCESS_END_SUCCESS, msg, createBy);
			}

		} catch (Exception e) {
			String errMsg = exampleConvertExcelToStage.messageSource.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR,
					new String[] { CommonUtility.genMessageOfException(e) }, Locale.getDefault());
			exampleConvertExcelToStage.logger.error(errMsg);
			exampleConvertExcelToStage.loggerBBW02130.error(appId, CST30000Messages.ERROR_UNDEFINED_ERROR, errMsg, createBy);
		}

		// End Spring
		((ConfigurableApplicationContext) appContext).close();

		System.exit(status);
	}
	
	private int[] convertExcelToStaging(String tableName, IST30000LoggerDb loggerDb) {
		int[] result = new int[2];
		try {
			// convert excel to staging
			service.loggerDb = loggerDb;
			Object[] cvResult = service.convertExcelToStaging(tableName);
			boolean cvValid = (boolean) cvResult[IDX_VALIDATE_STATUS];
			result[IDX_VALIDATE_STATUS] = cvValid ? CST30000Constants.SUCCESS : CST30000Constants.ERROR;
			result[IDX_ROWS_EFFECTED] = (int) cvResult[IDX_ROWS_EFFECTED];
			return result;
		} catch (Exception e) {
			result[IDX_VALIDATE_STATUS] = CST30000Constants.ERROR;
			result[IDX_ROWS_EFFECTED] = 0;
			return result;
		}
	}
	
}
