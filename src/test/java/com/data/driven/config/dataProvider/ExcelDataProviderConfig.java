package com.data.driven.config.dataProvider;

import com.data.driven.config.excel.ExcelDataCacheConfig;
import com.data.driven.utils.ErrorHandler;
import com.data.driven.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ExcelDataProviderConfig {
    private static final Logger logger = LoggerUtils.getLogger(ExcelDataProviderConfig.class);
    private static final String UNKNOWN_VALUE = "Unknown";
    private static final Set<String> INVALID_VALUES = new HashSet<>(Arrays.asList(
            UNKNOWN_VALUE, "N/A", "NULL", "undefined", "#N/A", "#VALUE!", "#REF!", "#DIV/0!"
    ));

    /**
     * Gets the value of the specified column for the given index with enhanced validation.
     *
     * @param filePath   Path to the Excel file
     * @param sheetName  Name of the sheet to read from
     * @param columnName The name of the column to extract data from
     * @param index      The index of the row to retrieve
     * @return Object[][] containing the validated value of the specified column
     * @throws IllegalArgumentException if input parameters are invalid
     */
    public static Object[][] getValueByIndex(String filePath, String sheetName, String columnName, int index) {
        validateInputParameters(filePath, sheetName, columnName);
        if (index < 0) {
            throw new IllegalArgumentException("Index must be non-negative");
        }

        try {
            Map<String, Object> dataMap = ExcelDataCacheConfig.getTestDataByIndex(filePath, sheetName, index);
            if (dataMap == null || !dataMap.containsKey(columnName)) {
                logger.warn("No data found for column {} at index {}", columnName, index);
                return new Object[][]{{""}}; // Return empty value for missing data
            }

            Object value = sanitizeValue(dataMap.get(columnName));
            return new Object[][]{{value}};
        } catch (Exception error) {
            ErrorHandler.logError(error, "getValueByIndex",
                    String.format("Failed to load test data from file: %s, sheet: %s, column: %s, index: %d",
                            filePath, sheetName, columnName, index));
            throw new RuntimeException("Failed to load test data", error);
        }
    }

    /**
     * Gets test data from a specified column with enhanced filtering and validation.
     *
     * @param filePath   Path to the Excel file
     * @param sheetName  Name of the sheet to read from
     * @param columnName The name of the column to extract data from
     * @return Iterator of validated test data objects
     * @throws IllegalArgumentException if input parameters are invalid
     */
    public static Iterator<Object[]> getColumnData(String filePath, String sheetName, String columnName) {
        validateInputParameters(filePath, sheetName, columnName);

        try {
            List<Map<String, Object>> rawData = convertToMapList(
                    ExcelDataCacheConfig.getTestData(filePath, sheetName)
            );

            List<Object[]> refinedData = new ArrayList<>();
            for (Map<String, Object> row : rawData) {
                Object value = sanitizeValue(row.get(columnName));
                if (isValidValue(value)) {
                    refinedData.add(new Object[]{value});
                }
            }

            if (refinedData.isEmpty()) {
                logger.warn("No valid data found for column: {}", columnName);
            }

            return refinedData.iterator();
        } catch (Exception error) {
            ErrorHandler.logError(error, "getColumnData",
                    String.format("Failed to load test data from file: %s, sheet: %s, column: %s",
                            filePath, sheetName, columnName));
            throw new RuntimeException("Failed to load test data", error);
        }
    }

    /**
     * Gets test data from multiple specified columns with enhanced validation.
     *
     * @param filePath    Path to the Excel file
     * @param sheetName   Name of the sheet to read from
     * @param columnNames Array of column names to extract data from each row
     * @return Iterator of validated test data objects
     * @throws IllegalArgumentException if input parameters are invalid
     */
    public static Iterator<Object[]> getMultiColumnData(String filePath, String sheetName, String... columnNames) {
        validateInputParameters(filePath, sheetName, columnNames);

        try {
            List<Map<String, Object>> rawData = convertToMapList(
                    ExcelDataCacheConfig.getTestData(filePath, sheetName)
            );

            List<Object[]> testData = new ArrayList<>();
            for (Map<String, Object> row : rawData) {
                if (hasAllRequiredData(row, columnNames)) {
                    Object[] rowData = extractColumnValues(row, columnNames);
                    testData.add(rowData);
                }
            }

            if (testData.isEmpty()) {
                logger.warn("No valid data found for columns: {}", Arrays.toString(columnNames));
            }

            return testData.iterator();
        } catch (Exception error) {
            ErrorHandler.logError(error, "getMultiColumnData",
                    String.format("Failed to load test data from file: %s, sheet: %s, columns: %s",
                            filePath, sheetName, Arrays.toString(columnNames)));
            throw new RuntimeException("Failed to load test data", error);
        }
    }

    /**
     * Validates input parameters for Excel operations.
     *
     * @param filePath    Path to the Excel file
     * @param sheetName   Name of the sheet
     * @param columnNames Names of the columns
     * @throws IllegalArgumentException if any parameter is invalid
     */
    private static void validateInputParameters(String filePath, String sheetName, String... columnNames) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        if (sheetName == null || sheetName.trim().isEmpty()) {
            throw new IllegalArgumentException("Sheet name cannot be null or empty");
        }
        if (columnNames == null || columnNames.length == 0) {
            throw new IllegalArgumentException("Column names cannot be null or empty");
        }
        for (String columnName : columnNames) {
            if (columnName == null || columnName.trim().isEmpty()) {
                throw new IllegalArgumentException("Column name cannot be null or empty");
            }
        }
    }

    /**
     * Converts a 2D array of objects into a list of maps with validation.
     */
    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> convertToMapList(Object[][] rawDataArray) {
        try {
            List<Map<String, Object>> rawData = new ArrayList<>();
            if (rawDataArray == null) {
                logger.warn("Raw data array is null");
                return rawData;
            }

            for (Object[] row : rawDataArray) {
                if (row != null && row.length > 0 && row[0] instanceof Map) {
                    Map<String, Object> rowData = (Map<String, Object>) row[0];
                    rawData.add(rowData);
                }
            }
            return rawData;
        } catch (Exception error) {
            ErrorHandler.logError(error, "convertToMapList", "Failed to convert to map list");
            throw new RuntimeException("Failed to convert to map list", error);
        }
    }

    /**
     * Sanitizes a value by handling common Excel data issues.
     */
    private static Object sanitizeValue(Object value) {
        if (value == null) return "";

        String stringValue = value.toString().trim();

        // Handle special Excel values
        if (INVALID_VALUES.contains(stringValue)) {
            return "";
        }

        // Handle numeric values
        if (value instanceof Number) {
            // Check if it's a whole number
            double numValue = ((Number) value).doubleValue();
            if (numValue == Math.floor(numValue) && !Double.isInfinite(numValue)) {
                return (int) numValue;
            }
            return value;
        }

        return stringValue;
    }

    /**
     * Checks if all specified columns in a row contain valid data.
     */
    private static boolean hasAllRequiredData(Map<String, Object> row, String... columnNames) {
        try {
            if (row == null) return false;

            for (String column : columnNames) {
                Object value = sanitizeValue(row.get(column));
                if (!isValidValue(value)) {
                    return false;
                }
            }
            return true;
        } catch (Exception error) {
            ErrorHandler.logError(error, "hasAllRequiredData", "Failed to check for required data");
            throw new RuntimeException("Failed to check for required data", error);
        }
    }

    /**
     * Determines if a value is valid with enhanced validation rules.
     */
    private static boolean isValidValue(Object value) {
        try {
            if (value == null) return false;

            // Handle numeric values
            if (value instanceof Number) {
                return true;
            }

            String stringValue = value.toString().trim();
            return !stringValue.isEmpty() && !INVALID_VALUES.contains(stringValue);
        } catch (Exception error) {
            ErrorHandler.logError(error, "isValidValue", "Failed to check for valid value");
            throw new RuntimeException("Failed to check for valid value", error);
        }
    }

    /**
     * Extracts and sanitizes column values from a row.
     */
    private static Object[] extractColumnValues(Map<String, Object> row, String... columnNames) {
        try {
            Object[] rowData = new Object[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                Object value = sanitizeValue(row.get(columnNames[i]));
                rowData[i] = value;
            }
            return rowData;
        } catch (Exception error) {
            ErrorHandler.logError(error, "extractColumnValues", "Failed to extract column values");
            throw new RuntimeException("Failed to extract column values", error);
        }
    }
}