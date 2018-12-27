/******************************************************
 * Program History
 * 
 * Project Name	            :  st3-web-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.model
 * Program ID 	            :  ServiceStatus.java
 * Program Description	    :  Application service status.
 * Environment	 	        :  Java 7
 * Author					:  danilo
 * Version					:  1.0
 * Creation Date            :  Feb 28, 2014
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2015-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.model;

/**
 * ST3 web application service status, status is define with three different
 * types.
 * <ul>
 * <li>Status OK : Indicates service execution is successful.
 * <li>Status NG : Indicate the service executed with error.
 * <li>Status WARN : Indicate service executed with warning.
 * </ul>
 * 
 * @author danilo
 * 
 */
public enum ServiceStatus {
	OK, NG, WARN
}