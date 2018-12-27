/******************************************************
 * Program History
 * 
 * Project Name	            :  st-server-webapp-template
 * Client Name				:  TMAP-EM
 * Package Name             :  th.co.toyota.application.web
 * Program ID 	            :  TimeController.java
 * Program Description	    :  Time Controller.
 * Environment	 	        :  Java 7
 * Author					:  danilo
 * Version					:  1.0
 * Creation Date            :  Aug 2, 2013
 *
 * Modification History	    :
 * Version	   Date		   Person Name		Chng Req No		Remarks
 *
 * Copyright(C) 2013-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.application.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.toyota.application.model.Payload;
import th.co.toyota.application.model.ServiceStatus;
import th.co.toyota.application.model.XmlPayload;

/**
 * This controller execute by ST3 java script libraries to get the time from
 * server after every 5 seconds.
 * 
 * @author danilo
 * 
 */
@Controller
@RequestMapping(value = "/getTime")
public class TimeController extends CommonBaseController {

	/**
	 * This function will be invoke to get time from server when time difference
	 * from server more than 5 sec.
	 * 
	 * @param request A http request
	 * @return A {@link Payload} instance.
	 */
	@RequestMapping(method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	Object getTimeServer(HttpServletRequest request) {
		Payload payload = new XmlPayload();
		payload.setStatus(ServiceStatus.OK);
		return payload;
	}
}
