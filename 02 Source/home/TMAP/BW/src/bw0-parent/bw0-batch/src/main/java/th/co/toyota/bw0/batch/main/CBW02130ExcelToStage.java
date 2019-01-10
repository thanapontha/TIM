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
import java.util.ArrayList;
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
import th.co.toyota.bw0.batch.preprocess.main.CBW02130Preprocess;
import th.co.toyota.bw0.batch.preprocess.repository.IBW02130PreprocessRepository;
import th.co.toyota.bw0.batch.service.CBW02130Service;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.util.CST32010DocNoGenerator;
import th.co.toyota.st3.api.util.IST30000LoggerDb;
import th.co.toyota.st3.batch.receiving.CST31250FileReceivingCmdOptions;

import com.google.common.base.Strings;

public class CBW02130ExcelToStage {

	final Logger logger = LoggerFactory.getLogger(CBW02130ExcelToStage.class);

	@Autowired
	private IST30000LoggerDb loggerBBW02130;

	@Autowired
	protected MessageSource messageSource;

	@Autowired
	protected CBW00000BatchUtil batchUtil;

	@Autowired
	private CBW00000CommonService commonService;
	
	@Autowired
	private CBW02130Service service;
	
	@Autowired
	private CBW02130Preprocess preprocess;
	
	@Autowired
	private IBW00000Repository repository;
	
	@Autowired
	private IBW02130PreprocessRepository repositoryPreprocess;
	
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
				
//				"TDEM", //param 1: Version (User Company Login TMAP-MS or TDEM)
//				"K", //param 2 : Upload Type P(PAMs Rundown), K(Kompokung)
//				"Mar-17", //paam 3: Getsudo Month
//				"D-14", // param 4: Timing
//				"TMT_GW1", // param 5: Vehicle Plant
//				"Camry", // param 6: Vehicle Model				
//				"TMMIN", // param 7: Unit Plant
//				"Engine", // param 8: Unit Type
//				"2NR_E85", // param 9: Unit Model
//				"2130_KOMPO.xlsx", // param 10: File name
//				"testUser", //param 11: User Login
//				"99990", //param 12: Application ID
//				TDEM::K::Sep-17::D-14::TMT^##^Test::C-HR_Conventional::TMT_IH::Engine::1GD::Kompo_OK_20170928012040.xlsx::suthida::000748
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
		String[] unitPlantArr = null;
		String[] unitModelArr = null;
		String[] unitTypeArr = null;
		
		String unitPlantAllSelected = null;
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
					unitPlantAllSelected = CBW00000Util.convertBatchParam(params[IDX_PARAM_UNIT_PLANT]);
				if (!Strings.isNullOrEmpty(params[IDX_PARAM_UNIT_TYPE]))
					unitTypeAllSelected = CBW00000Util.convertBatchParam(params[IDX_PARAM_UNIT_TYPE]);
				if (!Strings.isNullOrEmpty(params[IDX_PARAM_UNIT_MODEL]))
					unitModelAllSelected = CBW00000Util.convertBatchParam(params[IDX_PARAM_UNIT_MODEL]);
				
				unitPlantArr = unitPlantAllSelected.split(selectSep);
				unitPlant = (unitPlantArr != null) ? unitPlantArr[0] : unitPlantAllSelected;
				
				unitTypeArr = unitTypeAllSelected.split(selectSep);
				unitType = (unitTypeArr != null) ? unitTypeArr[0] : unitTypeAllSelected;
				
				unitModelArr = unitModelAllSelected.split(selectSep);
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
		CBW02120ExcelToStage pamsMain = appContext.getBean(CBW02120ExcelToStage.class);
		CBW02130ExcelToStage kompoMain = appContext.getBean(CBW02130ExcelToStage.class);
		
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
		int statusPamsRundownChk = CST30000Constants.SUCCESS;
		try {
			int[] resultPamsRundown = null;
			int[] resultKompo = null;
			int totalConvert = 0;
			String msg = "";
			
			msg = kompoMain.messageSource.getMessage(CST30000Messages.INFO_PROCESS_START,	new String[] { batchName }, Locale.getDefault());
			kompoMain.logger.info(msg);
			kompoMain.loggerBBW02130.start(appId, CST30000Messages.INFO_PROCESS_START, msg, createBy);
			
			String statusUpload = kompoMain.repository.getStatusOfLogUpload(appId, null);
			if(AppConstants.STATUS_INTERRUPT.equalsIgnoreCase(statusUpload)){
				String arg1 = "update";
				String arg2 = "this operation was interupted by user";
				msg = kompoMain.messageSource.getMessage(MessagesConstants.B_ERROR_CONCURRENTCY_INTERRUPT, new String[] {arg1, arg2 },Locale.getDefault());
				kompoMain.logger.error(msg);
				kompoMain.loggerBBW02130.error(appId, MessagesConstants.B_ERROR_CONCURRENTCY_INTERRUPT, msg, createBy);
				
				kompoMain.repository.updateStatusOfLogUpload(appId, createBy, AppConstants.STATUS_ERROR, AppConstants.STATUS_ERROR_DESC+" Upload KOMPO.");
				statusPamsRundownChk = CST30000Constants.ERROR;
			}else if(Strings.isNullOrEmpty(statusUpload)){
				msg = kompoMain.messageSource.getMessage(MessagesConstants.B_ERROR_CONCURRENTCY, new String[] {},Locale.getDefault());
				kompoMain.logger.error(msg);
				kompoMain.loggerBBW02130.error(appId, MessagesConstants.B_ERROR_CONCURRENTCY, msg, createBy);
				statusPamsRundownChk = CST30000Constants.ERROR;
			}else{
				kompoMain.repository.updateStatusOfLogUpload(appId, createBy, AppConstants.STATUS_PROCESSING, AppConstants.STATUS_PROCESSING_DESC+" Upload KOMPO.");
		        
				resultPamsRundown = pamsMain.convertExcelToStaging(params, lengthParamCheck, appId, createBy, filename, fileId1, tableName1, sysdate, kompoMain.loggerBBW02130);
				statusPamsRundownChk = resultPamsRundown[IDX_VALIDATE_STATUS];
				totalConvert = (int)resultPamsRundown[IDX_ROWS_EFFECTED];
			}
	        
			if (statusPamsRundownChk == CST30000Constants.SUCCESS 
					|| statusPamsRundownChk == CST30000Constants.WARNING){
				resultKompo = kompoMain.convertExcelToStaging(params, lengthParamCheck, appId, createBy, filename, fileId2, tableName2, sysdate, kompoMain.loggerBBW02130);
			}else{
				resultKompo = new int[]{CST30000Constants.ERROR,0};
			}
	        	
	        	
        	if (statusPamsRundownChk == CST30000Constants.ERROR 
        			|| resultKompo[IDX_VALIDATE_STATUS] == CST30000Constants.ERROR) {
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
				
				//Change Status to Error (E) in Log Upload Status table
				kompoMain.repository.updateStatusOfLogUpload(appId, createBy, AppConstants.STATUS_ERROR, AppConstants.STATUS_ERROR_DESC+" Upload KOMPO.");
				
				kompoMain.batchUtil.archiveFile(filename, kompoMain.service.companyCode, true);
				
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
	
				kompoMain.batchUtil.archiveFile(filename, kompoMain.service.companyCode, false);
			}
        	
        	//Get Unit model relation
        	List<Object[]> unitLs = kompoMain.repositoryPreprocess.getUnitModelRelateList(null, getsudoMonth, vehiclePlant, vehicleModel);
        	
			if(status==CST30000Constants.SUCCESS){
				CST31250FileReceivingCmdOptions arg0 = new CST31250FileReceivingCmdOptions();
				arg0.setModuleId(AppConstants.MODULE_ID_BW02);
				arg0.setFunctionId(AppConstants.FUNCTION_ID_BBW02130);
				arg0.setApplicationId(appId);
				arg0.setUser(createBy);

				List<String> additionalPrameters = new ArrayList<>();
				for(String paramObj : params){
					additionalPrameters.add(paramObj);
				}	
				arg0.setAdditionalPrameters(additionalPrameters);
				
				//Call Preprocess - Basic Validation, Business Validation, Final Process
				kompoMain.preprocess.sysdate = sysdate;
				kompoMain.preprocess.unitModelCount = unitLs.size();
				resultPreprocess = kompoMain.preprocess.preProcessInterfaceFileData(arg0);
			}
			
			//Insert-Delete Log Detail
			if (status == CST30000Constants.ERROR || resultPreprocess == CST30000Constants.ERROR) {
				//INSERT log detail
				kompoMain.preprocess.insertLogDetail( new Object[] {appId, getsudoMonth, timing, vehiclePlant,
															   vehicleModel, unitPlant, unitModel,
															   createBy,sysdate});
				status = CST30000Constants.ERROR;
			}else{
				//DELETE log detail
				kompoMain.preprocess.deleteLogDetail( new Object[] {appId, getsudoMonth, timing, vehiclePlant,
						  									   vehicleModel, unitPlant, unitModel,
						  									   createBy,sysdate});
				status = CST30000Constants.SUCCESS;
			}
			
			statusUpload = kompoMain.repository.getStatusOfLogUpload(appId, null);
			if(kompoMain.preprocess.errConcurrentcy == false && !Strings.isNullOrEmpty(statusUpload)){
				//Insert Rundown Kompo status
				String uploadStatus = kompoMain.commonService.convertRundownKompoUploadStatus(status);
//				if(unitLs!=null && !unitLs.isEmpty()){
					List<Object[]> datas = new ArrayList<>();
//					for(int i=0;i<unitLs.size();i++){
//						Object[] uData = unitLs.get(i);
//						String uModel = (String)uData[0];
//						String uPlant = (String)uData[1];
//						String uType = (String)uData[2];
//						Object[] data = new Object[] {version,getsudoMonth,timing,
//								  vehiclePlant, vehicleModel, uPlant, 
//								  uType, uModel, pamsKompoFlag, 
//								  uploadStatus,AppConstants.NO_STR,filename, appId, 
//								  createBy,sysdate,
//								  createBy,sysdate};
//						datas.add(data);
//					}
					
					//CR UT-002 2018/02/16 Thanawut T. : select multiple Unit Model for Kompokung Validate
				if(unitPlantArr != null && unitPlantArr.length > 0
					&& unitModelArr != null && unitModelArr.length > 0
					&& unitPlantArr.length == unitModelArr.length){
						for(int i=0; i<unitModelArr.length;i++){
							String uModel = unitModelArr[i];
							String uPlant = unitPlantArr[i];
							String uType = unitTypeArr[i];
							Object[] data = new Object[] {version,getsudoMonth,timing,
									  vehiclePlant, vehicleModel, uPlant, 
									  uType, uModel, pamsKompoFlag, 
									  uploadStatus,AppConstants.NO_STR,filename, appId, 
									  createBy,sysdate,
									  createBy,sysdate};
							datas.add(data);
							
						}
					}else{
						String uModel = unitModel;
						String uPlant = unitPlant;
						String uType = unitType;
						Object[] data = new Object[] {version,getsudoMonth,timing,
								  vehiclePlant, vehicleModel, uPlant, 
								  uType, uModel, pamsKompoFlag, 
								  uploadStatus,AppConstants.NO_STR,filename, appId, 
								  createBy,sysdate,
								  createBy,sysdate};
						datas.add(data);
					}
					kompoMain.commonService.addRundownKompoStsKOMPO(null, datas);
//				}
			}			
			
		} catch (CommonErrorException e) {
			String errMsg = kompoMain.messageSource.getMessage(e.getMessageCode(),e.getMessageArg(), Locale.getDefault());
			kompoMain.logger.error(errMsg);
			kompoMain.loggerBBW02130.error(appId, e.getMessageCode(), errMsg, createBy);
		} catch (Exception e) {
			String errMsg = kompoMain.messageSource.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR, 
					new String[] { CBW00000Util.genMessageOfException(e) }, 
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
