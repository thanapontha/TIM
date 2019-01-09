package th.co.toyota.bw0.api.common.upload;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.constants.MessagesConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;

public class CommonFileUploadXMLLoader {
	protected String[] columnNames;
	
	public CommonExcelConversionDTO loadXMLConfig(String pathXmlConfigFile, String xmlFileName) throws Exception {
		String pullFile = pathXmlConfigFile+File.separator + xmlFileName;
		File configFile = new File(pullFile);
		if (!configFile.exists()) {
			throw new CommonErrorException(MessagesConstants.B_ERROR_FILE_NOT_EXISTS, new String[]{pullFile}, AppConstants.ERROR);
		}
		
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(configFile);
        if (doc == null) {
        	throw new CommonErrorException("Cannot parse DocumentBuilder object to Document object by " + configFile, AppConstants.ERROR);
        }
        else {
        	doc.getDocumentElement().normalize();
            NodeList rootNode = doc.getElementsByTagName(
            		CommonExcelConversionDTO.TAG_EXCEL_CONVERSION_CONFIG);
            
            if (rootNode == null || rootNode.getLength() == 0) {
            	throw new CommonErrorException("Not found <" + CommonExcelConversionDTO.TAG_EXCEL_CONVERSION_CONFIG + "> in config file", AppConstants.ERROR);
            }
            else {
            	CommonExcelConversionDTO excelConvVo = new CommonExcelConversionDTO();
            	
            	NodeList childNodes = rootNode.item(0).getChildNodes();
            	for (int i = 0; i < childNodes.getLength(); i++) {
            		loadConfiguration(excelConvVo, childNodes.item(i));
            	}
            	
            	excelConvVo.setColumnNames(columnNames);
                return excelConvVo;
            }
        }
	}
	
	private void loadConfiguration(CommonExcelConversionDTO excelConvVo, Node node) throws Exception {
		String nodeName = node.getNodeName();
		NamedNodeMap attribs = node.getAttributes();
		if (CommonExcelConversionDTO.TAG_CONVERSION_INFO.equals(nodeName)) {
			String wrkShtIdx = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_WRKSHT_IDX);
			if (wrkShtIdx != null && wrkShtIdx.length() > 0) {
				String[] strWrkShtIdx = wrkShtIdx.split(",");
				Integer[] intWrkShtIdx = new Integer[strWrkShtIdx.length];
				for (int i = 0; i < strWrkShtIdx.length; i++) {
					intWrkShtIdx[i] = Integer.valueOf(strWrkShtIdx[i]);
				}
				excelConvVo.setArrayWorkSheetIdx(intWrkShtIdx);
			}
			
			String wrkShtName = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_WRKSHT_NAME);
			if (wrkShtName != null && wrkShtName.length() > 0) {
				String[] strWrkShtName = wrkShtName.split(",");
				excelConvVo.setArrayWorkSheetName(strWrkShtName);
			}
		}else if (CommonExcelConversionDTO.TAG_CHECK_HEADERS.equals(nodeName)) {
			excelConvVo.setCheckHeaders(new ArrayList());

			NodeList childNodes = node.getChildNodes();
        	for (int i = 0; i < childNodes.getLength(); i++) {
        		loadConfiguration(excelConvVo, childNodes.item(i));
        	}
			String startRow = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_START_ROW);
			String endRow = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_END_ROW);
			HashMap<String, Object> hConfig = new HashMap<>();
			hConfig.put(CommonExcelConversionDTO.ATTR_START_ROW, Integer.valueOf(startRow));
			hConfig.put(CommonExcelConversionDTO.ATTR_END_ROW, null!=endRow?Integer.valueOf(endRow):null);
			hConfig.put(CommonExcelConversionDTO.TAG_CHECK_HEADERS, excelConvVo.getCheckHeaders());
			
			excelConvVo.setExcelCheckHeadersConfig(hConfig);
			
		}else if (CommonExcelConversionDTO.TAG_CHECK_HEADER.equals(nodeName)) {
		
			String startRow = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_START_ROW);
			String endRow = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_END_ROW);
			String startCol = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_START_COL);
			String endCol = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_END_COL);
			Map<String, Object> hCheckConfig = new HashMap<>();
			hCheckConfig.put(CommonExcelConversionDTO.ATTR_START_ROW, Integer.valueOf(startRow));
			hCheckConfig.put(CommonExcelConversionDTO.ATTR_END_ROW, null!=endRow?Integer.valueOf(endRow):null);
			hCheckConfig.put(CommonExcelConversionDTO.ATTR_START_COL, Integer.valueOf(startCol));
			hCheckConfig.put(CommonExcelConversionDTO.ATTR_END_COL, null!=endCol?Integer.valueOf(endCol):null);
			hCheckConfig.put(CommonExcelConversionDTO.TAG_CHECK_HEADER, getMappingInfo(node, false));
			
			excelConvVo.getCheckHeaders().add(hCheckConfig);
		}
		else if (CommonExcelConversionDTO.TAG_CONVERSION_HEADER.equals(nodeName)) {
			String startRow = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_START_ROW);
			String endRow = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_END_ROW);
			String startCol = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_START_COL);
			String endCol = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_END_COL);
			HashMap<String, Object> hConfig = new HashMap<>();
			hConfig.put(CommonExcelConversionDTO.ATTR_START_ROW, Integer.valueOf(startRow));
			hConfig.put(CommonExcelConversionDTO.ATTR_END_ROW, null!=endRow?Integer.valueOf(endRow):null);
			hConfig.put(CommonExcelConversionDTO.ATTR_START_COL, Integer.valueOf(startCol));
			hConfig.put(CommonExcelConversionDTO.ATTR_END_COL, null!=endCol?Integer.valueOf(endCol):null);
			hConfig.put(CommonExcelConversionDTO.TAG_CONVERSION_HEADER, getMappingInfo(node, false));
			
			excelConvVo.setExcelHeaderConfig(hConfig);
		}
		else if (CommonExcelConversionDTO.TAG_CONVERSION_DETAIL.equals(nodeName)) {
			String startRow = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_START_ROW);
			String endRow = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_END_ROW);
			String startCol = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_START_COL);
			String endCol = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_END_COL);
			HashMap<String, Object> dConfig = new HashMap<>();
			
			columnNames = new String[endCol==null?0:Integer.parseInt(endCol)];
			dConfig.put(CommonExcelConversionDTO.ATTR_START_ROW, null!=startRow?Integer.valueOf(startRow):null);
			dConfig.put(CommonExcelConversionDTO.ATTR_END_ROW, null!=endRow?Integer.valueOf(endRow):null);
			dConfig.put(CommonExcelConversionDTO.ATTR_START_COL, null!=startCol?Integer.valueOf(startCol):null);
			dConfig.put(CommonExcelConversionDTO.ATTR_END_COL, null!=endCol?Integer.valueOf(endCol):null);
			dConfig.put(CommonExcelConversionDTO.TAG_CONVERSION_DETAIL, getMappingInfo(node, true));
			

			excelConvVo.setExcelDetailConfig(dConfig);
		}
	}

	private HashMap<String, HashMap<String,String>> getMappingInfo(Node node, boolean detailFlag) {
		//Get Mapping Node
		NodeList mappingNodes = node.getChildNodes();
		if (mappingNodes != null && mappingNodes.getLength() > 0) {
			HashMap<String, HashMap<String,String>> mappingHash = new HashMap<>();
			int index = 0;
			for (int i = 0; i < mappingNodes.getLength(); i++) {
				Node curNode = mappingNodes.item(i);
				String nodeName = curNode.getNodeName();
				if (CommonExcelConversionDTO.TAG_FIELD_MAP.equals(nodeName)) {
					//Get Excel Field Name and DB Field Name Attributes
					NamedNodeMap attribs = curNode.getAttributes();
					String excelFieldName = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_NAME);
					String dbFieldName = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_VALUE);
					String displayLabel = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_LABEL);
					String xlsDtFormat = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_XLS_DT_FORMAT);
					String ifDtFormat = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_IF_DT_FORMAT);
					String overrideLength = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_LENGTH);
					String mandatoryField = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_MANDATORY_FIELD);
					String replaceKey = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_REPLACE_KEY);
					String replaceToValue = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_REPLACE_TO_VALUE);
					String decimal = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_DECIMAL);
					String regExCheck = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_REGEX_CHECK);
					String convertToNumberic = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_CONVERT_TO_NUMBERIC);
					String convertToDateBy = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_CONVERT_STRING_TO_DATE);
					String correctNumberFormat = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_CORRECT_NUMBER_FORMAT);
					String replaceValueBeforeChkLength = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_REPLACE_CALUE_BEFORE_CHK_LENGTH);
					String roundHaftUp = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_ROUND_HAFT_UP);
					String alphaNumericCheck = getAttributeValue(attribs, CommonExcelConversionDTO.ATTR_ALPHANUMERIC_CHECK);
					
					//Assign to Hash
					HashMap<String,String> mappingField = new HashMap<>();
					mappingField.put(CommonExcelConversionDTO.ATTR_VALUE, dbFieldName);
					mappingField.put(CommonExcelConversionDTO.ATTR_LABEL, displayLabel);
					mappingField.put(CommonExcelConversionDTO.ATTR_LENGTH, overrideLength);
					mappingField.put(CommonExcelConversionDTO.ATTR_MANDATORY_FIELD, mandatoryField);
					mappingField.put(CommonExcelConversionDTO.ATTR_XLS_DT_FORMAT, xlsDtFormat);
					mappingField.put(CommonExcelConversionDTO.ATTR_IF_DT_FORMAT, ifDtFormat);
					mappingField.put(CommonExcelConversionDTO.ATTR_REPLACE_KEY, replaceKey);
					mappingField.put(CommonExcelConversionDTO.ATTR_REPLACE_TO_VALUE, replaceToValue);
					mappingField.put(CommonExcelConversionDTO.ATTR_DECIMAL, decimal);
					mappingField.put(CommonExcelConversionDTO.ATTR_REGEX_CHECK, regExCheck);
					mappingField.put(CommonExcelConversionDTO.ATTR_CONVERT_TO_NUMBERIC, convertToNumberic);
					mappingField.put(CommonExcelConversionDTO.ATTR_CONVERT_STRING_TO_DATE, convertToDateBy);
					mappingField.put(CommonExcelConversionDTO.ATTR_CORRECT_NUMBER_FORMAT, correctNumberFormat);
					mappingField.put(CommonExcelConversionDTO.ATTR_REPLACE_CALUE_BEFORE_CHK_LENGTH, replaceValueBeforeChkLength);
					mappingField.put(CommonExcelConversionDTO.ATTR_ROUND_HAFT_UP, roundHaftUp);
					mappingField.put(CommonExcelConversionDTO.ATTR_ALPHANUMERIC_CHECK, alphaNumericCheck);
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
