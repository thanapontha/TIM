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

public class CBW00000CommonXMLLoader {
	protected String[] columnNames;
	
	public CBW00000CommonExcelConversionDTO loadXMLConfig(String pathXmlConfigFile, String xmlFileName) throws Exception {
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
                		CBW00000CommonExcelConversionDTO.TAG_EXCEL_CONVERSION_CONFIG);
                
                if (rootNode == null || rootNode.getLength() == 0) {
                	throw new Exception("Not found <" + CBW00000CommonExcelConversionDTO.TAG_EXCEL_CONVERSION_CONFIG + "> in config file");
                }
                else {
                	CBW00000CommonExcelConversionDTO excelConvVo = new CBW00000CommonExcelConversionDTO();
                	
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
	
	private void loadConfiguration(CBW00000CommonExcelConversionDTO excelConvVo, Node node) throws Exception {
		String nodeName = node.getNodeName();
		NamedNodeMap attribs = node.getAttributes();
		if (CBW00000CommonExcelConversionDTO.TAG_CONVERSION_INFO.equals(nodeName)) {
			String wrkShtIdx = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_WRKSHT_IDX);
			if (wrkShtIdx != null && wrkShtIdx.length() > 0) {
				String[] strWrkShtIdx = wrkShtIdx.split(",");
				Integer[] intWrkShtIdx = new Integer[strWrkShtIdx.length];
				for (int i = 0; i < strWrkShtIdx.length; i++) {
					intWrkShtIdx[i] = new Integer(Integer.parseInt(strWrkShtIdx[i]));
				}
				excelConvVo.setArrayWorkSheetIdx(intWrkShtIdx);
			}
			
			String wrkShtName = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_WRKSHT_NAME);
			if (wrkShtName != null && wrkShtName.length() > 0) {
				String[] strWrkShtName = wrkShtName.split(",");
				excelConvVo.setArrayWorkSheetName(strWrkShtName);
			}
		}else if (CBW00000CommonExcelConversionDTO.TAG_CHECK_HEADERS.equals(nodeName)) {
			excelConvVo.setCheckHeaders(new ArrayList());

			NodeList childNodes = node.getChildNodes();
        	for (int i = 0; i < childNodes.getLength(); i++) {
        		loadConfiguration(excelConvVo, childNodes.item(i));
        	}
			String startRow = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_START_ROW);
			String endRow = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_END_ROW);
			HashMap<String, Object> hConfig = new HashMap<String, Object>();
			hConfig.put(CBW00000CommonExcelConversionDTO.ATTR_START_ROW, new Integer(Integer.parseInt(startRow)));
			hConfig.put(CBW00000CommonExcelConversionDTO.ATTR_END_ROW, null!=endRow?new Integer(Integer.parseInt(endRow)):null);
			hConfig.put(CBW00000CommonExcelConversionDTO.TAG_CHECK_HEADERS, excelConvVo.getCheckHeaders());
			
			excelConvVo.setExcelCheckHeadersConfig(hConfig);
			
		}else if (CBW00000CommonExcelConversionDTO.TAG_CHECK_HEADER.equals(nodeName)) {
		
			String startRow = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_START_ROW);
			String endRow = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_END_ROW);
			String startCol = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_START_COL);
			String endCol = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_END_COL);
			HashMap<String, Object> hCheckConfig = new HashMap<String, Object>();
			hCheckConfig.put(CBW00000CommonExcelConversionDTO.ATTR_START_ROW, new Integer(Integer.parseInt(startRow)));
			hCheckConfig.put(CBW00000CommonExcelConversionDTO.ATTR_END_ROW, null!=endRow?new Integer(Integer.parseInt(endRow)):null);
			hCheckConfig.put(CBW00000CommonExcelConversionDTO.ATTR_START_COL, new Integer(Integer.parseInt(startCol)));
			hCheckConfig.put(CBW00000CommonExcelConversionDTO.ATTR_END_COL, null!=endCol?new Integer(Integer.parseInt(endCol)):null);
			hCheckConfig.put(CBW00000CommonExcelConversionDTO.TAG_CHECK_HEADER, getMappingInfo(node, false));
			
			excelConvVo.getCheckHeaders().add(hCheckConfig);
		}
		else if (CBW00000CommonExcelConversionDTO.TAG_CONVERSION_HEADER.equals(nodeName)) {
			String startRow = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_START_ROW);
			String endRow = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_END_ROW);
			String startCol = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_START_COL);
			String endCol = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_END_COL);
			HashMap<String, Object> hConfig = new HashMap<String, Object>();
			hConfig.put(CBW00000CommonExcelConversionDTO.ATTR_START_ROW, new Integer(Integer.parseInt(startRow)));
			hConfig.put(CBW00000CommonExcelConversionDTO.ATTR_END_ROW, null!=endRow?new Integer(Integer.parseInt(endRow)):null);
			hConfig.put(CBW00000CommonExcelConversionDTO.ATTR_START_COL, new Integer(Integer.parseInt(startCol)));
			hConfig.put(CBW00000CommonExcelConversionDTO.ATTR_END_COL, null!=endCol?new Integer(Integer.parseInt(endCol)):null);
			hConfig.put(CBW00000CommonExcelConversionDTO.TAG_CONVERSION_HEADER, getMappingInfo(node, false));
			
			excelConvVo.setExcelHeaderConfig(hConfig);
		}
		else if (CBW00000CommonExcelConversionDTO.TAG_CONVERSION_DETAIL.equals(nodeName)) {
			String startRow = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_START_ROW);
			String endRow = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_END_ROW);
			String startCol = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_START_COL);
			String endCol = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_END_COL);
			HashMap<String, Object> dConfig = new HashMap<String, Object>();
			
			columnNames = new String[endCol==null?0:Integer.parseInt(endCol)];
			dConfig.put(CBW00000CommonExcelConversionDTO.ATTR_START_ROW, null!=startRow?new Integer(Integer.parseInt(startRow)):null);
			dConfig.put(CBW00000CommonExcelConversionDTO.ATTR_END_ROW, null!=endRow?new Integer(Integer.parseInt(endRow)):null);
			dConfig.put(CBW00000CommonExcelConversionDTO.ATTR_START_COL, null!=startCol?new Integer(Integer.parseInt(startCol)):null);
			dConfig.put(CBW00000CommonExcelConversionDTO.ATTR_END_COL, null!=endCol?new Integer(Integer.parseInt(endCol)):null);
			dConfig.put(CBW00000CommonExcelConversionDTO.TAG_CONVERSION_DETAIL, getMappingInfo(node, true));
			

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
				if (CBW00000CommonExcelConversionDTO.TAG_FIELD_MAP.equals(nodeName)) {
					//Get Excel Field Name and DB Field Name Attributes
					NamedNodeMap attribs = curNode.getAttributes();
					String excelFieldName = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_NAME);
					String dbFieldName = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_VALUE);
					String displayLabel = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_LABEL);
					String xlsDtFormat = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_XLS_DT_FORMAT);
					String ifDtFormat = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_IF_DT_FORMAT);
					String overrideLength = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_LENGTH);
					String mandatoryField = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_MANDATORY_FIELD);
					String replaceKey = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_REPLACE_KEY);
					String replaceToValue = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_REPLACE_TO_VALUE);
					String decimal = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_DECIMAL);
					String regExCheck = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_REGEX_CHECK);
					String convertToNumberic = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_CONVERT_TO_NUMBERIC);
					String convertToDateBy = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_CONVERT_STRING_TO_DATE);
					String correctNumberFormat = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_CORRECT_NUMBER_FORMAT);
					String replaceValueBeforeChkLength = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_REPLACE_CALUE_BEFORE_CHK_LENGTH);
					String roundHaftUp = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_ROUND_HAFT_UP);
					String alphaNumericCheck = getAttributeValue(attribs, CBW00000CommonExcelConversionDTO.ATTR_ALPHANUMERIC_CHECK);
					
					//Assign to Hash
					HashMap<String,String> mappingField = new HashMap<String,String>();
					mappingField.put(CBW00000CommonExcelConversionDTO.ATTR_VALUE, dbFieldName);
					mappingField.put(CBW00000CommonExcelConversionDTO.ATTR_LABEL, displayLabel);
					mappingField.put(CBW00000CommonExcelConversionDTO.ATTR_LENGTH, overrideLength);
					mappingField.put(CBW00000CommonExcelConversionDTO.ATTR_MANDATORY_FIELD, mandatoryField);
					mappingField.put(CBW00000CommonExcelConversionDTO.ATTR_XLS_DT_FORMAT, xlsDtFormat);
					mappingField.put(CBW00000CommonExcelConversionDTO.ATTR_IF_DT_FORMAT, ifDtFormat);
					mappingField.put(CBW00000CommonExcelConversionDTO.ATTR_REPLACE_KEY, replaceKey);
					mappingField.put(CBW00000CommonExcelConversionDTO.ATTR_REPLACE_TO_VALUE, replaceToValue);
					mappingField.put(CBW00000CommonExcelConversionDTO.ATTR_DECIMAL, decimal);
					mappingField.put(CBW00000CommonExcelConversionDTO.ATTR_REGEX_CHECK, regExCheck);
					mappingField.put(CBW00000CommonExcelConversionDTO.ATTR_CONVERT_TO_NUMBERIC, convertToNumberic);
					mappingField.put(CBW00000CommonExcelConversionDTO.ATTR_CONVERT_STRING_TO_DATE, convertToDateBy);
					mappingField.put(CBW00000CommonExcelConversionDTO.ATTR_CORRECT_NUMBER_FORMAT, correctNumberFormat);
					mappingField.put(CBW00000CommonExcelConversionDTO.ATTR_REPLACE_CALUE_BEFORE_CHK_LENGTH, replaceValueBeforeChkLength);
					mappingField.put(CBW00000CommonExcelConversionDTO.ATTR_ROUND_HAFT_UP, roundHaftUp);
					mappingField.put(CBW00000CommonExcelConversionDTO.ATTR_ALPHANUMERIC_CHECK, alphaNumericCheck);
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
