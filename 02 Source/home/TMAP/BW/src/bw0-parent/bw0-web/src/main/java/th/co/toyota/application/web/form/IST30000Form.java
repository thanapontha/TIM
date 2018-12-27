/******************************************************
 * Program History
 * 
 * Project Name	            :  st3-web-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.web.form
 * Program ID 	            :  IST30000Form.java
 * Program Description	    :  Base interface for ST3 froms.
 * Environment	 	        :  Java 7
 * Author					:  danilo
 * Version					:  1.0
 * Creation Date            :  Jul 16, 2013
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.web.form;

/**
 * A base for for all ST3 form.
 * 
 * @author danilo
 * 
 */
public interface IST30000Form {

	/**
	 * Method use to display the friendly name of each field on screen.
	 * 
	 * @param field
	 *            A form field
	 * @return A friendly field name.
	 */
	String displayFriendlyField(String field);
}
