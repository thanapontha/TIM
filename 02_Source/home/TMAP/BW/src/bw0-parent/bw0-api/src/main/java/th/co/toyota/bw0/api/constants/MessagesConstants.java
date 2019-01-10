/******************************************************
 * Program History
 * 
 * Project Name	            :  GWRDS : 
 * Client Name				:  TDEM
 * Package Name             :  th.co.toyota.bw0.batch.constants
 * Program ID 	            :  MessagesConstants.java
 * Program Description	    :  Messages Constants
 * Environment	 	        ;  Java 7
 * Author					:  Thanapon T.
 * Version					:  1.0
 * Creation Date            :  August 9, 2017
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.bw0.api.constants;

public final class MessagesConstants {
	
	private MessagesConstants() {
		throw new IllegalStateException("Message Constants class");
	}
	
	 //Information of Web Application
	  
	 //Error of Web Application
	 public static final String A_ERROR_LENGTH_FILE_NAME_OVER = "MBW00001AERR";
	 public static final String A_ERROR_FILE_SIZE_OVER = "MBW00002AERR";
	 public static final String A_ERROR_PROCESS_FINISHED_WITH_ERROR = "MBW00007AERR";
	 public static final String A_ERROR_CANNOT_BECAUSE_OF = "MBW00009AERR";
	 public static final String A_ERROR_FILE_TYPE_IS_INCORRECT = "MBW00010AERR";
	 public static final String A_ERROR_DO_NOT_ALLOW = "MBW00014AERR";
	 public static final String A_ERROR_NOT_REGISTER = "MBW00015AERR";
	 public static final String A_ERROR_KOMPO_DIFF_UPLOAD_TYPE_SAME_VEHICLE_MODEL = "MBW00016AERR";
	 public static final String A_ERROR_VEHICLE_UNIT_RELATION_NOT_EXISTS = "MBW00018AERR";
	 
	 
	 public static final String A_ERROR_TEMPLATE_DOES_NOT_EXIST = "MSTD0001AERR";
	 public static final String A_ERROR_INVALID_DAYS_VALUE = "MBW00011AERR";
	 public static final String A_ERROR_VALUE_COMPARE_LESS_THAN = "MSTD0024AERR";
	 public static final String A_ERROR_INVALID_VALUE = "MSTD0044AERR";
	 
	 public static final String A_ERROR_MUST_LESSTHAN_OR_EQUAL = "MSTD0025AERR";
	 public static final String A_ERROR_DOESNT_EXISTS_IN = "MSTD0012AERR";
	 public static final String A_ERROR_IS_NOT_IN_THE_RANGE = "MSTD1003AERR";
	 
	 //Information of Web Application
	 public static final String A_INFO_SUBMIT_FINISHED_SUCCESSFULLY = "MBW00002AINF";
	 public static final String A_INFO_PROCESS_FINISHED_SUCCESSFULLY = "MSTD0085AINF";
	 public static final String A_INFO_PROCESS_FINISHED_WITH_CUSTOM_INFO = "MBW00003AINF";
	 
	 //Warning of  Web Application
	 public static final String A_WARNING_PROCESS_FINISHED_WITH_WARNING = "MBW00002AWRN";
	 public static final String A_WARNING_UNIT_PLANT_CAPACITY_ALREADY_EXISTED = "MBW00003AWRN";
	 
	 //Error of Batch
	 public static final String B_ERROR_INVALID_MISSING_FIELD = "MBW00001BERR";
	 public static final String B_ERROR_IN_UPLOAD_FILE_NOT_FOUND = "MBW00002BERR";
	 public static final String B_ERROR_RUNDOWN_ALREADY_FIXED = "MBW00003BERR";
	 public static final String B_ERROR_INVALID_DAYS_VALUE = "MBW00004BERR";
	 public static final String B_ERROR_SUB_LINE_DIFF_FROM_MASTER = "MBW00005BERR";
	 public static final String B_ERROR_UNIT_MODEL_DIFF_FROM_MASTER = "MBW00006BERR";
	 public static final String B_ERROR_CONCURRENTCY_INTERRUPT = "MBW00007BERR";
	 public static final String B_ERROR_VOLUME_DIFF = "MBW00008BERR";
	 public static final String B_ERROR_INVALID_CALENDAR_DATE = "MBW00009BERR";
	 public static final String B_ERROR_INVALID_EMPTY_FIELD = "MBW00010BERR";
	 public static final String B_ERROR_INVALID_FORMAT = "MBW00011BERR";
	 public static final String B_ERROR_INVALID_LENGTH = "MBW00012BERR";
	 public static final String B_ERROR_VALUE_COMPARE_NOT_MATCH = "MBW00013BERR";
	 public static final String B_ERROR_FILE_NOT_EXISTS ="MBW00014BERR";
	 public static final String B_ERROR_INVALID_FILE_TEMPLATE = "MBW00015BERR";
	 public static final String B_ERROR_INVALID_VALUE = "MBW00016BERR";
	 public static final String B_ERROR_VALUE_MUST_BE_SAME = "MBW00017BERR";
	 public static final String B_ERROR_DUPLICATE_FOUND = "MBW00018BERR";
	 public static final String B_ERROR_NOT_EXIST = "MBW00019BERR";
	 public static final String B_ERROR_MUST_BETWEEN = "MBW00020BERR";
	 public static final String B_ERROR_MUST_GREATER_THAN = "MBW00021BERR";
	 public static final String B_ERROR_MUST_EQUAL = "MBW00022BERR";
	 public static final String B_ERROR_NOT_FOUND_IN_RELATION = "MBW00023BERR";
	 public static final String B_ERROR_NOT_FOUND_WORKSHEET = "MBW00024BERR";
	 public static final String B_ERROR_OFFSET_OVER_ZERO_RUNDOWN_NOT_FIX = "MBW00025BERR";
	 public static final String B_ERROR_FOUND_INCOME_VOLUME_STS_NOT_RECEIVED = "MBW00026BERR";
	 public static final String B_ERROR_UNDEFINED_ERROR = "MBW00029BERR";
	 public static final String B_ERROR_DATA_NOT_FOUND_FROM = "MBW00030BERR";
	 public static final String B_ERROR_ON_WRITING_DATA_INTO_FILE = "MBW00031BERR";	 
	 public static final String B_ERROR_CAPA_RESULT_CONFIRMED = "MBW00033BERR";
	 public static final String B_ERROR_PREV_WORKSHEET_NOT_COMPLETED = "MBW00035BERR";
	 public static final String B_ERROR_INVALID_MUST_BE_LESS_THAN = "MBW00036BERR";
	 public static final String B_ERROR_CONCURRENTCY ="MBW00038BERR";
	 public static final String B_ERROR_VOLUME_NOT_IN_TCFROM_TO ="MBW00039BERR";
	 public static final String B_ERROR_NOT_FOUND_IN_RELATION_MODEL ="MBW00040BERR";
	 public static final String B_ERROR_MUST_EQUAL_OR_LESS_THAN = "MBW00041BERR";
	 public static final String B_ERROR_MISSING = "MBW00042BERR";
	 public static final String B_ERROR_NO_KAIKIENG = "MBW00043BERR";
	 public static final String B_ERROR_NO_PACKING_VOLUME = "MBW00044BERR";
	 
	 //Warning of Batch
	 public static final String A_WARNING_VALUE_HAS_BEEN_CHANGE = "MBW00001BWRN";
	 
	 
	 public static final String A_ERROR_MUST_EQUAL = "MBW00003AERR";
	 public static final String A_ERROR_MUST_BE_GREATER_THAN_OR_EQUAL = "MSTD0022AERR";
	 public static final String A_ERROR_OVER_LAP = "MBW00006AERR";
	 public static final String A_ERROR_INVALID_LENGTH = "MSTD0051AERR";
	 
	 public static final String A_ERROR_RUNDOWNS_OR_DIAGRAM_FILE = "MBW00008AERR";
	 public static final String A_ERROR_ALREADY_EXIST_IN = "MSTD0010AERR";
	 public static final String A_ERROR_MINMAX_INVALID_VALUE = "MSTD0016AERR";
	 
	 public static final String A_ERROR_DOES_NOT_CONFIG_PACK_LEAD_TIME = "MBW00004AERR";
	 public static final String A_ERROR_NOT_ALLOW_TO_SET_STANDARD_STOCK = "MBW00017AERR";
	 public static final String A_ERROR_INVALID_MISSING_FIELD = "MBW00019AERR";
	 public static final String A_ERROR_MISSING = "MBW00020AERR";
		 
}
