package th.co.toyota.bw0.api.common;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.constants.MessagesConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.api.repository.common.SystemMasterAPIRepository;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.model.SystemInfo;
import th.co.toyota.st3.api.model.SystemInfoId;
import th.co.toyota.st3.api.util.IST30000LoggerDb;

@Component
public class MailSenderVelocityImpl implements MailSender {

	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private VelocityEngine velocity;
    private JavaMailSender mailSender;
    private String applicationUrl;
    
    public String getApplicationUrl() {
		return applicationUrl;
	}

	public void setApplicationUrl(String applicationUrl) {
		this.applicationUrl = applicationUrl;
	}

    private String mailFromDescription;
    private String mailFrom;
    private String senderFooter;
	private String encoding;  	
    private String templateLocation;
  
    // templates
    private String templateSubmitKaikiengData;
    private String templateRejectKaikiengData;
    private String templateFixKaikiengData;

	@Autowired
	private SystemMasterAPIRepository systemRepository;
	
	@Autowired
	protected MessageSource messageSource;
	
    @Autowired
    public MailSenderVelocityImpl(VelocityEngine velocity,
            JavaMailSender mailSender) {
        this.velocity = velocity;
        this.mailSender = mailSender;
    }
    
    private void sendEmail(final Map<String, Object> model,
    						  final String[] emailTo,
    						  final String[] emailCc,
    						  final String mailSubject,
    						  final String velocityTemplate,
    						  final ArrayList<String> attachFile) throws CommonErrorException {
    	MimeMessagePreparator preparator = new MimeMessagePreparator() {
			
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
				message.setSubject(mailSubject);
				if (emailTo != null && emailTo.length > 0){
					message.setTo(emailTo);
				}
				if (emailCc != null && emailCc.length > 0){
					message.setCc(emailCc);
				}
               
                message.setFrom(mailFrom, mailFromDescription);
                
                if (attachFile != null && !attachFile.isEmpty()){
                	for (int i=0; i < attachFile.size(); i++){
                		FileSystemResource file = new FileSystemResource(new File(attachFile.get(i)));
                		message.addAttachment(file.getFilename(), file);
                	}
                }
                
                StringBuilder velocityMailTemplate = new StringBuilder(templateLocation).append("/").append(velocityTemplate);

                logger.debug("Velocity Template: {}", velocityMailTemplate.toString());
                
                String text = VelocityEngineUtils.mergeTemplateIntoString(velocity,
                														  velocityMailTemplate.toString(),
                														  encoding,
                														  model);
                
                message.setText(text, true);
//                logger.info("Text: {}", text);
//                logger.info("Email sent to: {}", Joiner.on(",").join(emailTo));
//                if (emailCc != null){
//                	logger.info("Email sent cc: {}", Joiner.on(",").join(emailCc));
//                }
                logger.debug("Email details: {}", text);
			}
		};
		
		try {
            this.mailSender.send(preparator);
        } catch (Exception ex) {
            logger.error("Unable to send mail.", ex);
            ex.printStackTrace();
            throw new CommonErrorException(CST30000Messages.ERROR_UNDEFINED_ERROR, 
            		new String[] { ex.getMessage()}, AppConstants.ERROR);
        }
    }
    
    
    
    @Override
    public void sendEmail(Connection conn, Map<String, Object> model, String functionId, String appId, String userId, IST30000LoggerDb loggerDb)throws CommonErrorException {
		boolean valid = true;
    	
    	StringBuffer emailTo = new StringBuffer();
		StringBuffer emailCc = new StringBuffer();
		
		SystemInfoId infoId = new SystemInfoId();
		infoId.setCategory(AppConstants.SYS_CATEGORY_EMAIL);
		infoId.setSubCategory(functionId);	
		infoId.setCode(AppConstants.SYS_CD_TO+"*");

		List<SystemInfo> emailToList = systemRepository.querySystemMasterInfo(infoId);
		if(emailToList != null && emailToList.size()==0){
			String errMsg = messageSource.getMessage(MessagesConstants.A_ERROR_NOT_REGISTER,
					new String[] {"CATEGORY="+AppConstants.SYS_CATEGORY_EMAIL+", SUB_CATEGORY="+functionId+", CD="+AppConstants.SYS_CD_TO,
								"System Master"},
					Locale.getDefault());
			logger.error(errMsg);
			loggerDb.error(appId, MessagesConstants.A_ERROR_NOT_REGISTER, errMsg, userId);
			valid = false;
		}else{
			for(int i=0; i < emailToList.size(); i++){
				SystemInfo sysInfo = emailToList.get(i);
				if (Strings.isNullOrEmpty(emailTo.toString())){
					emailTo.append(sysInfo.getValue());
				}else{
					if(i <= emailToList.size() -1 ){
						emailTo.append(",");
					}
					emailTo.append(sysInfo.getValue());
				}
			}
		}
		
		String subjectMail = systemRepository.findSystemMasterValue(conn, AppConstants.SYS_CATEGORY_EMAIL, functionId, AppConstants.SYS_CD_SUBJECT);
		if(Strings.isNullOrEmpty(subjectMail)){
			String errMsg = messageSource.getMessage(MessagesConstants.A_ERROR_NOT_REGISTER,
					new String[] {"CATEGORY="+AppConstants.SYS_CATEGORY_EMAIL+", SUB_CATEGORY="+functionId+", CD="+AppConstants.SYS_CD_SUBJECT,
								"System Master"},
					Locale.getDefault());
			logger.error(errMsg);
			loggerDb.error(appId, MessagesConstants.A_ERROR_NOT_REGISTER, errMsg, userId);
			valid = false;
		}
		
		infoId.setCategory(AppConstants.SYS_CATEGORY_EMAIL);
		infoId.setSubCategory(AppConstants.FUNCTION_ID_BBW01140);		
		infoId.setCode(AppConstants.SYS_CD_CC+"*");
		List<SystemInfo> emailCcList = systemRepository.querySystemMasterInfo(infoId);
		if(emailCcList != null && emailCcList.size()==0){
			for(int i=0; i < emailCcList.size(); i++){
				SystemInfo sysInfo = emailCcList.get(i);
				if (Strings.isNullOrEmpty(emailCc.toString())){
					emailCc.append(sysInfo.getValue());
				}else{
					if(i <= emailCcList.size() -1 ){
						emailCc.append(",");
					}
					emailCc.append(sysInfo.getValue());
				}
			}
		}
		if(valid){
			String[] emailToArr = emailTo.toString().split(","); 
			String[] emailCcArr = Strings.isNullOrEmpty(emailCc.toString())? null : emailCc.toString().split(",");
			if(model!= null){
				model.put("SENDER", this.senderFooter);
			}
			if (functionId.equals("WBW01110")) {
				this.sendEmail(model, emailToArr, emailCcArr, subjectMail, this.templateSubmitKaikiengData, null);
			} else {
//				this.sendEmail(model, emailToArr, emailCcArr, subjectMail, this.templateSubmitKaikiengData, null);
			}
		}
	}
    
    public void sendEmail(Map<String, Object> model, String subjectMail, String[] emailToArr, String[] emailCcArr, String functionId, String action)throws CommonErrorException {
		if (AppConstants.SCREEN_ID_WBW01110.equals(functionId) && AppConstants.ACTION_SUBMIT_KAIKIENGDATA.equals(action)) {
			this.sendEmail(model, emailToArr, emailCcArr, subjectMail, this.templateSubmitKaikiengData, null);
		}else if (AppConstants.SCREEN_ID_WBW01110.equals(functionId) && AppConstants.ACTION_REJECT_KAIKIENGDATA.equals(action)) {
			this.sendEmail(model, emailToArr, emailCcArr, subjectMail, this.templateRejectKaikiengData, null);
		}else if (AppConstants.SCREEN_ID_WBW01110.equals(functionId) && AppConstants.ACTION_FIX_KAIKIENGDATA.equals(action)) {
			this.sendEmail(model, emailToArr, emailCcArr, subjectMail, this.templateFixKaikiengData, null);
		}
    	
    }
			
	public VelocityEngine getVelocity() {
		return velocity;
	}

	public void setVelocity(VelocityEngine velocity) {
		this.velocity = velocity;
	}

	public JavaMailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public String getMailFromDescription() {
		return mailFromDescription;
	}

	public void setMailFromDescription(String mailFromDescription) {
		this.mailFromDescription = mailFromDescription;
	}

	public String getMailFrom() {
		return mailFrom;
	}

	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	public String getSenderFooter() {
		return senderFooter;
	}

	public void setSenderFooter(String senderFooter) {
		this.senderFooter = senderFooter;
	}

	public String getTemplateLocation() {
		return templateLocation;
	}

	public void setTemplateLocation(String templateLocation) {
		this.templateLocation = templateLocation;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getTemplateSubmitKaikiengData() {
		return templateSubmitKaikiengData;
	}

	public void setTemplateSubmitKaikiengData(String templateSubmitKaikiengData) {
		this.templateSubmitKaikiengData = templateSubmitKaikiengData;
	}

	public String getTemplateRejectKaikiengData() {
		return templateRejectKaikiengData;
	}

	public void setTemplateRejectKaikiengData(String templateRejectKaikiengData) {
		this.templateRejectKaikiengData = templateRejectKaikiengData;
	}
	
	public String getTemplateFixKaikiengData() {
		return templateFixKaikiengData;
	}

	public void setTemplateFixKaikiengData(String templateFixKaikiengData) {
		this.templateFixKaikiengData = templateFixKaikiengData;
	}

}
