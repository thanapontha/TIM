package th.co.toyota.bw0.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

/**
 * This utility class converts a JSON String into and Object of passed type. If
 * the string is not parsed correctly then it throws
 * JsonStringToObjectConversionException
 * 
 * @author PatilSan
 * 
 */
@Component
public class JsonStringToObjectConverter {
	final Logger logger = LoggerFactory
			.getLogger(JsonStringToObjectConverter.class);

	/**
	 * This method converts a JSON String into and Object of passed class name.
	 * 
	 * @param className
	 *            {@link String}
	 * @param jsonString
	 *            {@link String}
	 * @return object {@link Object}
	 * @throws JsonStringToObjectConversionException
	 */
	public Object converJsonStringToObject(String className, String jsonString)
			throws JsonStringToObjectConversionException {
		
		if(className == null){
			throw new JsonStringToObjectConversionException("Class name can not be null");
		}
		
		if(jsonString ==  null){
			throw new JsonStringToObjectConversionException("Json string to conver in object can not be null");
		}
		
		Object object = null;
		try {
			Class<?> objectClass = Class.forName(className);
			Gson gson = new Gson();
			object = gson.fromJson(jsonString, objectClass);
		} catch (ClassNotFoundException e) {
			logger.info("ClassNotFoundException occured while converting jsonString into object of type"
					+ className);
			logger.info(e.getMessage());
			throw new JsonStringToObjectConversionException(className
					+ " not found in class path");
		} catch (Exception e) {
			logger.info("Exception occured while converting jsonString into object of type"
					+ className);
			logger.info(e.getMessage());
			throw new JsonStringToObjectConversionException(
					"Exception occured while converting jsonString into an Object of type :-"
							+ className);
		}
		return object;
	}

}
