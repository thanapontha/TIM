package th.co.toyota.bw0.api.common.upload;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.constants.MessagesConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;

public class CommonDataFileUploadXMLLoader {
	protected String[] columnNames;
	
	public CommonDataFileUploadExcelConversionDTO loadXMLConfig(String pathXmlConfigFile, String xmlFileName) throws Exception {
		try {
			String pullFile = pathXmlConfigFile+File.separator + xmlFileName;
			File configFile = new File(pullFile);
			if (!configFile.exists()) {
				throw new CommonErrorException(MessagesConstants.B_ERROR_FILE_NOT_EXISTS, new String[]{pullFile}, AppConstants.ERROR);
			}
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(configFile);
            if (doc == null) {
            	throw new Exception("Cannot parse DocumentBuilder object to Document object by " + configFile);
            }
            else {
            	doc.getDocumentElement().normalize();
                NodeList rootNode = doc.getElementsByTagName(
                		CommonDataFileUploadExcelConversionDTO.TAG_EXCEL_CONVERSION_CONFIG);
                
                if (rootNode == null || rootNode.getLength() == 0) {
                	throw new Exception("Not found <" + CommonDataFileUploadExcelConversionDTO.TAG_EXCEL_CONVERSION_CONFIG + "> in config file");
                }
                else {
                	CommonDataFileUploadExcelConversionDTO excelConvVo = new CommonDataFileUploadExcelConversionDTO();
                	
                	NodeList childNodes = rootNode.item(0).getChildNodes();
                	for (int i = 0; i < childNodes.getLength(); i++) {
                		loadConfiguration(excelConvVo, childNodes.item(i));
                	}
                	
                	excelConvVo.setColumnNames(columnNames);
                    return excelConvVo;
                }
            }
		} catch(Exception ex) {
			throw ex;
		}
	}
	
	private void loadConfiguration(CommonDataFileUploadExcelConversionDTO excelConvVo, Node node) throws Exception {
		String nodeName = node.getNodeName();
		NamedNodeMap attribs = node.getAttributes();
		if (CommonDataFileUploadExcelConversionDTO.TAG_CONVERSION_INFO.equals(nodeName)) {
			String wrkShtIdx = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_WRKSHT_IDX);
			if (wrkShtIdx != null && wrkShtIdx.length() > 0) {
				String[] strWrkShtIdx = wrkShtIdx.split(",");
				Integer[] intWrkShtIdx = new Integer[strWrkShtIdx.length];
				for (int i = 0; i < strWrkShtIdx.length; i++) {
					intWrkShtIdx[i] = new Integer(Integer.parseInt(strWrkShtIdx[i]));
				}
				excelConvVo.setArrayWorkSheetIdx(intWrkShtIdx);
			}
			
			String wrkShtName = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_WRKSHT_NAME);
			if (wrkShtName != null && wrkShtName.length() > 0) {
				String[] strWrkShtName = wrkShtName.split(",");
				excelConvVo.setArrayWorkSheetName(strWrkShtName);
			}
		}else if (CommonDataFileUploadExcelConversionDTO.TAG_CHECK_HEADERS.equals(nodeName)) {
			excelConvVo.setCheckHeaders(new ArrayList());

			NodeList childNodes = node.getChildNodes();
        	for (int i = 0; i < childNodes.getLength(); i++) {
        		loadConfiguration(excelConvVo, childNodes.item(i));
        	}
			String startRow = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_START_ROW);
			String endRow = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_END_ROW);
			HashMap<String, Object> hConfig = new HashMap<String, Object>();
			hConfig.put(CommonDataFileUploadExcelConversionDTO.ATTR_START_ROW, new Integer(Integer.parseInt(startRow)));
			hConfig.put(CommonDataFileUploadExcelConversionDTO.ATTR_END_ROW, null!=endRow?new Integer(Integer.parseInt(endRow)):null);
			hConfig.put(CommonDataFileUploadExcelConversionDTO.TAG_CHECK_HEADERS, excelConvVo.getCheckHeaders());
			
			excelConvVo.setExcelCheckHeadersConfig(hConfig);
			
		}else if (CommonDataFileUploadExcelConversionDTO.TAG_CHECK_HEADER.equals(nodeName)) {
		
			String startRow = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_START_ROW);
			String endRow = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_END_ROW);
			String startCol = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_START_COL);
			String endCol = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_END_COL);
			HashMap<String, Object> hCheckConfig = new HashMap<String, Object>();
			hCheckConfig.put(CommonDataFileUploadExcelConversionDTO.ATTR_START_ROW, new Integer(Integer.parseInt(startRow)));
			hCheckConfig.put(CommonDataFileUploadExcelConversionDTO.ATTR_END_ROW, null!=endRow?new Integer(Integer.parseInt(endRow)):null);
			hCheckConfig.put(CommonDataFileUploadExcelConversionDTO.ATTR_START_COL, new Integer(Integer.parseInt(startCol)));
			hCheckConfig.put(CommonDataFileUploadExcelConversionDTO.ATTR_END_COL, null!=endCol?new Integer(Integer.parseInt(endCol)):null);
			hCheckConfig.put(CommonDataFileUploadExcelConversionDTO.TAG_CHECK_HEADER, getMappingInfo(node, false));
			
			excelConvVo.getCheckHeaders().add(hCheckConfig);
		}
		else if (CommonDataFileUploadExcelConversionDTO.TAG_CONVERSION_HEADER.equals(nodeName)) {
			String startRow = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_START_ROW);
			String endRow = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_END_ROW);
			String startCol = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_START_COL);
			String endCol = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_END_COL);
			HashMap<String, Object> hConfig = new HashMap<String, Object>();
			hConfig.put(CommonDataFileUploadExcelConversionDTO.ATTR_START_ROW, new Integer(Integer.parseInt(startRow)));
			hConfig.put(CommonDataFileUploadExcelConversionDTO.ATTR_END_ROW, null!=endRow?new Integer(Integer.parseInt(endRow)):null);
			hConfig.put(CommonDataFileUploadExcelConversionDTO.ATTR_START_COL, new Integer(Integer.parseInt(startCol)));
			hConfig.put(CommonDataFileUploadExcelConversionDTO.ATTR_END_COL, null!=endCol?new Integer(Integer.parseInt(endCol)):null);
			hConfig.put(CommonDataFileUploadExcelConversionDTO.TAG_CONVERSION_HEADER, getMappingInfo(node, false));
			
			excelConvVo.setExcelHeaderConfig(hConfig);
		}
		else if (CommonDataFileUploadExcelConversionDTO.TAG_CONVERSION_DETAIL.equals(nodeName)) {
			String startRow = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_START_ROW);
			String endRow = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_END_ROW);
			String startCol = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_START_COL);
			String endCol = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_END_COL);
			HashMap<String, Object> dConfig = new HashMap<String, Object>();
			
			columnNames = new String[endCol==null?0:Integer.parseInt(endCol)];
			dConfig.put(CommonDataFileUploadExcelConversionDTO.ATTR_START_ROW, null!=startRow?new Integer(Integer.parseInt(startRow)):null);
			dConfig.put(CommonDataFileUploadExcelConversionDTO.ATTR_END_ROW, null!=endRow?new Integer(Integer.parseInt(endRow)):null);
			dConfig.put(CommonDataFileUploadExcelConversionDTO.ATTR_START_COL, null!=startCol?new Integer(Integer.parseInt(startCol)):null);
			dConfig.put(CommonDataFileUploadExcelConversionDTO.ATTR_END_COL, null!=endCol?new Integer(Integer.parseInt(endCol)):null);
			dConfig.put(CommonDataFileUploadExcelConversionDTO.TAG_CONVERSION_DETAIL, getMappingInfo(node, true));
			

			excelConvVo.setExcelDetailConfig(dConfig);
		}
	}

	private HashMap<String, HashMap<String,String>> getMappingInfo(Node node, boolean detailFlag) throws Exception {
		//Get Mapping Node
		NodeList mappingNodes = node.getChildNodes();
		if (mappingNodes != null && mappingNodes.getLength() > 0) {
			HashMap<String, HashMap<String,String>> mappingHash = new HashMap<String, HashMap<String,String>>();
			int index = 0;
			for (int i = 0; i < mappingNodes.getLength(); i++) {
				Node curNode = mappingNodes.item(i);
				String nodeName = curNode.getNodeName();
				if (CommonDataFileUploadExcelConversionDTO.TAG_FIELD_MAP.equals(nodeName)) {
					//Get Excel Field Name and DB Field Name Attributes
					NamedNodeMap attribs = curNode.getAttributes();
					String excelFieldName = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_NAME);
					String dbFieldName = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_VALUE);
					String displayLabel = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_LABEL);
					String xlsDtFormat = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_XLS_DT_FORMAT);
					String ifDtFormat = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_IF_DT_FORMAT);
					String overrideLength = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_LENGTH);
					String mandatoryField = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_MANDATORY_FIELD);
					String replaceKey = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_REPLACE_KEY);
					String replaceToValue = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_REPLACE_TO_VALUE);
					String decimal = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_DECIMAL);
					String regExCheck = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_REGEX_CHECK);
					String convertToNumberic = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_CONVERT_TO_NUMBERIC);
					String convertToDateBy = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_CONVERT_STRING_TO_DATE);
					String correctNumberFormat = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_CORRECT_NUMBER_FORMAT);
					String replaceValueBeforeChkLength = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_REPLACE_CALUE_BEFORE_CHK_LENGTH);
					String roundHaftUp = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_ROUND_HAFT_UP);
					String alphaNumericCheck = getAttributeValue(attribs, CommonDataFileUploadExcelConversionDTO.ATTR_ALPHANUMERIC_CHECK);
					
					//Assign to Hash
					HashMap<String,String> mappingField = new HashMap<String,String>();
					mappingField.put(CommonDataFileUploadExcelConversionDTO.ATTR_VALUE, dbFieldName);
					mappingField.put(CommonDataFileUploadExcelConversionDTO.ATTR_LABEL, displayLabel);
					mappingField.put(CommonDataFileUploadExcelConversionDTO.ATTR_LENGTH, overrideLength);
					mappingField.put(CommonDataFileUploadExcelConversionDTO.ATTR_MANDATORY_FIELD, mandatoryField);
					mappingField.put(CommonDataFileUploadExcelConversionDTO.ATTR_XLS_DT_FORMAT, xlsDtFormat);
					mappingField.put(CommonDataFileUploadExcelConversionDTO.ATTR_IF_DT_FORMAT, ifDtFormat);
					mappingField.put(CommonDataFileUploadExcelConversionDTO.ATTR_REPLACE_KEY, replaceKey);
					mappingField.put(CommonDataFileUploadExcelConversionDTO.ATTR_REPLACE_TO_VALUE, replaceToValue);
					mappingField.put(CommonDataFileUploadExcelConversionDTO.ATTR_DECIMAL, decimal);
					mappingField.put(CommonDataFileUploadExcelConversionDTO.ATTR_REGEX_CHECK, regExCheck);
					mappingField.put(CommonDataFileUploadExcelConversionDTO.ATTR_CONVERT_TO_NUMBERIC, convertToNumberic);
					mappingField.put(CommonDataFileUploadExcelConversionDTO.ATTR_CONVERT_STRING_TO_DATE, convertToDateBy);
					mappingField.put(CommonDataFileUploadExcelConversionDTO.ATTR_CORRECT_NUMBER_FORMAT, correctNumberFormat);
					mappingField.put(CommonDataFileUploadExcelConversionDTO.ATTR_REPLACE_CALUE_BEFORE_CHK_LENGTH, replaceValueBeforeChkLength);
					mappingField.put(CommonDataFileUploadExcelConversionDTO.ATTR_ROUND_HAFT_UP, roundHaftUp);
					mappingField.put(CommonDataFileUploadExcelConversionDTO.ATTR_ALPHANUMERIC_CHECK, alphaNumericCheck);
					mappingHash.put(excelFieldName, mappingField);
					if(detailFlag){
						columnNames[index] = excelFieldName;
					}
					index++;
				}
			}
			
			return mappingHash;
		}
		
		return null;
	}
	
	private String getAttributeValue(NamedNodeMap attribs, String attrName) {
		if (attribs.getNamedItem(attrName) != null) {
			return attribs.getNamedItem(attrName).getNodeValue();
		}
		else {
			return null;
		}
	}

	public String getOutputDirectory(String outDir){
		if (outDir.lastIndexOf(File.separator) == outDir.length()-1){
			outDir = outDir.substring(0,outDir.length()-1);
		}
		return outDir;
	}
}
