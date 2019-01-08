package th.co.toyota.bw0.api.common.upload;

import java.util.HashMap;
import java.util.List;

public class CommonExcelConversionDTO {
	public static final String TAG_EXCEL_CONVERSION_CONFIG = "excel-conversion-config";
	public static final String TAG_CONVERSION_INFO = "conversion-info";
	public static final String TAG_CONVERSION_HEADER = "conversion-header";
	public static final String TAG_CONVERSION_DETAIL = "conversion-detail";
	public static final String TAG_FIELD_MAP = "field-mapping";
	public static final String ATTR_WRKSHT_IDX = "workSheetIdx";
	public static final String ATTR_WRKSHT_NAME = "workSheetName";
	public static final String ATTR_NAME = "name";
	public static final String ATTR_LABEL = "label";
	public static final String ATTR_VALUE = "value";
	public static final String ATTR_LENGTH = "length";
	public static final String ATTR_MANDATORY_FIELD = "mandatoryField";
	
	public static final String ATTR_XLS_DT_FORMAT = "xlsDtFormat";
	public static final String ATTR_IF_DT_FORMAT = "ifDtFormat";
	public static final String ATTR_DECIMAL = "decimal";
	public static final String ATTR_START_ROW = "startRow";
	public static final String ATTR_END_ROW = "endRow";
	public static final String ATTR_START_COL = "startCol";
	public static final String ATTR_END_COL = "endCol";
	public static final String ATTR_REPLACE_KEY = "replaceKey";
	public static final String ATTR_REPLACE_TO_VALUE = "replaceToValue";
	public static final String ATTR_REGEX_CHECK = "regexCheck";
	public static final String ATTR_CONVERT_TO_NUMBERIC = "convertToNumberic";
	public static final String ATTR_CORRECT_NUMBER_FORMAT = "correctNumberFormat";
	public static final String ATTR_CONVERT_STRING_TO_DATE = "convertToDateBy";	
	public static final String ATTR_REPLACE_CALUE_BEFORE_CHK_LENGTH = "replaceValueBeforeChkLength";
	public static final String ATTR_ROUND_HAFT_UP = "roundHaftUp";
	public static final String ATTR_ALPHANUMERIC_CHECK = "alphaNumericCheck";
	
	

	public static final String TAG_CHECK_HEADERS = "check-headers";
	public static final String TAG_CHECK_HEADER = "check-header";

	private Integer[] arrWorkSheetIdx;
	private String[] arrWorkSheetName;
	
	//Data Structure:
	// HashMap<String, Object>
	//		- String: "conversion-info", "conversion-header", "conversion-detail"
	//		- Object: HashMap<String, HashMap<String, String>> = "Excel Field Name", "DB Field Name"
	private HashMap excelCheckHeadersConfig;
	private HashMap excelHeaderConfig;
	private HashMap excelDetailConfig;
	protected List<HashMap> checkHeaders;
	protected String[] columnNames;
	
	public Integer[] getArrayWorkSheetIdx() {
		return arrWorkSheetIdx;
	}
	public void setArrayWorkSheetIdx(Integer[] arrWorkSheetIdx) {
		this.arrWorkSheetIdx = arrWorkSheetIdx;
	}
	public String[] getArrayWorkSheetName() {
		return arrWorkSheetName;
	}
	public void setArrayWorkSheetName(String[] arrWorkSheetName) {
		this.arrWorkSheetName = arrWorkSheetName;
	}
	public HashMap getExcelHeaderConfig() {
		return excelHeaderConfig;
	}
	public void setExcelHeaderConfig(HashMap excelHeaderConfig) {
		this.excelHeaderConfig = excelHeaderConfig;
	}
	public HashMap getExcelDetailConfig() {
		return excelDetailConfig;
	}
	public void setExcelDetailConfig(HashMap excelDetailConfig) {
		this.excelDetailConfig = excelDetailConfig;
	}
	
	public Integer getHeaderStartRow() {
		return (Integer)excelHeaderConfig.get(ATTR_START_ROW);
	}
	
	public Integer getHeaderEndRow() {
        return (Integer)excelHeaderConfig.get(ATTR_END_ROW);
    }

	public Integer getHeaderStartCol() {
		return (Integer)excelHeaderConfig.get(ATTR_START_COL);
	}
	//Create by Prapan 23/8/2012
	public Integer getHeaderEndCol() {
		return (Integer)excelHeaderConfig.get(ATTR_END_COL);
	}
	
	public HashMap getHeaderMappingField() {
		return (HashMap)excelHeaderConfig.get(TAG_CONVERSION_HEADER);
	}
	
	public Integer getDetailStartRow() {
		return (Integer)excelDetailConfig.get(ATTR_START_ROW);
	}
	
	public Integer getDetailEndRow() {
        return (Integer)excelDetailConfig.get(ATTR_END_ROW);
    }
	
	public Integer getDetailStartCol() {
		return (Integer)excelDetailConfig.get(ATTR_START_COL);
	}
	
	public Integer getDetailEndCol() {
        return (Integer)excelDetailConfig.get(ATTR_END_COL);
    }

	public HashMap getDetailMappingField() {
		return (HashMap)excelDetailConfig.get(TAG_CONVERSION_DETAIL);
	}
	
	public Integer getCheckHeaderStartRow(int index) {
		return (Integer)((HashMap)this.getCheckHeaders().get(index)).get(ATTR_START_ROW);
	}
	
	public Integer getCheckHeaderEndRow(int index) {
        return (Integer)((HashMap)this.getCheckHeaders().get(index)).get(ATTR_END_ROW);
    }

	public Integer getCheckHeaderStartCol(int index) {
		return (Integer)((HashMap)this.getCheckHeaders().get(index)).get(ATTR_START_COL);
	}

	public Integer getCheckHeaderEndCol(int index) {
		return (Integer)((HashMap)this.getCheckHeaders().get(index)).get(ATTR_END_COL);
	}
	public List getCheckHeaders() {
		return checkHeaders;
	}
	public void setCheckHeaders(List checkHeaders) {
		this.checkHeaders = checkHeaders;
	}
	
	public HashMap<String, Object> getExcelCheckHeadersConfig() {
		return excelCheckHeadersConfig;
	}
	public void setExcelCheckHeadersConfig(HashMap<String, Object> excelCheckHeaderConfig) {
		this.excelCheckHeadersConfig = excelCheckHeaderConfig;
	}
	public HashMap getCheckHeadersMapping() {
		return (HashMap)excelCheckHeadersConfig.get(TAG_CHECK_HEADERS);
	}
	public Integer getCheckHeadersStartRow() {
		return (Integer)this.excelCheckHeadersConfig.get(ATTR_START_ROW);
	}
	
	public Integer getCheckHeadersEndRow() {
        return (Integer)this.excelCheckHeadersConfig.get(ATTR_END_ROW);
    }
	public String[] getColumnNames() {
		return columnNames;
	}
	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}
}
