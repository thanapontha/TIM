/******************************************************
 * Program History
 * 
 * Project Name             :  TRIM Kaizen 6.0
 * Client Name              :  TMAP-EM
 * Package Name             :  th.co.toyota.tri.common.batch.upload
 * Program ID               :  CBW00000CommonExcelConversion.java
 * Program Description      :  Common Excel conversion to ASCII file Common Class
 * Environment              :  Java 1.5
 * Author                   :  FSBT) Thanapon
 * Version                  :  1.0
 * Creation Date            :  Aug 24, 2015 
 *
 * Modification History     :
 * Version    Date        Person Name  Chng Req No  Remarks
 * 0.1       24/08/2015   Thanapon                  copy from CTRI0001CommonExcelConversion.java
 *
 * Copyright(C) 2010-TOYOTA Motor Asia Pacific. All Rights Reserved.             
 ********************************************************/
package th.co.toyota.bw0.api.common.upload;

import java.util.List;




public class CBW00000CommonExcelConversion {	
//	private static final int EXIT_SUCCESS = 0;
//	private static final int EXIT_ERROR = 1;
//	private static final int EXIT_WARNING = 2;
//
//	protected static final String DEFAULT_USER_NAME = "SYSTEM";
//	protected static final String DEFAULT_XLS_DT_FORMAT = "dd/MM/yyyy";
//	protected static final String DEFAULT_IF_DT_FORMAT = "yyyyMMdd";
//	protected static final String DEFAULT_PAD = " ";
//	protected String DEFAULT_SEPARATOR = "|";
//
//	protected String pathUploadFile;
//	protected String pathASCIIFile;
//	protected String pathArchiveFile;
//	
//	protected String processName = "Upload and Validation.";
//	
//	protected int retStatus;
//	protected String projectId;
//	protected String sysConfigCd;
//	protected String moduleId;
//	protected String functionId;
//	protected String functionName;
//	protected String fileId;
//	protected String fileName;
//	protected String companyAbbr;
//	protected String userId;
//	protected String aplId;
//	protected String uploadFileId;
//	
//	protected String configXmlFile;
//	protected String stagingTable;
//	
//	protected String dataOwner;
//	protected String asciiFileName;
//	protected String parameterId;
//	protected List paramList;
//	protected HashMap tableMetaData;
//
//	
//	private CSTD0068ConfigurationResolver resolver;
//	private CSTD0069MessageResolver msgResolver;
//	private CTRI0023DBAdapter adapter;
//
//	public CBW00000CommonExcelConversionDTO xlsConvVo;
//	private CTRI9001CommonExcelConversionDAO dao;
//	protected CTRI0032CommonBatchLogger loggerDB;
//
//	protected CBW00000CommonXMLLoader xmlLoader = new CBW00000CommonXMLLoader();
//	
//	
//	protected void getArgumentValues(String[] args) throws Exception {
//		if (args.length == 6) {
//			moduleId = args[0];
//			functionId = args[1];
//			fileId = args[2];
//			fileName = args[3];
//			if(fileName != null && fileName.length() > 0){
////				fileName = fileName.replaceAll(CTRI0029CommonBatchConstants.REPLACE_SPACE_WITH_KEY, " ");
////				fileName = fileName.replaceAll(CTRI0029CommonBatchConstants.REPLACE_SINGLE_QUOTE_WITH_KEY, "\\'");
////				fileName = fileName.replaceAll(CTRI0029CommonBatchConstants.REPLACE_BRACKET_START_WITH_KEY, "\\(");
////				fileName = fileName.replaceAll(CTRI0029CommonBatchConstants.REPLACE_BRACKET_END_WITH_KEY, "\\)");
//			}
//			userId = args[4];
//			aplId = args[5];
////			loggerDB = new CTRI0032CommonBatchLogger(aplId, moduleId, functionId, userId);
//		} else {
//			for(int i=0;i<args.length;i++){
//				System.out.println(args[i]);
//			}
//			throw new Exception("Incorrect number of arguments: Minimum is 6 arguments."+ "Current length is "+args.length);
//		}
//	}
//    
//	protected void initialize() throws Exception {
//		projectId = "TRI";
//		ArrayList<String> msgArgs = new ArrayList<String>();
//		msgArgs.add(this.processName);
//
//		loggerDB.logStart(msgArgs, this.getClass());
//		
//		adapter = CTRI0023DBAdapter.getInstance();
//		msgResolver = new CSTD0069MessageResolver();		
//		resolver = new CSTD0068ConfigurationResolver(CTRI0029CommonBatchConstants.xs_PropsFile);
//		
//		//Get default upload path ..shared/<COMPANY>/data/upload
//		if(this.pathUploadFile == null){
//			this.pathUploadFile =  getOutputDirectory(getUploadPathFromTBSystem()) + File.separator;
//		}
//
//		//Set Module ID
//		dao = new CTRI9001CommonExcelConversionDAO();
//		ArrayList moduleInfo = dao.getModuleInfo(functionId);
//		if (moduleInfo == null || moduleInfo.size() == 0) {
//			throw new Exception("Not found Module information in Module Master for Function ID = " + functionId);
//		}
//		else {
//			Object[] module = (Object[])moduleInfo.get(0);
//			functionName = (String)module[3];
//		}
//		
//		//Load xml mapping configuration
//		xlsConvVo = xmlLoader.loadXMLConfig(configXmlFile);
//		
//		//Get table metadata
//		tableMetaData = dao.getTableMeataData(stagingTable);
//	}
//	
//	//if some report want to check File type. Need override this method and return true
//	public boolean checkFileSize(){
//		return false;
//	}
//
//	protected int doProcessing() throws Exception {
//		InputStream is = null;
//		BufferedInputStream bis = null;
//		List sheetDataList = null;
//		
//		BufferedReader br= null;
//		FileReader csvFile = null;
//		String line = "";
//		String cvsSplitBy = ",";
//		
//		ZipFile zipFile = null;
//		InputStreamReader isr = null;
//		File fileUpload = null;
//
//		int result = EXIT_SUCCESS;
//		try {
//			CTRI0034PerformanceMonitoring.keepStartTime();
//			
//			if(isZipFileExtension(fileName)){
//				//Check File Extension
//				if (!validateFileExtension(fileName)) {
//					throw CSTD0054ErrorUtil.generateError("MSTD7013BERR", new String[]{fileName, "zip", "", ""}, 1);
//				}			
//				String fullXlsFileName = this.pathUploadFile + fileName;				
//				
//			    zipFile = new ZipFile(fullXlsFileName);
//
//			    Enumeration<? extends ZipEntry> entries = zipFile.entries();
//			    int fileCsvCnt = 0;
//			    int filesCntInZip = 0;
//			    while(entries.hasMoreElements()){
//			        ZipEntry entry = entries.nextElement();
//			        String entryName = entry.getName()==null?"":entry.getName();
//			        if (isCSVFileExtension(entryName)) {
//			        	fileCsvCnt++;
//					}
//			        filesCntInZip++;
//			    }
//			    if(fileCsvCnt == 0){
//			    	throw CSTD0054ErrorUtil.generateError("MTRI0000BERR", new String[]{"Not found csv file in zip file ("+fileName+")"}, 1);
//			    }else if (filesCntInZip > 1){
//			    	throw CSTD0054ErrorUtil.generateError("MTRI0000BERR", new String[]{"Found file in zip file ("+fileName+") more than one file."}, 1);
//			    }
//			    entries = zipFile.entries();
//			    while(entries.hasMoreElements()){			    	
//			    	ZipEntry entry = entries.nextElement();
//			    	is = zipFile.getInputStream(entry);
//			    	
//			    	if(this.checkFileSize()){
//			    		int size = is.available();
//				    	BigDecimal fileSizeMB = new BigDecimal(size/(1024.0*1024.0));
//				    	fileSizeMB = fileSizeMB.setScale(2, BigDecimal.ROUND_DOWN);
//				    	BigDecimal maxFizeSize = getMaxFileSizeForUpload("MXZCSVSIZE");
//				    	if(fileSizeMB.compareTo(maxFizeSize) > 0){
//				    		throw CSTD0054ErrorUtil.generateError("MTRI0000BERR", new String[]{"Found csv file size "+fileSizeMB+"MB in zip file ("+fileName+") more than "+maxFizeSize.toString()+" MB."}, 1);
//				    	}
//			    	}
//			    	isr = new InputStreamReader(is);
//			    	br = new BufferedReader(isr);
//					int headerCheckStartRow = 0;
//					int headerCheckEndRow = 0;				
//					int detailStartRow = 0;
//					int detailStartCol = 0;
//					int detailEndCol = 0;
//					
//					if (xlsConvVo != null) {
//						headerCheckStartRow = xlsConvVo.getCheckHeadersStartRow().intValue() - 1;
//						headerCheckEndRow = xlsConvVo.getCheckHeadersEndRow().intValue() - 1;
//					}
//
//					if (xlsConvVo.getExcelDetailConfig() != null) {
//						detailStartRow = xlsConvVo.getDetailStartRow().intValue() - 1;
//						detailStartCol = xlsConvVo.getDetailStartCol().intValue() - 1;
//						detailEndCol = xlsConvVo.getDetailEndCol().intValue();
//					}
//					String[] headerData = null;
//					int rowIndex = 0;
//					while ((line = br.readLine()) != null) {
//					    // use comma as separator
//						line =line.replaceAll(",\"", ",");
//						line =line.replaceAll("\",", ",");
//						
//						String[] data = line.split(cvsSplitBy);
//						if(rowIndex >= headerCheckStartRow && rowIndex <= headerCheckEndRow){
//							headerData = data;
//							checkHeaderTemplateFromCSV(headerData, rowIndex);	
//							sheetDataList = new ArrayList();
//						}else if(rowIndex >= detailStartRow){
//							List dataList = generateDataFromCSV(data, rowIndex, detailStartCol, detailEndCol);
//							sheetDataList.add(dataList);
//						}
//						rowIndex++;
//					}
//					if (rowIndex == 0 || rowIndex-1 < headerCheckStartRow) {
//			        	throw CSTD0054ErrorUtil.generateError("MSTD0043AERR", "header column in csv file.");
//					}else if (rowIndex-1 < detailStartRow) {
//						sheetDataList = null;
//					}
//			    }		    
//			    
//			}else if(isCSVFileExtension(fileName)){
//				//Check File Extension
//				if (!validateFileExtension(fileName)) {
//					throw CSTD0054ErrorUtil.generateError("MSTD7013BERR", new String[]{fileName, "csv", "", ""}, 1);
//				}			
//				String fullXlsFileName = this.pathUploadFile + fileName;
//				
//				if(this.checkFileSize()){
//					is = new FileInputStream(fullXlsFileName);
//		    		int size = is.available();
//			    	BigDecimal fileSizeMB = new BigDecimal(size/(1024.0*1024.0));
//			    	fileSizeMB = fileSizeMB.setScale(2, BigDecimal.ROUND_DOWN);
//			    	BigDecimal maxFizeSize = getMaxFileSizeForUpload("MXCSVSIZE");
//			    	if(fileSizeMB.compareTo(maxFizeSize) > 0){
//			    		throw CSTD0054ErrorUtil.generateError("MTRI0000BERR", new String[]{"Found csv file size "+fileSizeMB+"MB ("+fileName+") more than "+maxFizeSize.toString()+" MB."}, 1);
//			    	}
//		    	}
//				
//				csvFile = new FileReader(fullXlsFileName);
//				br = new BufferedReader(csvFile);
//				int headerCheckStartRow = 0;
//				int headerCheckEndRow = 0;				
//				int detailStartRow = 0;
//				int detailStartCol = 0;
//				int detailEndCol = 0;
//				
//				if (xlsConvVo != null) {
//					headerCheckStartRow = xlsConvVo.getCheckHeadersStartRow().intValue() - 1;
//					headerCheckEndRow = xlsConvVo.getCheckHeadersEndRow().intValue() - 1;
//				}
//
//				if (xlsConvVo.getExcelDetailConfig() != null) {
//					detailStartRow = xlsConvVo.getDetailStartRow().intValue() - 1;
//					detailStartCol = xlsConvVo.getDetailStartCol().intValue() - 1;
//					detailEndCol = xlsConvVo.getDetailEndCol().intValue() ;
//				}
//				String[] headerData = null;
//				int rowIndex = 0;
//				while ((line = br.readLine()) != null) {
//				    // use comma as separator
//					line =line.replaceAll(",\"", ",");
//					line =line.replaceAll("\",", ",");
//					String[] data = line.split(cvsSplitBy);
//					if(rowIndex >= headerCheckStartRow && rowIndex <= headerCheckEndRow){
//						headerData = data;
//						checkHeaderTemplateFromCSV(headerData, rowIndex);	
//						sheetDataList = new ArrayList();
//					}else if(rowIndex >= detailStartRow){
//						List dataList = generateDataFromCSV(data, rowIndex, detailStartCol, detailEndCol);
//						sheetDataList.add(dataList);
//					}
//					rowIndex++;
//				}
//				if (rowIndex == 0 || rowIndex-1 < headerCheckStartRow) {
//		        	throw CSTD0054ErrorUtil.generateError("MSTD0043AERR", "header column in csv file.");
//				}else if (rowIndex-1 < detailStartRow) {
//					sheetDataList = null;
//				}
//				
//			}else{
//				//Check File Extension
//				if (!validateFileExtension(fileName)) {
//					throw CSTD0054ErrorUtil.generateError("MSTD7013BERR", new String[]{fileName, "excel", "", ""}, 1);
//				}			
//				String fullXlsFileName = this.pathUploadFile + fileName;
//				
//		        fileUpload = new File(fullXlsFileName);
//	            is = new FileInputStream( fileUpload );
//	            
//	            if(this.checkFileSize()){
//		    		int size = is.available();
//			    	BigDecimal fileSizeMB = new BigDecimal(size/(1024.0*1024.0));
//			    	fileSizeMB = fileSizeMB.setScale(2, BigDecimal.ROUND_DOWN);
//			    	BigDecimal maxFizeSize = getMaxFileSizeForUpload("MXXLSXSIZE");
//			    	if(fileSizeMB.compareTo(maxFizeSize) > 0){
//			    		throw CSTD0054ErrorUtil.generateError("MTRI0000BERR", new String[]{"Found excel file size "+fileSizeMB+"MB ("+fileName+") more than "+maxFizeSize.toString()+" MB."}, 1);
//			    	}
//		    	}
//	            Workbook wb = WorkbookFactory.create(is);
//	            CTRI0034PerformanceMonitoring.showResponse("---->> after create Workbook");
//	            
//	            //MTRI3028AERR: Invalid {0} excel file. Only 1 sheet is allowed for upload.
//	            if(wb!=null && wb.getNumberOfSheets() >1 ){
//	            	throw CSTD0054ErrorUtil.generateError("MTRI3028AERR", fileName);
//	            }
//	            
//	            List<Sheet> workingSheet = this.getWokringSheet(wb);
//	            
//	            this.checkHeaderTemplate(workingSheet);
//				
//				//Convert Data
//	            sheetDataList = generateData(wb);
//			}
//			
//			if (null == sheetDataList || sheetDataList.isEmpty()) {
//				//Fixed org_file_name
//				String orgFileName = fileName;				
//				throw CSTD0054ErrorUtil.generateError("MSTD1049AERR", new String[]{orgFileName,"It does not contain data to upload"}, 1);
//			} else {
//			    List tmp = (List)sheetDataList.get(0);
//			    if (null == tmp || tmp.isEmpty()) {
//			    	//Fixed org_file_name
//					String orgFileName = fileName;					
//					throw CSTD0054ErrorUtil.generateError("MSTD1049AERR", new String[]{orgFileName,"It does not contain data to upload"}, 1);
//			    }
//			}
//			
//            //Generate Ascii File
//			asciiFileName = genAsciiFileName();
//			if(this.pathASCIIFile == null){
//				this.pathASCIIFile = getOutputDirectory(resolver.getProperty("FS_INPUT_PATH")) + File.separator + projectId + File.separator;
//			}
//			String fullAsciiFileName = this.pathASCIIFile + asciiFileName;
//			CTRI0034PerformanceMonitoring.showResponse("---->> after read data from Workbook and begin generate ASCII file");
//            createAsciiFile(fullAsciiFileName, sheetDataList);
//            CTRI0034PerformanceMonitoring.showResponse("---->> after generated ASCII file");
//		}catch(Exception e){
//			throw e;
//		}catch (Throwable ex){
//			 if (!(ex instanceof ThreadDeath))
//			 {
//				 if(ex.getMessage() != null && ex.getMessage().indexOf("OutOfMemoryError") > 0){
//					 ex.printStackTrace(System.err);
//					 throw new Exception("java.lang.OutOfMemoryError");
//				 }else{
//					 throw new Exception("ThreadDeath");
//				 }
//			 }else{
//				 throw new Exception("ThreadDeath");
//			 }
//		}finally {
//			if(fileUpload!=null){
//				fileUpload = null;
//			}
//			if (is != null) {
//				is.close();
//				is = null;
//			}
//			if (isr != null) {
//				isr.close();
//				isr = null;
//			}
//			if (bis != null) {
//				bis.close();
//				bis = null;
//			}
//			if(br != null){
//				br.close();
//				br = null;
//			}
//			if(csvFile != null){
//				csvFile.close();
//				csvFile = null;
//			}
//			if(zipFile != null){
//				zipFile.close();
//				zipFile = null;
//			}
//		}		
//		return result;
//	}
//
//	private String genAsciiFileName() {
//		return fileId + "_" + 
//			   new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + 
//			   "_D";
//	}
//
//	//Remark by Jimmy: private void createAsciiFile(String fullAsciiFileName, List sheetDataList) 7.02.2014. Incident TRIM:20140207-13: For Passing UserID create_by
//	//Changed tobe protected so can be override by subclass 
//	protected void createAsciiFile(String fullAsciiFileName, List sheetDataList)
//	throws IOException, CSTD0056CommonException {
//		File file = null;
//		OutputStream os = null;
//		PrintWriter pw = null;
//		boolean isErrorOccur = false;
//		
//		try {
//			//Write Ascii File
//			file = new File(fullAsciiFileName);
//			os = new FileOutputStream(file);
//			pw = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
//			List lineDataList = (List)sheetDataList.get(0);
//			StringBuffer lineData = (StringBuffer)lineDataList.get(0);
//			pw.println(createStdHeader(lineData.length()));
//			int recNum = 0;
//			//Sheet Level
//			for (int i = 0; i < sheetDataList.size(); i++) {
//				lineDataList = (List)sheetDataList.get(i);
//				//Row Level
//				for (int j = 0; j < lineDataList.size(); j++) {
//					lineData = (StringBuffer)lineDataList.get(j);
//					pw.println(lineData.toString());
//					recNum ++;
//				}
//			}
//			pw.println(createStdTailer(recNum));
//			
//			pw.flush();
//		} 
//		finally {
//			if (pw != null) {
//				pw.close();
//				pw = null;
//			}
//			if (os != null) {
//				os.close();
//				os = null;
//			}
//			if (isErrorOccur && file.exists()) {
//				file.delete();
//			}
//		}
//	}
//
//	protected List<Sheet> getWokringSheet(Workbook workbook) throws Exception {
//		//Generate Data
//		List<Sheet> sheetList = new ArrayList<Sheet>();
//		Integer[] intWrkShtIdx = xlsConvVo.getArrayWorkSheetIdx();
//		String[] strWrkShtName = xlsConvVo.getArrayWorkSheetName();
//		if (intWrkShtIdx != null && intWrkShtIdx.length > 0) {
//			//Higher priority than Work Sheet Name
//			for (int i = 0; i < intWrkShtIdx.length; i++) {
//				Sheet curSheet = workbook.getSheetAt(intWrkShtIdx[i].intValue());
//				sheetList.add(curSheet);
//			}
//		}
//		else if (strWrkShtName != null && strWrkShtName.length > 0) {
//			for (int i = 0; i < strWrkShtName.length; i++) {
//				Sheet curSheet = workbook.getSheet(strWrkShtName[i]);
//				sheetList.add(curSheet);
//			}
//		}
//		
//		return sheetList;
//	}
//	
//	protected void checkHeaderTemplate(List<Sheet> workingSheet) throws Exception {
//
//		int headerCheckStartRow = xlsConvVo.getCheckHeadersStartRow().intValue() - 1;
//		int headerCheckEndRow = xlsConvVo.getCheckHeadersEndRow().intValue() - 1;
//		List checkHeaderLs = xlsConvVo.getCheckHeaders();
//		if(checkHeaderLs != null && checkHeaderLs.size() > 0){
//			for(int i=0; i < workingSheet.size(); i++){
//				Sheet curSheet = workingSheet.get(i);
//				int lastRow = curSheet.getLastRowNum();
//				if (lastRow == 0 || lastRow < headerCheckEndRow) {
//		        	throw CSTD0054ErrorUtil.generateError("MTRI3027AERR", "");
//				}
//				for(int ri=0; ri<lastRow; ri++){
//					if(ri > headerCheckEndRow){
//						break;
//					}
//					
//					for(int j=0; j<checkHeaderLs.size(); j++){
//						
//						int headerCheckRow = xlsConvVo.getCheckHeaderStartRow(j).intValue() - 1;
//						int headerStartCol = xlsConvVo.getCheckHeaderStartCol(j).intValue() - 1;
//						int headerEndCol = xlsConvVo.getCheckHeaderEndCol(j).intValue() - 1;
//						if(ri==headerCheckRow){
//							HashMap obj = (HashMap)checkHeaderLs.get(j);
//							if(obj != null){
//								HashMap headerNameCheck = (HashMap)obj.get(CTRI9001CommonExcelConversionDTO.TAG_CHECK_HEADER);
//								Row headerRow = curSheet.getRow(ri);
//								if (headerRow == null) {
//						        	throw CSTD0054ErrorUtil.generateError("MTRI3027AERR","");
//								}
//								
//								Iterator<Object> iter = headerNameCheck.entrySet().iterator();
//								while(iter.hasNext()){
//									Map.Entry<Object,Object> entry = (Map.Entry<Object,Object>) iter.next();
//									String rowKey = (String)entry.getKey();
//									int rowKeyIdx = Integer.parseInt(rowKey)-1;
//									HashMap mapInfo =  (HashMap)entry.getValue();
//									String value = (String)mapInfo.get(CTRI9001CommonExcelConversionDTO.ATTR_VALUE);
//									String cellValue = "";
//									for (int k = headerStartCol; k <= headerEndCol; k++) {
//										Cell colNameCell = headerRow.getCell((short)k);
//										if (null == colNameCell) {
//								        	throw CSTD0054ErrorUtil.generateError("MTRI3027AERR", "");
//										}
//										
//										if( colNameCell != null && 
//												(colNameCell.getCellType() == Cell.CELL_TYPE_STRING)){						
//												 cellValue = colNameCell.getStringCellValue();
//												 if(cellValue != null){
//													 cellValue = cellValue.trim();
//												 }
//											}
//											else if(colNameCell != null && 
//													(colNameCell.getCellType() == Cell.CELL_TYPE_NUMERIC)){
//													 cellValue = Double.toString(colNameCell.getNumericCellValue());
//											} 
//											else if(colNameCell == null || 
//													colNameCell.getCellType() == Cell.CELL_TYPE_BLANK){
//													cellValue = "";
//											}							
//										
//										if(k == rowKeyIdx){
//											if(!CTRI0030StringUtil.evaluateStringNull(value).trim().equals(CTRI0030StringUtil.evaluateStringNull(cellValue).trim())){
//									        	throw CSTD0054ErrorUtil.generateError("MTRI3027AERR", "");
//											}
//											break;
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//	}
//	
//	protected void checkHeaderTemplateFromCSV(String[] headerData, int rowIndex) throws Exception {
//		List checkHeaderLs = xlsConvVo.getCheckHeaders();
//		if(checkHeaderLs != null && checkHeaderLs.size() > 0){
//					
//			for(int j=0; j<checkHeaderLs.size(); j++){				
//				int headerCheckRow = xlsConvVo.getCheckHeaderStartRow(j).intValue() - 1;
//				int headerStartCol = xlsConvVo.getCheckHeaderStartCol(j).intValue() - 1;
//				int headerEndCol = xlsConvVo.getCheckHeaderEndCol(j).intValue() - 1;
//				if(rowIndex == headerCheckRow){
//					HashMap obj = (HashMap)checkHeaderLs.get(j);
//					if(obj != null){
//						HashMap headerNameCheck = (HashMap)obj.get(CTRI9001CommonExcelConversionDTO.TAG_CHECK_HEADER);
//
//						
//						Iterator<Object> iter = headerNameCheck.entrySet().iterator();
//						while(iter.hasNext()){
//							Map.Entry<Object,Object> entry = (Map.Entry<Object,Object>) iter.next();
//							String rowKey = (String)entry.getKey();
//							int rowKeyIdx = Integer.parseInt(rowKey)-1;
//							HashMap mapInfo =  (HashMap)entry.getValue();
//							String value = (String)mapInfo.get(CTRI9001CommonExcelConversionDTO.ATTR_VALUE);
//							for (int k = headerStartCol; k <= headerEndCol; k++) {
//								String cellValue = headerData[k];
//								if(k == rowKeyIdx){
//									if(!CTRI0030StringUtil.evaluateStringNull(value).trim().equals(CTRI0030StringUtil.evaluateStringNull(cellValue).trim())){
//										throw CSTD0054ErrorUtil.generateError("MSTD0043AERR", "header column in csv file.");
//									}
//									break;
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//	}
//	
//	//Begin Generate Ascii Data
//	protected List generateData(Workbook workbook) throws Exception {
//		//Generate Data
//		List<Object> sheetList = new ArrayList<Object>();
//		Integer[] intWrkShtIdx = xlsConvVo.getArrayWorkSheetIdx();
//		String[] strWrkShtName = xlsConvVo.getArrayWorkSheetName();
//		if (intWrkShtIdx != null && intWrkShtIdx.length > 0) {
//			//Higher priority than Work Sheet Name
//			for (int i = 0; i < intWrkShtIdx.length; i++) {
//				Sheet curSheet = workbook.getSheetAt(intWrkShtIdx[i].intValue());
//				sheetList.add(generateData(curSheet));
//			}
//		}
//		else if (strWrkShtName != null && strWrkShtName.length > 0) {
//			for (int i = 0; i < strWrkShtName.length; i++) {
//				Sheet curSheet = workbook.getSheet(strWrkShtName[i]);
//				sheetList.add(generateData(curSheet));
//			}
//		}
//		
//		return sheetList;
//	}
//	
//	//Begin Generate Ascii Data
//	private List generateDataFromCSV(String[] data, int currentRowIndex, 
//											int detailStartCol, int detailEndCol) throws Exception {
//		List dataList = new ArrayList();
//		StringBuffer lineData = new StringBuffer();
//		doSpecifyPreProcess(null);
//		
//		StringBuffer dData = generateLineDataFromCSV(currentRowIndex, detailStartCol, detailEndCol, data);
//
//		//Additional data
//		StringBuffer additionalData = generateAdditionalData();
//		if (additionalData != null && additionalData.length() > 0) {
//			lineData.append(additionalData);
//			lineData.append(DEFAULT_SEPARATOR);
//		}
//		
//		if (dData != null && dData.length() > 0) {
//			lineData.append(dData).append(DEFAULT_SEPARATOR);
//		}
//		
//        doSpecifyPostProcess(null);
//        dataList.add(lineData);
//		return dataList;
//	}
//	
//	private List generateData(Sheet curSheet) throws Exception {
//		List dataList = null;
//		StringBuffer hData = null;
//		
//		doSpecifyPreProcess(curSheet);
//		
//		if (xlsConvVo.getExcelHeaderConfig() != null) {
//			int headerStartRow = xlsConvVo.getHeaderStartRow().intValue() - 1;
//			int headerStartCol = xlsConvVo.getHeaderStartCol().intValue() - 1;
//			int rowNum = curSheet.getLastRowNum();// End Row
//            if (null != xlsConvVo.getHeaderEndRow()) {
//                rowNum = xlsConvVo.getHeaderEndRow().intValue() - 1;
//            }
//			if (rowNum == 0 || rowNum < headerStartRow) {
//	        	throw CSTD0054ErrorUtil.generateError("MTRI3027AERR", "");
//			}
//			
//			//Header used for only STD Movement function
//			//which always have only 1 record
//			List hDataList = (List)generateTextData(true, curSheet, headerStartRow, headerStartRow, headerStartCol);
//			if (null != hDataList && !hDataList.isEmpty()) {
//			    hData = (StringBuffer)hDataList.get(0);
//			}
//		}
//		
//		if (xlsConvVo.getExcelDetailConfig() != null) {
//			int detailStartRow = xlsConvVo.getDetailStartRow().intValue() - 1;
//			int detailStartCol = xlsConvVo.getDetailStartCol().intValue() - 1;
//			
//			int rowNum = curSheet.getLastRowNum();// End Row
//            if (null != xlsConvVo.getDetailEndRow()) {
//                rowNum = xlsConvVo.getDetailEndRow().intValue() - 1;
//            }
//			if (rowNum == 0 || rowNum < detailStartRow) {
//				return dataList;
//			}
//			
//			List dDataList = generateTextData(false, curSheet, detailStartRow, rowNum, detailStartCol);
//			
//			dataList = new ArrayList();
//			for (int j = 0; j < dDataList.size(); j++) {
//				StringBuffer lineData = new StringBuffer();
//				
//				//Additional data
//				StringBuffer additionalData = generateAdditionalData();
//				if (additionalData != null && additionalData.length() > 0) {
//					lineData.append(additionalData);
//					lineData.append(DEFAULT_SEPARATOR);
//				}
//				
//				StringBuffer dData = (StringBuffer)dDataList.get(j);
//				if (hData != null && hData.length() > 0) {
//					lineData.append(hData).append(DEFAULT_SEPARATOR);
//				}
//				
//				lineData.append(dData);
//				lineData.append(DEFAULT_SEPARATOR);	// Lieu added 04/02/2013 <CR No.32>
//				dataList.add(lineData);
//			}
//		}
//		
//        doSpecifyPostProcess(curSheet);
//		
//		return dataList;
//	}
//	
//	/**
//	 * Create standard header row
//	 */
//	private String createStdHeader(int recordLength) throws CSTD0056CommonException {
//		String header = "#H#";
//		header += projectId;
//		header += "#";
//		header += projectId;
//		header += "#";
//		header += asciiFileName;
//		header += "#";
//		header += fileId;
//		header += "#";
//		header += alignLength(String.valueOf(recordLength), CSTD0095ASCIIConstant.ALIGNMENT_RIGHT,
//                5, "0");
//		header += "#";
//		
//		return header;
//	}
//	
//	/**
//	 * Create standard tailer row
//	 * @param dataNum
//	 * @return
//	 * @throws CSTD0056CommonException
//	 */
//	private String createStdTailer(int dataNum) throws CSTD0056CommonException {
//		String tailer = "#T#";
//		tailer += alignLength(String.valueOf(dataNum + 2), CSTD0095ASCIIConstant.ALIGNMENT_RIGHT,
//                7, "0");
//		tailer += "#";
//		return tailer;
//	}
//	
//	protected void doSpecifyPostProcess(Sheet curSheet) throws Exception {
//    }
//
//    protected void doSpecifyPreProcess(Sheet curSheet) throws Exception {
//    }
//
//    protected StringBuffer generateAdditionalData() throws Exception {
//		return null;
//	}
//	
//    protected List<StringBuffer> generateTextData(boolean isHeader, Sheet curSheet, int startRow,
//									int endRow, int startCol) throws Exception {
//    	
//		List<StringBuffer> lineDataList = new ArrayList<StringBuffer>();
//		Row colNameRow = curSheet.getRow(startRow);
//		if (null == colNameRow) {
//			throw CSTD0054ErrorUtil.generateError("MTRI3027AERR", "");
//		}
//		for (int i = startRow; i <= endRow; i++) {
//			Row dataRow = curSheet.getRow(i);
//			int endCol;
//			
//			if(isHeader){
//				if (null == xlsConvVo.getHeaderEndCol()) {
//				    endCol = dataRow.getLastCellNum();
//				} else {
//					endCol = xlsConvVo.getHeaderEndCol().intValue();
//				}
//			}else{
//				if (null == xlsConvVo.getDetailEndCol()) {
//				    endCol = dataRow.getLastCellNum();
//				} else {
//					endCol = xlsConvVo.getDetailEndCol().intValue();
//				}
//			}
//			if (dataRow != null) {
//				int excelLineNo = i + 1;
//
//                StringBuffer lineData = generateLineData(isHeader, excelLineNo, startCol, endCol, colNameRow, dataRow);
//				if (lineData != null && lineData.length() > 0) {
//					lineDataList.add(lineData);
//				}
//			}
//		}
//		
//		return lineDataList;
//	}
//	
//	private StringBuffer generateLineData(boolean isHeader, int excelLineNo, 
//										  int startCol, int endCol,
//										  Row colNameRow, Row dataRow) throws Exception {
//		StringBuffer lineData = new StringBuffer();
//		boolean hasValue = false;
//		int notHaveValueCnt = 0;
//		for (int j = startCol; j < endCol; j++) {
//			Cell colNameCell = colNameRow.getCell((short)j);
//			if (null == colNameCell) {
//	        	throw CSTD0054ErrorUtil.generateError("MTRI3027AERR", "");
//			}
//			String colName = this.getXlsConvVo().getColumnNames()[j];			
//			
//			Cell dataCell = dataRow.getCell((short)j);
//			
//			if (null != dataCell) {
//				HashMap mapInfo;
//				
//				if (isHeader) {
//					HashMap hMapField = xlsConvVo.getHeaderMappingField();
//					mapInfo = (HashMap)hMapField.get(colName);
//				}
//				else {
//					HashMap dMapField = xlsConvVo.getDetailMappingField();
//					mapInfo = (HashMap)dMapField.get(colName);
//				}
//				
//				if (mapInfo == null) {
//					throw CSTD0054ErrorUtil.generateError("MTRI3027AERR", "");
//				}
//				
//			    String[] cellValues = readCell(mapInfo, dataCell,colName);
//			    String cellValue = cellValues[0];
//			    String cellValueOriginal = cellValues[1];
//			    if (cellValue.trim().length() > 0 || isHeader) {
//	                hasValue = true;
//	            }
//			    
//                if(isHeader == false && cellValue.trim().length() == 0){
//                	notHaveValueCnt++;
//                }
//	            
//	            if (lineData.length() > 0) {
//	                lineData.append(DEFAULT_SEPARATOR);
//	            }
//	            
//	            cellValue = doExtraCellValueProcess(isHeader, excelLineNo, cellValue, colName);
//	            
//	            cellValue = setAlignLenth(mapInfo, excelLineNo, cellValue, colName, cellValueOriginal);
//	            lineData.append(cellValue);
//			}
//		}
//		if(notHaveValueCnt==endCol){ //found some row data has empty
//        	throw CSTD0054ErrorUtil.generateError("MTRI6108BERR", new String[]{"Excel line no "+excelLineNo, "Please input value or delete this row."}, 1);
//		}
//		
//		return (hasValue)?lineData:null;
//	}
//	
//	private StringBuffer generateLineDataFromCSV(int currentRowIndex, 
//										  int startCol, int endCol,
//										  String[] dataRow) throws Exception {
//		StringBuffer lineData = new StringBuffer();
//		boolean hasValue = false;
//		int notHaveValueCnt = 0;
//		int dataLength = dataRow.length;	
//		for (int j = startCol; j < endCol; j++) {
//
//				String colName = this.getXlsConvVo().getColumnNames()[j];	
//
//				HashMap dMapField = xlsConvVo.getDetailMappingField();
//				HashMap mapInfo = (HashMap)dMapField.get(colName);
//
//				
//				if (mapInfo == null) {
//					throw CSTD0054ErrorUtil.generateError("MSTD0043AERR", "header column in csv file.");
//				}
//				
//			    String cellValue = "";
//				if(j <= dataLength-1){
//					cellValue = dataRow[j];
//				}
//			    String cellValueOriginal = cellValue;
//			    if (cellValue.trim().length() > 0) {
//	                hasValue = true;
//	            }
//			    
//                if(cellValue.trim().length() == 0){
//                	notHaveValueCnt++;
//                }
//	            
//	            if (lineData.length() > 0) {
//	                lineData.append(DEFAULT_SEPARATOR);
//	            }
//	            
//	            cellValue = doExtraCellValueProcess(false, currentRowIndex, cellValue, colName);
//	            
//	            cellValue = setAlignLenth(mapInfo, currentRowIndex, cellValue, colName, cellValueOriginal);
//	            lineData.append(cellValue);
//	            
//		}
//		if(notHaveValueCnt==endCol){ //found some row data has empty
//        	throw CSTD0054ErrorUtil.generateError("MTRI6108BERR", new String[]{"Excel line no "+currentRowIndex, "Please input value or delete this row."}, 1);
//		}
//		
//		return (hasValue)?lineData:null;
//	}
//	
//	protected String doExtraCellValueProcess(boolean isHeader, int excelLineNo, 
//											 String cellValue, String colName) {
//        return cellValue;
//    }
//
//    /**
//	 * Read Cell
//	 * @param dataCell
//	 * @return String
//	 */
//	protected String[] readCell(HashMap mapInfo, Cell dataCell, String columnName) {
//	    String cellValue = "";
//	    String cellValueOriginal = "";
//	    int cellType = dataCell.getCellType();
//	    
//	    if (cellType == Cell.CELL_TYPE_BLANK) {
//            cellValue = "";
//            cellValueOriginal = cellValue;
//        }
//	    else if (cellType == Cell.CELL_TYPE_STRING) {
//	    	boolean flg = true;
//            // Read the Numeric first
//            try {
//            	String tmpValue = dataCell.getStringCellValue();
//            	double decimalPoint = (Double.parseDouble(tmpValue) % 1);
//                if (decimalPoint == 0) {
//                    cellValue = tmpValue;
//                }
//                else {
//                	cellValue = String.valueOf(new BigDecimal("" + tmpValue));
//                }
//            } catch (Exception e) {
//                flg = false;
//            }
//            // If can not read Fomular Numeric Cell
//            // Try to read Fomular Text Cell
//            if (!flg) {
//                cellValue = dataCell.getStringCellValue();
//            }
//        	try{
//        		cellValueOriginal = cellValue;
//        		String convertToNumberic = "N";
//        		HashMap detailMap = this.xlsConvVo.getDetailMappingField();
//                if(detailMap != null){
//                	HashMap mapInfoDetail = (HashMap)detailMap.get(columnName);
//                	if(mapInfoDetail!=null){
//		        		convertToNumberic = (String)mapInfoDetail.get(CTRI9001CommonExcelConversionDTO.ATTR_CONVERT_TO_NUMBERIC);
//                	}
//                }
//                if(convertToNumberic!=null && convertToNumberic.equalsIgnoreCase("Y")){
//        			cellValue = this.convertNumbericValue(cellValue, columnName);
//        		}else{
//        			cellValue = dataCell.getStringCellValue();
//            		cellValueOriginal = cellValue;
//        		}
//        	}catch(Exception e){
//        		cellValue = dataCell.getStringCellValue();
//        		cellValueOriginal = cellValue;
//        	}
//	    }
//	    else if(DateUtil.isCellDateFormatted(dataCell)){
//	    	cellValue = CTRI0006FormatUtil.dateToString(dataCell.getDateCellValue(), DEFAULT_XLS_DT_FORMAT);
//	    	cellValueOriginal = cellValue;
//	    }
//        else if (cellType == Cell.CELL_TYPE_NUMERIC) {
//            double tmpValue = dataCell.getNumericCellValue();
//            double decimalPoint = (tmpValue % 1);
//            
//            if (decimalPoint == 0) {
//                cellValue = String.valueOf((long)tmpValue);
//            }
//            else {
//                cellValue = String.valueOf(new BigDecimal("" + tmpValue));
//            }
//            cellValueOriginal = cellValue;
//            cellValue = this.convertNumbericValue(cellValue, columnName);
//        }
//        else if (cellType == Cell.CELL_TYPE_FORMULA){
//            boolean flg = true;
//            // Read the Numeric first
//            try {
//            	double tmpValue = dataCell.getNumericCellValue();
//            	double decimalPoint = (tmpValue % 1);
//                if (decimalPoint == 0) {
//                    cellValue = String.valueOf((long)tmpValue);
//                }
//                else {
//                    //cellValue = String.valueOf(tmpValue);
//                	cellValue = String.valueOf(new BigDecimal("" + tmpValue));
//                }
//            } catch (Exception e) {
//                flg = false;
//            }
//            // If can not read Fomular Numeric Cell
//            // Try to read Fomular Text Cell
//            if (!flg) {
//                cellValue = dataCell.getStringCellValue();
//            }
//            cellValueOriginal = cellValue;
//            cellValue = this.convertNumbericValue(cellValue, columnName);
//        }
//        else {
//            cellValue = dataCell.getStringCellValue();
//            cellValueOriginal = cellValue;
//        }
//        return new String[]{cellValue.trim(),cellValueOriginal.trim()};
//    }
//    //End Generate Ascii Data
//	
//	private String convertNumbericValue(String cellValue, String columnName){
//		HashMap detailMap = this.xlsConvVo.getDetailMappingField();
//        if(detailMap != null){
//        	HashMap mapInfo = (HashMap)detailMap.get(columnName);
//        	if(mapInfo!=null){
//        		cellValue = this.checkMatchRegularAndReplaceValueBeforeGenByFormat(mapInfo, cellValue);
//        		
//            	String decimal = (String)mapInfo.get(CTRI9001CommonExcelConversionDTO.ATTR_DECIMAL);
//            	if(decimal!=null && !decimal.equalsIgnoreCase("NULL")){
//            		int intDecimal = Integer.parseInt(decimal);
//            		
//            		String roundHaftUp = "N";
//            		roundHaftUp = (String)mapInfo.get(CTRI9001CommonExcelConversionDTO.ATTR_ROUND_HAFT_UP);
//     
//                    if(roundHaftUp!=null && roundHaftUp.equalsIgnoreCase("Y")){
//                    	BigDecimal tmpcellValue = new BigDecimal(cellValue);
//                		tmpcellValue = tmpcellValue.setScale(intDecimal, BigDecimal.ROUND_HALF_UP);
//                		
//                		cellValue = tmpcellValue.toString();
//            		}else{
//            			int index = cellValue.lastIndexOf(".");
//            			String paddingzero = "Y";
//                		if(index > 0){
//                			String decimalString = cellValue.substring(index+1, cellValue.length());
//                			if(decimalString !=null && decimalString.length() >= intDecimal){
//                				paddingzero = "N";
//                			}
//                		}
//            			
//            			BigDecimal tmpcellValue = new BigDecimal(cellValue);
//                		if(paddingzero.equals("Y")){
//                			tmpcellValue = tmpcellValue.setScale(intDecimal);
//                		}
//                		cellValue = tmpcellValue.toString();
//            		}
//            	}
//        	}
//        }
//        return cellValue;
//	}
//	
//	private String checkMatchRegularAndReplaceValueBeforeGenByFormat(HashMap mapInfo, String cellValue){
//		String regExCheck = (String)mapInfo.get(CTRI9001CommonExcelConversionDTO.ATTR_REGEX_CHECK);
//		boolean replaceFlag = false;
//		if (regExCheck != null && regExCheck.length() > 0) {				
//			if (cellValue != null && cellValue.trim().length() > 0 && cellValue.matches(regExCheck)) {
//				replaceFlag = true;
//			}else{
//				replaceFlag = false;
//			}
//		}
//		if(replaceFlag){
//			String replaceKey = (String)mapInfo.get(CTRI9001CommonExcelConversionDTO.ATTR_REPLACE_KEY);
//			if (replaceKey != null && replaceKey.length() > 0) {
//				String replaceToValue = (String)mapInfo.get(CTRI9001CommonExcelConversionDTO.ATTR_REPLACE_TO_VALUE);
//				if (replaceToValue != null && replaceToValue.length() > -1) {
//					if(cellValue != null){
//						cellValue = cellValue.replaceAll(replaceKey, replaceToValue);
//					}
//				}
//			}	
//		}
//		return cellValue;
//	}
//	
//	private String checkAndReplaceValueBeforeChkLength(HashMap mapInfo, String cellValue){
//		String replaceValueBeforeChkLength = (String)mapInfo.get(CTRI9001CommonExcelConversionDTO.ATTR_REPLACE_CALUE_BEFORE_CHK_LENGTH);
//		if (replaceValueBeforeChkLength != null && replaceValueBeforeChkLength.equalsIgnoreCase("Y")) {
//			String replaceKey = (String)mapInfo.get(CTRI9001CommonExcelConversionDTO.ATTR_REPLACE_KEY);
//			if (replaceKey != null && replaceKey.length() > 0) {
//				String replaceToValue = (String)mapInfo.get(CTRI9001CommonExcelConversionDTO.ATTR_REPLACE_TO_VALUE);
//				if (replaceToValue != null && replaceToValue.length() > -1) {
//					if(cellValue != null){
//						cellValue = cellValue.replaceAll(replaceKey, replaceToValue);
//					}
//				}
//			}	
//		}
//		return cellValue;
//	}
//	
//	//Begin Set Alignment and Fixed Length
//	protected String setAlignLenth(HashMap mapInfo, int excelLineNo, String cellValue, 
//								   String columnName,String cellValueOriginal) throws Exception {		
//		String dbColumnName = (String)mapInfo.get(CTRI9001CommonExcelConversionDTO.ATTR_VALUE);
//		HashMap colInfo = (HashMap)tableMetaData.get(dbColumnName);
//		String colType = (String)colInfo.get("TYPE");
//		if (colType.equals("DATE")) {
//		    int length = DEFAULT_XLS_DT_FORMAT.length();
//		    int dataLength = cellValue.length();
//            if (dataLength > length) {
//            	String[] args = new String[2];
//            	args[0] = "Column: \"" + columnName + "\" with Value {" + cellValueOriginal + "}";
//            	args[1] = "" + length;
//            	throw CSTD0054ErrorUtil.generateError("MSTD0051AERR", args, 1);
//            }
//			return alignLength(cellValue, CSTD0095ASCIIConstant.ALIGNMENT_LEFT, length, null);
//			
//		}
//		else if (colType.equals("NUMBER")) {
//		    // Revise the Length calculation
//		    int dbLength = ((Integer)colInfo.get("PRECISION")).intValue();
//		    if (((Integer)colInfo.get("SCALE")).intValue() > 0) {
//		        dbLength += 1;
//		    }
//			String overrideLength = (String)mapInfo.get(CTRI9001CommonExcelConversionDTO.ATTR_LENGTH);
//			if (overrideLength != null && overrideLength.length() > 0) {
//				dbLength = Integer.parseInt(overrideLength);
//			}
//			
//			int dataLength = cellValue.length();
//			if (dataLength > dbLength) {
//				String[] args = new String[2];
//				args[0] = "Column: \"" + columnName + "\" with Value {" + cellValueOriginal + "}";
//				args[1] = "" + dbLength;
//            	throw CSTD0054ErrorUtil.generateError("MSTD0051AERR", args, 1);
//			}
//			
//			return alignLength(cellValue.trim(), CSTD0095ASCIIConstant.ALIGNMENT_RIGHT, 
//							   dbLength, null);
//		}
//		else {
//			int dbLength = ((Integer)colInfo.get("LENGTH")).intValue();
//			String overrideLength = (String)mapInfo.get(CTRI9001CommonExcelConversionDTO.ATTR_LENGTH);
//			if (overrideLength != null && overrideLength.length() > 0) {
//				dbLength = Integer.parseInt(overrideLength);
//			}
//
//			String cellValueLog = cellValue;
//			cellValue = this.checkAndReplaceValueBeforeChkLength(mapInfo, cellValueLog);
//			
//			int dataLength = cellValue.trim().length();
//			if (dataLength > dbLength) {
//				String[] args = new String[2];
//				args[0] = "Column: \"" + columnName + "\" with Value {" + cellValueOriginal + "}";
//				args[1] = "" + dbLength;
//            	throw CSTD0054ErrorUtil.generateError("MSTD0051AERR", args, 1);
//			}
//			
//			return alignLength(cellValue.trim(), CSTD0095ASCIIConstant.ALIGNMENT_LEFT, 
//							   dbLength, null);
//		}
//	}
//	
//
//	private boolean validateFileExtension(String orgFileName) throws CSTD0056CommonException{
//		int iPos = orgFileName.lastIndexOf('.');
//		if(iPos != -1) {
//			String fileExtension = orgFileName.substring(iPos + 1, 
//											orgFileName.length()).toUpperCase();
//			if (CTRI0029CommonBatchConstants.XLS.equals(fileExtension)) {
//				return true;
//			}else if (CTRI0029CommonBatchConstants.XLSX.equals(fileExtension)) {
//				return true;
//			}else if (CTRI0029CommonBatchConstants.CSV.equals(fileExtension)) {
//				return true;
//			}else if (CTRI0029CommonBatchConstants.ZIP.equals(fileExtension)) {
//				return true;
//			}else {
//				return false;
//			}
//		}
//		else {
//			return false;
//		}
//	}
//	
//	private boolean isCSVFileExtension(String orgFileName) throws CSTD0056CommonException{
//		int iPos = orgFileName.lastIndexOf('.');
//		if(iPos != -1) {
//			String fileExtension = orgFileName.substring(iPos + 1, 
//											orgFileName.length()).toUpperCase();
//			if (CTRI0029CommonBatchConstants.CSV.equals(fileExtension)) {
//				return true;
//			}else {
//				return false;
//			}
//		}
//		else {
//			return false;
//		}
//	}
//	
//	private boolean isZipFileExtension(String orgFileName) throws CSTD0056CommonException{
//		int iPos = orgFileName.lastIndexOf('.');
//		if(iPos != -1) {
//			String fileExtension = orgFileName.substring(iPos + 1, 
//											orgFileName.length()).toUpperCase();
//			if (CTRI0029CommonBatchConstants.ZIP.equals(fileExtension)) {
//				return true;
//			}else {
//				return false;
//			}
//		}
//		else {
//			return false;
//		}
//	}    
//
//	protected void doFinally() throws Exception {
//		FileInputStream ist = null;
//		FileOutputStream ost = null;
//		
//		try{
//			//Move upload Excel file to "../archive/error" folder if conversion failed.
//			String fullXlsFileName = this.pathUploadFile + fileName;
//			File moveFile = new File(fullXlsFileName);
//			
//			if (retStatus == EXIT_ERROR) {
//				if(pathArchiveFile == null){
//					pathArchiveFile = getOutputDirectory(resolver.getProperty("FS_ARCHIVE_ERROR")) + File.separator;
//				}
//				String outputFile = pathArchiveFile + fileName;
//				ist = new FileInputStream(moveFile);
//				ost = new FileOutputStream(outputFile);
//				CTRI0015StreamTransfer.transferBuffered(ist, ost);
//			}
//			else if (retStatus == EXIT_SUCCESS) {
//				if(pathArchiveFile == null){
//					pathArchiveFile = getOutputDirectory(resolver.getProperty("FS_ARCHIVE_PATH")) + File.separator;
//				}
//				String outputFile = pathArchiveFile + fileName;
//				ist = new FileInputStream(moveFile);
//				ost = new FileOutputStream(outputFile);
//				CTRI0015StreamTransfer.transferBuffered(ist, ost);
//			}
//			else if (retStatus == EXIT_WARNING) {
//				if(pathArchiveFile == null){
//					pathArchiveFile = getOutputDirectory(resolver.getProperty("FS_ARCHIVE_WARN")) + File.separator;
//				}
//				String outputFile = pathArchiveFile + fileName;
//				ist = new FileInputStream(moveFile);
//				ost = new FileOutputStream(outputFile);
//				CTRI0015StreamTransfer.transferBuffered(ist, ost);
//			}
//			
//			//Delete file from input path
//			moveFile.delete();
//		} catch (IOException io){
//			ArrayList<String> msgArgs = new ArrayList<String>();
//		    msgArgs.add(fileName);
//		    msgArgs.add(this.pathUploadFile);
//		    msgArgs.add(this.pathArchiveFile);		    
//			CSTD0050CommonLogger.log(this.aplId, this.moduleId, this.functionId, 
//					this.userId, "MSTD7009BERR", "P", msgArgs, 
//	                CSTD0067Constant.INFO, CSTD0067Constant.DEST_BOTH, this.getClass());
//		} finally {
//			try {
//				if(ist != null){
//					ist.close();
//					ist = null;
//				}
//				
//				if(ost != null){
//					ost.close();
//					ost = null;
//				}
//			}catch (Exception ex){} 
//		}
//		
//	}
//
//	protected void logExceptionProcess(Exception ex) throws Exception {
//       	if (ex instanceof CSTD0056CommonException) {
//    		ex.printStackTrace();	    	
//    		loggerDB.logCommonException((CSTD0056CommonException)ex, this.getClass());
//        } else {
//        	ex.printStackTrace();
//        	ArrayList<String> ls = new ArrayList<String>();
//            ls.add(ex.getMessage());
//            loggerDB.logError("MSTD0067AERR", ls, this.getClass());			
//        }        
//        retStatus = EXIT_ERROR;
//    }
//    
//    protected void logEndProcess() throws Exception {
//    	ArrayList<String> msgArgs = new ArrayList<String>();
//        if (retStatus == EXIT_SUCCESS) {
//        	msgArgs.clear();
//        	msgArgs.add("Excel to ASCII Conversion");
//        	CSTD0050CommonLogger.log(aplId, moduleId, functionId, 
//		            userId, "MSTD0085AINF", "P", msgArgs, 
//	                CSTD0067Constant.INFO, CSTD0067Constant.DEST_BOTH, this.getClass());
//        }
//        else if (retStatus == EXIT_WARNING) {
//        	msgArgs.clear();
//        	msgArgs.add("Excel to ASCII Conversion");
//        	msgArgs.add("");
//        	msgArgs.add("");
//        	msgArgs.add("");
//        	CSTD0050CommonLogger.log(aplId, moduleId, functionId, 
//        			userId, "MSTD7003BINF", "E", msgArgs, 
//	                CSTD0067Constant.INFO, CSTD0067Constant.DEST_BOTH, this.getClass());
//        }
//        else {
//        	CSTD0050CommonLogger.log(aplId, moduleId, functionId, 
//        			userId, "MTRI3031AERR", "P", msgArgs, 
//	                CSTD0067Constant.ERROR, CSTD0067Constant.DEST_BOTH, this.getClass());
//        	
//        	msgArgs.clear();
//        	msgArgs.add(this.processName);
//    		msgArgs.add("");
//        	msgArgs.add("");
//        	msgArgs.add("");
//        	CSTD0050CommonLogger.log(aplId, moduleId, functionId, 
//        			userId, "MSTD7002BINF", "E", msgArgs, 
//	                CSTD0067Constant.INFO, CSTD0067Constant.DEST_BOTH, this.getClass());
//        }
//    }
//    
//	protected void postProcessing() throws Exception {
//
//	}
//
//	protected String alignLength(String fieldValue, String align, int len, String paddingChar)
//	throws CSTD0056CommonException {
//		if (paddingChar == null || paddingChar.length() == 0) {
//			paddingChar = DEFAULT_PAD;
//		}
//
//		StringBuffer strPerColumn = new StringBuffer(fieldValue);
//		if (CSTD0095ASCIIConstant.ALIGNMENT_LEFT.equalsIgnoreCase(align)) {
//			while (strPerColumn.length() < len) {
//				strPerColumn.append(paddingChar);
//			}
//		} 
//		else {
//			while (strPerColumn.length() < len) {
//				strPerColumn.insert(0, paddingChar);
//			}
//		}
//
//		return strPerColumn.toString();
//	}
//
//	// retStatus = 0 : SUCCESS
//	// retStatus = 1 : ERROR
//	// retStatus = 2 : WARNING
//	public int process(String[] args) throws Exception {
//		try {//Get Argument Values
//			getArgumentValues(args);
//
//			//Initialize Values
//			initialize();
//
//			//Main processing process
//			retStatus = doProcessing();
//
//			//Post processing process >> Call Common Receiving
//			postProcessing();
//		}
//		catch (Exception ex) {
//			retStatus = 1;
//			logExceptionProcess(ex);
//		}
//		finally {
//			//move file to archive folder
//			doFinally();
//            logEndProcess();
//		}
//		
//		return retStatus;
//	}
//
//	protected String getUploadPathFromTBSystem() throws Exception {
//		String strRet = ""; 
//		
//		try{
//			ArrayList al = adapter.executeQuery("SELECT VALUE FROM TB_M_SYSTEM WHERE CATEGORY = 'SINVC' AND CD = 'UPLOADDIR'", null);
//			if(al != null && al.size() > 0){
//				strRet = al.get(0).toString();
//			} else {
//				throw new Exception();
//			}
//		} catch (Exception ex){
//			throw new Exception("Excel input path doesn't set in to System table (TB_M_SYSTEM) with Category Code:{SINVC} and Code: {UPLOADDIR}." );
//		}
//		
//		return strRet;
//	}
//	
//	protected BigDecimal getMaxFileSizeForUpload(String code) throws Exception {
//		try{
//			ArrayList al = adapter.executeQuery("SELECT VALUE FROM TB_M_SYSTEM WHERE CATEGORY = 'ODB' AND CD = '"+code+"'", null);
//			if(al != null && al.size() > 0){
//				return new BigDecimal(al.get(0).toString());
//			} else {
//				throw new Exception("Doesn't set maximum file size of upload in to System table (TB_M_SYSTEM) with Category Code:{ODB} and Code: {"+code+"}." );
//			}
//		} catch (Exception ex){
//			throw new Exception("Found incorrect config maximum file size of upload in to System table (TB_M_SYSTEM) with Category Code:{ODB} and Code: {"+code+"}." );
//		}
//		
//	}
//
//	public String getOutputDirectory(String outDir){
//		if (outDir.lastIndexOf(File.separator) == outDir.length()-1){
//			outDir = outDir.substring(0,outDir.length()-1);
//		}
//		return outDir;
//	}
//
//	public CTRI9001CommonExcelConversionDTO getXlsConvVo() {
//		return xlsConvVo;
//	}
//
//	public void setXlsConvVo(CTRI9001CommonExcelConversionDTO xlsConvVo) {
//		this.xlsConvVo = xlsConvVo;
//	}

}
