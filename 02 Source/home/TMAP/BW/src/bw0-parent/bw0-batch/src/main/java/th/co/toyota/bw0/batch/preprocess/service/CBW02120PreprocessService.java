/******************************************************
 * Program History
 * 
 * Project Name	            :  GWRDS : 
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.batch.preprocess.service
 * Program ID 	            :  CBW02120PreprocessService.java
 * Program Description	    :  PAMs Rundown Upload
 * Environment	 	    	:  Java 7
 * Author		    		:  Thanawut T.
 * Version		    		:  1.0
 * Creation Date            :  30 August 2017
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.bw0.batch.preprocess.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import th.co.toyota.bw0.api.common.CBW00000Util;
import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.constants.MessagesConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.api.model.common.GetsudoMonthConfigInfo;
import th.co.toyota.bw0.api.repository.common.IBW00000Repository;
import th.co.toyota.bw0.api.repository.common.IBW03060Repository;
import th.co.toyota.bw0.api.service.common.CBW00000CommonService;
import th.co.toyota.bw0.batch.preprocess.repository.IBW02120PreprocessRepository;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.util.IST30000LoggerDb;
import th.co.toyota.st3.batch.receiving.CST31250FileReceivingCmdOptions;

import com.google.common.base.Strings;

@Service
public class CBW02120PreprocessService {
	final Logger logger = LoggerFactory.getLogger(CBW02120PreprocessService.class);

	@Autowired
	private IST30000LoggerDb loggerBBW02120;
	
	@Autowired
	private IST30000LoggerDb loggerBBW02130;

	@Autowired
	protected MessageSource messageSource;

	@Autowired
	private IBW02120PreprocessRepository repository;

	@Autowired
	private IBW03060Repository systemRepository;

	@Autowired
	private IBW00000Repository commonRepository;
	
	@Autowired
	protected CBW00000CommonService commonService;
	
	@Value("${projectCode}")
	protected String PROJECT_CODE;
	
	// common receiving 5 params
	//private String projectId; // args[0] = Project Id
	private String moduleId; // args[1] = Module Id
	private String functionId; // args[2] = Function Id
	//private String fileId; // args[3] = File Id
	// private String filename;// args[4] = File name
	private String appId; // args[5] = Application Id

	private String createBy;
	//parameter from screen
	public String version;
	public String pamsKompoFlag;
	public String getsudoMonth;
	public String timing;
	public String vehiclePlant;
	public String vehicleModel;
	public String unitPlant;
	public String unitType;
	public String unitModel;
	public String fileName;
	public Timestamp sysdate;
	
	StringBuffer keys = new StringBuffer();
	
	public int totalRead = 0;
	public int insertedCnt = 0;
	public int insertedDetailCnt = 0;
	public int warningCnt = 0;
	public int errorCnt = 0;
	public String beginMonth = "";
	public String endMonth = "";
	
	public Map<String, String> mapError = new HashMap<>();
	private Map<String, String> invalidField = new HashMap<>();
	
	private String labelFileId = "File Id";
	private String labelVehiclePlant = "Vehicle Plant";
	private String labelUnitPlant = "Unit Plant";
	private String labelStandardStock = "Standard Stock";
	
	private String labelFileName = "File Name";
	private String labelImporter = "Importer";
	private String labelRundownKey = "Rundown Key";
	private String labelExporter = "Exporter";
	private String labelOrderDate = "Order Date";
	private String labelProductionDate = "Procution Date";
	private String labelProductionVolume = "Production Volume";
	private String labelLocalStock = "Local Stock";
	private String labelStockDays = "Stock Days";
	private String labelPackVolume = "Pack Volume";
	private String labelTotalStock = "Total Stock";

	public List<Object[]> logDetailCalendar = new ArrayList<>();
	public List<Object[]> logDetailStdStock = new ArrayList<>();
	public List<Object[]> logDetailProdVol = new ArrayList<>();
	public List<Object[]> logDetailPackVol = new ArrayList<>();
	public List<Object[]> logDetailOther = new ArrayList<>();
	
	public int nextRunNoVCalendar = 1;
	public int nextRunNoUCalendar = 1;
	public int nextRunNoStock = 1;
	public int nextRunNoProdVol = 1;
	public int nextRunNoPackVol = 1;
	public int nextRunNoOther = 1;
	
	String[] unitPlantArr = null;
	String[] unitModelArr = null;
	String[] unitTypeArr = null;
	private String selectSep = AppConstants.BATCH_CHARACTOR_REPLACE_SELECTED_MULTI_UNIT_BACK;
	
	private void loadParameter(CST31250FileReceivingCmdOptions params)throws CommonErrorException {
		// parameter1-5 from common receiving
		// args[0] = (-r)Project Id
		//projectId = CBW00000Util.convertBatchParam(params.getProjectId());
		// args[1] = (-m)Module Id
		moduleId = CBW00000Util.convertBatchParam(params.getModuleId());
		// args[2] = (-n)Function Id
		functionId = CBW00000Util.convertBatchParam(params.getFunctionId());
		// args[3] = (-f)File Id
		//fileId = CBW00000Util.convertBatchParam(params.getFileId());
		// args[4] = (-e)File name
		// logger.debug("filename:" + params.getFileName());
//		 filename = CBW00000Util.convertBatchParam(params.getFileName());
		// args[5] = (-a)Application Id (AppId)
		appId = CBW00000Util.convertBatchParam(params.getApplicationId());
//		// (-u)UserId
		createBy = CBW00000Util.convertBatchParam(params.getUser());
//		// (-x)Additional Param
		
		logger.debug("[loadParameter] moduleId:" + moduleId + ", functionId:" + functionId + ", appId:" + appId);
		
		List<String> paramList = params.getAdditionalPrameters();
		logger.debug("paramList.size() => " + paramList.size());
		if (paramList== null || (paramList!=null && paramList.size() < 5)) {
			throw new CommonErrorException(CST30000Messages.ERROR_MESSAGE_MISSING_PARAMETER, new String[]{}, AppConstants.ERROR);
		}
		this.version = CBW00000Util.convertBatchParam(Strings.nullToEmpty(paramList.get(0)));
		this.pamsKompoFlag = CBW00000Util.convertBatchParam(Strings.nullToEmpty(paramList.get(1)));
		this.getsudoMonth = CBW00000Util.convertBatchParam(Strings.nullToEmpty(paramList.get(2)));
		this.timing = CBW00000Util.convertBatchParam(Strings.nullToEmpty(paramList.get(3)));
		this.vehiclePlant = CBW00000Util.convertBatchParam(Strings.nullToEmpty(paramList.get(4)));
		this.vehicleModel = CBW00000Util.convertBatchParam(Strings.nullToEmpty(paramList.get(5)));
//		this.unitPlant = CBW00000Util.convertBatchParam(Strings.nullToEmpty(paramList.get(6)));
//		this.unitType = CBW00000Util.convertBatchParam(Strings.nullToEmpty(paramList.get(7)));
//		this.unitModel = CBW00000Util.convertBatchParam(Strings.nullToEmpty(paramList.get(8)));
		this.fileName = CBW00000Util.convertBatchParam(Strings.nullToEmpty(paramList.get(9)));
		
		//CR UT-002 2018/02/16 Thanawut T. : select multiple Unit Model for Kompokung Validate
		//KOMPO
		if(AppConstants.UPLOAD_KOMPO_FLAG.equals(this.pamsKompoFlag)){
			String unitPlantAllSelected = null;
			String unitModelAllSelected = null;
			String unitTypeAllSelected = null;
			
			if (!Strings.isNullOrEmpty(Strings.nullToEmpty(paramList.get(6))))
				unitPlantAllSelected = CBW00000Util.convertBatchParam(Strings.nullToEmpty(paramList.get(6)));
			if (!Strings.isNullOrEmpty(Strings.nullToEmpty(paramList.get(7))))
				unitTypeAllSelected = CBW00000Util.convertBatchParam(Strings.nullToEmpty(paramList.get(7)));
			if (!Strings.isNullOrEmpty(Strings.nullToEmpty(paramList.get(8))))
				unitModelAllSelected = CBW00000Util.convertBatchParam(Strings.nullToEmpty(paramList.get(8)));
			
			this.unitPlantArr = unitPlantAllSelected.split(selectSep);
			this.unitPlant = (unitPlantArr != null) ? unitPlantArr[0] : unitPlantAllSelected;
			
			this.unitTypeArr = unitTypeAllSelected.split(selectSep);
			this.unitType = (unitTypeArr != null) ? unitTypeArr[0] : unitTypeAllSelected;
			
			this.unitModelArr = unitModelAllSelected.split(selectSep);
			this.unitModel = (unitModelArr != null) ? unitModelArr[0] : unitModelAllSelected;
		}else{ //PAMs
			//In Case PAMs use 1 unit model
			this.unitPlant = CBW00000Util.convertBatchParam(Strings.nullToEmpty(paramList.get(6)));
			this.unitType = CBW00000Util.convertBatchParam(Strings.nullToEmpty(paramList.get(7)));
			this.unitModel = CBW00000Util.convertBatchParam(Strings.nullToEmpty(paramList.get(8)));
		}
		//END CR UT-002 2018/02/16
		
		repository.setSysdate(this.sysdate);
	}
	
	public int validate(Connection conn, CST31250FileReceivingCmdOptions params) {
		
		boolean warning = false;
		GetsudoMonthConfigInfo getsudoMonthInfo = null;
		int validateErrorCount = 0;
		
		//Load Parameter
		try {
			this.loadParameter(params);
		} catch (CommonErrorException e) {			
			String errMsg = messageSource.getMessage(e.getMessageCode(), e.getMessageArg(), Locale.getDefault());
			logger.error(errMsg);
			loggerBBW02120.error(appId, e.getMessageCode(), errMsg, createBy);
			return CST30000Constants.ERROR;
		}
		
		try {
			//Prepare data for validate
			getsudoMonthInfo = commonService.getGetsudoMonthInfo(conn, getsudoMonth);
			
			this.endMonth = this.getGMEndMonth(getsudoMonthInfo);
			
			
			//CR 2018/02/01 Thanawut T. - UT phase inc no.3
			if(AppConstants.UPLOAD_PAMS_FLAG.equals(pamsKompoFlag)){
				boolean isKKUnitVolBlank = false;
				boolean isPackingVolBlank = false;
				
				List<Object[]> kaikiengUnitVolList = repository.getKaikiengUnitVolumeCheck(conn,
																						   this.version, 
																						   this.getsudoMonth, 
																						   this.endMonth, 
																						   this.timing, 
																						   this.vehiclePlant, 
																						   this.vehicleModel, 
																						   this.unitPlant, 
																						   this.unitModel, 
																						   this.createBy,
																						   this.pamsKompoFlag);
				if(kaikiengUnitVolList!=null && !kaikiengUnitVolList.isEmpty()){
					for (int i = 0; i < kaikiengUnitVolList.size(); i++) {
						Object[] kkRowObj = kaikiengUnitVolList.get(i);
						String volumeMonth = (String)kkRowObj[5];
						BigDecimal unitVolumeKK = (BigDecimal)kkRowObj[6];
						if(unitVolumeKK == null){
							String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_NO_KAIKIENG,
									new String[] {volumeMonth},
									Locale.getDefault());
							if (!validateLogError(errMsg)) {
								logger.error(errMsg);
								if(AppConstants.UPLOAD_PAMS_FLAG.equals(this.pamsKompoFlag)){
									loggerBBW02120.error(appId, MessagesConstants.B_ERROR_NO_KAIKIENG, errMsg, createBy);
								}else{
									loggerBBW02130.error(appId, MessagesConstants.B_ERROR_NO_KAIKIENG, errMsg, createBy);
								}
							}
							isKKUnitVolBlank = true;
						}
					}
				}
				
//				List<Object[]> packingVolList = repository.getPackingVolumeCheck(conn,
//																			   this.version, 
//																			   this.getsudoMonth, 
//																			   this.endMonth, 
//																			   this.timing, 
//																			   this.vehiclePlant, 
//																			   this.vehicleModel, 
//																			   this.unitPlant, 
//																			   this.unitModel, 
//																			   this.createBy,
//																			   this.pamsKompoFlag);
//				if(packingVolList!=null && !packingVolList.isEmpty()){
//					for (int i = 0; i < packingVolList.size(); i++) {
//						Object[] rowObj = packingVolList.get(i);
//						String volumeMonth = (String)rowObj[0];
//						BigDecimal packingVolume = (BigDecimal)rowObj[1];
//						if(packingVolume == null){
//							String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_NO_PACKING_VOLUME,
//									new String[] {volumeMonth},
//									Locale.getDefault());
//							if (!validateLogError(errMsg)) {
//								logger.error(errMsg);
//								if(AppConstants.UPLOAD_PAMS_FLAG.equals(this.pamsKompoFlag)){
//									loggerBBW02120.error(appId, MessagesConstants.B_ERROR_NO_PACKING_VOLUME, errMsg, createBy);
//								}else{
//									loggerBBW02130.error(appId, MessagesConstants.B_ERROR_NO_PACKING_VOLUME, errMsg, createBy);
//								}
//							}
//							isPackingVolBlank = true;
//						}
//					}
//				}
				
				if(isKKUnitVolBlank || isPackingVolBlank)
					return CST30000Constants.ERROR;
			}
			//END CR 2018/02/01 Thanawut T. - UT phase inc no.3
			
			List<Object[]> objList = repository.getStagingList(conn,
															   this.version,
															   this.getsudoMonth,
															   this.endMonth,
															   this.timing,
															   this.vehiclePlant, 
															   this.vehicleModel, 
															   this.unitPlant,
															   this.unitModel,
															   this.createBy,
															   this.pamsKompoFlag);
			
			if(objList != null && !objList.isEmpty()){
				this.totalRead = objList.size();
			}
			
			for (int iRowStage = 0; iRowStage < objList.size(); iRowStage++) {
				boolean isLastRecord = false;
				if(iRowStage == objList.size() - 1){
					isLastRecord = true;
				}
				Object[] currentObj = objList.get(iRowStage);
				this.invalidField.clear();
				this.generateKeys(currentObj);
				boolean error = false;
				boolean duplicate = ((BigDecimal)currentObj[IBW02120PreprocessRepository.IDX_DUPCNT]).intValue()>1?true:false;
				if(!duplicate){
					boolean valid = this.validateMandatory(currentObj);
					boolean passMandatoryChk = false;
					boolean passValueChk = false;
					boolean passMasterChk = false;
					if(valid){
						passMandatoryChk = true;
					}else{
						error = true;
						validateErrorCount++;
					}
					//format checking
					valid = this.validateFormat(currentObj);
					if(!valid){
						error = true;
					}
					//master validation
					valid = this.validationMaster(conn, currentObj, iRowStage, isLastRecord);
					if(valid){
						passMasterChk = true;
					}else{
						error = true;
						validateErrorCount++;
					}
					//value checking
					valid = this.validateValue(conn, currentObj, isLastRecord);
					if(valid){
						passValueChk = true;
					}else{
						error = true;
						validateErrorCount++;
					}
					if(passMandatoryChk && passMasterChk && passValueChk){
						//A. PAMS Rundown data validation
						//business validation (No.1 - 5)
						//business validation (No.6 - 7)
						Object[] objChk = this.businessValidation(conn, currentObj, iRowStage);
						valid = (boolean)objChk[0];
						boolean warningChk = (boolean)objChk[1];
						if(!valid){
							error = true;
						}
						if(warningChk){
							warning = true;
						}
					}
				}else{
					String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_DUPLICATE_FOUND,
							new String[] { this.keys.toString() },
							Locale.getDefault());
					if (!validateLogError(errMsg)) {
						logger.error(errMsg);
						if(AppConstants.UPLOAD_PAMS_FLAG.equals(this.pamsKompoFlag)){
							loggerBBW02120.error(appId,MessagesConstants.B_ERROR_DUPLICATE_FOUND, errMsg, createBy);
						}else{
							loggerBBW02130.error(appId,MessagesConstants.B_ERROR_DUPLICATE_FOUND, errMsg, createBy);
						}
					}
					error = true;
					validateErrorCount++;
				}
				if(error){
					this.errorCnt++;
				}
			}
			
		} catch (CommonErrorException e) {
			String errMsg = messageSource.getMessage(e.getMessageCode(), e.getMessageArg(), Locale.getDefault());
			logger.error(errMsg);
			loggerBBW02120.error(appId, e.getMessageCode(), errMsg, createBy);
			return CST30000Constants.ERROR;
		}
		return (this.errorCnt>0||validateErrorCount > 0) ? CST30000Constants.ERROR:(warning?CST30000Constants.WARNING:CST30000Constants.SUCCESS);
	}
	
	public HashMap<String, String> getHeaderCheckMandatory(GetsudoMonthConfigInfo getsudoMonthInfo) {
		HashMap<String, String> mapChkMadatory = new HashMap<String, String>();
		if(getsudoMonthInfo!=null){
			int cnt = getsudoMonthInfo.getDisplayMonth().intValue();
			if(AppConstants.COMPANY_CD_TMAP_MS.equals(this.version)){
				cnt = getsudoMonthInfo.getCheckMonth().intValue();
			}
			Date gm = FormatUtil.convertStringToDate(getsudoMonth, AppConstants.DATE_SHOW_IN_SCREEN); 
			for(int j=0;j<cnt;j++){
				Date gmDt = FormatUtil.addMonth(gm, j); 
				String gmStr = FormatUtil.convertDateToString(gmDt, AppConstants.DATE_SHOW_IN_SCREEN);
				mapChkMadatory.put(gmStr, gmStr);
			}
			
		}
		return mapChkMadatory;
	}
	
	public String getGMEndMonth(GetsudoMonthConfigInfo getsudoMonthInfo) {
		String endMonth = getsudoMonth;
		if(getsudoMonthInfo!=null){
			Date gm = FormatUtil.convertStringToDate(getsudoMonth, AppConstants.DATE_SHOW_IN_SCREEN); 
			Date gmDt = FormatUtil.addMonth(gm, getsudoMonthInfo.getDisplayMonth().intValue()-1); 
			String gmStr = FormatUtil.convertDateToString(gmDt, AppConstants.DATE_SHOW_IN_SCREEN);
			endMonth = gmStr;
		}
		return endMonth;
	}
	
	private void generateKeys(Object[] currentObj){
		keys = new StringBuffer();
		keys.append("{Keys:");
		keys.append(this.labelProductionDate+"=").append(FormatUtil.convertDateToString((Date)currentObj[IBW02120PreprocessRepository.IDX_PROD_DT], AppConstants.DATE_STRING_SCREEN_FORMAT));
		keys.append("}");
	}
	
	private boolean validateMandatory(Object[] currentObj) {
		boolean valid = true;
		if(currentObj!=null){
			String fileId = (String)currentObj[IBW02120PreprocessRepository.IDX_FILE_ID];
			String fileName = (String)currentObj[IBW02120PreprocessRepository.IDX_FILE_NAME];
			String importer = (String)currentObj[IBW02120PreprocessRepository.IDX_IMPORTER];
			String rundownKey = (String)currentObj[IBW02120PreprocessRepository.IDX_RUNDOWN_KEY];
			String exporter = (String)currentObj[IBW02120PreprocessRepository.IDX_EXPORTER];
			Date orderDate = (Date)currentObj[IBW02120PreprocessRepository.IDX_ORDER_DT];
			Date productionDate = (Date)currentObj[IBW02120PreprocessRepository.IDX_PROD_DT];
			BigDecimal productionVolume = (BigDecimal)currentObj[IBW02120PreprocessRepository.IDX_PROD_VOLUME];
			BigDecimal localStock = (BigDecimal)currentObj[IBW02120PreprocessRepository.IDX_LOCAL_STOCK];
			BigDecimal stockDays = (BigDecimal)currentObj[IBW02120PreprocessRepository.IDX_STOCK_DAYS];
			BigDecimal packVolume = (BigDecimal)currentObj[IBW02120PreprocessRepository.IDX_PACK_VOLUME];
			BigDecimal totalStock = (BigDecimal)currentObj[IBW02120PreprocessRepository.IDX_TOTAL_STOCK];
			
			
			StringBuffer errValues = new StringBuffer();
			if(Strings.isNullOrEmpty(fileId)){
				this.appendValue(errValues, this.labelFileId);
				this.loggedMandatoryError(errValues);
				valid = false;
			}
			
			errValues = new StringBuffer();
			if(Strings.isNullOrEmpty(fileName)){
				this.appendValue(errValues, this.labelFileName);
				this.loggedMandatoryError(errValues);
				valid = false;
			}
			
			errValues = new StringBuffer();
			if(Strings.isNullOrEmpty(importer)){
				this.appendValue(errValues, this.labelImporter);
				this.loggedMandatoryError(errValues);
				valid = false;
			}
			
			errValues = new StringBuffer();
			if(Strings.isNullOrEmpty(rundownKey)){
				this.appendValue(errValues, this.labelRundownKey);
				this.loggedMandatoryError(errValues);
				valid = false;
			}
			
			errValues = new StringBuffer();
			if(Strings.isNullOrEmpty(exporter)){
				this.appendValue(errValues, this.labelExporter);
				this.loggedMandatoryError(errValues);
				valid = false;
			}
			
			errValues = new StringBuffer();
			if(orderDate == null){
				this.appendValue(errValues, this.labelOrderDate);
				this.loggedMandatoryError(errValues);
				valid = false;
			}
			
			errValues = new StringBuffer();
			if(productionDate == null){
				this.appendValue(errValues, this.labelProductionDate);
				this.loggedMandatoryError(errValues);
				valid = false;
			}
			
			
			errValues = new StringBuffer();
			if(productionVolume == null){
				this.appendValue(errValues, this.labelProductionVolume);
				this.loggedMandatoryError(errValues);
				valid = false;
			}
			
			errValues = new StringBuffer();
			if(localStock == null){
				this.appendValue(errValues, this.labelLocalStock);
				this.loggedMandatoryError(errValues);
				valid = false;
			}
			
			errValues = new StringBuffer();
			if(stockDays == null){
				this.appendValue(errValues, this.labelStockDays);
				this.loggedMandatoryError(errValues);
				valid = false;
			}
			
			errValues = new StringBuffer();
			if(packVolume == null){
				this.appendValue(errValues, this.labelPackVolume);
				this.loggedMandatoryError(errValues);
				valid = false;
			}
			
			errValues = new StringBuffer();
			if(totalStock == null){
				this.appendValue(errValues, this.labelTotalStock);
				this.loggedMandatoryError(errValues);
				valid = false;
			}
		}
		return valid;
	}
	
	private void loggedMandatoryError(StringBuffer errValues){
		String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_INVALID_EMPTY_FIELD,
												new String[] { errValues.toString() },
													Locale.getDefault());
		if (!validateLogError(errMsg)) {
			logger.error(errMsg);
			if(AppConstants.UPLOAD_PAMS_FLAG.equals(this.pamsKompoFlag)){
				loggerBBW02120.error(appId, MessagesConstants.B_ERROR_INVALID_EMPTY_FIELD, errMsg, createBy);
			}else{
				loggerBBW02130.error(appId, MessagesConstants.B_ERROR_INVALID_EMPTY_FIELD, errMsg, createBy);
			}
		}
	}
	
	public boolean validateFormat(Object[] currentObj) {
		boolean valid = true;
		return valid;
	}

	public boolean validationMaster(Connection conn, Object[] currentObj, int iRecord, boolean isLastRecord) throws CommonErrorException{
		boolean valid = true;
		
		if(currentObj != null){
			BigDecimal unitVolumeKK = (BigDecimal)currentObj[IBW02120PreprocessRepository.IDX_UNIT_VOLUME_KK];
			BigDecimal packingVolumeChk = (BigDecimal)currentObj[IBW02120PreprocessRepository.IDX_PACKING_VOLUME_CHK];
			
			//Run one round only
			if(isLastRecord){
				String vehiclePlantExistFlag = (String)currentObj[IBW02120PreprocessRepository.IDX_VEHICLE_PLANT_EXIST];
				String unitPlantExistFlag = (String)currentObj[IBW02120PreprocessRepository.IDX_UNIT_PLANT_EXIST];
				
				//Vehicle Plant Calendar checking
				if(AppConstants.UPLOAD_PAMS_FLAG.equals(this.pamsKompoFlag)){ //PAMS
					if(AppConstants.NO_INFO.equals(vehiclePlantExistFlag)){
							String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_NOT_EXIST,
									new String[] {this.labelVehiclePlant+" Calendar","Calendar Master"},
									Locale.getDefault());
							if (!validateLogError(errMsg)) {
								logger.error(errMsg);
								if(AppConstants.UPLOAD_PAMS_FLAG.equals(this.pamsKompoFlag)){
									loggerBBW02120.error(appId, MessagesConstants.B_ERROR_NOT_EXIST, errMsg, createBy);
								}else{
									loggerBBW02130.error(appId, MessagesConstants.B_ERROR_NOT_EXIST, errMsg, createBy);
								}
							}
							valid = false;
						}
				}else{ //KOMPO
					if(AppConstants.NO_INFO.equals(vehiclePlantExistFlag)){
						String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_NOT_EXIST,
								new String[] {this.labelVehiclePlant+" Calendar","Calendar Master"},
								Locale.getDefault());
						if (!validateLogError(errMsg)) {
							logger.error(errMsg);
							if(AppConstants.UPLOAD_PAMS_FLAG.equals(this.pamsKompoFlag)){
								loggerBBW02120.error(appId, MessagesConstants.B_ERROR_NOT_EXIST, errMsg, createBy);
							}else{
								loggerBBW02130.error(appId, MessagesConstants.B_ERROR_NOT_EXIST, errMsg, createBy);
							}
						}
						valid = false;
					}
					
				}
				//Unit Plant Calendar checking
				if(AppConstants.NO_INFO.equals(unitPlantExistFlag)){
					String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_NOT_EXIST,
							new String[] {this.labelUnitPlant+" Calendar","Calendar Master"},
							Locale.getDefault());
					if (!validateLogError(errMsg)) {
						logger.error(errMsg);
						
						if(AppConstants.UPLOAD_PAMS_FLAG.equals(this.pamsKompoFlag)){
							loggerBBW02120.error(appId, MessagesConstants.B_ERROR_NOT_EXIST, errMsg, createBy);
						}else{
							loggerBBW02130.error(appId, MessagesConstants.B_ERROR_NOT_EXIST, errMsg, createBy);
						}
					}
					valid = false;
				}
			}
			
			//Standard Stock checking (PAMS only)
			if(AppConstants.UPLOAD_PAMS_FLAG.equals(this.pamsKompoFlag)){
//				if(unitVolumeKK != null && unitVolumeKK.intValue() != 0){
				String isEffectiveInRelation = (String)currentObj[IBW02120PreprocessRepository.IDX_IS_EFF_IN_RELATION_CHK];
				String stdStockExistFlag = (String)currentObj[IBW02120PreprocessRepository.IDX_STOCK_EXIST];
				if(AppConstants.YES_INFO.equals(isEffectiveInRelation)){
					if(AppConstants.NO_INFO.equals(stdStockExistFlag)){
						String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_NOT_EXIST,
								new String[] {this.labelStandardStock, "Standard Stock Master" + " " + this.keys.toString()},
								Locale.getDefault());
						if (!validateLogError(errMsg)) {
							logger.error(errMsg);
							loggerBBW02120.error(appId, MessagesConstants.B_ERROR_NOT_EXIST, errMsg, createBy);
						}
						valid = false;
					}
//					}
				}
			}
		}
		
		return valid;
	}
	
	private boolean validateValue(Connection conn, Object[] currentObj, boolean isLastRecord) throws CommonErrorException {
		boolean valid = true;
		
		//Run last record only
		if(currentObj != null && isLastRecord){
			String startProdExist = (String)currentObj[IBW02120PreprocessRepository.IDX_START_PROD_EXIST];
			String startEffectiveKKVolMonth = (String)currentObj[IBW02120PreprocessRepository.IDX_START_EFF_KK_MONTH];
			
			if(AppConstants.NO_INFO.equals(startProdExist)){
				if(startEffectiveKKVolMonth!=null && !"".equals(startEffectiveKKVolMonth)){
					StringBuilder k = new StringBuilder();
					k.append("{Keys:");
					k.append(this.labelProductionDate+"=").append(FormatUtil.convertDateToString((Date)currentObj[IBW02120PreprocessRepository.IDX_START_PROD_DT], AppConstants.DATE_SHOW_IN_SCREEN_MMM_YYYY));
					k.append("}");
					String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_INVALID_VALUE,
							new String[] {"Start Month of PAMS Rundown data " + k.toString(), "Start on " + 
									FormatUtil.convertDateToString(FormatUtil.convertStringToDate(startEffectiveKKVolMonth), AppConstants.DATE_SHOW_IN_SCREEN_MMM_YYYY)},
							Locale.getDefault());
					if (!validateLogError(errMsg)) {
						logger.error(errMsg);
						if(AppConstants.UPLOAD_PAMS_FLAG.equals(this.pamsKompoFlag)){
							loggerBBW02120.error(appId, MessagesConstants.B_ERROR_INVALID_VALUE, errMsg, createBy);
						}else{
							loggerBBW02130.error(appId, MessagesConstants.B_ERROR_INVALID_VALUE, errMsg, createBy);
						}
						
					}
					valid = false;
				}
			}
			
			List<String> wsNotExistInPamsMonth = repository.getLastMonthOfWorksheetExistInPams(conn,
																						this.pamsKompoFlag,
																						this.version, 
																						this.getsudoMonth, 
																						this.timing, 
																						this.vehiclePlant, 
																						this.vehicleModel, 
																						this.unitPlant,
																						this.unitModel,
																						this.createBy, 
																						this.appId);
			
			if(wsNotExistInPamsMonth!=null && !wsNotExistInPamsMonth.isEmpty()){
				String lastMonth = (wsNotExistInPamsMonth.get(0) == null || "".equals(wsNotExistInPamsMonth.get(0))) ? "" : wsNotExistInPamsMonth.get(0);
				String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_NOT_EXIST,
						new String[] {"Last Month of Worksheet ("+lastMonth+")", "PAMs Rundown upload file"},
						Locale.getDefault());
				if (!validateLogError(errMsg)) {
					logger.error(errMsg);
					if(AppConstants.UPLOAD_PAMS_FLAG.equals(this.pamsKompoFlag)){
						loggerBBW02120.error(appId, MessagesConstants.B_ERROR_NOT_EXIST, errMsg, createBy);
					}else{
						loggerBBW02130.error(appId, MessagesConstants.B_ERROR_NOT_EXIST, errMsg, createBy);
					}
				}
				valid = false;
			}
		
		}
		return valid;
	}
	
	public Object[] businessValidation(Connection conn, Object[] currentObj, int iRecord) throws CommonErrorException {
		boolean valid = true;
		boolean warning = false;
		String vehiclePlantCalFlag = (String)currentObj[IBW02120PreprocessRepository.IDX_VEHICLE_PLANT_CAL_FLAG];
		String vehiclePlantCalDisplay = (String)currentObj[IBW02120PreprocessRepository.IDX_VEHICLE_PLANT_CAL_DIS];
		Date productionDt = (Date)currentObj[IBW02120PreprocessRepository.IDX_PROD_DT];
		BigDecimal productionVolume = (BigDecimal)currentObj[IBW02120PreprocessRepository.IDX_PROD_VOLUME];
		BigDecimal packingVolume = (BigDecimal)currentObj[IBW02120PreprocessRepository.IDX_PACK_VOLUME];
		BigDecimal unitVolumeKK = (BigDecimal)currentObj[IBW02120PreprocessRepository.IDX_UNIT_VOLUME_KK];
		BigDecimal packingVolumeChk = (BigDecimal)currentObj[IBW02120PreprocessRepository.IDX_PACKING_VOLUME_CHK];
		
		//2. Check Vehicle Plant Calendar
		//PAMS Rundown
		if(AppConstants.UPLOAD_PAMS_FLAG.equals(this.pamsKompoFlag)){
			if(unitVolumeKK != null && unitVolumeKK.intValue() != 0){ //CR 2018/02/01 Thanawut T. - UT phase inc no.3
				String pamsCalendarCheck = (String)currentObj[IBW02120PreprocessRepository.IDX_PAMS_VEHICLE_PLANT_CAL_CHK];
				if(AppConstants.YES_INFO.equals(pamsCalendarCheck)){
					//2.1 In case Calendar Flag is BLANK or H and Production Volume = 0
					if((Strings.isNullOrEmpty(vehiclePlantCalFlag) 
							|| AppConstants.CALENDAR_HALF_WORKING_DAY.equals(vehiclePlantCalFlag)) 
							&& productionVolume.equals(BigDecimal.ZERO)){
						//Write log message into log table.	(MBW00021BERR)
						String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_MUST_GREATER_THAN,
								new String[] {"Production Volume of "+vehiclePlantCalDisplay+this.keys.toString(), "0"},
								Locale.getDefault());
						if (!validateLogError(errMsg)) {
							logger.error(errMsg);
							loggerBBW02120.error(appId, MessagesConstants.B_ERROR_MUST_GREATER_THAN, errMsg, createBy);
							Object[] logDetail = new Object[] {appId, getsudoMonth, timing, vehiclePlant,
																vehicleModel, unitPlant, unitModel,
																"1",nextRunNoVCalendar++,
																FormatUtil.convertDateToString(productionDt, AppConstants.DATE_SHOW_IN_DETAIL_REPORT),
																null,AppConstants.CALENDAR_FULL_NON_WORKING_DAY,vehiclePlantCalDisplay,
																null,null,null,
																this.createBy, FormatUtil.convert(this.sysdate),
																this.createBy, FormatUtil.convert(this.sysdate)};
							logDetailCalendar.add(logDetail);
						}
						valid = false;
					}
					//2.2 In case Calendar Flag is F and Production Volume > 0
					if(vehiclePlantCalFlag != null 
							&& AppConstants.CALENDAR_FULL_NON_WORKING_DAY.equals(vehiclePlantCalFlag) 
							&& productionVolume.compareTo(BigDecimal.ZERO) == 1){  //productionVolume > 0
						//Write log message into log table.(MBW00022BERR)
						String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_MUST_EQUAL,
								new String[] {"Production Volume of "+vehiclePlantCalDisplay+this.keys.toString(), "0"},
								Locale.getDefault());
						if (!validateLogError(errMsg)) {
							logger.error(errMsg);
							loggerBBW02120.error(appId, MessagesConstants.B_ERROR_MUST_EQUAL, errMsg, createBy);
							Object[] logDetail = new Object[] {appId, getsudoMonth, timing, vehiclePlant,
									vehicleModel, unitPlant, unitModel,
									"1",nextRunNoVCalendar++,
									FormatUtil.convertDateToString(productionDt, AppConstants.DATE_SHOW_IN_DETAIL_REPORT),
									null,AppConstants.CALENDAR_WORKING_DAY_W,vehiclePlantCalDisplay,
									null,null,null,
									this.createBy, FormatUtil.convert(this.sysdate),
									this.createBy, FormatUtil.convert(this.sysdate)};
							logDetailCalendar.add(logDetail);
						}
						valid = false;
					}
				}
			}
		}
		
		//Kompo
		if(AppConstants.UPLOAD_KOMPO_FLAG.equals(this.pamsKompoFlag)){
			if(unitVolumeKK != null && unitVolumeKK.intValue() != 0){ //CR 2018/02/01 Thanawut T. - UT phase inc no.3
				String kompoCalendarCheck = (String)currentObj[IBW02120PreprocessRepository.IDX_KOMPO_VEHICLE_PLANT_CAL_CHK];
				if(AppConstants.YES_INFO.equals(kompoCalendarCheck)){
					//2.1 In case Calendar Flag is BLANK or H and Production Volume = 0
					if((Strings.isNullOrEmpty(vehiclePlantCalFlag) 
							|| AppConstants.CALENDAR_HALF_WORKING_DAY.equals(vehiclePlantCalFlag)) 
							&& productionVolume.equals(BigDecimal.ZERO)){
						//Write log message into log table.	(MBW00021BERR)
						String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_MUST_GREATER_THAN,
								new String[] {"Production Volume of "+vehiclePlantCalDisplay+this.keys.toString(), "0"},
								Locale.getDefault());
						if (!validateLogError(errMsg)) {
							logger.error(errMsg);
							loggerBBW02130.error(appId, MessagesConstants.B_ERROR_MUST_GREATER_THAN, errMsg, createBy);
							Object[] logDetail = new Object[] {appId, getsudoMonth, timing, vehiclePlant,
									vehicleModel, unitPlant, unitModel,
									"1",nextRunNoVCalendar++,
									FormatUtil.convertDateToString(productionDt, AppConstants.DATE_SHOW_IN_DETAIL_REPORT),
									null,AppConstants.CALENDAR_FULL_NON_WORKING_DAY,vehiclePlantCalDisplay,
									null,null,null,
									this.createBy, FormatUtil.convert(this.sysdate),
									this.createBy, FormatUtil.convert(this.sysdate)};
							logDetailCalendar.add(logDetail);
						}
						valid = false;
					}
					//2.2 In case Calendar Flag is F and Production Volume > 0
					if(vehiclePlantCalFlag != null 
							&& AppConstants.CALENDAR_FULL_NON_WORKING_DAY.equals(vehiclePlantCalFlag) 
							&& productionVolume.compareTo(BigDecimal.ZERO) == 1){ //
						//Write log message into log table.(MBW00022BERR)
						String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_MUST_EQUAL,
								new String[] {"Production Volume of "+vehiclePlantCalDisplay+this.keys.toString(), "0"},
								Locale.getDefault());
						if (!validateLogError(errMsg)) {
							logger.error(errMsg);
							loggerBBW02130.error(appId, MessagesConstants.B_ERROR_MUST_EQUAL, errMsg, createBy);
							Object[] logDetail = new Object[] {appId, getsudoMonth, timing, vehiclePlant,
									vehicleModel, unitPlant, unitModel,
									"1",nextRunNoVCalendar++,
									FormatUtil.convertDateToString(productionDt, AppConstants.DATE_SHOW_IN_DETAIL_REPORT),
									null,AppConstants.CALENDAR_WORKING_DAY_W,vehiclePlantCalDisplay,
									null,null,null,
									this.createBy, FormatUtil.convert(this.sysdate),
									this.createBy, FormatUtil.convert(this.sysdate)};
							logDetailCalendar.add(logDetail);
						}
						valid = false;
					}
				}
			}
		}
		
		//3. Check Unit Plant Calendar
		String unitPlantCalChk = (String)currentObj[IBW02120PreprocessRepository.IDX_UNIT_PLANT_CAL_CHK];
		String unitPlantCalFlag = (String)currentObj[IBW02120PreprocessRepository.IDX_UNIT_PLANT_CAL_FLAG];
		String unitPlantCalDisplay = (String)currentObj[IBW02120PreprocessRepository.IDX_UNIT_PLANT_CAL_DISY];
//		String isOneVehiclePerOneUnitPlant;
//		
//		if(AppConstants.UPLOAD_PAMS_FLAG.equals(this.pamsKompoFlag)){
//			isOneVehiclePerOneUnitPlant = AppConstants.YES_INFO;
//		}else{
//			isOneVehiclePerOneUnitPlant = (String)currentObj[IBW02120PreprocessRepository.IDX_IS_VEHICLE_PER_UNIT];
//		}
		
		if(AppConstants.YES_INFO.equals(unitPlantCalChk) /*&& AppConstants.YES_INFO.equals(isOneVehiclePerOneUnitPlant)*/){
			if(packingVolumeChk == null || (packingVolumeChk != null && packingVolumeChk.intValue() != 0)){ //CR 2018/02/01 Thanawut T. - UT phase inc no.3
				//3.1 In case Calendar Flag is BLANK or H and Production Volume = 0
				if((Strings.isNullOrEmpty(unitPlantCalFlag) 
						|| AppConstants.CALENDAR_HALF_WORKING_DAY.equals(unitPlantCalFlag)) 
						&& packingVolume.equals(BigDecimal.ZERO)){
					//Write log message into log table.	(MBW00021BERR)
					String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_MUST_GREATER_THAN,
							new String[] {"Packing Volume of "+unitPlantCalDisplay + this.keys.toString(), "0"},
							Locale.getDefault());
					if (!validateLogError(errMsg)) {
						logger.error(errMsg);
						if(AppConstants.UPLOAD_PAMS_FLAG.equals(this.pamsKompoFlag)){
							loggerBBW02120.error(appId, MessagesConstants.B_ERROR_MUST_GREATER_THAN, errMsg, createBy);
						}else{
							loggerBBW02130.error(appId, MessagesConstants.B_ERROR_MUST_GREATER_THAN, errMsg, createBy);
						}
						
						Object[] logDetail = new Object[] {appId, getsudoMonth, timing, vehiclePlant,
								vehicleModel, unitPlant, unitModel,
								"2",nextRunNoUCalendar++,
								FormatUtil.convertDateToString(productionDt, AppConstants.DATE_SHOW_IN_DETAIL_REPORT),
								null,AppConstants.CALENDAR_FULL_NON_WORKING_DAY,unitPlantCalDisplay,
								null,null,null,
								this.createBy, FormatUtil.convert(this.sysdate),
								this.createBy, FormatUtil.convert(this.sysdate)};
						logDetailCalendar.add(logDetail);
					}
					valid = false;
				}
				//3.2 In case Calendar Flag is F and Packing  Volume > 0
				if(unitPlantCalFlag != null 
						&& AppConstants.CALENDAR_FULL_NON_WORKING_DAY.equals(unitPlantCalFlag) 
						&& packingVolume.compareTo(BigDecimal.ZERO) == 1){ 
					//Write log message into log table.(MBW00022BERR)
					String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_MUST_EQUAL,
							new String[] {"Packing Volume of "+unitPlantCalDisplay+this.keys.toString(), "0"},
							Locale.getDefault());
					if (!validateLogError(errMsg)) {
						logger.error(errMsg);
						if(AppConstants.UPLOAD_PAMS_FLAG.equals(this.pamsKompoFlag)){
							loggerBBW02120.error(appId, MessagesConstants.B_ERROR_MUST_EQUAL, errMsg, createBy);
						}else{
							loggerBBW02130.error(appId, MessagesConstants.B_ERROR_MUST_EQUAL, errMsg, createBy);
						}
						
						Object[] logDetail = new Object[] {appId, getsudoMonth, timing, vehiclePlant,
								vehicleModel, unitPlant, unitModel,
								"2",nextRunNoUCalendar++,
								FormatUtil.convertDateToString(productionDt, AppConstants.DATE_SHOW_IN_DETAIL_REPORT),
								null,AppConstants.CALENDAR_WORKING_DAY_W,unitPlantCalDisplay,
								null,null,null,
								this.createBy, FormatUtil.convert(this.sysdate),
								this.createBy, FormatUtil.convert(this.sysdate)};
						logDetailCalendar.add(logDetail);
					}
					valid = false;
				}
			
			}
		}
		
		//4. Check Stock Value
		if(AppConstants.UPLOAD_PAMS_FLAG.equals(this.pamsKompoFlag)){
			if(unitVolumeKK != null && unitVolumeKK.intValue() != 0){  //CR 2018/02/01 Thanawut T. - UT phase inc no.3
				String isStockErrorChk = (String)currentObj[IBW02120PreprocessRepository.IDX_IS_STOCK_ERROR_CHK];
				if(!Strings.isNullOrEmpty(isStockErrorChk) && AppConstants.YES_INFO.equals(isStockErrorChk)){
					BigDecimal stockDays = (BigDecimal)currentObj[IBW02120PreprocessRepository.IDX_STOCK_DAYS];
					BigDecimal stockMin = (BigDecimal)currentObj[IBW02120PreprocessRepository.IDX_STOCK_MIN];
					BigDecimal stockMax = (BigDecimal)currentObj[IBW02120PreprocessRepository.IDX_STOCK_MAX];
					String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_MUST_BETWEEN,
							new String[] {"Stock days in PAMs Rundownd "+this.keys.toString(), stockMin.toString(), stockMax.toString()},
							Locale.getDefault());
					if (!validateLogError(errMsg)) {
						logger.error(errMsg);
						loggerBBW02120.error(appId, MessagesConstants.B_ERROR_MUST_BETWEEN, errMsg, createBy);
						Object[] logDetail = new Object[] {appId, getsudoMonth, timing, vehiclePlant, vehicleModel, unitPlant, unitModel, 
														   "4",nextRunNoStock++,
															FormatUtil.convertDateToString(productionDt, AppConstants.DATE_SHOW_IN_DETAIL_REPORT),
														   null,stockDays,null,
														   null,stockMin,stockMax,
														   this.createBy, FormatUtil.convert(this.sysdate),
														   this.createBy, FormatUtil.convert(this.sysdate)};
						logDetailStdStock.add(logDetail);
					}
					valid = false;
				}
			}
		}
		
		//5. Check Vehicle Production Volume between Worksheet 
		if(iRecord == 0){
			
			List<Object[]> vehicleProdVolvsWSL = repository.getVehicleProdVolumeWithWorksheet(conn,
																				  this.pamsKompoFlag, 
																				  this.version, 
																				  this.getsudoMonth,
																				  this.endMonth,
																				  this.timing, 
																				  this.vehiclePlant, 
																				  this.vehicleModel, 
																				  this.unitModel, 
																				  this.unitPlant,
																				  this.createBy);

			
			if(vehicleProdVolvsWSL != null && !vehicleProdVolvsWSL.isEmpty()){
				for(int i=0; i<vehicleProdVolvsWSL.size(); i++){
					Object[] rowData = vehicleProdVolvsWSL.get(i);
					String volumeMonth = (rowData[0] == null) ? "":(String)rowData[0];
					BigDecimal sumProdVolumeMonth = (rowData[1] == null) ? BigDecimal.ZERO:(BigDecimal)rowData[1];
					BigDecimal unitVolume = (rowData[2] == null) ? BigDecimal.ZERO:(BigDecimal)rowData[2];
					String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_MUST_EQUAL,
							new String[] {"Production Volume PAMs Rundown data {Key : Volume Month= "+volumeMonth+"} ", "Production Volume in worksheet"},
							Locale.getDefault());
					if (!validateLogError(errMsg)) {
						logger.error(errMsg);
						if(AppConstants.UPLOAD_PAMS_FLAG.equals(this.pamsKompoFlag)){
							loggerBBW02120.error(appId, MessagesConstants.B_ERROR_MUST_EQUAL, errMsg, createBy);
						}else{
							loggerBBW02130.error(appId, MessagesConstants.B_ERROR_MUST_EQUAL, errMsg, createBy);
						}
						Object[] logDetail = new Object[] {appId, getsudoMonth, timing, vehiclePlant,
															vehicleModel, unitPlant, unitModel,
															"3",nextRunNoProdVol++,
															null,
															volumeMonth,sumProdVolumeMonth,null,
															unitVolume,null,null,
														    this.createBy, FormatUtil.convert(this.sysdate),
														    this.createBy, FormatUtil.convert(this.sysdate)};
						logDetailProdVol.add(logDetail);
					}
				}
				valid = false;
			}
			
			//6. Check next validation
			if(AppConstants.UPLOAD_PAMS_FLAG.equals(this.pamsKompoFlag)){ //PAMS
				//B. Packing volume accuracy validation
				//7. Check Packing Volume between Worksheet
				List<Object[]> packVolWsVsPamsLs = repository.getPackingVolumeWorksheetVsPams(conn, 
																							  this.version, this.getsudoMonth, this.endMonth,
																							  this.timing, this.vehiclePlant, 
																							  this.vehicleModel, this.unitPlant, 
																							  this.unitModel, this.createBy);

				if(packVolWsVsPamsLs!=null && !packVolWsVsPamsLs.isEmpty()){
					for(int i=0; i<packVolWsVsPamsLs.size(); i++){
						Object[] rowData = packVolWsVsPamsLs.get(i);
						String volumeMonth = (rowData[0] == null) ? "":(String)rowData[0];
						BigDecimal sumPackVolumeMonth = (rowData[7] == null) ? BigDecimal.ZERO:(BigDecimal)rowData[7];
						BigDecimal packVolume = (rowData[9] == null) ? BigDecimal.ZERO:(BigDecimal)rowData[9];
						String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_MUST_EQUAL,
								new String[] {"Packing Volume PAMs Rundown data {Key : Volume Month= "+volumeMonth+"} ", "Packing Volume in worksheet"},
								Locale.getDefault());
						if (!validateLogError(errMsg)) {
							logger.error(errMsg);
							loggerBBW02120.error(appId, MessagesConstants.B_ERROR_MUST_EQUAL, errMsg, createBy);
							Object[] logDetail = new Object[] {appId, getsudoMonth, timing, vehiclePlant,
																vehicleModel, unitPlant, unitModel,
																"5",nextRunNoPackVol++,
																null,
																volumeMonth,sumPackVolumeMonth,null,
																packVolume,null,null,
																this.createBy, FormatUtil.convert(this.sysdate),
																this.createBy, FormatUtil.convert(this.sysdate)};
							logDetailPackVol.add(logDetail);
						}
					}
					valid = false;
				}
			}
		}
		
		return new Object[]{valid, warning};
	}

	private String appendValue(StringBuffer errValues, String value){
		if(!Strings.isNullOrEmpty(errValues.toString()))
			errValues.append(", ");
		errValues.append(value);
		return errValues.toString();
	}
	
	@SuppressWarnings("unchecked")
	public Object[] manageTransaction(Connection conn, int statusOfValidate) {
		Object[] result = new Object[2];
		try {
			String[] paramDel = new String[]{this.version, 
											 this.getsudoMonth, 
											 this.timing, 
											 this.vehiclePlant, 
											 this.vehicleModel,
											 this.unitPlant,
											 this.unitModel};
			String[] paramChk = new String[] {this.version, 
													this.pamsKompoFlag, 
													this.getsudoMonth, 
													this.timing, 
													this.vehiclePlant, 
													this.vehicleModel, 
													this.unitPlant, 
													this.unitType,
													this.unitModel};
			Object[] chkresult = repository.insertAndCalculateDataToTarget(conn,
																		   this.createBy, 
																		   this.appId, 
																		   this.beginMonth, 
																		   this.endMonth,
																		   this.getsudoMonth, 
																		   this.timing,
																		   statusOfValidate, 
																		   paramDel,
																		   paramChk);
			int status = (int)chkresult[0];
			HashMap<String, String> resultMap = (HashMap<String, String>)chkresult[1];
			insertedCnt = Integer.parseInt(FormatUtil.nullToZero(resultMap.get("INSERT_TB_R_PAMS_RUNDOWN")));
			insertedDetailCnt = Integer.parseInt(FormatUtil.nullToZero(resultMap.get("INSERT_TB_L_UPLOAD_DETAIL")));

			result[0] = status;
			result[1] = resultMap;
			return result;
		} catch (CommonErrorException e) {
			e.printStackTrace();
			String errMsg = messageSource.getMessage(e.getMessageCode(), e.getMessageArg(), Locale.getDefault());
			if (!validateLogError(errMsg)) {
				loggerBBW02120.error(appId, e.getMessageCode(), errMsg, createBy);
			}
			result[0] = CST30000Constants.ERROR;
			result[1] = new HashMap<String, String>();
			return result;	
		} catch (Exception e) {
			e.printStackTrace();
			String errMsg = messageSource.getMessage("MSTD0067AERR", new String[] { CBW00000Util.genMessageOfException(e) }, Locale.getDefault());
			if (!validateLogError(errMsg)) {
				loggerBBW02120.error(appId, "MSTD0067AERR", errMsg, createBy);
			}
			result[0] = CST30000Constants.ERROR;
			result[1] = new HashMap<String, String>();
			return result;
		}
	}
	
	private Boolean validateLogError(String errMsg) {
		if (mapError.containsKey(errMsg)) {
			return true;
		} else {
			mapError.put(errMsg, AppConstants.YES_STR);	
			return false;
		}
	}
	
//	private boolean isValidAndNotEmpty(String labelChk, String value){
//		if(!this.invalidField.containsKey(labelChk) && !Strings.isNullOrEmpty(value)){
//			return true;
//		}else{
//			return false;
//		}
//	}
//	private boolean isValidAndNotEmpty(String labelChk, BigDecimal value){
//		if(!this.invalidField.containsKey(labelChk) && value != null){
//			return true;
//		}else{
//			return false;
//		}
//	}

	private void setKeyParamerter(Object[] keys) {
		final int IDX_APP_ID = 0;
		final int IDX_GETSUDO_MONTH = 1;
		final int IDX_TIMING = 2;
		final int IDX_VEHICLE_PLANT = 3;
		final int IDX_VEHICLE_MODEL = 4;
		final int IDX_UNIT_PLANT = 5;
		final int IDX_UNIT_MODEL = 6;
		final int IDX_CREATE_BY = 7;
		final int IDX_SYSDATE = 8;
		if(keys != null && keys.length >= 9){
			if(Strings.isNullOrEmpty(appId)){
				appId = keys[IDX_APP_ID].toString();
			}
			if(Strings.isNullOrEmpty(getsudoMonth)){
				getsudoMonth = keys[IDX_GETSUDO_MONTH].toString();
			}
			if(Strings.isNullOrEmpty(timing)){
				timing = keys[IDX_TIMING].toString();
			}
			if(Strings.isNullOrEmpty(vehiclePlant)){
				vehiclePlant = keys[IDX_VEHICLE_PLANT].toString();
			}
			if(Strings.isNullOrEmpty(vehicleModel)){
				vehicleModel = keys[IDX_VEHICLE_MODEL].toString();
			}
			if(Strings.isNullOrEmpty(unitPlant)){
				unitPlant = keys[IDX_UNIT_PLANT].toString();
			}
			if(Strings.isNullOrEmpty(unitModel)){
				unitModel = keys[IDX_UNIT_MODEL].toString();
			}
			if(Strings.isNullOrEmpty(createBy)){
				createBy = keys[IDX_CREATE_BY].toString();
			}
			if(sysdate == null){
				sysdate = (Timestamp)keys[IDX_SYSDATE];
			}
		}
	}
	
	//Insert log detail Other Eror to DB
	public void insertLogDetailOther(Connection conn, Object[] keys) throws CommonErrorException {
		
		this.setKeyParamerter(keys);
		
		Object[] logDetail = new Object[] {appId, getsudoMonth, timing, vehiclePlant, 
										   vehicleModel, unitPlant, unitModel, 
										   "7",1, null,
										   null,null,null,
										   null,null,null,
										   createBy, FormatUtil.convert(this.sysdate),
										   createBy, FormatUtil.convert(this.sysdate)};
		
		//Insert only 1 record
		if(!logDetailOther.contains(logDetail)){
			logDetailOther.add(logDetail);
			commonRepository.insertLogDetail(conn, logDetailOther, AppConstants.LOG_DETAIL_OTHER);
		}
		
	}
	
	public void insertLogDetail(Connection conn) throws CommonErrorException{
			if(logDetailCalendar!=null && !logDetailCalendar.isEmpty()){
				commonRepository.insertLogDetail(conn, logDetailCalendar, AppConstants.LOG_DETAIL_CALENDAR);
			}
			if(logDetailStdStock!=null && !logDetailStdStock.isEmpty()){
				commonRepository.insertLogDetail(conn, logDetailStdStock, AppConstants.LOG_DETAIL_STOCK);
			}
			if(logDetailProdVol!=null && !logDetailProdVol.isEmpty()){
				commonRepository.insertLogDetail(conn, logDetailProdVol, AppConstants.LOG_DETAIL_PROD_VOL);
			}
			if(logDetailPackVol!=null && !logDetailPackVol.isEmpty()){
				commonRepository.insertLogDetail(conn, logDetailPackVol, AppConstants.LOG_DETAIL_PACK_VOL);
			}
	}
	
	//Delete all log detail to DB
	public void deleteAllLogDetail(Connection conn, Object[] keys) throws CommonErrorException{
		this.setKeyParamerter(keys);
		String[] keySrc = new String[]{this.getsudoMonth,this.timing, this.vehiclePlant, this.vehicleModel, this.unitPlant, this.unitModel};
		commonRepository.deleteLogDetail(conn, keySrc);
	}
}
