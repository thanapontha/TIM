package th.co.toyota.bw0.api.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import th.co.toyota.bw0.api.constants.AppConstants;
import th.co.toyota.bw0.api.constants.MessagesConstants;
import th.co.toyota.bw0.api.exception.common.CommonErrorException;
import th.co.toyota.bw0.api.repository.common.IBW03060Repository;
import th.co.toyota.bw0.util.FormatUtil;
import th.co.toyota.st3.api.constants.CST30000Messages;
import th.co.toyota.st3.api.exception.FileDoesNotExistException;
import th.co.toyota.st3.api.model.SystemInfo;
import th.co.toyota.st3.api.model.SystemInfoId;

@Component
public class CBW00000CommonPOI {

	final static Logger logger = LoggerFactory.getLogger(CBW00000CommonPOI.class);
    public static final int BUFFER_SIZE = 4096;

	@Autowired
	protected IBW03060Repository systemMasterRepository;

	@Value("${template.report.folder}")
	protected String templateReportFolder;

	protected static final String TEMPLATE_POSTFIX = "_Template";
	protected Workbook wb = null;

	public void init(Workbook wb) {
		this.wb = wb;
	}

	public void init(String templateName, String batchName, boolean onDemand) throws CommonErrorException {
		wb = prepareWorkbook(templateName, batchName, onDemand);
	}

	/**
	 * Used to prepared Workbook for Batch/Online Report generation.
	 * 
	 * @param templateName
	 * @param batchName
	 * @param ondemand
	 * @return
	 * @throws FileDoesNotExistException
	 * @throws CannotOpenFileForReadException
	 */
	private Workbook prepareWorkbook(String templateName, String batchName, boolean onDemand)
			throws CommonErrorException {
		String templateDirectory = templateReportFolder;
		String filePath = templateDirectory + templateName + TEMPLATE_POSTFIX
				+ AppConstants.XLS_REPORT_EXTENTION;
		if (!isExistFile(filePath)) {
			// If not exists then look for .xlsx type
			filePath = templateDirectory + templateName + TEMPLATE_POSTFIX
					+ AppConstants.XLSX_REPORT_EXTENTION;
			if (!isExistFile(filePath)) {
				throw new CommonErrorException(MessagesConstants.A_ERROR_TEMPLATE_DOES_NOT_EXIST, 
						new String[]{batchName}, AppConstants.ERROR);
			}
		}
		if(onDemand){
			SystemInfoId infoId = new SystemInfoId();
			infoId.setCategory(AppConstants.SYS_CATEGORY_COMMON);
			infoId.setSubCategory(AppConstants.SYS_CATEGORY_REPORT);
			infoId.setCode("GENERATED_EXCEL_FOLDER_LOCATION");
	
			List<SystemInfo> excelDestDirList = systemMasterRepository
					.querySystemMasterInfo(infoId);
	
			if (excelDestDirList == null || excelDestDirList.size() == 0) {
				// throw CSTD0054ErrorUtil.generateError("MSTD7021BERR",
				// new String[]{"Excel Destination Path",
				// "TB_M_SYSTEM with key CATEGORY=COMMON",
				// "SUB_CATEGORY=REPORT and CD=GENERATED_EXCEL_FOLDER_LOCATION"},
				// 1);
			} else {
				String excelDestDir = excelDestDirList.get(0).getValue();
				File directory = new File(excelDestDir);
				if (!directory.exists()) {
					throw new CommonErrorException(MessagesConstants.A_ERROR_TEMPLATE_DOES_NOT_EXIST, 
							new String[]{batchName}, AppConstants.ERROR);
				}
			}
		}
		InputStream is = null;
		File template = new File(filePath);
		try {
			is = new FileInputStream(template);
			wb = WorkbookFactory.create(is);
		} catch (IOException e) {
			throw new CommonErrorException(CST30000Messages.ERROR_CANNOT_OPEN_FILE, new String[]{filePath}, AppConstants.ERROR);
		} catch (InvalidFormatException e) {
			throw new CommonErrorException(CST30000Messages.ERROR_CANNOT_OPEN_FILE, new String[]{filePath}, AppConstants.ERROR);
		} catch (Exception e) {
			throw new CommonErrorException(CST30000Messages.ERROR_CANNOT_OPEN_FILE, new String[]{filePath}, AppConstants.ERROR);
		} finally {
			template = null;
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}

		return wb;
	}

	/**
	 * @return Returns the workbook.
	 */
	public Workbook getWorkBook() {
		return wb;
	}

    public void protectedSheet(Sheet curSheet){
        if(curSheet instanceof HSSFSheet){
            ((HSSFSheet)curSheet).protectSheet(AppConstants.PWD_PROTECT_SHEET);
        }else if(curSheet instanceof XSSFSheet){
        	((XSSFSheet)curSheet).setSheetPassword(AppConstants.PWD_PROTECT_SHEET, HashAlgorithm.md5);
    	 	((XSSFSheet)curSheet).lockSelectLockedCells(true);
    	 	((XSSFSheet)curSheet).enableLocking();
        }else if(curSheet instanceof SXSSFSheet){
        	((SXSSFSheet)curSheet).protectSheet(AppConstants.PWD_PROTECT_SHEET);
        }
    }
	/**
	 * Function to create new cell with format base on cellToCopy.
	 * 
	 * @param sheet
	 * @param cellToCopy
	 * @param intRow
	 * @param intCol
	 * @return
	 */
	public Cell createCell(Sheet sheet, Cell cellToCopy, int intRow, int intCol) {

		Cell newCell = createCell(sheet, intRow, intCol);
		if (cellToCopy != null) {
			try {
				newCell.setCellStyle(cellToCopy.getCellStyle());
				newCell.setCellType(cellToCopy.getCellType());
			} catch (Exception e) {
			}
		}
		return newCell;
	}

	/**
	 * Function to create new cell base on param. (in default POI will return
	 * null to unformated cell.)
	 * 
	 * @param sheet
	 * @param intRow
	 * @param intCol
	 * @return
	 */
	public Cell createCell(Sheet sheet, int intRow, int intCol) {

		Row row = sheet.getRow(intRow);
		if (row == null)
			row = sheet.createRow(intRow);
		Cell cell = row.getCell(intCol);
		if (cell == null)
			cell = row.createCell(intCol);
		return cell;
	}

	/**
	 * Created by Pipin 28/04/2008 Method to copy format from source to
	 * destination. this method used especially to insert total/ summary in
	 * tabular data because the posisition is depend on row size.
	 * 
	 * @param intSheet
	 *            sheet destination
	 * @param intSheetCopy
	 *            sheet source
	 * @param row
	 *            row destination
	 * @param rowCopy
	 *            row copy
	 * @param colStart
	 *            col start source copy
	 * @param colEnd
	 *            col End Source copy
	 * @throws CSTD0056CommonException
	 */
	public void copyRow(int intSheet, int intSheetCopy, int row, int rowCopy,
			int colStart, int colEnd) throws Exception {
		Sheet sheetCopy = wb.getSheetAt(intSheetCopy);
		Sheet sheet = wb.getSheetAt(intSheet);
		Row rowToCopy = sheetCopy.getRow(rowCopy);
		for (int i = colStart; i <= colEnd; i++) {
			Cell cellToCopy = rowToCopy.getCell(i);
			Cell newCell = createCell(sheet, cellToCopy, row, i);
			if (cellToCopy != null && cellToCopy.getCellType() != 3) {
				Object obj = null;
				if (cellToCopy.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					obj = Integer.valueOf((int) cellToCopy
							.getNumericCellValue());
				} else if (cellToCopy.getCellType() == Cell.CELL_TYPE_STRING) {
					obj = cellToCopy.getStringCellValue();
				}
				newCell = setObject(newCell, obj);
			}
		}
	}

	public void copyRow(Sheet sheet, Sheet sheetCopy, int row, int rowCopy,
			int colStart, int colEnd) throws Exception {
		Row rowToCopy = sheetCopy.getRow(rowCopy);
		for (int i = colStart; i <= colEnd; i++) {
			Cell cellToCopy = rowToCopy.getCell((i));
			Cell newCell = createCell(sheet, cellToCopy, row, i);
			if (cellToCopy.getCellType() != 3) {
				Object obj = null;
				if (cellToCopy.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					obj = Integer.valueOf((int) cellToCopy
							.getNumericCellValue());
				} else if (cellToCopy.getCellType() == Cell.CELL_TYPE_STRING) {
					obj = cellToCopy.getStringCellValue();
				}
				newCell = setObject(newCell, obj);
			}
		}
	}

	/**
	 * Created by Parinya 25/01/2011 Method to insert new row to destination.
	 * Method to copy format from source to destination. this method used
	 * especially to insert total/ summary in tabular data because the
	 * posisition is depend on row size.
	 * 
	 * @param intSheet
	 *            sheet destination
	 * @param intSheetCopy
	 *            sheet source
	 * @param row
	 *            row destination
	 * @param rowCopy
	 *            row copy
	 * @param colStart
	 *            col start source copy
	 * @param colEnd
	 *            col End Source copy
	 * @param insertRowFlag
	 *            insert new row before copy row
	 * @throws CSTD0056CommonException
	 */
	public void copyRow(int intSheet, int intSheetCopy, int row, int rowCopy,
			int colStart, int colEnd, boolean insertRowFlag) throws Exception {
		if (insertRowFlag) {
			Sheet sheet = wb.getSheetAt(intSheet);
			sheet.shiftRows(row, sheet.getLastRowNum(), 1);
		}
		this.copyRow(intSheet, intSheetCopy, row, rowCopy, colStart, colEnd);
	}

	public void removeRow(Sheet sheet, int rowStart, int rowEnd) {
		Row removeRow = null;
		for (int i = rowStart; i <= rowEnd; i++) {
			removeRow = sheet.getRow(i);
			if (removeRow != null) {
				sheet.removeRow(removeRow);
			}
		}
	}

	/**
	 * Function to assign current cell with object value.
	 * 
	 * @param cell
	 * @param obj
	 * @return
	 * @throws CSTD0056CommonException
	 */
	public Cell setObject(Cell cell, Object obj) throws Exception {
		if (obj != null) {
			if (obj instanceof String)
				cell.setCellValue((String) obj);
			else if (obj instanceof Number)
				cell.setCellValue(((Number) obj).doubleValue());
			else if (obj instanceof Date)
				cell.setCellValue((Date) obj);
		}
		return cell;
	}

	public Cell setObject(Sheet sheet, int row, int col, Object obj)
			throws Exception {
		Cell cell = createCell(sheet, row, col);

		cell = setObject(cell, obj);

		return cell;

	}

	public Cell setObject(Sheet sheet, int row, int col, Object obj,
			CellStyle cellStyle, int width) throws Exception {
		Cell cell = createCell(sheet, row, col);
		cell = setObject(cell, obj);
		if (cellStyle != null) {
			cell.setCellStyle(cellStyle);
		}
		if (width != 0) {
			cell.getSheet().setColumnWidth(col, width);
		}
		return cell;

	}

	public boolean isExistFile(String filePath) {
		boolean result;
		File file = new File(filePath);
		if (file.isFile()) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}
    
    /**
     * Copy format of 1 row from 1 sheet to another sheet.
     * The range of columns is from startCol to endCol
     * @param fromSheet
     * @param toSheet
     * @param fromRow
     * @param toRow
     * @param startCol
     * @param endCol
     */
    public void copyFormat(Sheet fromSheet, Sheet toSheet, int fromRow, 
    		int toRow, int startCol, int endCol, boolean copyValue) {
		Row rowToCopy = fromSheet.getRow(fromRow);
		if (rowToCopy == null) {
			return;
		}
		for (int col = startCol; col <= endCol; col++) {
			Cell cellToCopy = rowToCopy.getCell(col);
			Cell cell = createCell(toSheet, cellToCopy, toRow, col);
			if (copyValue) {
				copyValue(cell, cellToCopy);
			}
		}
	}

	private void copyValue(Cell cell, Cell cellToCopy) {

		if (cellToCopy != null) {
			switch (cellToCopy.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				cell.setCellValue(cellToCopy.getStringCellValue());
				break;
			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					cell.setCellValue(cellToCopy.getDateCellValue());
				} else
					cell.setCellValue(cellToCopy.getNumericCellValue());
				break;
			case Cell.CELL_TYPE_FORMULA:
				cell.setCellValue(cell.getNumericCellValue());
			}
		}
	}

	/**
	 * Created by Parinya 28/01/2011 Method to create excel file to destination.
	 * 
	 * @param report
	 *            workbook of data
	 * @param stFileName
	 *            report file name
	 * @param dirPath
	 *            path for create report file
	 * @param timestampFlag
	 *            true : system will append timestamp value to end of report
	 *            file name false : system will skip step for append timestamp
	 *            value
	 * @throws CSTD0056CommonException
	 */
	public String writeFile(Workbook report, String stFileName, String dirPath, boolean timestampFlag, List<String> excelFileList) throws Exception {
		String reportFilePath = "";
		StringBuffer reportFileName = new StringBuffer();
		String templateExtention = AppConstants.XLS_REPORT_EXTENTION;
		if (report instanceof XSSFWorkbook) {
			templateExtention = AppConstants.XLSX_REPORT_EXTENTION;
		}else if (report instanceof SXSSFWorkbook) {
			templateExtention = AppConstants.XLSX_REPORT_EXTENTION;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.DATE_TIME_STRING_FILENAME_FORMAT_WITHOUT24);
		String currentDateTime = sdf.format(new Date());
		if(excelFileList!=null && !excelFileList.isEmpty()){
			boolean loop = true;
			do{
				boolean dupFileName = false;
					for(String existFileName : excelFileList){
						if(existFileName.indexOf(currentDateTime)>=0){
							dupFileName = true;
							break;
						}
					}
					if(dupFileName){
						currentDateTime = sdf.format(new Date());
						loop = true;
					}else{
						loop = false;
					}
			}while(loop);
		}
		
		if (timestampFlag) {
			if(stFileName.lastIndexOf("_") > 0){
				reportFileName.append(stFileName).append(currentDateTime);
			} else {
				reportFileName.append(stFileName).append(AppConstants.UNDERSCORE).append(currentDateTime);
			}
		} else {
			reportFileName.append(stFileName);
		}
		reportFileName.append(templateExtention);

		reportFilePath = new File(dirPath, reportFileName.toString()).getAbsolutePath();
		
		boolean writeable = Files.isWritable(new File(dirPath).toPath());
		if(writeable){
			
			FileOutputStream fileout = new FileOutputStream(reportFilePath);

			report.setActiveSheet(0);
			report.write(fileout);
			report = null;
			fileout.close();
		}else{
			throw new CommonErrorException(MessagesConstants.B_ERROR_ON_WRITING_DATA_INTO_FILE, 
					new String[]{reportFilePath, "No Permission"}, AppConstants.ERROR);
		}
		
		return reportFileName.toString();
	}
	
	
	public Workbook createSXSSFWorkbook(){
		Workbook wb = new SXSSFWorkbook();
		return wb;
	}
	
	public void setMergeCellWithStyle(Sheet sheet, int rowStart, int rowEnd, int cellStart, int cellEnd, String value, CellStyle cs) {
		
		Row row = null;
		Cell cell;
		for (int i = rowStart; i <= rowEnd; ++i) {
			if(sheet.getRow(i) == null) {
				row = sheet.createRow(i);
			}else{
				row = sheet.getRow(i);
			}
		    for(int j=cellStart;j<=cellEnd;j++){
		        if(row.getCell(j) == null) {
		        	cell= row.createCell(j);
		        }else{
		        	cell= row.getCell(j);
		        }
		        cell.setCellStyle(cs);
		        if (i == rowStart && j==cellStart) {
		            cell.setCellValue(value);
		        } 
		    }
		}
		sheet.addMergedRegion(new CellRangeAddress(rowStart, rowEnd, cellStart, cellEnd));
	}
	
    public static void transfer(InputStream is, OutputStream os)
    throws IOException {
        byte buffer[] = new byte[BUFFER_SIZE];
        int l = 0;
        while ((l = is.read(buffer)) > -1) {
            if (l > 0) {
                os.write(buffer, 0, l);
            }
        }
        os.flush();
        buffer = null;
    }
    
    public static void transferBuffered(BufferedInputStream is, BufferedOutputStream os)
    throws IOException {
        byte buffer[] = new byte[BUFFER_SIZE];
        int l = 0;
        while ((l = is.read(buffer)) > -1) {
            if (l > 0) {
                os.write(buffer, 0, l);
            }
        }
        os.flush();
        buffer = null;
    }
}
