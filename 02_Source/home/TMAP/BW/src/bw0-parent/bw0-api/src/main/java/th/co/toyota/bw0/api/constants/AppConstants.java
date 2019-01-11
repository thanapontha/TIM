package th.co.toyota.bw0.api.constants;

public final class AppConstants {
	
	private AppConstants() {
		throw new IllegalStateException("Application Constants class");
	}
	public static final String PWD_PROTECT_SHEET = "TDEM";

	public static final String DATE_STRING_SCREEN_FORMAT = "dd/MM/yyyy";
	public static final String DATE_TIME_STRING_FILENAME_FORMAT = "yyyyMMddHH24mmss";
//	public static final String DATE_TIME_FORMAT_UPDATEDT_KEY = "yyyyMMddHHmmss";
//	public static final String DATE_TIME_STRING_FILENAME_FORMAT_WITHOUT24 = "yyyyMMddHHmmss";
	public static final String DATE_TIME_STRING_FILENAME_FORMAT_WITHOUT24 = "yyyyMMddHHmmSSS";
	public static final String DATE_STRING_FORMAT_YYMMDD = "yyMMdd";
	public static final String DATE_STRING_FORMAT_YYYYMMDD = "yyyyMMdd";
	public static final String DATE_STRING_FORMAT_YYYYMM = "yyyyMM";
	public static final String DATE_STRING_FORMAT_YYYY = "yyyy";
	public static final String DATE_STRING_FORMAT_YYMM = "yyMM";
	public static final String DATE_STRING_FORMAT_MM = "MM";
	
	public static final String DATE_STRING_MONTH_YEAR_FULLNAME = "MMMM YYYY";
	public static final String DATE_STRING_MONTH_FULLNAME = "MMMM";
	public static final String DATE_STRING_FORMAT_DDMM = "ddMM";
	public static final String DATE_MONTH_YEAR = "MM/yyyy";
	public static final String DATE_SHOW_IN_SCREEN = "MMM-yy";
	public static final String DATE_SHOW_IN_SCREEN_MMM_YYYY = "MMM-yyyy";
	public static final String DATE_TIME_IN_SCREEN = "dd-MMM-yy HH24:mm:ss";
	public static final String DATE_SHOW_IN_REPORT = "dd-MMM-yyyy";
	public static final String DATE_SHOW_IN_DETAIL_REPORT = "dd-MMM-yy";
	public static final String DATE_FORMAT_DDMMMYYYY = "dd MMM yyyy";
	public static final String DATE_TIME_IN_REPORT = "dd MMM yyyy HH:MM";
	public static final String DATE_STRING_FORMAT_FROM_DOT_SYSTEM = "dd MMM yy";
	public static final String DATE_STRING_FORMAT_MMM = "MMM";
	public static final String DATE_STRING_FORMAT_YY = "yy";
	public static final String DATE_STRING_FORMAT_DDMMYY = "ddMMyy";
	public static final String DATE_STRING_FORMAT_DDMMYYYY = "ddMMyyyy";
	public static final String TIME_IN_SCREEN = "HH:mm:ss";
	public static final String TIME_IN_REPORT = "HH:mm:ss";
	public static final String DATE_TIME_IN_SCREEN_NOT_24 = "dd-MMM-yy, HH:mm:ss";
	
	public static final String DATE_ORACLE_FORMAT_DDMMYYYY = "DD/MM/YYYY";
	public static final String DATE_ORACLE_FORMAT_DDMONYYYY = "DD Mon YYYY";
	public static final String DATE_ORACLE_FORMAT_DATETIME_SQL = "yyyy/mm/dd hh24:mi:ss";
	public static final String DATE_ORACLE_FORMAT_DATETIME_JAVA_CONVERT = "yyyy/MM/dd HH:mm:ss";
//	public static final String DATE_ORACLE_FORMAT_UPDATEDT_DATETIME = "YYYYMMDDHH24MISS";
//	public static final String DATE_ORACLE_FORMAT_GETSUDO_MONTH = "Mon-YY";
	
	public static final String BLANK = "";
	public static final String BLANK_SPACE = " ";

    public static final int LEFT_PAD = 0x00;
    public static final int RIGHT_PAD = 0x01;
    
    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    public static final int WARNING = 2;
    
    public static final String ALIGNMENT_LEFT = "L";
    public static final String ALIGNMENT_RIGHT = "R";
    
	public static final String SEQ_CODE_APP_ID = "APP_ID";

	public static final String GWRDS_CD = "GWRDS";
	public static final String COMPANY_CD_TDEM = "TDEM";//Short TMAP-EM
	public static final String COMPANY_CD_TMAP_MS = "TMAP-MS";//Short TMAP-MS
	
	public static final String BATCH_ID = "BST33020";
	
	public static final String MODULE_ID_BW01 = "BW01";//Kaikieng
	public static final String MODULE_ID_BW02 = "BW02";//Rundown and Unit
	public static final String MODULE_ID_BW03 = "BW03";//Common
	public static final String MODULE_ID_BW04 = "BW04";//Master
	public static final String FUNCTION_ID_LBW01130 = "LBW01130";//Kaikieng Template Download
	public static final String FUNCTION_ID_BBW01140 = "BBW01140";//Kaikieng Upload (Dynamic)
	public static final String FUNCTION_ID_LBW01160 = "LBW01160";//Getsudo Worksheet (Download)
	public static final String FUNCTION_ID_BBW01170 = "BBW01170";//Getsudo Worksheet Upload
	public static final String FUNCTION_ID_BBW02120 = "BBW02120";//PAMs Rundown Upload
	public static final String FUNCTION_ID_BBW02130 = "BBW02130";//Kompo Upload
	public static final String FUNCTION_ID_LBW02140 = "LBW02140";//PAMs Rundown Detail Report
	public static final String FUNCTION_ID_LBW02150 = "LBW02150";//Kompo Detail Report
	public static final String FUNCTION_ID_BBW02170 = "BBW02170";//Multi-line Order Spliting Upload
	public static final String FUNCTION_ID_LBW03220 = "LBW03220";//Getsudo Worksheet Report
	public static final String FUNCTION_ID_LBW03230 = "LBW03230";//Unit Allocation Summary Report
	public static final String FUNCTION_ID_LBW03240 = "LBW03240";//Unit Capacity Checking Report
	public static final String FUNCTION_ID_LBW03250 = "LBW03250";//Multi-line Order Spliting Download

	public static final String FUNCTION_ID_BBW04220 = "BBW04220";//Calendar Validation
	public static final String FUNCTION_ID_LBW04221 = "LBW04221";//Calendar Download
	public static final String FUNCTION_ID_LBW04250 = "LBW04250";//Unit Capacity Download
	public static final String FUNCTION_ID_BBW04260 = "BBW04260";//Unit Capacity Upload
	
	public static final String SCREEN_ID_WBW01110 = "WBW01110";
	public static final String SCREEN_ID_WBW01150 = "WBW01150";//Getsudo Worksheet Management Screen
	public static final String SCREEN_ID_WBW02110= "WBW02110";//rundown and kompo management screen
	public static final String SCREEN_ID_WBW02111 ="WBW02111";//vanning adjustment screen
	public static final String SCREEN_ID_WBW02160 = "WBW02160";//Unit Capacity Management Screen
	public static final String SCREEN_ID_WBW04110 = "WBW04110";//Vehicle Plant Master
	public static final String SCREEN_ID_WBW04120 = "WBW04120";//Unit Plant Master
	public static final String SCREEN_ID_WBW04130 = "WBW04130";//Vehicle Unit Relation Master
	
	public static final String SCREEN_ID_WBW04210 = "WBW04210";//Calendar Master
	public static final String SCREEN_ID_WBW04230 = "WBW04230";//Standard Stock Master
	
	public static final int MAX_LENGTH_UPLOAD_FILENAME = 255;
	
	public static final String SYS_CD_STAR = "*";	
	public static final String SYS_PERCENT = "%";

	public static final String CHECKBOX_SEPERATER = "|";
	public static final String UNDERSCORE = "_";
	public static final String COLON = ":";
	
	public static final String SORT_METHOD_ASCENDING = "ASC";
	public static final String SORT_METHOD_DESCENDING = "DESC";
	
	//public static final long UPLOAD_MAX_FILESIZE_FOR_PART_COST_STRUCTURE = 10485760; // 10MB
	//public static final long UPLOAD_MAX_FILESIZE_FOR_MSP_PPR = 2097152; // 2MB
	//public static final long UPLOAD_MAX_FILESIZE_FOR_PRICE_COMPARE = 3145728; // 3MB

	public static final String SYS_CD_TO = "TO";
	public static final String SYS_CD_CC = "CC";
	public static final String SYS_CD_SUBJECT = "SUBJECT";
	public static final String SYS_CD_SENDER = "SENDER";

	public static final String YES_STR = "Y";
	public static final String NO_STR = "N";
	public static final String YES_INFO = "YES";
	public static final String NO_INFO = "NO";
	
	public static final String FILE_FORMAT_EXCEL_XLS = "XLS";
	public static final String FILE_FORMAT_EXCEL_XLSX = "XLSX";
	//public static final String FILE_FORMAT_ASCII_TEXT = "TXT";
	//public static final String FILE_FORMAT_ZIP = "ZIP";
	
	//public static final String CSV_REPORT_EXTENTION = ".csv";
    public static final String XLS_REPORT_EXTENTION = ".xls";
    public static final String XLSX_REPORT_EXTENTION = ".xlsx";
    
	public static final char ACTIVE = 'Y';
	public static final char INACTIVE = 'N';


	public static final String BATCH_CHARACTOR_REPLACE_BLANK = "^##^";
	public static final String BATCH_CHARACTOR_REPLACE_LEFT_PARENTHESIS = "^L#^";
	public static final String BATCH_CHARACTOR_REPLACE_RIGHT_PARENTHESIS = "^R#^";
	public static final String BATCH_CHARACTOR_REPLACE_SELECTED_MULTI_UNIT = "^#^";
	public static final String BATCH_CHARACTOR_REPLACE_BLANK_BACK = "\\^##\\^";
	public static final String BATCH_CHARACTOR_REPLACE_LEFT_PARENTHESIS_BACK = "\\^L#\\^";
	public static final String BATCH_CHARACTOR_REPLACE_RIGHT_PARENTHESIS_BACK = "\\^R#\\^";
	public static final String BATCH_CHARACTOR_REPLACE_SELECTED_MULTI_UNIT_BACK = "\\^#\\^";
	
	public static final String LEFT_PARENTHESIS = "\\(";
	public static final String RIGHT_PARENTHESIS = "\\)";
	
	public static final String TABLE_TB_C_UNIT_PLANT = "TB_C_UNIT_PLANT";
	public static final String TABLE_TB_C_VEHICLE_PLANT = "TB_C_VEHICLE_PLANT";
	public static final String TABLE_TB_M_SYSTEM = "TB_M_SYSTEM";
	public static final String TABLE_TB_M_UNIT_PLANT = "TB_M_UNIT_PLANT";
	public static final String TABLE_TB_M_UNIT_PLANT_CAPA = "TB_M_UNIT_PLANT_CAPA";
	public static final String TABLE_TB_M_VEHICLE_PLANT = "TB_M_VEHICLE_PLANT";
	public static final String TABLE_TB_M_VEHICLE_UNIT_RELATION = "TB_M_VEHICLE_UNIT_RELATION";	
	public static final String TABLE_TB_R_MULTI_LINE_SPLITING = "TB_R_MULTI_LINE_SPLITING";
	public static final String TABLE_TB_R_KAIKIENG_H = "TB_R_KAIKIENG_H";
	public static final String TABLE_TB_R_CAPA_RESULT = "TB_R_CAPA_RESULT";
	
	public static final String SYS_CATEGORY_REPORT = "REPORT";
	public static final String SYS_CATEGORY_REPORT_NAME = "REPORT_NAME";
	public static final String SYS_CATEGORY_MASTER = "MASTER";
	public static final String SYS_CATEGORY_COMMON = "COMMON";
	public static final String SYS_CATEGORY_EMAIL = "EMAIL";
	public static final String SYS_CATEGORY_GWRDS = "GWRDS";
	public static final String SYS_CATEGORY_PIC = "PIC";
	public static final String SYS_CATEGORY_UNIT_CAPACITY = "UNIT_CAPACITY";
	public static final String SYS_CATEGORY_COMMON_UPLOAD = "COMMON_UPLOAD";
	
	public static final String SYS_SUB_CATEGORY_FUNCTION_UPLOAD = "FUNCTION";
	public static final String SYS_SUB_CATEGORY_MAX_SIZE_EXCEL = "MAX_SIZE_EXCEL";
	public static final String SYS_SUB_CATEGORY_MAX_LENGTH_UPLOAD_FILENAME = "MAX_LENGTH_UPLOAD_FILENAME";
	public static final String SYS_SUB_CATEGORY_INTRFC_FILE_ID = "INTRFC_FILE_ID";
	public static final String SYS_SUB_CATEGORY_STATUS = "STATUS";
	public static final String SYS_SUB_CATEGORY_UNIT_MODEL = "UNIT_MODEL";
	public static final String SYS_SUB_CATEGORY_UNIT_LINE = "UNIT_LINE";
	public static final String SYS_SUB_CATEGORY_VEHICLE_MODEL = "VEHICLE_MODEL";	
	public static final String SYS_SUB_CATEGORY_TIMING = "TIMING";
	public static final String SYS_SUB_CATEGORY_INCOMING_VOL_STS = "INCOMING_VOLUME_STATUS";
	public static final String SYS_SUB_CATEGORY_SUB_LINE_STATUS = "SUB_LINE_STATUS";
	public static final String SYS_SUB_CATEGORY_TITLE = "TITLE";
	public static final String SYS_SUB_CATEGORY_SUB_TITLE = "SUB_TITLE";
	public static final String SYS_SUB_CATEGORY_REPORT_NAME = "REPORT_NAME";
	public static final String SYS_SUB_CATEGORY_USER_ROLE_FOR_DOWNLOAD = "USER_ROLE_FOR_DOWNLOAD";
	public static final String SYS_SUB_CATEGORY_MAXROW_OF_REPORT = "MAXROW";
	public static final String SYS_SUB_CATEGORY_UPLOAD = "UPLOAD";	
	public static final String SYS_SUB_CATEGORY_WORKING_TIME = "WORKING_TIME";
	public static final String SYS_SUB_CATEGORY_CONTACT_NAME = "CONTACT_NAME";
	public static final String SYS_SUB_CATEGORY_OFFSET_LT = "OFFSET_LT";
	
	public static final String SYSTEM_NAME = "SYSTEM";
	public static final String SYS_CD_MANAGER = "MANAGER";
	public static final String ROLE_ADMIN = "GWRDS_ADMIN.tmap-em.toyota.co.th.asia";
	
	public static final String UPDATE_KEYS_SEPERATER = "|";
	
	public static final int INX_OF_UNIT_PLANT = 0;
	public static final int INX_OF_UNIT_PARENT_LINE = 1;
	public static final int INX_OF_UNIT_SUB_LINE = 2;
	
	public static final String ACTION_ADD = "add";
	public static final String ACTION_EDIT = "edit";
	public static final String ACTION_UPLOAD = "upload";
	public static final String ACTION_SEARCH = "search";
	public static final String ACTION_SAVE_ADD = "save_add";
	public static final String ACTION_SAVE_EDIT = "save_edit";
	public static final String ACTION_DELETE = "delete";
	public static final String ACTION_SUBMIT = "submit";
	public static final String ACTION_DOWNLOAD = "download";
	public static final String ACTION_SUBMIT_KAIKIENGDATA = "SubmitKaikiengData";
	public static final String ACTION_REJECT_KAIKIENGDATA = "RejectKaikiengData";
	public static final String ACTION_FIX_KAIKIENGDATA = "FixKaikiengData";
	
	public static final String MV_USER_COMPANY = "userCompany";
	public static final String MV_USER = "user";
	public static final String MV_FORM = "form";
	public static final String MV_PAYLOAD = "payload";
	public static final String ALL = "ALL";
	
	public static final String KAIKIENG_STATUS_MP = "MP";//MS Processing
	public static final String KAIKIENG_STATUS_PC = "PC";//TDEM Processing
	public static final String KAIKIENG_STATUS_SM = "SM";//MS Submitted
	public static final String KAIKIENG_STATUS_FX = "FX";//TDEM Fixed
	public static final String STATUS_QUEUE = "Q";//Queue
	public static final String STATUS_PROCESSING = "P";//Processing
	public static final String STATUS_SUCCESS = "S";//Success
	public static final String STATUS_WARNING = "W";//Warning
	public static final String STATUS_ERROR = "E";//Error
	public static final String STATUS_INTERRUPT = "I";//Interrupt
	
	public static final String STATUS_PROCESSING_DESC = "Processing";
	public static final String STATUS_QUEUE_DESC = "On Queue";
	public static final String STATUS_SUCCESS_DESC = "Successfully";
	public static final String STATUS_WARNING_DESC = "Warning";
	public static final String STATUS_ERROR_DESC = "Error";
	public static final String STATUS_INTERRUPT_DESC = "Interrupt";
	
	public static final String WORKSHEET_STATUS_COMPLETED = "CP";//Completed
	public static final String WORKSHEET_STATUS_ERROR = "WE";//Worksheet Error
	public static final String WORKSHEET_STATUS_UPLOADED = "WS";//Worksheet Uploaded
	public static final String WORKSHEET_STATUS_RESET = "RS";//Reset
	
	public static final String DEFULAT_DATE = "31/12/2999";
	
	public static final String PLANT_TYPE_OTHER = "H";
	public static final String PLANT_TYPE_UNIT_PLANT = "U";
	public static final String PLANT_TYPE_VEHICLE_PLANT = "V";
	
	public static final String SECTION_VEHICLE = "Vehicle";
	public static final String UNIT_TYPE_ENGINE = "Engine";
	public static final String UNIT_TYPE_TRANSMISSION = "Transmission";
	public static final String UNIT_TYPE_OTHER = "Other";
	
	public static final String UNIT_TYPE_CODE_ENGINE = "EG";
	public static final String UNIT_TYPE_CODE_TRANSMISSION = "TM";
	public static final String UNIT_TYPE_CODE_OTHER = "OT";
	
	public static final String SECTION_MULTI_SOURCE_UNITS = "Multi-Source Units";
	public static final String SECTION_PACKING_MOVEMENT = "Packing Movement";
	public static final String TOTAL = "Total";
	
	public static final String REPLACE_NEW_FILE_NAME_OF_UPLOAD = "{REPLACE_NEW_FILE_NAME_OF_UPLOAD}";
	public static final String UPLOAD_TYPE_KAIKIENG = "Kaikieng";
	public static final String UPLOAD_TYPE_WORKSHEET = "Worksheet";
	public static final String UPLOAD_UNIT_PLANT_CAPACITY = "Unit Plant Capacity";
	public static final String UPLOAD_TYPE_RUNDOWN = "PAMS Rundown";
	public static final String UPLOAD_TYPE_KOMPOKUNG = "Kompokung";
	public static final String UPLOAD_TYPE_MULTI_LINE_SPLITING = "Multi Line Spliting";
	public static final String UPLOAD_TYPE_CALENDAR = "Calendar";
	public static final String UPLOAD_TYPE_DOWNLOAD = "Download";
	
	public static final String UPLOAD_PAMS_FLAG = "R";
	public static final String UPLOAD_KOMPO_FLAG = "K";
	public static final String UPLOAD_SKIP_FLAG = "S";
	public static final String UPLOADED_BUT_NOT_YET_FIX = "SC";
	public static final String UPLOADED_ERROR = "ER";
	
	public static final String CALENDAR_FULL_NON_WORKING_DAY = "F";
	public static final String CALENDAR_HALF_WORKING_DAY = "H";
	public static final String CALENDAR_WORKING_DAY = "";
	public static final String CALENDAR_WORKING_DAY_W = "W";
	
	public static final String LOG_DETAIL_CALENDAR = "Calendar";
	public static final String LOG_DETAIL_STOCK = "Stock Level";
	public static final String LOG_DETAIL_PROD_VOL = "Vehicle Production Volume";
	public static final String LOG_DETAIL_PROD_VOL_DIAGRAM = "Vehicle Production Volume Diagram";
	public static final String LOG_DETAIL_PACK_VOL = "Packing Volume";
	public static final String LOG_DETAIL_OTHER = "Other";
	
	public static final String ERROR_CONCURRENT = "CONCURRENT";
	
	public static final String OK = "OK";
	public static final String NG = "NG";
	public static final String WARN = "WARN";
	public static final String OVER = "OVER";
	
	public static final String CONTACT_NAME_OF_EMAIL = "Admin";
	
	public static final String UNIT_CAPA_MNG_INCOMING_VOL_STS_W1_CD = "W1";
	public static final String UNIT_CAPA_MNG_INCOMING_VOL_STS_W2_CD = "W2";
	public static final String UNIT_CAPA_MNG_INCOMING_VOL_STS_R2_CD = "R2";
	public static final String UNIT_CAPA_MNG_INCOMING_VOL_STS_R1_CD = "R1";
	public static final String EXIST = "EXIST";
	
	public static final String LANGUAGE_THAI = "th";
	public static final String LANGUAGE_ENG = "en";
	
	public static final String TYPE_OF_CUSTOMER_INDIVIDUAL = "Individual";
}
