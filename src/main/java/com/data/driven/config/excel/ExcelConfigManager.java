package com.data.driven.config.excel;

import com.data.driven.utils.ErrorHandler;
import com.data.driven.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelConfigManager {

    private static final Logger logger = LoggerUtils.getLogger(ExcelConfigManager.class);

    /**
     * Reads data from an Excel file and converts it to a list of maps.
     *
     * @param filePath  The path to the Excel file
     * @param sheetName The name of the sheet to read
     * @return List of maps where each map represents a row of data
     * @throws ExcelOperationException if there are issues reading the file
     */
    public static List<Map<String, Object>> loadExcelDataAsList(String filePath, String sheetName) {
        List<Map<String, Object>> dataList = new ArrayList<>();

        try (WorkbookManager workbookManager = new WorkbookManager(filePath)) {
            Sheet sheet = workbookManager.getSheet(sheetName);
            processSheet(sheet, dataList);
        } catch (IOException error) {
            ErrorHandler.logError(error, "loadExcelDataAsList", "Failed to read Excel data");
            throw new ExcelOperationException("Error closing workbook", error);
        }

        return dataList;
    }

    /**
     * Process the data in an Excel sheet and convert it to a list of maps.
     * This method assumes that the first row contains the headers.
     * It processes each row, skipping completely empty rows.
     * For each row with data, it converts the row to a map and adds it to the data list.
     *
     * @param sheet    The sheet to process
     * @param dataList The list to which the row data should be added
     * @throws ExcelOperationException if there are issues processing the sheet
     */
    private static void processSheet(Sheet sheet, List<Map<String, Object>> dataList) {
        try {
            int rowCount = sheet.getPhysicalNumberOfRows();
            if (rowCount <= 1) {
                logger.warn("Sheet is empty or contains only headers");
                return;
            }

            Row headerRow = sheet.getRow(0);
            if (!CellReader.isValidCell(headerRow.getCell(0))) {
                throw new ExcelOperationException("Invalid header row");
            }

            List<String> headers = getHeaders(headerRow);

            // Process each row, skipping completely empty rows
            for (int i = 1; i < rowCount; i++) {
                Row currentRow = sheet.getRow(i);
                if (currentRow != null && !isRowEmpty(currentRow)) {
                    Map<String, Object> rowData = processRow(currentRow, headers);
                    if (!rowData.isEmpty()) {
                        dataList.add(rowData);
                    }
                }
            }
        } catch (Exception error) {
            ErrorHandler.logError(error, "processSheet", "Failed to process sheet");
            throw new ExcelOperationException("Error processing sheet", error);
        }
    }

    /**
     * Retrieves the headers from a row, trims the values, and returns them as a list.
     *
     * @param headerRow The row containing the headers
     * @return A list of headers
     * @throws ExcelOperationException if there are issues retrieving the headers
     */
    private static List<String> getHeaders(Row headerRow) {
        try {
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                String header = CellReader.getCellValueAsString(cell).trim();
                if (!header.isEmpty()) {
                    headers.add(header);
                }
            }
            return headers;
        } catch (Exception error) {
            ErrorHandler.logError(error, "getHeaders", "Failed to get headers");
            throw new ExcelOperationException("Error processing headers", error);
        }
    }

    /**
     * Processes a row of cells and returns a map of the column names to their respective values.
     *
     * @param row     The row to process
     * @param headers List of column names as headers
     * @return A map of the column names to their respective values
     */
    private static Map<String, Object> processRow(Row row, List<String> headers) {
        try {
            Map<String, Object> rowData = new HashMap<>();
            boolean hasValidData = false;

            for (int j = 0; j < headers.size(); j++) {
                Cell cell = row.getCell(j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (CellReader.isValidCell(cell)) {
                    Object cellValue = getCellValue(cell);
                    if (cellValue != null) {
                        rowData.put(headers.get(j), cellValue);
                        hasValidData = true;
                    }
                }
            }

            return hasValidData ? rowData : new HashMap<>();
        } catch (Exception error) {
            ErrorHandler.logError(error, "processRow", "Failed to process row");
            throw new ExcelOperationException("Error processing row", error);
        }
    }

    /**
     * Retrieves the value of the cell as an object.
     * Uses the enhanced CellReader capabilities for better type handling.
     *
     * @param cell The cell to read the value from
     * @return The value of the cell as an object, or null if the cell value cannot be read
     */
    private static Object getCellValue(Cell cell) {
        try {
            return switch (cell.getCellType()) {
                case BOOLEAN -> CellReader.getCellValueAsBoolean(cell);
                case NUMERIC -> {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        yield CellReader.getCellValueAsDate(cell);
                    }
                    yield CellReader.getCellValueAsNumber(cell);
                }
                case STRING -> {
                    String value = CellReader.getCellValueAsString(cell);
                    yield value.isEmpty() ? null : value;
                }
                case FORMULA -> {
                    String value = CellReader.getCellValueAsString(cell);
                    if (value.isEmpty()) {
                        yield null;
                    }
                    // Try to convert formula result to appropriate type
                    if (cell.getCachedFormulaResultType() == CellType.NUMERIC) {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            yield CellReader.getCellValueAsDate(cell);
                        }
                        yield CellReader.getCellValueAsNumber(cell);
                    }
                    yield value;
                }
                default -> null;
            };
        } catch (Exception error) {
            ErrorHandler.logError(error, "getCellValue", "Failed to get cell value");
            throw new ExcelOperationException("Error getting cell value", error);
        }
    }

    /**
     * Determines if a row is empty by checking all cells.
     *
     * @param row The row to check
     * @return true if the row is empty, false otherwise
     */
    private static boolean isRowEmpty(Row row) {
        try {
            if (row == null) {
                return true;
            }

            for (Cell cell : row) {
                if (CellReader.isValidCell(cell)) {
                    return false;
                }
            }
            return true;
        } catch (Exception error) {
            ErrorHandler.logError(error, "isRowEmpty", "Failed to check if row is empty");
            throw new ExcelOperationException("Error checking if row is empty", error);
        }
    }
}