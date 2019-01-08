/******************************************************
 * Program History
 * 
 * Project Name	            :  st3-web-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.service
 * Program ID 	            :  CST30900ExcelDownloadService.java
 * Program Description	    :  <put description>
 * Environment	 	        :  Java 7
 * Author					:  Sira
 * Version					:  1.0
 * Creation Date            :  April 8, 2015
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 * 1.1		   13/5/2015   Elaine           N/A				update requestODBReport 
 * 															to call requestExcelDownload
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.SQLSyntaxErrorException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.persistence.EmbeddedId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import th.co.toyota.application.repository.IST30900ExcelDownloadRepository;
import th.co.toyota.application.web.form.CST30900ExcelDownloadForm;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.sc2.client.model.simple.CSC22110UserInfo;
import th.co.toyota.st3.api.constants.CST30000Constants;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.download.CST30090ExcelGenerator;
import th.co.toyota.st3.api.exception.NoDataFoundException;
import th.co.toyota.st3.api.exception.OverLimitException;
import th.co.toyota.st3.api.model.BatchQueue;
import th.co.toyota.st3.api.model.ExcelDownloadStatus;
import th.co.toyota.st3.api.model.ODBRoles;
import th.co.toyota.st3.api.model.OrderedFields;
import th.co.toyota.st3.api.model.SettingInfo;
import th.co.toyota.st3.api.model.SystemInfo;
import th.co.toyota.st3.api.model.TableRoleMap;
import th.co.toyota.st3.api.util.CST30000BatchManager;
import th.co.toyota.st3.api.util.CST32010DocNoGenerator;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

/**
 * Excel Download service implementation class.
 * 
 * @author Sira
 * 
 */
@Service
@Component
public class CST30900ExcelDownloadService implements IST30900ExcelDownloadService {
	
	private static String EQUAL = "=";
    private static String BIGGER = ">";
    private static String SMALLER = "<";
    private static String BIGGER_EQUAL = ">=";
    private static String SMALLER_EQUAL = "<=";
    private static String NOT_EQUAL = "<>";
    public final static String LIKE = "LIKE";
    public final static String NOTLIKE = "NOT LIKE";
    public final static int OPERATOR_CAT1 = 1;
    
    private static String NOTBETWEEN = "NOT BETWEEN";
    private static String BETWEEN = "BETWEEN";
    public final static int OPERATOR_CAT2 = 2;

    private static String IN = "IN";
    private static String NOTIN = "NOT IN";
    public final static int OPERATOR_CAT3 = 3;
    
    private static String IS = "IS";
    private static String ISNOT = "IS NOT";
    public final static int OPERATOR_CAT4 = 4;
    
	public final static String CHECKBOX_CHECKED = "1";
    
    public final static int DATATYPE_UNSUPPORTED = 0;
    public final static int DATATYPE_VARCHAR = 1;
    public final static int DATATYPE_NUMBER = 2;
    public final static int DATATYPE_DATE = 3;
    //Fixed IncidentForm_PH(IFT-ST3)_0.9.0_017 by Thanapon 22/5/2015
    public final static int DATATYPE_CHARACTER = 4;
    public final static int DATATYPE_CHARACTER_ARRAY = 5;
    //Fixed by thanapon 22/02/2017 current program not support decimal,double,float,long sample = 2000.00
    public final static int DATATYPE_BIGDECIMAL = 6;
    public final static int DATATYPE_DOUBLE = 7;
    public final static int DATATYPE_FLOAT = 8;
    public final static int DATATYPE_LONG = 9;
    
    public static String DATETIME_FORMAT = "dd/MM/yyyy";
    public static String DATE_FORMAT = "dd/MM/yyyy";
	public final static String BLANK = "";
	private static String PARAMETER_CHAR = "?";
	
	public static final String MODULE_ID = "ST3400";
	public static final String FUNCTION_ID = "ST3090";
	private static final String REQUEST_SCREEN_ID = "WST30900";
	
	private List<Object> listParams = new ArrayList<Object>();
	private String reportQuery;
	private List<String> displayCriteria = new ArrayList<String>();
	private Map<String, String> displayMapping = new HashMap<String, String>();

	final Logger logger = LoggerFactory.getLogger(CST30900ExcelDownloadService.class);
	
	@Autowired
	private IST30900ExcelDownloadRepository excelDownloadRepository;
	
	@Autowired
	private CST32010DocNoGenerator docNoGen;
	
	@Autowired
	private CST30000BatchManager batchManager;
	
	@Autowired
	@Qualifier("CST30090ExcelGenerator")
	protected CST30090ExcelGenerator generator;
	
	@Value("${projectCode}")
	protected String PROJECT_CODE;
	
	@Value("${default.download.folder}")
	protected String sharedFolder;

	@Value("${rowsperpage}")
	protected int maxRowsPerPage;
	
	@Value("${report.onlineLimitation:0}")
	protected int excelLimitRecord;
	
	@Value("${download.batchId}")
	protected String DOWNLOAD_BATCH_ID;

	@Value("${valid.pattern.1}")
	protected String validPatternNormal;
	
	@Value("${valid.pattern.2}")
	protected String validPatternLikeOper;
	
	@Value("${valid.pattern.3}")
	protected String validPatternInOper;
	
	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.service.IST30900ExcelDownloadService#getTableList(java.util.List)
	 */
	@Override
	public List<TableRoleMap> getTableList(List<String> roleId) {
		return excelDownloadRepository.getTableList(roleId);
	}
	
	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.service.IST30900ExcelDownloadService#getBookmarkList(java.util.List, java.lang.String)
	 */
	//Fixed by Thanapon add parameter tableName
	@Override
	public List<SettingInfo> getBookmarkList(List<String> roleId, String selectedTableName) {
		return excelDownloadRepository.getBookmarkList(roleId, selectedTableName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.service.IST30900ExcelDownloadService#querySystemMaster(boolean, java.lang.String)
	 */
	public List<SystemInfo> querySystemMaster(boolean mandatoryFlag,String code) {
		if(mandatoryFlag){
			return excelDownloadRepository.querySystemMaster(PROJECT_CODE, FUNCTION_ID+"_MANDATORY", code);
		}else{
			return excelDownloadRepository.querySystemMaster(PROJECT_CODE, FUNCTION_ID, code);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.service.IST30900ExcelDownloadService#loadTableViewInfo(java.lang.String)
	 */
	@Override
	public List<SettingInfo> loadTableViewInfo(String tableName) throws SQLSyntaxErrorException, ClassNotFoundException {
		return excelDownloadRepository.getMetadata(tableName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.service.IST30900ExcelDownloadService#getBookmarkDetails(java.util.List, java.lang.String)
	 */
	@Override
	public List<SettingInfo> getBookmarkDetails(List<String> roleId, String settingId) {
		return excelDownloadRepository.getBookmarkDetails(roleId, settingId);
	}
	
	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.service.IST30900ExcelDownloadService#saveBookmark(java.util.List)
	 */
	@Override
	public boolean saveBookmark(List<SettingInfo> settingInfo, List<String> roles) {
		boolean result = false;
		for (String role : roles) {
			for (int i=0;i<settingInfo.size();i++) {
				settingInfo.get(i).setRoleID(role);
			}
			result = excelDownloadRepository.saveBookmark(settingInfo);
			if (!result) {
				break;
			}
		}
//		return excelDownloadRepository.saveBookmark(settingInfo);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.service.IST30900ExcelDownloadService#getRecordCount(th.co.toyota.application.web.form.CST30900ExcelDownloadForm)
	 */
	@Override
	public int getRecordCount(CST30900ExcelDownloadForm form) throws ClassNotFoundException, OverLimitException, NoDataFoundException {
		int totalRecordCount = excelDownloadRepository.getRecordCount(
				this.generateQuery(form, false), this.listParams.toArray(), form.getModelClassName());
		
		if ((this.excelLimitRecord > 0) && (totalRecordCount > 0)) {
			if (Strings.isNullOrEmpty(form.getWST30900BigData())&& (totalRecordCount > this.excelLimitRecord)) {
				throw new OverLimitException(new StringBuffer("Table/View with more than ").append(this.excelLimitRecord).append(" records").toString());
			}
		}
		
		if (totalRecordCount == 0) {
			throw new NoDataFoundException();
		}
		
		return totalRecordCount;
	}

	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.service.IST30900ExcelDownloadService#generateReport(th.co.toyota.application.web.form.CST30900ExcelDownloadForm, th.co.toyota.sc2.client.model.simple.CSC22110UserInfo, java.util.List)
	 */
	@Override
	public String generateReport(CST30900ExcelDownloadForm form, CSC22110UserInfo userInfo, List<String> userRoles) throws Exception {
		String filename = "";
		String reportQuery = generateQuery(form, false);
		generator.setReportName(Strings.isNullOrEmpty(form.getReportName()) ? form.getTableName() : form.getReportName());
		generator.setReportTitle(form.getReportTitle());
		generator.setStartRow(Strings.isNullOrEmpty(form.getStartRow()) ? 1 : Integer.parseInt(form.getStartRow()));
		generator.setStartColumn(Strings.isNullOrEmpty(form.getStartColumn()) ? 1 : Integer.parseInt(form.getStartColumn()));
		generator.setCriteria(getDisplayCriteria().toArray(new String[getDisplayCriteria().size()]));
		generator.setDisplayNames(getDisplayMapping());
		generator.setFileType(form.getFileType()); // C = CSV, E = XLS, X = XLSX
		generator.setFirstResult(0);
		generator.setOverridePath(this.sharedFolder);

		filename = generator.createExcel(reportQuery, this.listParams.toArray(), form.getModelClassName());
		
		return filename;
	}
	
	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.service.IST30900ExcelDownloadService#requestODBReport(th.co.toyota.application.web.form.CST30900ExcelDownloadForm, th.co.toyota.sc2.client.model.simple.CSC22110UserInfo, java.util.List)
	 */
	
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public String requestODBReport(CST30900ExcelDownloadForm form, CSC22110UserInfo userInfo, List<String> userRoles) 
		throws Exception {
		
//		String docId = docNoGen.generateDocNo(DOC_SEQ_CD, new Date());
		String reportQuery = generateQuery(form, true);
		
		ExcelDownloadStatus excel = new ExcelDownloadStatus();
//		excel.setDocId(docId);
		excel.setModuleId(MODULE_ID);
		excel.setFunctionId(FUNCTION_ID);
		excel.setReportName(Strings.isNullOrEmpty(form.getReportTitle()) ? form.getTableName() : form.getReportTitle());
		//Fixed IncidentForm_PH(IFT-ST3)_4_0.9.0_014 by Thanapon 22/05/2015
//		excel.setFileCount(1);		
		excel.setCriteriaLegend(this.getCriteriaLegend());
		//end Fixed IncidentForm_PH(IFT-ST3)_4_0.9.0_014 by Thanapon 22/05/2015
		excel.setStartRow(Strings.isNullOrEmpty(form.getStartRow()) ? 1 : Integer.parseInt(form.getStartRow()));
		excel.setStartColumn(Strings.isNullOrEmpty(form.getStartColumn()) ? 1 : Integer.parseInt(form.getStartColumn()));
		excel.setPicEmail(userInfo.getEmail());
		excel.setSqlStmt(reportQuery);
		excel.setExtXlsParams(null);
		excel.setStatus(CST30000Constants.EXCEL_DL_STATUS_ON_QUEUE);
		excel.setRequestDt(new Date());
		excel.setRequestBy(userInfo.getUserId());
		excel.setUpdateBy(userInfo.getUserId());
		excel.setUpdateDt(new Date());
		excel.setModelClassName(form.getModelClassName());
		//Lieu added new field for on demand batch
		//Get fields with alias and append to one string
		//Format should be: <field1>=<alias1>|<field2>=<alias2>...
		StringBuffer aliasFields = new StringBuffer();
		for (int i = 0; i < form.getDbFieldName().length; i++) {
			if (!Strings.isNullOrEmpty(form.getDisplayName()[i])) {
				if (!Strings.isNullOrEmpty(aliasFields.toString())) {
					aliasFields.append(CST30000Constants.STRING_SEPARATOR_PIPE);
				}
				aliasFields.append(form.getDbFieldName()[i])
						.append(CST30000Constants.STRING_SEPARATOR_EQUAL)
						.append(form.getDisplayName()[i]);
			}
		}
		excel.setDisplayNames(aliasFields.toString());
		//End
//		excelDownloadRepository.insertExcelDownloadStatus(excel);
		
//		Set ODB role for document
		List<ODBRoles> odbRoleList = new ArrayList<ODBRoles>();
		for (String role : userRoles) {
			ODBRoles odbRole = new ODBRoles();
//			odbRole.setDocId(docId);
			odbRole.setRoleId(role);
			odbRole.setCreateDt(new Date());
			
			odbRoleList.add(odbRole);
			
//			excelDownloadRepository.insertODBRole(odbRole);
		}
//		End set ODB role
		
//		Start set batch queue
		BatchQueue batch = new BatchQueue();
		batch.setRequestId(REQUEST_SCREEN_ID);
		batch.setBatchId(DOWNLOAD_BATCH_ID);
		batch.setRequestBy(userInfo.getUserId());
		batch.setRequestDate(new Date());
		batch.setSupportId(BLANK);
		batch.setProjectCode(PROJECT_CODE);

//		Set batch parameter
		List<String> listParameter = new ArrayList<String>();
		listParameter.add(CST30000Constants.PARAM_SIGN_MODULE_ID);
		listParameter.add(MODULE_ID);
		listParameter.add(CST30000Constants.PARAM_SIGN_FUNCTION_ID);
		listParameter.add(FUNCTION_ID);
		listParameter.add(CST30000Constants.PARAM_SIGN_FILE_TYPE);
		listParameter.add(Strings.nullToEmpty(form.getFileType()));
		
		batch.setParameters(Joiner.on(CST30000Constants.ODB_REQUEST_PARAM_SEPARATOR).join(listParameter));

//		batchManager.createBatchQueue(batch);
//		End set batch queue
		
		String docId = generator.requestExcelDownload(excel, odbRoleList, batch);
		
		return docId;
	}

	/*
	 * (non-Javadoc)
	 * @see th.co.toyota.application.service.IST30900ExcelDownloadService#validateForm(org.springframework.context.MessageSource, java.util.Locale, th.co.toyota.application.web.form.CST30900ExcelDownloadForm)
	 */
	@Override
	@SuppressWarnings("all")
	public List<String> validateForm(MessageSource messageSource, Locale locale, CST30900ExcelDownloadForm form) {
		List<String> errors = new ArrayList<String>();
        
//        if (form.getModelClassName().trim().isEmpty()) {
        if (Strings.isNullOrEmpty(form.getModelClassName())) {
        	errors.add(messageSource.getMessage(CST30000Messages.ERROR_NO_DATA_SAVE, null, locale));
        	return errors;
		}else{
			//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon
			try {
				excelDownloadRepository.getMetadata(form.getModelClassName().trim());
			} catch (SQLSyntaxErrorException e) {
				logger.error(e.getMessage());
				errors.add(e.getMessage());
				return errors;
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage());
				errors.add(messageSource.getMessage(CST30000Messages.ERROR_MESSAGE_TABLE_VIEW_DOES_NOT_EXIST, new String[] {form.getTableName()}, locale));
				return errors;
			}
			//end Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon
		}
        
        List<SettingInfo> setting = assignFormtoSettingInfo(form);
		for(int n=0; n<setting.size(); n++) {
			SettingInfo tmp = setting.get(n);
			/* Basic Validate */
			// Display Name
			if (tmp.getDisplayName().trim().length() > 30) {
				errors.add(messageSource.getMessage(CST30000Messages.ERROR_MESSAGE_INVALID_LENGTH_OVER, 
						new String[] {form.displayFriendlyField("displayName", tmp.getFieldName()), "30"}, locale));
			} else if (!Pattern.matches("^[0-9A-Za-z\\s]{0,30}+$", tmp.getDisplayName())) {
				//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon
				errors.add(messageSource.getMessage(CST30000Messages.ERROR_MESSAGE_INVALID_FIELD, 
						new String[] {form.displayFriendlyField("displayName", tmp.getFieldName())}, locale));
				//end Fixed IncidentForm_PH(IFT-ST3)_0.9.0_009 by Thanapon
			}
			// Order Display
			if (tmp.getOrderDisp().trim().length() > 3) {
				errors.add(messageSource.getMessage(CST30000Messages.ERROR_MESSAGE_INVALID_LENGTH_OVER, 
						new String[] {form.displayFriendlyField("orderDisp", tmp.getFieldName()), "3"}, locale));
			} else if (!Pattern.matches("^[0-9]{0,3}+$", tmp.getOrderDisp())) {
				errors.add(messageSource.getMessage(CST30000Messages.ERROR_INPUT_MUST_BE_NUMBER, 
						new String[] {form.displayFriendlyField("orderDisp", tmp.getFieldName())}, locale));
			}
			// Sort
			if (tmp.getSort().trim().length() > 3) {
				errors.add(messageSource.getMessage(CST30000Messages.ERROR_MESSAGE_INVALID_LENGTH_OVER, 
						new String[] {form.displayFriendlyField("sort", tmp.getFieldName()), "3"}, locale));
			} else if (!Pattern.matches("^[0-9]{0,3}+$", tmp.getSort())) {
				errors.add(messageSource.getMessage(CST30000Messages.ERROR_INPUT_MUST_BE_ALPHANUMERIC, 
						new String[] {form.displayFriendlyField("sort", tmp.getFieldName())}, locale));
			}
			// Criteria
			if (tmp.getCriteria().trim().length() > 256) {
				errors.add(messageSource.getMessage(CST30000Messages.ERROR_MESSAGE_INVALID_LENGTH_OVER, 
						new String[] {form.displayFriendlyField("criteria", tmp.getFieldName()), "50"}, locale));
			}
			
			//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_006 by Thanapon 19/05/2015
			if(n == setting.size()-1){//the last criteria
				if(setting.size() == 1){
					if ((!Strings.isNullOrEmpty(tmp.getCriteria().trim())) && (!Strings.isNullOrEmpty(tmp.getLogicalOpr()))) {
						errors.add(messageSource.getMessage(CST30000Messages.ERROR_DATA_SHOULD_BE_EMPTY,
	        				new String[] {"Logical Operator of " + tmp.getFieldName()}, locale));
					}else if ((Strings.isNullOrEmpty(tmp.getCriteria().trim())) && (!Strings.isNullOrEmpty(tmp.getLogicalOpr()))) {
						errors.add(messageSource.getMessage(CST30000Messages.ERROR_DATA_SHOULD_BE_EMPTY,
	        				new String[] {"Logical Operator of " + tmp.getFieldName()}, locale));
					}
				}else{
					if (!Strings.isNullOrEmpty(tmp.getLogicalOpr())) {
						errors.add(messageSource.getMessage(CST30000Messages.ERROR_DATA_SHOULD_BE_EMPTY,
	        				new String[] {"Logical Operator of " + tmp.getFieldName()}, locale));
					}
					//loop for checking has been criteria or logical before current record?
					boolean found = false;
					for(int k=0; k < setting.size()-1; k++){
						SettingInfo tmpChk = setting.get(k);
						if(!Strings.isNullOrEmpty(tmpChk.getLogicalOpr())) {
							found = true;
							break;
						}
					}
				}
			}else{
				//except the last criteria, all other criteria must have a corresponding logical operator
				if(Strings.isNullOrEmpty(tmp.getCriteria().trim()) && !Strings.isNullOrEmpty(tmp.getLogicalOpr())) {
					errors.add(messageSource.getMessage(CST30000Messages.ERROR_INVALID_CRITERIA_AND_LOGIC_OPTION,
	        				new String[] {tmp.getFieldName() + " : "}, locale));
				}else if (!Strings.isNullOrEmpty(tmp.getCriteria().trim()) && Strings.isNullOrEmpty(tmp.getLogicalOpr())){
					//loop for checking has been criteria or logical after current record?
					boolean found = false;
					for(int k=n+1; k < setting.size(); k++){
						SettingInfo tmpChk = setting.get(k);
						if(!Strings.isNullOrEmpty(tmpChk.getCriteria().trim()) || !Strings.isNullOrEmpty(tmpChk.getLogicalOpr())) {
							found = true;
							break;
						}
					}
					if(found){
						errors.add(messageSource.getMessage(CST30000Messages.ERROR_INVALID_CRITERIA_AND_LOGIC_OPTION,
		        				new String[] {tmp.getFieldName() + " : "}, locale));
					}
				}else if (!Strings.isNullOrEmpty(tmp.getCriteria().trim()) && !Strings.isNullOrEmpty(tmp.getLogicalOpr())){
					//loop for checking has been criteria or logical after current record?
					boolean isBlank = true;
					for(int k=n+1; k < setting.size(); k++){
						SettingInfo tmpChk = setting.get(k);
						if(!Strings.isNullOrEmpty(tmpChk.getCriteria().trim()) || !Strings.isNullOrEmpty(tmpChk.getLogicalOpr())) {
							isBlank = false;
							break;
						}
					}
					if(isBlank){
						errors.add(messageSource.getMessage(CST30000Messages.ERROR_DATA_SHOULD_BE_EMPTY,
		        				new String[] {"Logical Operator of " + tmp.getFieldName()}, locale));
					}
				}
			}
			//end Fixed IncidentForm_PH(IFT-ST3)_0.9.0_006 by Thanapon 19/05/2015
			
        	// validate relation between Criteria and Logical Option columns 
        	// Incorrect criteria field format. The format should be: 'Operator Value1 [Value2]'
        	String stOperatorCat = getOperatorCat(tmp.getCriteria());
        	if (stOperatorCat.startsWith("0|")){
        		if (!tmp.getCriteria().trim().equalsIgnoreCase(BLANK)){
        			errors.add(messageSource.getMessage(CST30000Messages.ERROR_INVALID_CRITERIA_FORMAT,
        					new String[] {tmp.getFieldName()}, locale));
        		}
        	} else {
        		// Validate the value itself (corresponding with each data type).
        		String stCriteria = getPureString(tmp.getCriteria());
        		int operatorCat = Integer.parseInt(getOperatorCat(stCriteria).split("\\|")[0]);
        		String operator = (String) getOperatorCat(stCriteria).split("\\|")[1];
        		String filterValue = BLANK;
        		
        		if (getOperatorCat(stCriteria).split("\\|").length >= 3){
        			filterValue = (String)getOperatorCat(stCriteria).split("\\|")[2];
         		   // 1. BETWEEN operator have to have Start value and End value
         			String[] arSt = null;
        			if (operatorCat == OPERATOR_CAT2) {
    					if (((filterValue.trim().indexOf(" ") < 0)) ||((filterValue.toUpperCase().trim().indexOf("AND")) < 0)){
    						errors.add(messageSource.getMessage(CST30000Messages.ERROR_BETWEEN_FORMAT,
    	        					new String[] {tmp.getFieldName()}, locale));
    					}
        			}
        			
        			// 2. Validate Date value
            		if (getDataType(tmp.getDataType()) ==  DATATYPE_DATE){
            			switch  (operatorCat) {
            				case OPERATOR_CAT1:
            					if ((!operator.trim().toUpperCase().equalsIgnoreCase(LIKE)) &&
            							(!operator.trim().toUpperCase().equalsIgnoreCase(NOTLIKE))){
	            					if (!isCorrectDateValue(filterValue)){
	            						Object[] oPars  = new Object[]{tmp.getFieldName(), filterValue};
	            						errors.add(messageSource.getMessage(CST30000Messages.ERROR_DATE_VALUE,
	            								oPars, locale));
	            					}
            					}
            					break;
            				case OPERATOR_CAT2:
            					arSt  = filterValue.toUpperCase().split(" AND ");
            					for (int i = 0; i< arSt.length;i++){
            						if (!isCorrectDateValue(arSt[i].trim())){
                						Object[] oPars  = new Object[]{tmp.getFieldName(), arSt[i].trim()};
                						errors.add(messageSource.getMessage(CST30000Messages.ERROR_DATE_VALUE,
                								oPars, locale));
                					}
            					}
            					break;
            				case OPERATOR_CAT3:
            					arSt  = filterValue.trim().split(" ");
								for (int i = 0; i < arSt.length; i++) {
	        						if (!isCorrectDateValue(arSt[i].trim())){
	            						Object[] oPars  = new Object[]{tmp.getFieldName(), arSt[i].trim()};
	            						errors.add(messageSource.getMessage(CST30000Messages.ERROR_DATE_VALUE,
	            								oPars, locale));
	            					}
	        					}
            					break;
            			}
            	    }
            		
            		// 3. Validate NUMBER value
            		if ((getDataType(tmp.getDataType()) == DATATYPE_NUMBER) || 
            			(getDataType(tmp.getDataType()) == DATATYPE_BIGDECIMAL) ||
            			(getDataType(tmp.getDataType()) == DATATYPE_DOUBLE) ||
            			(getDataType(tmp.getDataType()) == DATATYPE_FLOAT) ||
            			(getDataType(tmp.getDataType()) == DATATYPE_LONG)){
            			switch  (operatorCat) {
            				case OPERATOR_CAT1:
            					if (!isNumeric(filterValue)){
            						String[] oPars  = new String[]{tmp.getFieldName(), filterValue};
            						errors.add(messageSource.getMessage(CST30000Messages.ERROR_NUMBER_VALUE,
            								oPars, locale));
            					}
            					break;
            				case OPERATOR_CAT2:
            					if ((filterValue.toUpperCase().trim().indexOf("AND")) >  0){
	            					arSt  = filterValue.toUpperCase().split(" AND ");
	            					for (int i = 0; i< arSt.length;i++){
	            						if (!isNumeric(arSt[i].trim())){
	            							String[] oPars  = new String[]{tmp.getFieldName(), arSt[i].trim()};
	                						errors.add(messageSource.getMessage(CST30000Messages.ERROR_NUMBER_VALUE,
	                								oPars, locale));
	                					}
	            					}
            					}
            					break;
            				case OPERATOR_CAT3:
            					arSt  = filterValue.trim().split(" ");
	        					for (int i = 0; i< arSt.length;i++){
	        						if (!isNumeric(arSt[i].trim())){
	        							String[] oPars  = new String[]{tmp.getFieldName(), arSt[i].trim()};
	            						errors.add(messageSource.getMessage(CST30000Messages.ERROR_NUMBER_VALUE,
	            								oPars, locale));
	            					}
	        					}
            					break;
            			}
            	    }
            		
            		//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_015 by Thanapon 19/05/2015
            		// 4. Validate String value
            		if ((getDataType(tmp.getDataType()) == DATATYPE_VARCHAR) || 
            			(getDataType(tmp.getDataType()) == DATATYPE_CHARACTER) || //Fixed IncidentForm_PH(IFT-ST3)_0.9.0_017 by Thanapon 22/5/2015
            			(getDataType(tmp.getDataType()) == DATATYPE_CHARACTER_ARRAY)){ //Fixed IncidentForm_PH(IFT-ST3)_0.9.0_017 by Thanapon 22/5/2015
            			switch  (operatorCat) {
            				case OPERATOR_CAT1:
            					if ((operator.trim().toUpperCase().equalsIgnoreCase(LIKE)) ||
            							(operator.trim().toUpperCase().equalsIgnoreCase(NOTLIKE))){
            						if (!Pattern.matches(validPatternLikeOper, filterValue)) {            						
	            						errors.add(messageSource.getMessage(CST30000Messages.ERROR_MESSAGE_INVALID_FIELD, 
	            								new String[] {form.displayFriendlyField("criteria", tmp.getFieldName())}, locale));
	            					}
            					}else{
	            					if (!Pattern.matches(validPatternNormal, filterValue)) {            						
	            						errors.add(messageSource.getMessage(CST30000Messages.ERROR_MESSAGE_INVALID_FIELD, 
	            								new String[] {form.displayFriendlyField("criteria", tmp.getFieldName())}, locale));
	            					}
            					}
            					break;
            				case OPERATOR_CAT2:
            					if ((filterValue.toUpperCase().trim().indexOf("AND")) >  0){
	            					arSt  = filterValue.toUpperCase().split(" AND ");
	            					for (int i = 0; i< arSt.length;i++){
	            						if (!Pattern.matches(validPatternNormal, arSt[i].trim())) { 
	            							errors.add(messageSource.getMessage(CST30000Messages.ERROR_MESSAGE_INVALID_FIELD, 
	                								new String[] {form.displayFriendlyField("criteria", tmp.getFieldName())}, locale));
	            							break;
	                					}
	            					}
            					}
            					break;
            				case OPERATOR_CAT3:
            					arSt  = filterValue.trim().split(" ");
	        					for (int i = 0; i< arSt.length;i++){
	        						if (!Pattern.matches(validPatternInOper, arSt[i].trim())) { 
	        							errors.add(messageSource.getMessage(CST30000Messages.ERROR_MESSAGE_INVALID_FIELD, 
                								new String[] {form.displayFriendlyField("criteria", tmp.getFieldName())}, locale));
	        							break;
	            					}
	        					}
            					break;
            			}
            	    }
            		//end Fixed IncidentForm_PH(IFT-ST3)_0.9.0_015 by Thanapon 19/05/2015
        		} else{
        			// Only have Operator but don't have Value
        			errors.add(messageSource.getMessage(CST30000Messages.ERROR_INVALID_CRITERIA_FORMAT,
        					new String[] {tmp.getFieldName()}, locale));
        		}
        	}
		}
		
		return errors;
	}
	
	/**
	 * Verifies the inputed date.
	 * @param inputedValue
	 * @return treu if date is correct value.
	 */
	private boolean isCorrectDateValue(String inputedValue){
		long lDateValue = 0;
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		sdf.setLenient(false);
		try{
			if (!inputedValue.trim().equalsIgnoreCase(BLANK)){
	    		lDateValue = sdf.parse(inputedValue).getTime();
	    		if (lDateValue < 0){
	    			return false;
	    		}
			}
			return true;
		}catch (Exception e) {
			return false;
		}
	 }
	
	/**
	 * Verifies the inputted number.
	 * @param s
	 * @return true if number is correct.
	 */
	private boolean isNumeric(String s) {
		  final char[] numbers = s.toCharArray();
		  for (int x = 0; x < numbers.length; x++) {      
		    final char c = numbers[x];
		    if (((c >= '0') && (c <= '9')) || (c == '.')) continue;
		    return false; // invalid
		  }
		  return true; // valid
	}
	
	/**
     * Indicate all field will be appear as selected fields in SELECT statement.
     * @param settingInfoList
     * @return A fields to select.
     */
	private String fieldsToSelect(List<SettingInfo> settingInfoList, String entity) throws ClassNotFoundException{
		String stReturn = null;
    	StringBuffer sbSelectFields = new StringBuffer();
    	List<SettingInfo> selectList = new ArrayList<SettingInfo>();
    	selectList.addAll(settingInfoList);
    	Collections.sort(selectList, SettingInfoComparator.COMPARATOR);

		// get entity model class
		Class<?> enModel = Class.forName(entity);
    	logger.debug(enModel.getName());
    	
    	for (SettingInfo setting : selectList) {
    		if (setting.getDisplayOption().equalsIgnoreCase(CHECKBOX_CHECKED)) {
    			String id = this.getIdFromSubClass(setting.getObjFieldName(), entity);
    			sbSelectFields.append(id+setting.getObjFieldName());
    			
    			if (!setting.getDisplayName().trim().isEmpty()) {
    				this.addDisplayMapping(setting.getFieldName(), setting.getDisplayName());
	    		}
	    		sbSelectFields.append(",");  	
    		}
    	}
    	selectList = null;
    	if (sbSelectFields.length() > 0){
    		stReturn = sbSelectFields.toString().substring(0,sbSelectFields.toString().length()-1);
    	}
    	return stReturn;
	}
	
	/**
	 * Sort column configs
	 * @author hoang
	 *
	 */
	protected static class SettingInfoComparator implements Comparator<Object> {
		public static SettingInfoComparator COMPARATOR = new SettingInfoComparator();

		public int compare(Object o1, Object o2) {
			Integer seq1 = Ints.tryParse(((SettingInfo) o1).getOrderDisp());
			Integer seq2 = Ints.tryParse(((SettingInfo) o2).getOrderDisp());
			if (seq1 == null) {
				seq1 = Integer.MAX_VALUE;
			}
			if (seq2 == null) {
				seq2 = Integer.MAX_VALUE;
			}
			return seq1 < seq2 ? -1 : (seq1 == seq2 ? 0 : 1);
		}
	}
	
	/**
	 * Condition Precedence (fully supported by Oracle)
	 * <ol>
	 * <li>=, !=, <, >, <=, >=, !=, <>, ^=, ~=
	 * <li>IS [NOT] NULL, LIKE, [NOT] BETWEEN, [NOT] IN, EXISTS, IS OF TYPE
	 * <li>NOT
	 * <li>AND
	 * <li>OR
	 * </ol>
	 * Condition Precedence (fully supported by Std)
	 * <ol>
	 * <li>=, <, >, <=, >=,
	 * <li>IS [NOT] NULL, LIKE, [NOT] BETWEEN, [NOT] IN
	 * <li>AND
	 * <li>OR
	 * <ol>
	 * Oracle Datatype:
	 * <ul>
	 * <li>VARCHAR2
	 * <li>TIMESTAMP
	 * <li>NUMBER
	 * <li>CHAR
	 * </ul>
	 * 
	 * @param settingInfoList
	 * @return A filtered conditions.
	 * 
	 */
	private String filterConditions(List<SettingInfo> settingInfoList, boolean passODB, String entity) throws ClassNotFoundException {
		String stReturn = null;
    	StringBuffer sbFilterConditions = new StringBuffer();
    	boolean isFirstCriteria=true;
    	
    	int i = 1;
		for (SettingInfo setting : settingInfoList) {
			String stCriteria = getPureString(setting.getCriteria());
			
			if (!stCriteria.isEmpty()) {
				if (isFirstCriteria) {
					sbFilterConditions.append(" WHERE ");
    		        isFirstCriteria=false;
				}				
				String stOperatorCatFull = getOperatorCat(stCriteria);
				
				int operatorCat = Integer.parseInt(stOperatorCatFull.split("\\|")[0]);
    			String operator = (String)stOperatorCatFull.split("\\|")[1];
    			String filterValue = (String)stOperatorCatFull.split("\\|")[2];
    			
    			if ((operatorCat != 0) && (!operator.trim().isEmpty())){
    				
    				String id = this.getIdFromSubClass(setting.getObjFieldName(), entity);
    				
    				String stEntryFilterValue = entryFilterValue(getDataType(
    						setting.getDataType()),operatorCat,filterValue, id+setting.getObjFieldName(),operator, i, passODB);
    				sbFilterConditions.append(stEntryFilterValue).append(" ");
    				//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_006 by Thanapon 19/05/2015
    				sbFilterConditions.append(" ").append(setting.getLogicalOpr());
    				
    				if ((operatorCat == OPERATOR_CAT2)) {	//between ... and ...
    					i++;
    				}
    				if ((operatorCat == OPERATOR_CAT3)) {	//in/not in (..., ...)
    					i += filterValue.split(" ", -1).length - 1;
    				}
    				if ((operatorCat == OPERATOR_CAT4)) {	//is null/ is not null
    					i--;
    				}
    			}
    			i++;
			}
		}
		
		stReturn = sbFilterConditions.toString();
    	return stReturn;
	}
	
	/**
	 * Retrive field ID for the gvien column name and model/entity.
	 * @param objFieldName
	 * @param entity
	 * @return A field id.
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("all")
	private String getIdFromSubClass(String objFieldName, String entity) throws ClassNotFoundException{
		String fieldId = "";
		// get entity model class
		Class<?> enModel = Class.forName(entity);
		boolean added = false;
		for (Field field : enModel.getDeclaredFields()) {    				
			EmbeddedId embId = field.getAnnotation(EmbeddedId.class);
			if (embId != null) {
				Class emClass = field.getType();
				for (Field embedIdField : emClass.getDeclaredFields()) {
					if (objFieldName.equalsIgnoreCase(embedIdField.getName())) {
						fieldId = field.getName()+".";
						added = true;
						break;
					}
				}
			} 
			if(added){
				break;
			}
		}
		return fieldId;
	}
	
	/**
	 * Apply the quote mark on the string.
	 * @param org
	 * @return A string value with quote.
	 */
	private String applyQuote(String org) {
		return "'" + org + "'";
	}
	
	/**
	 * Prepare the criteria legend.
	 * 
	 * @param form
	 * @param settingInfoList
	 */
	//fix IncidentForm_PH(IFT-ST3)_4_0.9.0_014 by thanapon 22/05/2015
	private void prepareCriteriaLegend(CST30900ExcelDownloadForm form, List<SettingInfo> settingInfoList){
		StringBuffer criteriaLegend = new StringBuffer();
		for (SettingInfo setting : settingInfoList) {
			if(!Strings.isNullOrEmpty(setting.getCriteria())){
				criteriaLegend = new StringBuffer();
				criteriaLegend.append(setting.getFieldName());
				criteriaLegend.append(" ");
				criteriaLegend.append(setting.getCriteria());
				this.addDisplayCriteria(criteriaLegend.toString());
			}
		}
	}
	
	/**
	 * Return the criteria legend.
	 * @return A criteria legend.
	 */
	//Fixed IncidentForm_PH(IFT-ST3)_4_0.9.0_014 by Thanapon 22/05/2015
	private String getCriteriaLegend(){
		StringBuffer criteriaLegend = new StringBuffer();
		for(int i=0; i<getDisplayCriteria().size(); i++){
			criteriaLegend.append(getDisplayCriteria().get(i));
			if(i != getDisplayCriteria().size()-1 ){
				criteriaLegend.append(CST30000Constants.STRING_SEPARATOR_COMMA);
			}
		}
		return criteriaLegend.toString();
	}
	
	/**
	 * Filter the value based on given data type and the column name.
	 * <p>
	 * It will build query for Following criteria's on data type String, Date,
	 * Char, Number etc.
	 * <ul>
	 * <li>BETWEEN
	 * <li>NOT BETWEEN
	 * <li>LIKE
	 * <li>NOT LIKE
	 * <li>AND
	 * <li>IN
	 * <li>NOT IN
	 * <li>NULL
	 * <li>NOT NULL
	 * </ul>
	 * 
	 * @param dataType
	 * @param operatorCat
	 * @param filterValue
	 * @param fieldName
	 * @param operator
	 * @return A criteria query.
	 */
    private String entryFilterValue(int dataType, int operatorCat, String filterValue, 
    		String fieldName, String operator, int index, boolean passODB){
    	String stReturn = null;
    	StringBuffer sbTmp1 = new StringBuffer();
    	StringBuffer sbTmp2 = new StringBuffer();
    	filterValue = filterValue.trim().replaceAll("(\\s)+", "$1");	//remove extra spaces
    	String stfilterValueTmp = filterValue.toUpperCase();
    	final String AND_SEPARATOR = " AND ";
    	String[] valueParams = null;
    	
		if (operatorCat == OPERATOR_CAT2) {
			if (stfilterValueTmp.indexOf(AND_SEPARATOR) < 0) {
				// throw Exception BETWEEN operator has to has format as: BETWEEN ... [AND] ...
			} else {
				valueParams = stfilterValueTmp.split(AND_SEPARATOR, -1);
			}
		} else if (operatorCat == OPERATOR_CAT3) {
			valueParams = stfilterValueTmp.split(" ", -1);
		} else if (operatorCat == OPERATOR_CAT4) {
			if (stfilterValueTmp.equalsIgnoreCase("NULL")) {
				sbTmp1.append(" ").append(fieldName).append(" ").append(operator).append(" NULL");
				sbTmp2.append(sbTmp1);
				
				stReturn = passODB? sbTmp1.toString() : sbTmp2.toString();
		    	return stReturn;
			} else {
				// throw Exception IS [NOT] NULL.
			}
		}
    	
    	switch (dataType){
    		case DATATYPE_VARCHAR:
    		case DATATYPE_CHARACTER://Fixed IncidentForm_PH(IFT-ST3)_0.9.0_017 by Thanapon 22/5/2015
    		case DATATYPE_CHARACTER_ARRAY://Fixed IncidentForm_PH(IFT-ST3)_0.9.0_017 by Thanapon 22/5/2015
    			stfilterValueTmp = applyQuote(stfilterValueTmp);
    			//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_017 by Thanapon 22/5/2015
    			if(dataType == DATATYPE_VARCHAR){
    				sbTmp1.append(" UPPER(").append(fieldName).append(") ").append(operator);
    			} else if(dataType == DATATYPE_CHARACTER){
    				sbTmp1.append(" UPPER(SUBSTRING(").append(fieldName).append(",1,2)) ").append(operator);
    			} else{
    				sbTmp1.append(" UPPER(SUBSTRING(").append(fieldName).append(",1,250)) ").append(operator);
    			}
    			sbTmp2.append(sbTmp1);
    			if (operatorCat == OPERATOR_CAT1){
    				sbTmp1.append(stfilterValueTmp.toUpperCase().replace('*', '%'));
    				sbTmp2.append(PARAMETER_CHAR).append(index);
    				this.listParams.add(filterValue.toUpperCase().replace('*', '%'));
    			}
    			if (operatorCat == OPERATOR_CAT2){					
					sbTmp1.append(" ").append(applyQuote(valueParams[0]).toUpperCase()).append(AND_SEPARATOR)
							.append(applyQuote(valueParams[1]).toUpperCase()); 
					sbTmp2.append(" ").append(PARAMETER_CHAR).append(index).append(AND_SEPARATOR)
							.append(PARAMETER_CHAR).append(index + 1); 
					this.listParams.add(valueParams[0].toUpperCase());
					this.listParams.add(valueParams[1].toUpperCase());
    			}
    			if (operatorCat == OPERATOR_CAT3){
    				//field-name IN (.. , ..)
    				sbTmp1.append(" ( ");
    				sbTmp2.append(" ( ");
    				for (int i = 0; i < valueParams.length; i ++) {
    					if (i > 0) {
    						sbTmp1.append(", ");
    						sbTmp2.append(", ");
    					}
    					sbTmp1.append(applyQuote(valueParams[i].trim().toUpperCase()));
    					sbTmp2.append(PARAMETER_CHAR).append(index + i);
    					this.listParams.add(valueParams[i].trim().toUpperCase());
    				}
    				sbTmp1.append(" ) ");
    				sbTmp2.append(" ) ");
    			}
    			break;
    		case DATATYPE_NUMBER:
    		case DATATYPE_BIGDECIMAL://Fixed by thanapon 22/02/2017 current program not support decimal sample = 2000.00
    		case DATATYPE_DOUBLE:
    		case DATATYPE_FLOAT:
    		case DATATYPE_LONG:
    			sbTmp1.append(" ").append(fieldName).append(" ").append(operator);
    			sbTmp2.append(sbTmp1);
    			if (operatorCat == OPERATOR_CAT1){
    				sbTmp1.append(filterValue);
    				sbTmp2.append(PARAMETER_CHAR).append(index);
    				if(dataType == DATATYPE_BIGDECIMAL){
    					this.listParams.add(FormatUtil.convertStringToBigDecimal(filterValue));
    				}else if(dataType == DATATYPE_DOUBLE){
    					this.listParams.add(Double.parseDouble(filterValue));
    				}else if(dataType == DATATYPE_FLOAT){
    					this.listParams.add(Float.parseFloat(filterValue));
    				}else if(dataType == DATATYPE_LONG){
    					this.listParams.add(Long.parseLong(filterValue));
    				}else{
    					this.listParams.add(Ints.tryParse(filterValue));
    				}
    			}
    			if (operatorCat == OPERATOR_CAT2){
    				sbTmp1.append(" ").append(valueParams[0]).append(AND_SEPARATOR)
    						.append(valueParams[1]); 
    				sbTmp2.append(" ").append(PARAMETER_CHAR).append(index).append(AND_SEPARATOR)
    						.append(PARAMETER_CHAR).append(index + 1); 
    				
    				if(dataType == DATATYPE_BIGDECIMAL){
    					this.listParams.add(FormatUtil.convertStringToBigDecimal(valueParams[0]));
        				this.listParams.add(FormatUtil.convertStringToBigDecimal(valueParams[1]));
    				}else if(dataType == DATATYPE_DOUBLE){
    					this.listParams.add(Double.parseDouble(valueParams[0]));
        				this.listParams.add(Double.parseDouble(valueParams[1]));
    				}else if(dataType == DATATYPE_FLOAT){
    					this.listParams.add(Float.parseFloat(valueParams[0]));
        				this.listParams.add(Float.parseFloat(valueParams[1]));
    				}else if(dataType == DATATYPE_LONG){
    					this.listParams.add(Long.parseLong(valueParams[0]));
        				this.listParams.add(Long.parseLong(valueParams[1]));
    				}else{
    					this.listParams.add(Ints.tryParse(valueParams[0]));
        				this.listParams.add(Ints.tryParse(valueParams[1]));
    				}
    			}
    			if (operatorCat == OPERATOR_CAT3){
    				if (operatorCat == OPERATOR_CAT3){
        				//field-name IN (.. , ..)
        				sbTmp1.append(" ( ");
        				sbTmp2.append(" ( ");
        				for (int i = 0; i < valueParams.length; i ++) {
        					if (i > 0) {
        						sbTmp1.append(", ");
        						sbTmp2.append(", ");
        					}
        					sbTmp1.append(valueParams[i].trim());
        					sbTmp2.append(PARAMETER_CHAR).append(index + i);
        					
        					if(dataType == DATATYPE_BIGDECIMAL){
        						this.listParams.add(FormatUtil.convertStringToBigDecimal(valueParams[i].trim()));
            				}else if(dataType == DATATYPE_DOUBLE){
            					this.listParams.add(Double.parseDouble(valueParams[i].trim()));
            				}else if(dataType == DATATYPE_FLOAT){
            					this.listParams.add(Float.parseFloat(valueParams[i].trim()));
            				}else if(dataType == DATATYPE_LONG){
            					this.listParams.add(Long.parseLong(valueParams[i].trim()));
            				}else{
            					this.listParams.add(Ints.tryParse(valueParams[i].trim()));
            				}
        				}
        				sbTmp1.append(" ) ");
        				sbTmp2.append(" ) ");
        			}
    			}
    			break;
    		case DATATYPE_DATE:
    			sbTmp1.append(" SUBSTRING(").append(fieldName).append(",1,10) ").append(operator);
    			sbTmp2.append(sbTmp1);
    			if (operatorCat == OPERATOR_CAT1){
    				sbTmp1.append(" SUBSTRING(").append(" @D").append(filterValue).append(",1,10) ");
    				sbTmp2.append(" SUBSTRING("+ PARAMETER_CHAR).append(index).append(",1,10) ");
    				this.listParams.add(convertToDate(filterValue));
    			}
    			if (operatorCat == OPERATOR_CAT2){
    				sbTmp1.append(" SUBSTRING(").append(" @D").append(valueParams[0]).append(",1,10) ").append(AND_SEPARATOR)
							.append(" SUBSTRING(").append(" @D").append(valueParams[1]).append(",1,10) "); 
    				sbTmp2.append(" SUBSTRING(").append(PARAMETER_CHAR).append(index).append(",1,10) ").append(AND_SEPARATOR)
    						.append(" SUBSTRING("+PARAMETER_CHAR).append(index + 1).append(",1,10) "); 
					this.listParams.add(convertToDate(valueParams[0]));
					this.listParams.add(convertToDate(valueParams[1]));
    			}
    			if (operatorCat == OPERATOR_CAT3){
    				//field-name IN (.. , ..)
    				sbTmp1.append(" ( ");
    				sbTmp2.append(" ( ");
    				for (int i = 0; i < valueParams.length; i ++) {
    					if (i > 0) {
    						sbTmp1.append(", ");
    						sbTmp2.append(", ");
    					}
    					sbTmp1.append(" SUBSTRING(").append("@D").append(valueParams[i].trim()).append(",1,10) ");
    					sbTmp2.append(" SUBSTRING("+ PARAMETER_CHAR).append(index + i).append(",1,10) ");
    					this.listParams.add(convertToDate(valueParams[i].trim()));
    				}
    				sbTmp1.append(" ) ");
    				sbTmp2.append(" ) ");
    			}
    			break;

    		default:
    			// future maybe support others datatype.
    			break;
    	}
		
    	stReturn = passODB? sbTmp1.toString() : sbTmp2.toString();
    	return stReturn;
    }
    
	/**
	 * Format date to string
	 * 
	 * @param value
	 *            A input date
	 * @return A {@link String} date.
	 * @throws Exception
	 *             if date is not valid.
	 */
    public Date convertToDate(String value) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(CST30000Constants.DATE_STRING_SCREEN_FORMAT);
		Date date = null;
		try {
			date = dateFormat.parse(value);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
    
	/**
	 * Puring input string
	 * 
	 * <pre>
	 * Ex: InputString -> "  test  puring      function "
	 * 	   Return	   -> "test puring function"
	 * </pre>
	 * 
	 * @param inputString
	 * @return pured string.
	 */
    private String getPureString(String inputString){
    	String stReturn = null;
    	if (inputString != null){
    		String[] arStTmp = inputString.trim().split(" ");
    		StringBuffer sbTmp = new StringBuffer();
    		for (int i = 0; i < arStTmp.length; i++){
    			if (!arStTmp[i].trim().isEmpty()){
        			sbTmp.append(arStTmp[i].trim()).append(" ");
    			}
    		}
    		stReturn = sbTmp.toString().trim(); 
    	}
    	return stReturn;
    }
    
	/**
	 * Return Following DB operators:
	 * <ul>
	 * <li>BETWEEN
	 * <li>NOT BETWEEN
	 * <li>LIKE
	 * <li>NOT LIKE
	 * <li>AND
	 * <li>IN
	 * <li>NOT IN
	 * <li>NULL
	 * <li>NOT NULL
	 * </ul>
	 * 
	 * @param input A input criteria
	 * @return OperatorCategory|Operator|FilterValue
	 */
    private String getOperatorCat(String input){
    	String stReturn = "0|0|0"; // defaul value
    	String inputOperator="";
    	String inputCriteria=input.toUpperCase();
    	
    	if (inputCriteria.startsWith(NOT_EQUAL)){
    	    stReturn = OPERATOR_CAT1 + "|";
    	    inputOperator = NOT_EQUAL;
    	}else if (inputCriteria.startsWith(BIGGER_EQUAL)){
    	    stReturn = OPERATOR_CAT1 + "|";
    	    inputOperator = BIGGER_EQUAL;
    	}else if (inputCriteria.startsWith(SMALLER_EQUAL)){
    	    stReturn = OPERATOR_CAT1 + "|";
    	    inputOperator = SMALLER_EQUAL;
    	}else if (inputCriteria.startsWith(EQUAL)){
    	    stReturn = OPERATOR_CAT1 + "|";
    	    inputOperator = EQUAL;
    	}else if (inputCriteria.startsWith(BIGGER)){
    	    stReturn = OPERATOR_CAT1 + "|";
    	    inputOperator = BIGGER;
    	}else if (inputCriteria.startsWith(SMALLER)){
    	    stReturn = OPERATOR_CAT1 + "|";
    	    inputOperator = SMALLER;
    	}else if (inputCriteria.startsWith(NOTLIKE+" ")){
    	    stReturn = OPERATOR_CAT1 + "|";
    	    inputOperator = NOTLIKE;
    	}else if (inputCriteria.startsWith(LIKE+" ")){
    	    stReturn = OPERATOR_CAT1 + "|";
    	    inputOperator = LIKE;
    	    
    	}else if (inputCriteria.startsWith(NOTBETWEEN+" ")){
    	    stReturn = OPERATOR_CAT2 + "|";
    	    inputOperator = NOTBETWEEN;
    	}else if (inputCriteria.startsWith(BETWEEN+" ")){
    	    stReturn = OPERATOR_CAT2 + "|";
    	    inputOperator = BETWEEN;

    	}else if (inputCriteria.startsWith(IN+" ")){
    	    stReturn = OPERATOR_CAT3 + "|"; 
    	    inputOperator = IN;
    	}else if (inputCriteria.startsWith(NOTIN+" ")){
    	    stReturn = OPERATOR_CAT3 + "|"; 
    	    inputOperator = NOTIN;
    	    
    	}else if (inputCriteria.startsWith(ISNOT+" ")){
    	    stReturn = OPERATOR_CAT4 + "|";
    	    inputOperator = ISNOT;
    	}else if (inputCriteria.startsWith(IS+" ")){
    	    stReturn = OPERATOR_CAT4 + "|";
    	    inputOperator = IS;
    	}

    	stReturn = stReturn + inputOperator + "|" + inputCriteria.trim().substring(inputOperator.length()).trim();
    	return stReturn;
    }
    
	/**
	 * Identifies the supportive data type of input data.
	 * 
	 * @param inputDataType {@link String} as a input data type 
	 * @return A data type.
	 */
    private int getDataType(String inputDataType){
    	inputDataType = inputDataType.toUpperCase();
    	int iReturn = DATATYPE_UNSUPPORTED; // Default Value
    	if (inputDataType.startsWith("NUMBER") ||
    			(inputDataType.startsWith(Integer.class.getSimpleName().toUpperCase())) || 
    			(inputDataType.startsWith(int.class.getSimpleName().toUpperCase())) || 
    			(inputDataType.startsWith(Byte.class.getSimpleName().toUpperCase())) ||
    			(inputDataType.startsWith(Double.class.getSimpleName().toUpperCase())) ||
    			(inputDataType.startsWith(BigDecimal.class.getSimpleName().toUpperCase())) ||
    			(inputDataType.startsWith(Float.class.getSimpleName().toUpperCase())) || 
    			(inputDataType.startsWith(Long.class.getSimpleName().toUpperCase())) ||    			
    			(inputDataType.startsWith(Short.class.getSimpleName().toUpperCase()))){
    		
    		if(inputDataType.startsWith(BigDecimal.class.getSimpleName().toUpperCase())){
    			iReturn = DATATYPE_BIGDECIMAL;
    		}else if(inputDataType.startsWith(Double.class.getSimpleName().toUpperCase())){
    			iReturn = DATATYPE_DOUBLE;
    		}else if(inputDataType.startsWith(Float.class.getSimpleName().toUpperCase())){
    			iReturn = DATATYPE_FLOAT;
    		}else if(inputDataType.startsWith(Long.class.getSimpleName().toUpperCase())){
    			iReturn = DATATYPE_LONG;
    		}else{
    			iReturn = DATATYPE_NUMBER;
    		}
    	}
    	if ((inputDataType.startsWith("VARCHAR")) || 
    			(inputDataType.startsWith("NVARCHAR")) || 
    			(inputDataType.startsWith("NCHAR")) ||
    			(inputDataType.startsWith(String.class.getSimpleName().toUpperCase()))){
    		iReturn = DATATYPE_VARCHAR;
    	}
    	if ((inputDataType.startsWith("DATE")) || (inputDataType.startsWith("TIMESTAMP")) || 
    			inputDataType.startsWith(Date.class.getSimpleName().toUpperCase())){
    		iReturn = DATATYPE_DATE;
    	}
    	//Fixed IncidentForm_PH(IFT-ST3)_0.9.0_017 by Thanapon 22/5/2015
    	if ((inputDataType.startsWith("CHAR")) || 
    			(inputDataType.startsWith(Character.class.getSimpleName().toUpperCase()))){
    		iReturn = DATATYPE_CHARACTER;
    	}
    	if (inputDataType.startsWith(Character.class.getSimpleName().toUpperCase()+"[]")){
    		iReturn = DATATYPE_CHARACTER_ARRAY;
    	}
    	//end Fixed IncidentForm_PH(IFT-ST3)_0.9.0_017 by Thanapon 22/5/2015
    	return iReturn;
    }
	
    /**
     * Indicate all field will be appear as ordered fields in SELECT statement.
     * @param settingInfoList A setting list
     * @param entity a entity
     * @return A ordered fields.
     */
    private String fieldsToOrder(List<SettingInfo> settingInfoList,String entity) throws ClassNotFoundException{
    	String stReturn = null;
    	StringBuffer sbOrderFields = new StringBuffer();    	
    	List<OrderedFields> alTmp = new ArrayList<OrderedFields>();
    	for (SettingInfo setting : settingInfoList) {
    		if (!setting.getSort().trim().isEmpty()) {
    			String id = this.getIdFromSubClass(setting.getObjFieldName(), entity);
    			alTmp.add(new OrderedFields(setting.getSort(), id+setting.getObjFieldName()));
    		}
    	}
    	
    	Collections.sort(alTmp);
    	
    	for (int i = 0; i < alTmp.size(); i++){
    		OrderedFields entryOrderedFields = (OrderedFields)alTmp.get(i);
    		sbOrderFields.append(entryOrderedFields.getFieldName()).append(",");
    	}
    	if (sbOrderFields.toString().trim().length() > 0){
    		stReturn = sbOrderFields.toString().substring(0,sbOrderFields.toString().length()-1);
    	}
    	return stReturn;
    }
	
    /**
     * Assign {@link CST30900ExcelDownloadForm} to the list of {@link SettingInfo}
     * @param form A {@link CST30900ExcelDownloadForm}
     * @return List of {@link SettingInfo}
     */
    private List<SettingInfo> assignFormtoSettingInfo(CST30900ExcelDownloadForm form) {
		List<SettingInfo> bookmarkList = new ArrayList<SettingInfo>();
		for (int i=0; i<form.getField().length; i++) {
			SettingInfo setting = new SettingInfo();
			
			setting.setReportName(form.getReportName());
			setting.setFieldName(form.getDbFieldName()[i]);
			setting.setObjFieldName(form.getField()[i]);
			setting.setPkOption(form.getPk()[i]);
			setting.setDataType(form.getDataType()[i]);
			setting.setDisplayOption(form.getChkDisplayOption()[i]);
			setting.setCriteria(form.getCriteria()[i]);
			setting.setLogicalOpr(form.getOperationLogic()[i]);
			setting.setReportTitle(form.getReportTitle());
			setting.setTableName(form.getTableName());
			setting.setOrderDisp(form.getOrderDisp()[i]);
			setting.setSort(form.getSort()[i]);
			setting.setStartColumn(form.getStartColumn());
			setting.setStartRow(form.getStartRow());
			setting.setDisplayName(form.getDisplayName()[i]);
			
			bookmarkList.add(setting);
		}
		
		return bookmarkList;
	}
	
	/**
	 * Build the JPQL query based on input {@link CST30900ExcelDownloadForm}.
	 * 
	 * @param form A {@link CST30900ExcelDownloadForm}
	 * @param passODB Flag indicates build query for ODB or not.
	 * @return A generated query string.
	 * @throws NoDataFoundException If no data found.
	 * @throws ClassNotFoundException If the entity not found.
	 */
	public String generateQuery(CST30900ExcelDownloadForm form, boolean passODB) throws NoDataFoundException, ClassNotFoundException {
		String stReturn = null;
		this.listParams.clear();
		this.displayCriteria.clear();
		this.displayMapping.clear();
		
    	StringBuffer sbSQL = new StringBuffer("SELECT ");
    	List<SettingInfo> bookmarkList = assignFormtoSettingInfo(form);
    	
    	this.prepareCriteriaLegend(form, bookmarkList);
		
//    	Get list of field name
    	String fieldsToSelect = fieldsToSelect(bookmarkList, form.getModelClassName());
    	if (fieldsToSelect != null){
    		sbSQL.append(fieldsToSelect);
    	} else {
    		throw new NoDataFoundException("No field to select.");
    	}
    	
    	String modelName = form.getModelClassName();
		String[] tmp = form.getModelClassName().split("\\.", -1);
		//in case the model name parameter is full name, Eg: th.co.model.ModelName
		//system will extract the final name behind the last dot
		if (tmp != null) {
			modelName = tmp[tmp.length - 1];
		}
    	sbSQL.append(" FROM ").append(modelName).append(" ");
    	
    	// get FilterCondition
    	sbSQL.append(filterConditions(bookmarkList, passODB, form.getModelClassName()));
    	// Get Order information.
    	String orderby = fieldsToOrder(bookmarkList, form.getModelClassName());
    	if ((orderby != null) && (!orderby.trim().equalsIgnoreCase(BLANK))){
	    	sbSQL.append(" ORDER BY ");
	    	sbSQL.append(orderby);
    	}
    	
    	stReturn = sbSQL.toString();
    	return stReturn;    	
	}

	/**
	 * Retrun report query
	 * 
	 * @return A report query.
	 */
	public String getReportQuery() {
		return reportQuery;
	}

	/**
	 * Setting report query.
	 * 
	 * @param reportQuery
	 *            A report query.
	 */
	public void setReportQuery(String reportQuery) {
		this.reportQuery = reportQuery;
	}

	/**
	 * Return display criteria.
	 * 
	 * @return A display criteria.
	 */
	public List<String> getDisplayCriteria() {
		return displayCriteria;
	}

	/**
	 * Set the display criteria.
	 * 
	 * @param displayCriteria
	 *            A display criteria.
	 */
	public void setDisplayCriteria(List<String> displayCriteria) {
		this.displayCriteria = displayCriteria;
	}

	/**
	 * Add the display criteria.
	 * 
	 * @param strCriteria
	 *            A display criteria.
	 */
	public void addDisplayCriteria(String strCriteria) {
		this.displayCriteria.add(strCriteria);
	}

	/**
	 * Get the display mapping
	 * 
	 * @return A mapping.
	 */
	public Map<String, String> getDisplayMapping() {
		return displayMapping;
	}

	/**
	 * Set the display mapping.
	 * 
	 * @param displayMapping
	 *            A display mapping.
	 */
	public void setDisplayMapping(Map<String, String> displayMapping) {
		this.displayMapping = displayMapping;
	}

	/**
	 * Add the display mapping.
	 * 
	 * @param fieldName
	 *            A field name.
	 * @param displayName
	 *            A display name.
	 */
	public void addDisplayMapping(String fieldName, String displayName) {
		this.displayMapping.put(fieldName, displayName);
	}
}