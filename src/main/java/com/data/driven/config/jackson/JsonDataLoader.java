package com.data.driven.config.jackson;

import com.data.driven.utils.ErrorHandler;
import com.data.driven.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;

public class JsonDataLoader {
    private static final Logger logger = LoggerUtils.getLogger(JsonDataLoader.class);

    private static JsonDataReader jsonDataReaderInstance;
    private static boolean isInitialized = false;

    /**
     * Loads all JSON feature data with schema validation.
     *
     * @param jsonDataFilePath Path to the JSON data file.
     * @param schemaFilePath   Path to the JSON schema file for validation.
     */
    public static synchronized void initializeJsonData(String jsonDataFilePath, String schemaFilePath) {
        if (isInitialized) {
            logger.warn("JSON data already loaded, ignoring new load request");
            return;
        }

        try {
            jsonDataReaderInstance = JsonDataReader.builder()
                    .setFilePath(jsonDataFilePath)
                    .setCacheEnabled(true)
                    .setSchemaPath(schemaFilePath)
                    .build();

            isInitialized = true;
            logger.info("Successfully loaded JSON feature data from '{}'", jsonDataFilePath);
        } catch (Exception error) {
            ErrorHandler.logError(error, "initializeJsonData", "Failed to load JSON data");
            throw new JsonDataLoaderException("Failed to load JSON data", error);
        }
    }

    /**
     * Creates a basic JSON reader with minimal configuration.
     *
     * @param jsonFilePath Path to the JSON data file.
     * @return A configured JsonDataReader instance.
     */
    public static JsonDataReader initializeJsonData(String jsonFilePath) {
        try {
            return JsonDataReader.builder()
                    .setFilePath(jsonFilePath)
                    .build();
        } catch (Exception e) {
            ErrorHandler.logError(e, "initializeJsonData", "Failed to create JSON reader");
            throw new JsonDataLoaderException("Failed to create JSON reader", e);
        }
    }

    /**
     * Retrieves the singleton instance of JsonDataReader initialized via loadJsonData.
     *
     * @return The singleton JsonDataReader instance.
     * @throws IllegalStateException if JSON data is not initialized.
     */
    public static JsonDataReader getJsonReaderInstance() {
        try {
            if (!isInitialized || jsonDataReaderInstance == null) {
                throw new IllegalStateException("JSON data not initialized. Call loadJsonData() first.");
            }
            return jsonDataReaderInstance;
        } catch (IllegalStateException error) {
            ErrorHandler.logError(error, "getJsonReaderInstance", "Failed to retrieve JSON reader instance");
            throw new JsonDataLoaderException("Failed to retrieve JSON reader instance", error);
        }
    }

    /**
     * Checks if JSON data has been loaded.
     *
     * @return true if JSON data is loaded, false otherwise.
     */
    public static boolean isJsonDataLoaded() {
        try {
            return isInitialized && jsonDataReaderInstance != null;
        } catch (Exception error) {
            ErrorHandler.logError(error, "isJsonDataLoaded", "Failed to check if JSON data is loaded");
            throw new JsonDataLoaderException("Failed to check if JSON data is loaded", error);
        }
    }

    /**
     * Resets the loaded JSON data and cleans up resources.
     */
    public static synchronized void resetJsonData() {
        try {
            if (isInitialized && jsonDataReaderInstance != null) {
                try {
                    jsonDataReaderInstance.close();
                } catch (Exception e) {
                    logger.warn("Error while closing JsonDataReader: {}", e.getMessage());
                }
                jsonDataReaderInstance = null;
                isInitialized = false;
                logger.info("JSON data has been reset");
            }
        } catch (Exception error) {
            ErrorHandler.logError(error, "resetJsonData", "Failed to reset JSON data");
            throw new JsonDataLoaderException("Failed to reset JSON data", error);
        }
    }

    /**
     * Custom exception for JsonDataLoader errors.
     */
    public static class JsonDataLoaderException extends RuntimeException {
        public JsonDataLoaderException(String message) {
            super(message);
        }

        public JsonDataLoaderException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}