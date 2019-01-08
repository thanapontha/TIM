package th.co.toyota.bw0.batch.common;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.upload.CST32020DataFileUploadConfig;
import th.co.toyota.st3.batch.util.CST30000BatchUtils;

import com.google.common.base.Strings;

public class CBW00000BatchUtil {

	final Logger logger = LoggerFactory.getLogger(CBW00000BatchUtil.class);

	@Autowired
	protected MessageSource messageSource;
	
    @Autowired
    private CST32020DataFileUploadConfig uploadConfig;

	public void archiveFile(String filename, String companyCode, boolean isErrorFile) {

		String targetFilepath = uploadConfig.getTempUploadFolder(companyCode);

		if (Strings.isNullOrEmpty(targetFilepath)) {
			String message = "Missing config default.updown.temp.folder in batch.properties";

			String errMsg = messageSource.getMessage(
					CST30000Messages.ERROR_UNDEFINED_ERROR,
					new String[] { message }, Locale.getDefault());

			logger.error(errMsg);
		}

		String archiveFilepath = uploadConfig.getArchiveSuccess(companyCode);
		if (isErrorFile) {
			archiveFilepath = uploadConfig.getArchiveError(companyCode);
		}

		if (Strings.isNullOrEmpty(archiveFilepath)) {
			String message = "Missing config archive.success.folder in batch.properties";
			if (isErrorFile) {
				message = "Missing config archive.error.folder in batch.properties";
			}

			String errMsg = messageSource.getMessage(
					CST30000Messages.ERROR_UNDEFINED_ERROR,
					new String[] { message }, Locale.getDefault());

			logger.error(errMsg);
		}

		CST30000BatchUtils.archiveFile(archiveFilepath, targetFilepath+ filename);
	}

}
