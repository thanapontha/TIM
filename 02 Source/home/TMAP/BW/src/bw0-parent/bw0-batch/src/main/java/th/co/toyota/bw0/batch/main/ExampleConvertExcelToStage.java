/******************************************************
 * Program History
 * 
 * Project Name	            :  GWRDS : 
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.batch.main
 * Program ID 	            :  CBW02130ExcelToStage.java
 * Program Description	    :  KOMPO Upload
 * Environment	 	    	:  Java 7
 * Author		    		:  Thanawut T.
 * Version		    		:  1.0
 * Creation Date            :  04 September 2017
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
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
import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.batch.common.CBW00000BatchUtil;
import th.co.toyota.bw0.batch.service.CBW02130Service;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.config.AppConfig;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.util.CST32010DocNoGenerator;
import th.co.toyota.st3.api.util.IST30000LoggerDb;

import com.google.common.base.Strings;

public class ExampleConvertExcelToStage {

	final Logger logger = LoggerFactory.getLogger(ExampleConvertExcelToStage.class);

	@Autowired
	private IST30000LoggerDb loggerBBW02130;

	@Autowired
	protected MessageSource messageSource;

	@Autowired
	protected CBW00000BatchUtil batchUtil;
	
	@Autowired
	private CBW02130Service service;
	
	
	@Autowired
	private CST32010DocNoGenerator docNoGenerator;
	
	public static final Integer IDX_VALIDATE_STATUS = 0;
	public static final Integer IDX_ROWS_EFFECTED = 1;
	
	public static final Integer IDX_PARAM_USER_COMPANY_LOGIN = 0;
	public static final Integer IDX_PARAM_PAMS_KOMPO_FLAG = 1;
	public static final Integer IDX_PARAM_GETSUDO_MONTH = 2;
	public static final Integer IDX_PARAM_TIMING = 3;
	public static final Integer IDX_PARAM_VEHICLE_PLANT = 4;
	public static final Integer IDX_PARAM_VEHICLE_MODEL = 5;
	public static final Integer IDX_PARAM_UNIT_PLANT = 6;
	public static final Integer IDX_PARAM_UNIT_TYPE = 7;
	public static final Integer IDX_PARAM_UNIT_MODEL = 8;
	public static final Integer IDX_PARAM_FILE_NAME = 9;
	public static final Integer IDX_PARAM_USER_LOGIN = 10;
	public static final Integer IDX_PARAM_APP_ID = 11;
	
	private static String selectSep = AppConstants.BATCH_CHARACTOR_REPLACE_SELECTED_MULTI_UNIT_BACK;
	
	public static void main(String[] args) {	
		String[] params = {
				"TDEM", //param 1: Version (User Company Login TMAP-MS or TDEM)
				"K", //param 2 : Upload Type P(PAMs Rundown), K(Kompokung)
				"Jan-19", //paam 3: Getsudo Month
				"D-14", // param 4: Timing
				"ASSB-Plant1", // param 5: Vehicle Plant
				"Camry", // param 6: Vehicle Model				
				"STM^#^STM", // param 7: Unit Plant
				"Engine^#^Engine", // param 8: Unit Type
				"1NR^#^2NR", // param 9: Unit Model
				"Test_Incident_0003_6_Kompo.xlsx", // param 10: File name
				"gwrds04", //param 11: User Login
				"1800001626", //param 12: Application ID
				//TDEM::K::Sep-17::D-14::TMT^##^Test::C-HR_Conventional::TMT_IH::Engine::1GD::Kompo_OK_20170928032538.xlsx::suthida::000780
		};
		
		if (args.length != 0) {
			params = args;
		}

		String batchName = "Upload KOMPO Batch";
		String fileId1 = "BW02120";
		String fileId2 = "BW02130";
		String tableName1 = "TB_S_PAMS_RUNDOWN";
		String tableName2 = "TB_S_KOMPO";
		
		String createBy = "SYSTEM";
		String appId = null;
		String filename = "blankFile";
		Timestamp sysdate = FormatUtil.currentTimestampToOracleDB();
		
		String version = null;
		String getsudoMonth = null;
		String timing = null;
		String vehiclePlant = null;
		String vehicleModel = null;
		String unitPlant = null;
		String unitModel = null;
		String pamsKompoFlag = null;
		String unitType = null;
		String[] unitPlantArr = null;
		String[] unitModelArr = null;
		String[] unitTypeArr = null;
		
		String unitPlantAllSelected = null;
		String unitModelAllSelected = null;
		String unitTypeAllSelected = null;
		
		int lengthParamCheck = 12;
		if (params.length == lengthParamCheck) {
			if (!Strings.isNullOrEmpty(params[IDX_PARAM_USER_LOGIN]))
				createBy = CommonUtility.convertBatchParam(params[IDX_PARAM_USER_LOGIN]);
			if (!Strings.isNullOrEmpty(params[IDX_PARAM_APP_ID]))
				appId = CommonUtility.convertBatchParam(params[IDX_PARAM_APP_ID]);
			filename = CommonUtility.convertBatchParam(params[IDX_PARAM_FILE_NAME]);
			
			if (!Strings.isNullOrEmpty(params[IDX_PARAM_USER_COMPANY_LOGIN]))
				version = CommonUtility.convertBatchParam(params[IDX_PARAM_USER_COMPANY_LOGIN]);
			if (!Strings.isNullOrEmpty(params[IDX_PARAM_PAMS_KOMPO_FLAG]))
				pamsKompoFlag = CommonUtility.convertBatchParam(params[IDX_PARAM_PAMS_KOMPO_FLAG]);
			if (!Strings.isNullOrEmpty(params[IDX_PARAM_GETSUDO_MONTH]))
				getsudoMonth = CommonUtility.convertBatchParam(params[IDX_PARAM_GETSUDO_MONTH]);
			if (!Strings.isNullOrEmpty(params[IDX_PARAM_TIMING]))
				timing = CommonUtility.convertBatchParam(params[IDX_PARAM_TIMING]);
			if (!Strings.isNullOrEmpty(params[IDX_PARAM_VEHICLE_PLANT]))
				vehiclePlant = CommonUtility.convertBatchParam(params[IDX_PARAM_VEHICLE_PLANT]);
			if (!Strings.isNullOrEmpty(params[IDX_PARAM_VEHICLE_MODEL]))
				vehicleModel = CommonUtility.convertBatchParam(params[IDX_PARAM_VEHICLE_MODEL]);
		}
		
		// Start Spring
		ApplicationContext appContext = new AnnotationConfigApplicationContext(AppConfig.class);
		ExampleConvertExcelToStage kompoMain = appContext.getBean(ExampleConvertExcelToStage.class);
		
		if (Strings.isNullOrEmpty(appId)){
			try {
				sysdate = FormatUtil.currentTimestampToOracleDB();
				appId = kompoMain.docNoGenerator.generateDocNo(AppConstants.SEQ_CODE_APP_ID, sysdate);
			} catch (Exception e) {
				String messageCode = CST30000Messages.ERROR_UNDEFINED_ERROR;
				appId = "999990";
				String errMsg = kompoMain.messageSource.getMessage(messageCode,
						new String[] { "Can't generate APP_ID " + e.getMessage()
								+ " then use APP_ID=999990" }, Locale.getDefault());
				kompoMain.logger.error(errMsg);
				kompoMain.loggerBBW02130.error(appId, messageCode, errMsg, createBy);
			}	
		}
		int status = CST30000Constants.SUCCESS;
		int resultPreprocess = CST30000Constants.SUCCESS;
		try {
			int[] resultPamsRundown = null;
			int[] resultKompo = null;
			int totalConvert = 0;
			String msg = "";
			
			msg = kompoMain.messageSource.getMessage(CST30000Messages.INFO_PROCESS_START,	new String[] { batchName }, Locale.getDefault());
			kompoMain.logger.info(msg);
			kompoMain.loggerBBW02130.start(appId, CST30000Messages.INFO_PROCESS_START, msg, createBy);
			

			resultKompo = kompoMain.convertExcelToStaging(params, lengthParamCheck, appId, createBy, filename, fileId2, tableName2, sysdate, kompoMain.loggerBBW02130);
			totalConvert = (int)resultPamsRundown[IDX_ROWS_EFFECTED];
	        	
	        	
        	if (resultKompo[IDX_VALIDATE_STATUS] == CST30000Constants.ERROR) {
				msg = kompoMain.messageSource.getMessage(
								CST30000Messages.INFO_PROCESS_END_ERROR, new String[] {
								batchName, "(Please see details on above log)", "Upload file:" + filename, "" },
						Locale.getDefault());
				kompoMain.logger.info(msg);
				kompoMain.loggerBBW02130
						.endError(appId,
								CST30000Messages.INFO_PROCESS_END_ERROR, msg,
								createBy);
				status = CST30000Constants.ERROR;
				
				kompoMain.batchUtil.archiveFile(filename, "", true);
				
			} else {
				msg = kompoMain.messageSource.getMessage(
						CST30000Messages.INFO_PROCESS_END_SUCCESS,
						new String[] { batchName
									 , "(Total convert PAMs Rundown " + totalConvert + " rows"
									 , "Total convert Diagram " + resultKompo[IDX_ROWS_EFFECTED] + " rows)"
									 , "Upload file:" + filename }
						, Locale.getDefault());
				kompoMain.logger.info(msg);
				kompoMain.loggerBBW02130.end(appId,
						CST30000Messages.INFO_PROCESS_END_SUCCESS, msg,
						createBy);
	
				status = CST30000Constants.SUCCESS;
	
				kompoMain.batchUtil.archiveFile(filename, "", false);
			}
        	
//		} catch (CommonErrorException e) {
//			String errMsg = kompoMain.messageSource.getMessage(e.getMessageCode(),e.getMessageArg(), Locale.getDefault());
//			kompoMain.logger.error(errMsg);
//			kompoMain.loggerBBW02130.error(appId, e.getMessageCode(), errMsg, createBy);
		} catch (Exception e) {
			String errMsg = kompoMain.messageSource.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR, 
					new String[] { CommonUtility.genMessageOfException(e) }, 
					Locale.getDefault());
			kompoMain.logger.error(errMsg);
			kompoMain.loggerBBW02130.error(appId, CST30000Messages.ERROR_UNDEFINED_ERROR, errMsg, createBy);
		}
		((ConfigurableApplicationContext) appContext).close();

		System.exit(status);
	}

	private int[] convertExcelToStaging(String[] params, 
								int lengthParamCheck,
								String appId,
								String createBy,
								String filename,
								String fileId,
								String tableName,
								Timestamp sysdate,
								IST30000LoggerDb loggerDb) {		
		int[] result = new int[2];
		try {
			if (!service.validateParameters(params, lengthParamCheck, appId, createBy, filename, fileId, sysdate)) {
				logger.debug("error when validate parameters");
				result[IDX_VALIDATE_STATUS] = CST30000Constants.ERROR;
				result[IDX_ROWS_EFFECTED] = 0;
				return result;
			}
			// convert excel to staging
			service.loggerDb = loggerDb;
			Object[] cvResult = service.convertExcelToStaging(tableName);
			boolean cvValid = (boolean)cvResult[IDX_VALIDATE_STATUS];
			result[IDX_VALIDATE_STATUS] = cvValid?CST30000Constants.SUCCESS:CST30000Constants.ERROR;
			result[IDX_ROWS_EFFECTED] = (int)cvResult[IDX_ROWS_EFFECTED];
			return result;
		} catch (Exception e) {
			result[IDX_VALIDATE_STATUS] = CST30000Constants.ERROR;
			result[IDX_ROWS_EFFECTED] = 0;
			return result;
		}
	}
}
