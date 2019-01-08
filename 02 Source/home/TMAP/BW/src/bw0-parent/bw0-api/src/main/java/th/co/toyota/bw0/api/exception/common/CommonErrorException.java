package th.co.toyota.bw0.api.exception.common;

public class CommonErrorException extends Exception {

	private static final long serialVersionUID = 3169799639634311543L;
	private String messageCode;
	private String[] messageArg;
	private int messageType;
	private String displayMessage;
	
	public CommonErrorException(String displayMessage, int messageType) {
		this.displayMessage = displayMessage;
		this.messageType = messageType;
	}
	
	public CommonErrorException(String messageCode, String[] messageArg, int messageType) {
		this.messageCode = messageCode;
		this.messageArg = messageArg;
		this.messageType = messageType;
	}
	
	public CommonErrorException(String messageCode, String[] messageArg, int messageType, String message) {
		super(message);
		this.messageCode = messageCode;
		this.messageArg = messageArg;
		this.messageType = messageType;
	}

	public String getMessageCode() {
		return messageCode;
	}

	public String[] getMessageArg() {
		return messageArg;
	}

	public int getMessageType() {
		return messageType;
	}

	public String getDisplayMessage() {
		return displayMessage;
	}

	public void setDisplayMessage(String displayMessage) {
		this.displayMessage = displayMessage;
	}

}
