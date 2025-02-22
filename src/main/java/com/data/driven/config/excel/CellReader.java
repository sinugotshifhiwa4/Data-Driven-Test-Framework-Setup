package com.data.driven.config.excel;

import com.data.driven.utils.ErrorHandler;
import org.apache.poi.ss.usermodel.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CellReader {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Retrieves the value of the cell as a string.
     * Improved handling of numeric values and formula evaluation.
     *
     * @param cell the cell to read the value from
     * @return the value of the cell as a string
     * @throws RuntimeException if there is an error reading the cell value
     */
    public static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        try {
            return switch (cell.getCellType()) {
                case STRING -> cell.getStringCellValue().trim();
                case NUMERIC -> formatNumericCell(cell);
                case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                case FORMULA -> evaluateFormulaCell(cell);
                default -> "";
            };
        } catch (Exception error) {
            ErrorHandler.logError(error, "getCellValueAsString", "Failed to get cell value");
            throw new RuntimeException("Failed to get cell value", error);
        }
    }

    /**
     * Retrieves the value of the cell as a Number.
     * Handles integers, longs, and decimals appropriately with proper precision.
     *
     * @param cell the cell to read the value from
     * @return Number representing the cell value, or null if not numeric
     */
    public static Number getCellValueAsNumber(Cell cell) {
        if (cell == null) return null;

        try {
            switch (cell.getCellType()) {
                case NUMERIC -> {
                    if (!DateUtil.isCellDateFormatted(cell)) {
                        double value = cell.getNumericCellValue();
                        return convertToAppropriateNumber(value);
                    }
                    return null;
                }
                case STRING -> {
                    String stringValue = cell.getStringCellValue().trim();
                    if (stringValue.isEmpty()) return null;
                    try {
                        // Remove any formatting characters and parse
                        stringValue = stringValue.replaceAll("[,\\s]", "");
                        return convertToAppropriateNumber(new BigDecimal(stringValue).doubleValue());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
                case FORMULA -> {
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook()
                            .getCreationHelper().createFormulaEvaluator();
                    CellValue cellValue = evaluator.evaluate(cell);
                    if (cellValue != null && cellValue.getCellType() == CellType.NUMERIC) {
                        return convertToAppropriateNumber(cellValue.getNumberValue());
                    }
                    return null;
                }
                default -> {
                    return null;
                }
            }
        } catch (Exception error) {
            ErrorHandler.logError(error, "getCellValueAsNumber", "Failed to get numeric cell value");
            throw new RuntimeException("Failed to get numeric cell value", error);
        }
    }

    /**
     * Retrieves the value of the cell as a Date.
     * Enhanced error handling and validation.
     *
     * @param cell the cell to read the value from
     * @return the value of the cell as a Date, or null if the cell value cannot be parsed
     */
    public static Date getCellValueAsDate(Cell cell) {
        if (cell == null) return null;

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue();
                    }
                    break;
                case FORMULA:
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook()
                            .getCreationHelper().createFormulaEvaluator();
                    CellValue cellValue = evaluator.evaluate(cell);
                    if (cellValue != null && cellValue.getCellType() == CellType.NUMERIC
                            && DateUtil.isCellDateFormatted(cell)) {
                        return DateUtil.getJavaDate(cellValue.getNumberValue());
                    }
                    break;
            }
            return null;
        } catch (Exception error) {
            ErrorHandler.logError(error, "getCellValueAsDate", "Failed to get cell value");
            throw new RuntimeException("Failed to get cell value", error);
        }
    }

    /**
     * Retrieves the value of the cell as a Boolean.
     * Enhanced string parsing and validation.
     *
     * @param cell the cell to read the value from
     * @return the value of the cell as a Boolean, or null if not a valid boolean
     */
    public static Boolean getCellValueAsBoolean(Cell cell) {
        if (cell == null) return null;

        try {
            return switch (cell.getCellType()) {
                case BOOLEAN -> cell.getBooleanCellValue();
                case STRING -> parseStringAsBoolean(cell.getStringCellValue().trim());
                case FORMULA -> {
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook()
                            .getCreationHelper().createFormulaEvaluator();
                    CellValue cellValue = evaluator.evaluate(cell);
                    if (cellValue != null && cellValue.getCellType() == CellType.BOOLEAN) {
                        yield cellValue.getBooleanValue();
                    }
                    yield null;
                }
                default -> null;
            };
        } catch (Exception error) {
            ErrorHandler.logError(error, "getCellValueAsBoolean", "Failed to get cell value");
            throw new RuntimeException("Failed to get cell value", error);
        }
    }

    /**
     * Determines if a cell is valid and contains actual data.
     *
     * @param cell the cell to check
     * @return true if the cell contains valid data, false otherwise
     */
    public static boolean isValidCell(Cell cell) {
        if (cell == null) return false;

        try {
            return switch (cell.getCellType()) {
                case STRING -> !cell.getStringCellValue().trim().isEmpty();
                case NUMERIC, BOOLEAN -> true;
                case FORMULA -> {
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook()
                            .getCreationHelper().createFormulaEvaluator();
                    yield evaluator.evaluate(cell) != null;
                }
                default -> false;
            };
        } catch (Exception error) {
            ErrorHandler.logError(error, "isValidCell", "Failed to check if cell is valid");
            throw new RuntimeException("Failed to check if cell is valid", error);
        }
    }

    // Private helper methods

    private static String formatNumericCell(Cell cell) {
        try {
            if (DateUtil.isCellDateFormatted(cell)) {
                return new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(cell.getDateCellValue());
            }

            Number number = getCellValueAsNumber(cell);
            return number != null ? number.toString() : "";
        } catch (Exception error) {
            ErrorHandler.logError(error, "formatNumericCell", "Failed to format numeric cell");
            throw new RuntimeException("Failed to format numeric cell", error);
        }
    }

    private static String evaluateFormulaCell(Cell cell) {
        try {
            FormulaEvaluator evaluator = cell.getSheet().getWorkbook()
                    .getCreationHelper().createFormulaEvaluator();
            evaluator.clearAllCachedResultValues(); // Clear cache for fresh evaluation

            try {
                CellValue cellValue = evaluator.evaluate(cell);
                if (cellValue == null) return "";

                return switch (cellValue.getCellType()) {
                    case STRING -> cellValue.getStringValue();
                    case NUMERIC -> {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            yield new SimpleDateFormat(DEFAULT_DATE_FORMAT)
                                    .format(DateUtil.getJavaDate(cellValue.getNumberValue()));
                        }
                        Number num = convertToAppropriateNumber(cellValue.getNumberValue());
                        yield num != null ? num.toString() : "";
                    }
                    case BOOLEAN -> String.valueOf(cellValue.getBooleanValue());
                    default -> "";
                };
            } catch (Exception e) {
                // Fall back to formula string if evaluation fails
                return cell.getCellFormula();
            }
        } catch (Exception error) {
            ErrorHandler.logError(error, "evaluateFormulaCell", "Failed to evaluate formula cell");
            throw new RuntimeException("Failed to evaluate formula cell", error);
        }
    }

    private static Number convertToAppropriateNumber(double value) {
        // Handle integers vs decimals with better precision
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
                return (int) value;
            }
            if (value >= Long.MIN_VALUE && value <= Long.MAX_VALUE) {
                return (long) value;
            }
        }
        // For very large numbers or decimals, use BigDecimal
        return BigDecimal.valueOf(value).stripTrailingZeros();
    }

    private static Boolean parseStringAsBoolean(String value) {
        String trimmedValue = value.toLowerCase();
        if (trimmedValue.equals("true") || trimmedValue.equals("yes") || trimmedValue.equals("1")) {
            return true;
        }
        if (trimmedValue.equals("false") || trimmedValue.equals("no") || trimmedValue.equals("0")) {
            return false;
        }
        return null;
    }
}