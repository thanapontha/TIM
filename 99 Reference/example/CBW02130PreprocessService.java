/******************************************************
 * Program History
 * 
 * Project Name	            :  GWRDS : 
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.batch.preprocess.service
 * Program ID 	            :  CBW02130PreprocessService.java
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
package th.co.toyota.bw0.batch.preprocess.service;

import java.math.BigDecimal;
import java.sql.Connection;
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
import th.co.toyota.bw0.batch.preprocess.repository.IBW02130PreprocessRepository;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.util.IST30000LoggerDb;
import th.co.toyota.st3.batch.receiving.CST31250FileReceivingCmdOptions;

import com.google.common.base.Strings;


@Service
public class CBW02130PreprocessService {
	final Logger logger = LoggerFactory.getLogger(CBW02130PreprocessService.class);

	@Autowired
	private IST30000LoggerDb loggerBBW02130;

	@Autowired
	protected MessageSource messageSource;
	
	@Autowired
	private IBW02130PreprocessRepository repositoryKompo;
	
	@Autowired
	private IBW00000Repository commonRepository;

	@Value("${projectCode}")
	protected String PROJECT_CODE;
	
	private final int FIRST_DATA_ROW = 5;
	
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
	public Date sysdate;
	
	StringBuilder keys = new StringBuilder();
	
	public int totalRead = 0;
	public int insertedPamsCnt = 0;
	public int insertedKompoCnt = 0;
	public int insertedDetailCnt = 0;
	public int warningCnt = 0;
	public int errorCnt = 0;
	public String beginMonth = "";
	public String endMonth = "";
	
	public Map<String, String> mapError = new HashMap<>();
	
	private String labelFileId = "File Id";
	
	private String labelFileName = "File Name";
	private String labelImporter = "Importer";
	private String labelRundownKey = "Rundown Key";
	private String labelExporter = "Exporter";
	private String labelOrderDate = "Order Date";
	private String labelProductionDate = "Procution Date";
	private String labelVanningVolume = "Vanning Volume";
	private String labelVanningDate = "Vanning Date";
	private String labelLoadingDate = "Loading Date";
	private String labelUnloadingDate = "Unloading Date";
	private String labelProductionVolume = "Production Volume";
	

	public List<Object[]> logDetailCalendar = new ArrayList<>();
	public List<Object[]> logDetailStdStock = new ArrayList<>();
	public List<Object[]> logDetailProdVol = new ArrayList<>();
	public List<Object[]> logDetailPackVol = new ArrayList<>();
	public List<Object[]> logDetailOther = new ArrayList<>();
	
	public List<Object[]> logDetailProdVolDiagram = new ArrayList<>();
	
	public int nextRunNoVCalendar = 1;
	public int nextRunNoUCalendar = 1;
	public int nextRunNoStock = 1;
	public int nextRunNoProdVol = 1;
	public int nextRunNoPackVol = 1;
	public int nextRunNoOther = 1;
	public int nextRunNoProdVolDiagram = 1;
	
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
			if (!Strings.isNullOrEmpty(Strings.nullToEmpty(paramList.get(6))))
				this.unitPlant = CBW00000Util.convertBatchParam(Strings.nullToEmpty(paramList.get(6)));
			if (!Strings.isNullOrEmpty(Strings.nullToEmpty(paramList.get(7))))
				this.unitType = CBW00000Util.convertBatchParam(Strings.nullToEmpty(paramList.get(7)));
			if (!Strings.isNullOrEmpty(Strings.nullToEmpty(paramList.get(8))))
				this.unitModel = CBW00000Util.convertBatchParam(Strings.nullToEmpty(paramList.get(8)));
		}
		//END CR UT-002 2018/02/16
	}
	
	public int validate(Connection conn, CST31250FileReceivingCmdOptions params) {
		
		boolean warning = false;
		int validateErrorCount = 0;
		
		//Load Parameter
		try {
			this.loadParameter(params);
		} catch (CommonErrorException e) {			
			String errMsg = messageSource.getMessage(e.getMessageCode(), e.getMessageArg(), Locale.getDefault());
			logger.error(errMsg);
			loggerBBW02130.error(appId, e.getMessageCode(), errMsg, createBy);
			return CST30000Constants.ERROR;
		}
		
		try {
			
			List<Object[]> objList = repositoryKompo.getStagingList(conn,
															   this.createBy, 
															   this.vehiclePlant, 
															   this.vehicleModel, 
															   this.getsudoMonth,
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
				this.generateKeys(currentObj);
				boolean error = false;
				boolean duplicate = ((BigDecimal)currentObj[IBW02130PreprocessRepository.IDX_DUPCNT]).intValue()>1?true:false;
				if(!duplicate){
					boolean valid = this.validateMandatory(currentObj, iRowStage);
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
					valid = this.validationMaster(currentObj, isLastRecord);
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
				}else{
					String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_DUPLICATE_FOUND,
							new String[] { this.keys.toString() },
							Locale.getDefault());
					if (!validateLogError(errMsg)) {
						logger.error(errMsg);
						loggerBBW02130.error(appId,MessagesConstants.B_ERROR_DUPLICATE_FOUND, errMsg, createBy);
					}
					error = true;
					validateErrorCount++;
				}
				if(error){
					this.errorCnt++;
				}
			}
			
			if(this.errorCnt == 0){
				//business validation (No.2)
				boolean error = false;
				Object[] objChk = this.businessValidation(conn);
				boolean valid = (boolean)objChk[0];
				boolean warningChk = (boolean)objChk[1];
				if(!valid){
					error = true;
					validateErrorCount++;
				}
				if(warningChk){
					warning = true;
				}
			}
			
			//3. Calculate vanning volume
			//Move to insertAndCalculateDataToTarget()
			
		} catch (CommonErrorException e) {
			String errMsg = messageSource.getMessage(e.getMessageCode(), e.getMessageArg(), Locale.getDefault());
			logger.error(errMsg);
			loggerBBW02130.error(appId, e.getMessageCode(), errMsg, createBy);
			return CST30000Constants.ERROR;
		}
		return (this.errorCnt>0||validateErrorCount > 0) ? CST30000Constants.ERROR:(warning?CST30000Constants.WARNING:CST30000Constants.SUCCESS);
	}
	
	public HashMap<String, String> getHeaderCheckMandatory(GetsudoMonthConfigInfo getsudoMonthInfo) {
		HashMap<String, String> mapChkMadatory = new HashMap<>();
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
	
	private void generateKeys(Object[] currentObj){
		keys = new StringBuilder();
		keys.append("{Keys:");
		keys.append(this.labelVanningDate+"=").append(FormatUtil.convertDateToString((Date)currentObj[IBW02130PreprocessRepository.IDX_VANNING_DT], AppConstants.DATE_STRING_SCREEN_FORMAT));
		keys.append(" ,");
		keys.append(this.labelProductionDate+"=").append(FormatUtil.convertDateToString((Date)currentObj[IBW02130PreprocessRepository.IDX_PROD_DT], AppConstants.DATE_STRING_SCREEN_FORMAT));
		keys.append("}");
	}
	
	private boolean validateMandatory(Object[] currentObj, int iRowStage) {
		boolean valid = true;
		if(currentObj!=null){
			String fileId = (String)currentObj[IBW02130PreprocessRepository.IDX_FILE_ID];
			String fileNameUL = (String)currentObj[IBW02130PreprocessRepository.IDX_FILE_NAME];
			String importer = (String)currentObj[IBW02130PreprocessRepository.IDX_IMPORTER];
			String rundownKey = (String)currentObj[IBW02130PreprocessRepository.IDX_RUNDOWN_KEY];
			String exporter = (String)currentObj[IBW02130PreprocessRepository.IDX_EXPORTER];
			Date orderDate = (Date)currentObj[IBW02130PreprocessRepository.IDX_ORDER_DT];
			BigDecimal vanningVolume = (BigDecimal)currentObj[IBW02130PreprocessRepository.IDX_VANNING_VOLUME];
			Date vanningDate = (Date)currentObj[IBW02130PreprocessRepository.IDX_VANNING_DT];
			Date loadingDate = (Date)currentObj[IBW02130PreprocessRepository.IDX_LOADING_DT];
			Date unloadingDate = (Date)currentObj[IBW02130PreprocessRepository.IDX_UNLOADING_DT];
			Date productionDate = (Date)currentObj[IBW02130PreprocessRepository.IDX_PROD_DT];
			BigDecimal productionVolume = (BigDecimal)currentObj[IBW02130PreprocessRepository.IDX_PROD_VOLUME];
			
			int iRowExcel = iRowStage + FIRST_DATA_ROW;
			
			StringBuilder errValues = new StringBuilder();
			if(Strings.isNullOrEmpty(fileId)){
				this.appendValue(errValues, this.labelFileId);
				this.loggedMandatoryError(errValues, -1);
				valid = false;
			}
			
			errValues = new StringBuilder();
			if(Strings.isNullOrEmpty(fileNameUL)){
				this.appendValue(errValues, this.labelFileName);
				this.loggedMandatoryError(errValues, -1);
				valid = false;
			}
			
			errValues = new StringBuilder();
			if(Strings.isNullOrEmpty(importer)){
				this.appendValue(errValues, this.labelImporter);
				this.loggedMandatoryError(errValues, iRowExcel);
				valid = false;
			}
			
			errValues = new StringBuilder();
			if(Strings.isNullOrEmpty(rundownKey)){
				this.appendValue(errValues, this.labelRundownKey);
				this.loggedMandatoryError(errValues, iRowExcel);
				valid = false;
			}
			
			errValues = new StringBuilder();
			if(Strings.isNullOrEmpty(exporter)){
				this.appendValue(errValues, this.labelExporter);
				this.loggedMandatoryError(errValues, iRowExcel);
				valid = false;
			}
			
			errValues = new StringBuilder();
			if(orderDate == null){
				this.appendValue(errValues, this.labelOrderDate);
				this.loggedMandatoryError(errValues, iRowExcel);
				valid = false;
			}
			
			errValues = new StringBuilder();
			if(vanningVolume == null){
				this.appendValue(errValues, this.labelVanningVolume);
				this.loggedMandatoryError(errValues, iRowExcel);
				valid = false;
			}
			
			errValues = new StringBuilder();
			if(vanningDate == null){
				this.appendValue(errValues, this.labelVanningDate);
				this.loggedMandatoryError(errValues, iRowExcel);
				valid = false;
			}
			
			errValues = new StringBuilder();
			if(loadingDate == null){
				this.appendValue(errValues, this.labelLoadingDate);
				this.loggedMandatoryError(errValues, iRowExcel);
				valid = false;
			}
			
			errValues = new StringBuilder();
			if(unloadingDate == null){
				this.appendValue(errValues, this.labelUnloadingDate);
				this.loggedMandatoryError(errValues, iRowExcel);
				valid = false;
			}
			
			errValues = new StringBuilder();
			if(productionDate == null){
				this.appendValue(errValues, this.labelProductionDate);
				this.loggedMandatoryError(errValues, iRowExcel);
				valid = false;
			}
			
			errValues = new StringBuilder();
			if(productionVolume == null){
				this.appendValue(errValues, this.labelProductionVolume);
				this.loggedMandatoryError(errValues, iRowExcel);
				valid = false;
			}
			
		}
		return valid;
	}
	
	private void loggedMandatoryError(StringBuilder errValues, int row){
		String rowMsg = "";
		if(row > 0){
			rowMsg = " {Row No. "+row+"}";
		}
		String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_INVALID_EMPTY_FIELD,
												new String[] { errValues.toString() + rowMsg },
													Locale.getDefault());
		if (!validateLogError(errMsg)) {
			logger.error(errMsg);
			loggerBBW02130.error(appId, MessagesConstants.B_ERROR_INVALID_EMPTY_FIELD, errMsg, createBy);
		}
	}
	
	private boolean validateFormat(Object[] currentObj) {
		//Validate format by config in XML
		return true;
	}

	private boolean validationMaster(Object[] currentObj, boolean isLastRecord){
		//Calendar Master already validate in PAMs Rundown 2120 function
		// - Vehicle Plant Calendar checking
		// - Unit Plant Calendar checking
		return true;
	}
	
	private boolean validateValue(Connection conn, Object[] currentObj, boolean isLastRecord) throws CommonErrorException {
		boolean valid = true;
		//Run last record only
		if(currentObj != null && isLastRecord){
			String startProdExist = (String)currentObj[IBW02130PreprocessRepository.IDX_START_PROD_EXIST];
			String startEffectiveKKVolMonth = (String)currentObj[IBW02130PreprocessRepository.IDX_START_EFF_KK_MONTH];
			if(AppConstants.NO_INFO.equals(startProdExist)){
				StringBuilder k = new StringBuilder();
				k.append("{Keys:");
				k.append(this.labelProductionDate+"=").append(FormatUtil.convertDateToString((Date)currentObj[IBW02130PreprocessRepository.IDX_START_PROD_DT], AppConstants.DATE_SHOW_IN_SCREEN_MMM_YYYY));
				k.append("}");
				String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_INVALID_VALUE,
						new String[] {"Start Month of Diagram data " + k.toString(), "Start on " + 
								FormatUtil.convertDateToString(FormatUtil.convertStringToDate(startEffectiveKKVolMonth), AppConstants.DATE_SHOW_IN_SCREEN_MMM_YYYY)},
						Locale.getDefault());
				if (!validateLogError(errMsg)) {
					logger.error(errMsg);
					loggerBBW02130.error(appId, MessagesConstants.B_ERROR_INVALID_VALUE, errMsg, createBy);
				}
				valid = false;
			}
			
			List<String> wsExistInKompo = repositoryKompo.getLastMonthOfWorksheetExistInKompo(conn,
																							 this.version,
																							 this.getsudoMonth, 
																							 this.timing, 
																							 this.vehiclePlant, 
																							 this.vehicleModel, 
																							 this.createBy, 
																							 this.appId);
			if(wsExistInKompo==null || wsExistInKompo.isEmpty()){
				String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_NOT_EXIST,
						new String[] {"Last Month of Worksheet ("+this.endMonth+")", "Diagram upload file"},
						Locale.getDefault());
				if (!validateLogError(errMsg)) {
					logger.error(errMsg);
					loggerBBW02130.error(appId, MessagesConstants.B_ERROR_NOT_EXIST, errMsg, createBy);
				}
				valid = false;
			}
		
		}
		return valid;
	}
	
	private Object[] businessValidation(Connection conn) throws CommonErrorException {
		boolean valid = true;
		boolean warning = false;

		//5. Check Vehicle Production Volume in Diagram sheet between Worksheet 
		List<Object[]> vehicleProdVolvsWSL = repositoryKompo.getVehicleProdVolumeDiagramWithWorksheet(conn,
																									  this.version, 
																									  this.getsudoMonth, 
																									  this.timing, 
																									  this.vehiclePlant, 
																									  this.vehicleModel, 
																									  this.createBy);
		if(vehicleProdVolvsWSL != null && !vehicleProdVolvsWSL.isEmpty()){
			for(int i=0; i<vehicleProdVolvsWSL.size(); i++){
				Object[] rowData = vehicleProdVolvsWSL.get(i);
				String volumeMonth = (rowData[0] == null) ? "":(String)rowData[0];
				BigDecimal sumProdVolumeMonth = (rowData[1] == null) ? BigDecimal.ZERO:(BigDecimal)rowData[1];
				BigDecimal unitVolume = (rowData[2] == null) ? BigDecimal.ZERO:(BigDecimal)rowData[2];
				String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_MUST_EQUAL,
						new String[] {"Production Volume Diagram data {Key : Volume Month= "+volumeMonth+"} ", "Production Volume in worksheet"},
						Locale.getDefault());
				if (!validateLogError(errMsg)) {
					logger.error(errMsg);
					loggerBBW02130.error(appId, MessagesConstants.B_ERROR_MUST_EQUAL, errMsg, createBy);
					Object[] logDetail = new Object[] {appId, getsudoMonth, timing, vehiclePlant,
														vehicleModel, unitPlant, unitModel,
														"6",nextRunNoProdVolDiagram++,
														null,
														volumeMonth,sumProdVolumeMonth,null,
														unitVolume,null,null,
													    this.createBy, FormatUtil.convert(this.sysdate),
													    this.createBy, FormatUtil.convert(this.sysdate)};
					logDetailProdVolDiagram.add(logDetail);
				}
			}
			valid = false;
		}
		
		return new Object[]{valid, warning};
	}

	private String appendValue(StringBuilder errValues, String value){
		if(!Strings.isNullOrEmpty(errValues.toString()))
			errValues.append(", ");
		errValues.append(value);
		return errValues.toString();
	}
	
	@SuppressWarnings("unchecked")
	public Object[] manageTransaction(Connection conn, int statusOfValidate) {
		Object[] result = new Object[3];
		try {
			String[] params = new String[] {this.version, 
											this.pamsKompoFlag, 
											this.getsudoMonth, 
											this.timing, 
											this.vehiclePlant, 
											this.vehicleModel, 
											this.unitPlant, 
											this.unitType,
											this.unitModel,
											this.createBy, 
											this.appId, 
											this.beginMonth, 
											this.endMonth};
			
			Object[] chkresult = repositoryKompo.insertAndCalculateDataToTarget(conn, params, statusOfValidate, this.unitPlantArr, this.unitModelArr);
			int status = (int)chkresult[0];
			HashMap<String, String> resultMap = (HashMap<String, String>)chkresult[1];
			insertedPamsCnt = Integer.parseInt(FormatUtil.nullToZero(resultMap.get("INSERT_TB_R_PAMS_RUNDOWN")));
			insertedKompoCnt = Integer.parseInt(FormatUtil.nullToZero(resultMap.get("INSERT_TB_R_KOMPO")));
			insertedDetailCnt = Integer.parseInt(FormatUtil.nullToZero(resultMap.get("INSERT_TB_L_UPLOAD_DETAIL")));

			result[0] = status;
			result[1] = resultMap;
			result[2] = "";
			return result;
		} catch (CommonErrorException e) {
			String errMsg = messageSource.getMessage(e.getMessageCode(), e.getMessageArg(), Locale.getDefault());
			if (!validateLogError(errMsg)) {
				loggerBBW02130.error(appId, e.getMessageCode(), errMsg, createBy);
			}
			result[0] = CST30000Constants.ERROR;
			result[1] = new HashMap<String, String>();
			if(MessagesConstants.B_ERROR_CONCURRENTCY.equals(e.getMessageCode())){
				result[2] = MessagesConstants.B_ERROR_CONCURRENTCY;
			}else{
				result[2] = "";
			}
			return result;	
		} catch (Exception e) {
			String errMsg = messageSource.getMessage("MSTD0067AERR", new String[] { CBW00000Util.genMessageOfException(e) }, Locale.getDefault());
			if (!validateLogError(errMsg)) {
				loggerBBW02130.error(appId, "MSTD0067AERR", errMsg, createBy);
			}
			result[0] = CST30000Constants.ERROR;
			result[1] = new HashMap<String, String>();
			result[2] = "";
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
				sysdate = (Date)keys[IDX_SYSDATE];
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
		if(logDetailProdVolDiagram!=null && !logDetailProdVolDiagram.isEmpty()){
			commonRepository.insertLogDetail(conn, logDetailProdVolDiagram, AppConstants.LOG_DETAIL_PROD_VOL_DIAGRAM);
		}
	}
	
	//Delete all log detail to DB
	public void deleteAllLogDetail(Connection conn, Object[] keys) throws CommonErrorException{
		this.setKeyParamerter(keys);
		String[] keySrc = new String[]{this.getsudoMonth,this.timing, this.vehiclePlant, this.vehicleModel};
		commonRepository.deleteLogDetail(conn, keySrc);
	}
}

