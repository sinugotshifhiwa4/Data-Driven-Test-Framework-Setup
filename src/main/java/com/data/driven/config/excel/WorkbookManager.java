package com.data.driven.config.excel;

import com.data.driven.utils.ErrorHandler;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class WorkbookManager implements AutoCloseable {

    private final XSSFWorkbook workbook;

    public WorkbookManager(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            this.workbook = new XSSFWorkbook(fis);
        } catch (IOException error) {
            ErrorHandler.logError(error, "WorkbookManager", "Failed to load workbook");
            throw new ExcelOperationException("Failed to load workbook: " + filePath, error);
        }
    }

    /**
     * Retrieves a sheet from the workbook by name.
     *
     * @param sheetName The name of the sheet to retrieve
     * @return The sheet object
     * @throws ExcelOperationException if the sheet is not found
     */
    public Sheet getSheet(String sheetName) {
        try {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new ExcelOperationException("Sheet not found: " + sheetName);
            }
            return sheet;
        } catch (Exception error) {
            ErrorHandler.logError(error, "WorkbookManager", "Failed to get sheet: " + sheetName);
            throw new ExcelOperationException("Failed to get sheet: " + sheetName, error);
        }
    }

    /**
     * Closes the workbook and releases any system resources it is using. This
     * method must be called after the workbook is no longer needed, otherwise
     * it may cause memory issues.
     *
     * @throws IOException If there is an error closing the workbook
     */
    @Override
    public void close() throws IOException {
        workbook.close();
    }
}
