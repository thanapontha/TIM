/******************************************************
 * Program History
 * 
 * Project Name	            :  GWRDS : 
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.batch.main
 * Program ID 	            :  CBW02120ExcelToStage.java
 * Program Description	    :  PAMs Rundown Upload
 * Environment	 	    	:  Java 7
 * Author		    		:  Thanapon T.
 * Version		    		:  1.0
 * Creation Date            :  18 August 2017
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.bw0.batch.main;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import th.co.toyota.config.AppConfig;
import th.co.toyota.bw0.api.common.CBW00000Util;
import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.constants.MessagesConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.api.repository.common.IBW00000Repository;
import th.co.toyota.bw0.api.service.common.CBW00000CommonService;
import th.co.toyota.bw0.batch.common.CBW00000BatchUtil;
import th.co.toyota.bw0.batch.preprocess.main.CBW02120Preprocess;
import th.co.toyota.bw0.batch.preprocess.repository.IBW02120PreprocessRepository;
import th.co.toyota.bw0.batch.service.CBW02120Service;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.util.CST32010DocNoGenerator;
import th.co.toyota.st3.api.util.IST30000LoggerDb;
import th.co.toyota.st3.batch.receiving.CST31250FileReceivingCmdOptions;

import com.google.common.base.Strings;

public class CBW02120ExcelToStage {

	final Logger logger = LoggerFactory.getLogger(CBW02120ExcelToStage.class);

	@Autowired
	private IST30000LoggerDb loggerBBW02120;

	@Autowired
	protected MessageSource messageSource;

	@Autowired
	protected CBW00000BatchUtil batchUtil;

	@Autowired
	private CBW00000CommonService commonService;
	
	@Autowired
	private CBW02120Service service;
	
	@Autowired
	private CBW02120Preprocess preprocess;
	
	@Autowired
	private IBW00000Repository commonRepository;
	
	@Autowired
	private IBW02120PreprocessRepository repositoryPreprocess;
	
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
				"R", //param 2 : Upload Type P(PAMs Rundown), K(Kompokung)
				"Jan-18", //paam 3: Getsudo Month
				"D-14", // param 4: Timing
				"TMMIN-Plant2", // param 5: Vehicle Plant
				"EFC-HB", // param 6: Vehicle Model				
				"AI-TH", // param 7: Unit Plant
				"Transmission", // param 8: Unit Type
				"C50", // param 9: Unit Model
				"PAMs_EFC-HB D-14_Zero.xlsx", // param 10: File name
				"gwrds03", //param 11: User Login
				"1800000595", //param 12: Application ID
		};
//		TDEM R Sep-17 D-11 TMT^##^Test Hiace TMT_IH Engine 1GD PAMS_OK_Hiace_1GD_20171020102813.xlsx suthida 002155
		
		
		if (args.length != 0) {
			params = args;
		}

		String batchName = "Upload PAMs Rundown Batch";
		String fileId = "BW02120";
		String tableName = "TB_S_PAMS_RUNDOWN";
		
		String createBy = AppConstants.SYSTEM_NAME;
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
		
		String unitPlantAllSelect = null;
		String unitModelAllSelected = null;
		String unitTypeAllSelected = null;
		
		int lengthParamCheck = 12;
		if (params.length == lengthParamCheck) {
			if (!Strings.isNullOrEmpty(params[IDX_PARAM_USER_LOGIN]))
				createBy = CBW00000Util.convertBatchParam(params[IDX_PARAM_USER_LOGIN]);
			if (!Strings.isNullOrEmpty(params[IDX_PARAM_APP_ID]))
				appId = CBW00000Util.convertBatchParam(params[IDX_PARAM_APP_ID]);
			filename = CBW00000Util.convertBatchParam(params[IDX_PARAM_FILE_NAME]);
			
			if (!Strings.isNullOrEmpty(params[IDX_PARAM_USER_COMPANY_LOGIN]))
				version = CBW00000Util.convertBatchParam(params[IDX_PARAM_USER_COMPANY_LOGIN]);
			if (!Strings.isNullOrEmpty(params[IDX_PARAM_PAMS_KOMPO_FLAG]))
				pamsKompoFlag = CBW00000Util.convertBatchParam(params[IDX_PARAM_PAMS_KOMPO_FLAG]);
			if (!Strings.isNullOrEmpty(params[IDX_PARAM_GETSUDO_MONTH]))
				getsudoMonth = CBW00000Util.convertBatchParam(params[IDX_PARAM_GETSUDO_MONTH]);
			if (!Strings.isNullOrEmpty(params[IDX_PARAM_TIMING]))
				timing = CBW00000Util.convertBatchParam(params[IDX_PARAM_TIMING]);
			if (!Strings.isNullOrEmpty(params[IDX_PARAM_VEHICLE_PLANT]))
				vehiclePlant = CBW00000Util.convertBatchParam(params[IDX_PARAM_VEHICLE_PLANT]);
			if (!Strings.isNullOrEmpty(params[IDX_PARAM_VEHICLE_MODEL]))
				vehicleModel = CBW00000Util.convertBatchParam(params[IDX_PARAM_VEHICLE_MODEL]);
			
			//CR UT-002 2018/02/16 Thanawut T. : select multiple Unit Model for Kompokung Validate
			//KOMPO
			if(AppConstants.UPLOAD_KOMPO_FLAG.equals(pamsKompoFlag)){
				if (!Strings.isNullOrEmpty(params[IDX_PARAM_UNIT_PLANT]))
					unitPlantAllSelect = CBW00000Util.convertBatchParam(params[IDX_PARAM_UNIT_PLANT]);
				if (!Strings.isNullOrEmpty(params[IDX_PARAM_UNIT_TYPE]))
					unitTypeAllSelected = CBW00000Util.convertBatchParam(params[IDX_PARAM_UNIT_TYPE]);
				if (!Strings.isNullOrEmpty(params[IDX_PARAM_UNIT_MODEL]))
					unitModelAllSelected = CBW00000Util.convertBatchParam(params[IDX_PARAM_UNIT_MODEL]);
				
				String[] unitPlantArr = unitPlantAllSelect.split(selectSep);
				unitPlant = (unitPlantArr != null) ? unitPlantArr[0] : unitPlantAllSelect;
				
				String[] unitTypeArr = unitTypeAllSelected.split(selectSep);
				unitType = (unitTypeArr != null) ? unitTypeArr[0] : unitTypeAllSelected;
				
				String[] unitModelArr = unitModelAllSelected.split(selectSep);
				unitModel = (unitModelArr != null) ? unitModelArr[0] : unitModelAllSelected;
			}else{ //PAMs
				//In Case PAMs use 1 unit model
				if (!Strings.isNullOrEmpty(params[IDX_PARAM_UNIT_PLANT]))
					unitPlant = CBW00000Util.convertBatchParam(params[IDX_PARAM_UNIT_PLANT]);
				if (!Strings.isNullOrEmpty(params[IDX_PARAM_UNIT_TYPE]))
					unitType = CBW00000Util.convertBatchParam(params[IDX_PARAM_UNIT_TYPE]);
				if (!Strings.isNullOrEmpty(params[IDX_PARAM_UNIT_MODEL]))
					unitModel = CBW00000Util.convertBatchParam(params[IDX_PARAM_UNIT_MODEL]);
			}
			//END CR UT-002 2018/02/16
		}
		// Start Spring
		ApplicationContext appContext = new AnnotationConfigApplicationContext(AppConfig.class);
		CBW02120ExcelToStage main = appContext.getBean(CBW02120ExcelToStage.class);
		
		if (Strings.isNullOrEmpty(appId)){
			try {
				Date currentTime = FormatUtil.currentTimestampToOracleDB();
				appId = main.docNoGenerator.generateDocNo(AppConstants.SEQ_CODE_APP_ID, currentTime);
			} catch (Exception e) {
				String messageCode = CST30000Messages.ERROR_UNDEFINED_ERROR;
				appId = "999999";
				String errMsg = main.messageSource.getMessage(messageCode,
						new String[] { "Can't generate APP_ID " + e.getMessage()
								+ " then use APP_ID=999999" }, Locale.getDefault());
				main.logger.error(errMsg);
				main.loggerBBW02120.error(appId, messageCode, errMsg, createBy);
			}	
		}
		int status = CST30000Constants.SUCCESS;
		int statusPreprocess = CST30000Constants.SUCCESS;
		int statusChk = CST30000Constants.SUCCESS;
		int totalConvert = 0;
		String msg = "";
		
		try {
			msg = main.messageSource.getMessage(CST30000Messages.INFO_PROCESS_START, new String[] { batchName }, Locale.getDefault());
			main.logger.info(msg);
			main.loggerBBW02120.start(appId, CST30000Messages.INFO_PROCESS_START, msg, createBy);
			
			String statusUpload = main.commonRepository.getStatusOfLogUpload(appId, null);
			if(AppConstants.STATUS_INTERRUPT.equalsIgnoreCase(statusUpload)){
				String arg1 = "update";
				String arg2 = "this operation was interupted by user";
				msg = main.messageSource.getMessage(MessagesConstants.B_ERROR_CONCURRENTCY_INTERRUPT, new String[] {arg1, arg2 },Locale.getDefault());
				main.logger.error(msg);
				main.loggerBBW02120.error(appId, MessagesConstants.B_ERROR_CONCURRENTCY_INTERRUPT, msg, createBy);
				
				main.commonRepository.updateStatusOfLogUpload(appId, createBy, AppConstants.STATUS_ERROR, AppConstants.STATUS_ERROR_DESC+" Upload PAMs Rundown.");
				statusChk = CST30000Constants.ERROR;
			}else if(Strings.isNullOrEmpty(statusUpload)){
				msg = main.messageSource.getMessage(MessagesConstants.B_ERROR_CONCURRENTCY, new String[]{},Locale.getDefault());
				main.logger.error(msg);
				main.loggerBBW02120.error(appId, MessagesConstants.B_ERROR_CONCURRENTCY, msg, createBy);
				statusChk = CST30000Constants.ERROR;
			}else{
				main.commonRepository.updateStatusOfLogUpload(appId, createBy, AppConstants.STATUS_PROCESSING, AppConstants.STATUS_PROCESSING_DESC+" Upload PAMs Rundown.");
		        
				int[] result = main.convertExcelToStaging(params, lengthParamCheck, appId, createBy, filename, fileId, tableName, sysdate, main.loggerBBW02120);
				statusChk = result[IDX_VALIDATE_STATUS];
				totalConvert = (int)result[IDX_ROWS_EFFECTED];
			}
			
			if (statusChk == CST30000Constants.ERROR) {
				msg = main.messageSource.getMessage(
								CST30000Messages.INFO_PROCESS_END_ERROR, new String[] {
								batchName, "(Please see details on above log)", "Upload file:" + filename, "" },
						Locale.getDefault());
				main.logger.info(msg);
				main.loggerBBW02120
						.endError(appId,
								CST30000Messages.INFO_PROCESS_END_ERROR, msg,
								createBy);
				status = CST30000Constants.ERROR;
				
				//Change Status to Error (E) in Log Upload Status table
				main.commonRepository.updateStatusOfLogUpload(appId, createBy, AppConstants.STATUS_ERROR, AppConstants.STATUS_ERROR_DESC+" Upload PAMs Rundown.");
				
				main.batchUtil.archiveFile(filename, main.service.companyCode, true);
				
			} else {
				msg = main.messageSource.getMessage(
						CST30000Messages.INFO_PROCESS_END_SUCCESS,
						new String[] { batchName,
								"(Total convert " + totalConvert + " rows)", "Upload file:" + filename, "" },
						Locale.getDefault());
				main.logger.info(msg);
				main.loggerBBW02120.end(appId,
						CST30000Messages.INFO_PROCESS_END_SUCCESS, msg,
						createBy);
	
				status = CST30000Constants.SUCCESS;
	
				main.batchUtil.archiveFile(filename, main.service.companyCode, false);
			}
			if(status==CST30000Constants.SUCCESS){
				CST31250FileReceivingCmdOptions arg0 = new CST31250FileReceivingCmdOptions();
				arg0.setModuleId(AppConstants.MODULE_ID_BW02);
				arg0.setFunctionId(AppConstants.FUNCTION_ID_BBW02120);
				arg0.setApplicationId(appId);
				arg0.setUser(createBy);

				List<String> additionalPrameters = new ArrayList<>();
				for(String paramObj : params){
					additionalPrameters.add(paramObj);
				}			
				arg0.setAdditionalPrameters(additionalPrameters);
				
				//Call Preprocess - Basic Validation, Business Validation, Final Process
				main.preprocess.sysdate = sysdate;
				statusPreprocess = main.preprocess.preProcessInterfaceFileData(arg0);
				
			}
			
			//Insert-Delete Log Detail
			if (status == CST30000Constants.ERROR || statusPreprocess == CST30000Constants.ERROR) {
				//INSERT log detail
				main.preprocess.insertLogDetail( new Object[] {appId, getsudoMonth, timing, vehiclePlant,
						  									   vehicleModel, unitPlant, unitModel,
						  									   createBy,sysdate});
				status = CST30000Constants.ERROR;
			}else{
				//DELETE log detail
				main.preprocess.deleteLogDetail( new Object[] {appId, getsudoMonth, timing, vehiclePlant,
						  									   vehicleModel, unitPlant, unitModel,
						  									   createBy,sysdate});
				status = CST30000Constants.SUCCESS;
			}
			
			Connection conn = null;
			try{
				String[] paramChk = new String[] {version, getsudoMonth, 
											  	  timing, vehiclePlant, vehicleModel, 
											  	  unitPlant, unitModel };
				conn = main.commonRepository.getConnection();
				String prvUpdateDt = main.commonRepository.getUpdateDateFromUploadtatusLog(appId, conn);
				String currentUpdatedDate = main.repositoryPreprocess.getConcurrencyDate(conn, paramChk, pamsKompoFlag);
				prvUpdateDt = Strings.nullToEmpty(prvUpdateDt);
				currentUpdatedDate = Strings.nullToEmpty(currentUpdatedDate);
				statusUpload = main.commonRepository.getStatusOfLogUpload(appId, null);
				if(prvUpdateDt.equals(currentUpdatedDate) && !Strings.isNullOrEmpty(statusUpload)){
					//Insert Rundown Kompo status
					String uploadStatus = main.commonService.convertRundownKompoUploadStatus(status);
					Object[] data = new Object[] {version,getsudoMonth,timing,
							  vehiclePlant, vehicleModel, unitPlant, 
							  unitType, unitModel, pamsKompoFlag, 
							  uploadStatus,"",filename, appId, 
							  createBy,sysdate,
							  createBy,sysdate};
					
					main.commonService.addRundownKompoSts(conn, data);
				}
				
			}finally{
				try {
					if(conn!=null && !conn.isClosed()){
						conn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		} catch (CommonErrorException e) {
			String errMsg = main.messageSource.getMessage(e.getMessageCode(),e.getMessageArg(), Locale.getDefault());
			main.logger.error(errMsg);
			main.loggerBBW02120.error(appId, e.getMessageCode(), errMsg, createBy);
		} catch (Exception e) {
			String errMsg = main.messageSource.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR, 
					new String[] { CBW00000Util.genMessageOfException(e) }, 
					Locale.getDefault());
			main.logger.error(errMsg);
			main.loggerBBW02120.error(appId, CST30000Messages.ERROR_UNDEFINED_ERROR, errMsg, createBy);
		}
		((ConfigurableApplicationContext) appContext).close();

		System.exit(status);
	}
	
	int[] convertExcelToStaging(String[] params, 
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
