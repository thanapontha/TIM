package th.co.toyota.bw0.util;

/**
 * This exception is thrown if any error occurs while converting a JSON string
 * into an Object.
 * 
 * @author PatilSan
 * 
 */
public class JsonStringToObjectConversionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6297127147898877785L;

	public JsonStringToObjectConversionException() {
		super();
	}

	public JsonStringToObjectConversionException(String message) {
		super(message);
	}

}
