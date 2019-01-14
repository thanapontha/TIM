/******************************************************
 * Program History
 * 
 * Project Name	            :  GWRDS : 
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.batch.preprocess.service
 * Program ID 	            :  ExampleFileUploadPreprocessService.java
 * Program Description	    :  Example File Upload Preprocess Service
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
package th.co.toyota.bw0.batch.preprocess.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import th.co.toyota.bw0.api.common.CommonUtility;
import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.constants.MessagesConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.batch.preprocess.repository.ExampleFileUploadPreprocessRepository;
import th.co.toyota.bw0.batch.preprocess.vo.UploadParamVO;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.util.IST30000LoggerDb;
import th.co.toyota.st3.batch.receiving.CST31250FileReceivingCmdOptions;

import com.google.common.base.Strings;


@Service
public class ExampleFileUploadPreprocessService {
	final Logger logger = LoggerFactory.getLogger(ExampleFileUploadPreprocessService.class);

	@Autowired
	private IST30000LoggerDb loggerBBW02130;

	@Autowired
	protected MessageSource messageSource;
	
	@Autowired
	private ExampleFileUploadPreprocessRepository repository;

//	@Value("${projectCode}")
//	protected String projectCode;
	
	private static final int FIRST_ROW_DATA = 5;
	
	UploadParamVO paramVo = new UploadParamVO();
	
	StringBuilder keys = new StringBuilder();
	
	public int totalRead = 0;
	public int insertedCnt = 0;
	public int warningCnt = 0;
	public int errorCnt = 0;
	
	public final Map<String, String> mapError = new HashMap<>();
	
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
	

	public UploadParamVO getParamVo() {
		return paramVo;
	}

	public void setParamVo(UploadParamVO paramVo) {
		this.paramVo = paramVo;
	}

	private void loadParameter(CST31250FileReceivingCmdOptions params)throws CommonErrorException {
		paramVo = new UploadParamVO();
		paramVo.setModuleId(CommonUtility.convertBatchParam(params.getModuleId()));
		paramVo.setFunctionId(CommonUtility.convertBatchParam(params.getFunctionId()));
		paramVo.setFileId(CommonUtility.convertBatchParam(params.getFileId()));
		paramVo.setFileName(CommonUtility.convertBatchParam(params.getFileName()));
		paramVo.setAppId(CommonUtility.convertBatchParam(params.getApplicationId()));
		paramVo.setUserId(CommonUtility.convertBatchParam(params.getUser()));
		List<String> paramList = params.getAdditionalPrameters();
		if (paramList== null || (paramList.size() < 3)) {
			throw new CommonErrorException(CST30000Messages.ERROR_MESSAGE_MISSING_PARAMETER, new String[]{}, AppConstants.ERROR);
		}
		paramVo.setUserId(CommonUtility.convertBatchParam(Strings.nullToEmpty(paramList.get(0))));
		paramVo.setAppId(CommonUtility.convertBatchParam(Strings.nullToEmpty(paramList.get(1))));
		paramVo.setFileName(CommonUtility.convertBatchParam(Strings.nullToEmpty(paramList.get(2))));
	}
	
	public int validate(Connection conn, CST31250FileReceivingCmdOptions params) {
		boolean warning = false;
		try {
			this.loadParameter(params);
		} catch (CommonErrorException e) {			
			String errMsg = messageSource.getMessage(e.getMessageCode(), e.getMessageArg(), Locale.getDefault());
			logger.error(errMsg);
			loggerBBW02130.error(paramVo.getAppId(), e.getMessageCode(), errMsg, paramVo.getUserId());
			return CST30000Constants.ERROR;
		}
		
		try {
			List<Object[]> objList = repository.getStagingList(conn, this.getParamVo());
			if(objList != null && !objList.isEmpty()){
				totalRead = objList.size();
			}
			
			for (int iRowStage = 0; iRowStage < objList.size(); iRowStage++) {
				boolean isLastRecord = false;
				if(iRowStage == objList.size() - 1){
					isLastRecord = true;
				}
				Object[] currentObj = objList.get(iRowStage);
				this.generateKeys(currentObj);
				boolean error = false;
				boolean duplicate = ((BigDecimal)currentObj[ExampleFileUploadPreprocessRepository.ColumnIndex.DUPCNT]).intValue()>1?Boolean.TRUE:Boolean.FALSE;
				if(!duplicate){
					boolean valid = this.validateMandatory(currentObj, iRowStage);
					boolean passMandatoryChk = false;
					if(valid){
						passMandatoryChk = true;
					}else{
						error = true;
					}
					//format checking
					valid = this.validateFormat(currentObj);
					if(!valid){
						error = true;
					}
					//master validation
					valid = this.validationMaster(currentObj, isLastRecord);
					if(!valid){
						error = true;
					}
					//value checking
					valid = this.validateValue(conn, currentObj, isLastRecord);
					if(!valid){
						error = true;
					}
					if(passMandatoryChk) {
						Object[] objectChk = this.businessValidation(conn);
						if(!(boolean)objectChk[0]) {
							error = true;
						}
						if((boolean)objectChk[1]) {
							warning = true;
						}
					}
				}else{
					String errMsg = messageSource.getMessage(MessagesConstants.B_ERROR_DUPLICATE_FOUND,
							new String[] { this.keys.toString() },
							Locale.getDefault());
					if (!validateLogError(errMsg)) {
						logger.error(errMsg);
						loggerBBW02130.error(this.getParamVo().getAppId(), MessagesConstants.B_ERROR_DUPLICATE_FOUND, errMsg, this.getParamVo().getUserId());
					}
					error = true;
				}
				if(error){
					this.errorCnt++;
				}
			}		
		} catch (CommonErrorException e) {
			String errMsg = messageSource.getMessage(e.getMessageCode(), e.getMessageArg(), Locale.getDefault());
			logger.error(errMsg);
			loggerBBW02130.error(this.getParamVo().getAppId(), e.getMessageCode(), errMsg, this.getParamVo().getUserId());
			return CST30000Constants.ERROR;
		}
		if(this.errorCnt > 0) {
			return CST30000Constants.ERROR;
		}
		return warning ? CST30000Constants.WARNING : CST30000Constants.SUCCESS;
	}
	
	
	private void generateKeys(Object[] currentObj){
		keys = new StringBuilder();
		keys.append("{Keys:");
		keys.append(this.labelVanningDate+"=").append(FormatUtil.convertDateToString((Date)currentObj[ExampleFileUploadPreprocessRepository.ColumnIndex.VANNING_DT], AppConstants.DATE_STRING_SCREEN_FORMAT));
		keys.append(" ,");
		keys.append(this.labelProductionDate+"=").append(FormatUtil.convertDateToString((Date)currentObj[ExampleFileUploadPreprocessRepository.ColumnIndex.PROD_DT], AppConstants.DATE_STRING_SCREEN_FORMAT));
		keys.append("}");
	}
	
	private boolean validateMandatory(Object[] currentObj, int iRowStage) {
		boolean valid = true;
		if(currentObj!=null){
			String fileId = (String)currentObj[ExampleFileUploadPreprocessRepository.ColumnIndex.FILE_ID];
			String fileNameUL = (String)currentObj[ExampleFileUploadPreprocessRepository.ColumnIndex.FILE_NAME];
			String importer = (String)currentObj[ExampleFileUploadPreprocessRepository.ColumnIndex.IMPORTER];
			String rundownKey = (String)currentObj[ExampleFileUploadPreprocessRepository.ColumnIndex.RUNDOWN_KEY];
			String exporter = (String)currentObj[ExampleFileUploadPreprocessRepository.ColumnIndex.EXPORTER];
			Date orderDate = (Date)currentObj[ExampleFileUploadPreprocessRepository.ColumnIndex.ORDER_DT];
			BigDecimal vanningVolume = (BigDecimal)currentObj[ExampleFileUploadPreprocessRepository.ColumnIndex.VANNING_VOLUME];
			Date vanningDate = (Date)currentObj[ExampleFileUploadPreprocessRepository.ColumnIndex.VANNING_DT];
			Date loadingDate = (Date)currentObj[ExampleFileUploadPreprocessRepository.ColumnIndex.LOADING_DT];
			Date unloadingDate = (Date)currentObj[ExampleFileUploadPreprocessRepository.ColumnIndex.UNLOADING_DT];
			Date productionDate = (Date)currentObj[ExampleFileUploadPreprocessRepository.ColumnIndex.PROD_DT];
			BigDecimal productionVolume = (BigDecimal)currentObj[ExampleFileUploadPreprocessRepository.ColumnIndex.PROD_VOLUME];
			
			int iRowExcel = iRowStage + FIRST_ROW_DATA;
			
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
			loggerBBW02130.error(this.getParamVo().getAppId(), MessagesConstants.B_ERROR_INVALID_EMPTY_FIELD, errMsg, this.getParamVo().getUserId());
		}
	}
	
	private boolean validateFormat(Object[] currentObj) {
		return true;
	}

	private boolean validationMaster(Object[] currentObj, boolean isLastRecord){
		return true;
	}
	
	private boolean validateValue(Connection conn, Object[] currentObj, boolean isLastRecord) throws CommonErrorException {
		boolean valid = true;
		return valid;
	}
	
	private Object[] businessValidation(Connection conn) throws CommonErrorException {
		boolean valid = true;
		boolean warning = false;		
		return new Object[]{valid, warning};
	}

	private String appendValue(StringBuilder errValues, String value){
		if(!Strings.isNullOrEmpty(errValues.toString()))
			errValues.append(", ");
		errValues.append(value);
		return errValues.toString();
	}
	
	@SuppressWarnings("unchecked")
	public Object[] manageTransaction(Connection conn) {
		Object[] result = new Object[3];
		try {
			Object[] chkresult = repository.insertAndCalculateDataToTarget(conn, this.getParamVo());
			int status = (int)chkresult[0];
			HashMap<String, String> resultMap = (HashMap<String, String>)chkresult[1];
			insertedCnt = Integer.parseInt(FormatUtil.nullToZero(resultMap.get("INSERT_TB_R_KOMPO")));

			result[0] = status;
			result[1] = resultMap;
			result[2] = "";
			return result;
		} catch (CommonErrorException e) {
			String errMsg = messageSource.getMessage(e.getMessageCode(), e.getMessageArg(), Locale.getDefault());
			if (!validateLogError(errMsg)) {
				loggerBBW02130.error(this.getParamVo().getAppId(), e.getMessageCode(), errMsg, this.getParamVo().getUserId());
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
			String errMsg = messageSource.getMessage(CST30000Messages.ERROR_UNDEFINED_ERROR, new String[] { CommonUtility.genMessageOfException(e) }, Locale.getDefault());
			if (!validateLogError(errMsg)) {
				loggerBBW02130.error(this.getParamVo().getAppId(), CST30000Messages.ERROR_UNDEFINED_ERROR, errMsg, this.getParamVo().getUserId());
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
}

