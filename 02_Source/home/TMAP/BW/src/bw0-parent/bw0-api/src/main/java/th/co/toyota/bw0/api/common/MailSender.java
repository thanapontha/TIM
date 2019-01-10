package th.co.toyota.bw0.api.common;

import java.sql.Connection;
import java.util.Map;

import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.st3.api.util.IST30000LoggerDb;

public interface MailSender {
	void sendEmail(Connection conn, Map<String, Object> model, String functionId, String appId, String userId, IST30000LoggerDb loggerDb)throws CommonErrorException;
	void sendEmail(Map<String, Object> model, String subjectMail, String[] emailToArr, String[] emailCcArr, String functionId, String action)throws CommonErrorException;
}
