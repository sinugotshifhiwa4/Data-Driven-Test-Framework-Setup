package com.data.driven.config.excel;


import com.data.driven.utils.ErrorHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExcelDataCacheConfig {

    private static final Map<String, Object[][]> TEST_DATA_CACHE = new ConcurrentHashMap<>();

    /**
     * Retrieves test data from the cache or loads it from the Excel file if not cached.
     *
     * @param fileName  Path to the Excel file
     * @param sheetName Name of the sheet to read from
     * @return 2D array of test data, where each row is an array of objects containing the data in their original types
     */
    public static Object[][] getTestData(String fileName, String sheetName) {
        try {
            return getCachedData(fileName, sheetName);
        } catch (Exception error) {
            ErrorHandler.logError(error, "getTestData", "Failed to load test data from file: " + fileName);
            throw new RuntimeException("Failed to load test data", error);
        }
    }

    /**
     * Gets test data for a specific index from the Excel file.
     *
     * @param filePath  Path to the Excel file
     * @param sheetName Name of the sheet to read from
     * @param index     Index of the row to retrieve
     * @return Map containing the row data with original data types
     * @throws IndexOutOfBoundsException if the index is invalid
     * @throws ClassCastException        if the data is not in the expected format
     */
    public static Map<String, Object> getTestDataByIndex(String filePath, String sheetName, int index) {
        try {
            Object[][] allData = getCachedData(filePath, sheetName);
            if (index < 0 || index >= allData.length) {
                throw new IndexOutOfBoundsException(
                        String.format("Invalid index: %d (valid range: 0-%d) for %s:%s",
                                index, allData.length - 1, filePath, sheetName));
            }

            Object rowData = allData[index][0];
            if (!(rowData instanceof Map)) {
                throw new ClassCastException("Data at index " + index + " is not a Map");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) rowData;
            return dataMap;
        } catch (Exception error) {
            ErrorHandler.logError(error, "getTestDataByIndex", "Failed to load test data from file: " + filePath);
            throw new RuntimeException("Failed to load test data", error);
        }
    }

    /**
     * Loads test data from cache or fetches from file if not cached.
     */
    private static Object[][] getCachedData(String filePath, String sheetName) {
        try {
            String cacheKey = generateCacheKey(filePath, sheetName);
            return TEST_DATA_CACHE.computeIfAbsent(cacheKey, key -> loadTestData(filePath, sheetName));
        } catch (Exception error) {
            ErrorHandler.logError(error, "getCachedData", "Failed to load test data from file: " + filePath);
            throw new RuntimeException("Failed to load test data", error);
        }
    }

    /**
     * Loads test data from the Excel file.
     */
    private static Object[][] loadTestData(String filePath, String sheetName) {
        try {
            List<Map<String, Object>> testData = ExcelConfigManager.loadExcelDataAsList(filePath, sheetName);
            return testData.stream().map(data -> new Object[]{data}).toArray(Object[][]::new);
        } catch (Exception error) {
            ErrorHandler.logError(error, "loadTestData", "Failed to load test data from file: " + filePath);
            throw new RuntimeException("Failed to load test data", error);
        }
    }

    /**
     * Generates a unique cache key.
     */
    private static String generateCacheKey(String filePath, String sheetName) {
        try {
            return filePath + "#" + sheetName;
        } catch (Exception error) {
            ErrorHandler.logError(error, "generateCacheKey", "Failed to generate cache key");
            throw new RuntimeException("Failed to generate cache key", error);
        }
    }

    public static void clearCache() {
        try {
            TEST_DATA_CACHE.clear();
        } catch (Exception error) {
            ErrorHandler.logError(error, "clearCache", "Failed to clear cache");
            throw new RuntimeException("Failed to clear cache", error);
        }
    }

    public static void refreshCache(String filePath, String sheetName) {
        try {
            String cacheKey = generateCacheKey(filePath, sheetName);
            TEST_DATA_CACHE.remove(cacheKey);
            getCachedData(filePath, sheetName); // Reload the data
        } catch (Exception error) {
            ErrorHandler.logError(error, "refreshCache", "Failed to refresh cache");
            throw new RuntimeException("Failed to refresh cache", error);
        }
    }
}