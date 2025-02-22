package com.data.driven.config.jackson;

import com.data.driven.utils.ErrorHandler;
import com.data.driven.utils.logging.LoggerUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reads and manages JSON data with support for schema validation, caching, and thread-safe operations.
 */
public class JsonDataReader implements AutoCloseable {
    private static final Logger logger = LoggerUtils.getLogger(JsonDataReader.class);
    private static final ObjectMapper objectMapper = JsonDataConfig.getObjectMapper();

    // JSON data and schema
    private final JsonNode jsonData;
    private final JsonSchema schema;
    private final String defaultSection;

    // Thread-local cache
    private final ThreadLocal<Map<String, Map<String, Object>>> cache;
    private final boolean cacheEnabled;

    // Constructor that accepts a JsonDataBuilder
    public JsonDataReader(JsonDataBuilder builder) {
        if (builder.getFilePath() == null || builder.getFilePath().trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        this.jsonData = loadJson(builder.getFilePath());
        this.cacheEnabled = builder.isCacheEnabled();
        this.defaultSection = builder.getDefaultSection();

        // Initialize thread-local cache if enabled
        this.cache = cacheEnabled ? ThreadLocal.withInitial(ConcurrentHashMap::new) : null;

        if (builder.getSchemaPath() != null && !builder.getSchemaPath().isEmpty()) {
            this.schema = loadSchema(builder.getSchemaPath());
            validateSchema();
        } else {
            this.schema = null;
        }
    }

    // Convenience methods for required values
    public String getString(String section, String key) {
        return getData(section, key, String.class)
                .orElseThrow(() -> new IllegalArgumentException("Missing String value for key: " + key));
    }

    public String getString(String key) {
        return getString(defaultSection, key);
    }

    public int getInt(String section, String key) {
        return getData(section, key, Integer.class)
                .orElseThrow(() -> new IllegalArgumentException("Missing int value for key: " + key));
    }

    public boolean getBoolean(String section, String key) {
        return getData(section, key, Boolean.class)
                .orElseThrow(() -> new IllegalArgumentException("Missing boolean value for key: " + key));
    }

    // Methods for optional values
    public Optional<String> getOptionalString(String section, String key) {
        return getData(section, key, String.class);
    }

    public Optional<Integer> getOptionalInt(String section, String key) {
        return getData(section, key, Integer.class);
    }

    public Optional<Boolean> getOptionalBoolean(String section, String key) {
        return getData(section, key, Boolean.class);
    }

    // Method to get a String value by index from a specified section
    public String getStringByIndex(String section, int index) {
        return getDataByIndex(section, index, String.class)
                .orElseThrow(() -> new IllegalArgumentException("Missing String value at index: " + index));
    }

    // Method to get an Integer value by index from a specified section
    public int getIntByIndex(String section, int index) {
        return getDataByIndex(section, index, Integer.class)
                .orElseThrow(() -> new IllegalArgumentException("Missing int value at index: " + index));
    }

    // Method to get a Boolean value by index from a specified section
    public boolean getBooleanByIndex(String section, int index) {
        return getDataByIndex(section, index, Boolean.class)
                .orElseThrow(() -> new IllegalArgumentException("Missing boolean value at index: " + index));
    }

    public List<String> getAllStrings(String section) {
        return getAllData(section, String.class);
    }

    public List<Integer> getAllIntegers(String section) {
        return getAllData(section, Integer.class);
    }

    public List<Boolean> getAllBooleans(String section) {
        return getAllData(section, Boolean.class);
    }

    // Generic method to get data by index
    private <T> Optional<T> getDataByIndex(String section, int index, Class<T> type) {
        try {
            JsonNode node = jsonData.path(section);
            if (node.isArray() && index >= 0 && index < node.size()) {
                JsonNode item = node.get(index);
                return Optional.ofNullable(objectMapper.convertValue(item, type));
            }
            return Optional.empty();
        } catch (Exception error) {
            ErrorHandler.logError(error, "getDataByIndex",
                    String.format("Failed to retrieve data for section: %s, index: %d", section, index));
            return Optional.empty();
        }
    }

    // Array access methods
    private <T> List<T> getAllData(String section, Class<T> type) {
        List<T> values = new ArrayList<>();
        try {
            JsonNode node = jsonData.path(section);
            if (node.isArray()) {
                for (JsonNode item : node) {
                    values.add(objectMapper.convertValue(item, type));
                }
            }
        } catch (Exception error) {
            ErrorHandler.logError(error, "getAllData",
                    String.format("Failed to retrieve all data from section: %s", section));
        }
        return values;
    }

    private JsonNode loadJson(String filePath) {
        try {
            return objectMapper.readTree(new File(filePath));
        } catch (IOException error) {
            String errorMsg = String.format("Failed to load JSON file: %s", filePath);
            ErrorHandler.logError(error, "loadJson", errorMsg);
            throw new JsonDataReaderException(errorMsg, error);
        }
    }

    private JsonSchema loadSchema(String schemaPath) {
        try {
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            return factory.getSchema(new File(schemaPath).toURI());
        } catch (Exception error) {
            String errorMsg = String.format("Failed to load JSON schema: %s", schemaPath);
            ErrorHandler.logError(error, "loadSchema", errorMsg);
            throw new JsonDataReaderException(errorMsg, error);
        }
    }

    private void validateSchema() {
        if (schema != null) {
            var validationErrors = schema.validate(jsonData);
            if (!validationErrors.isEmpty()) {
                validationErrors.forEach(error -> logger.warn("Schema validation error: {}", error));
                throw new JsonDataReaderException("JSON data does not match schema: " + validationErrors);
            }
        }
    }

    private <T> Optional<T> getData(String section, String key, Class<T> type) {
        if (cacheEnabled) {
            return getFromCache(section, key, type);
        }
        try {
            JsonNode node = jsonData.path(section).path(key);
            if (node.isMissingNode() || node.isNull()) {
                return Optional.empty();
            }
            return Optional.ofNullable(objectMapper.convertValue(node, type));
        } catch (Exception error) {
            ErrorHandler.logError(error, "getData",
                    String.format("Failed to retrieve data for section: %s, key: %s", section, key));
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> getFromCache(String section, String key, Class<T> type) {
        Map<String, Map<String, Object>> threadCache = cache.get();
        Map<String, Object> sectionCache = threadCache.computeIfAbsent(section, k -> new HashMap<>());

        if (!sectionCache.containsKey(key)) {
            Optional<T> fetchedValue = getData(section, key, type);
            sectionCache.put(key, fetchedValue.orElse(null));
        }

        return Optional.ofNullable((T) sectionCache.get(key));
    }


    @Override
    public void close() {
        if (cacheEnabled && cache != null) {
            Map<String, Map<String, Object>> threadCache = cache.get();
            if (threadCache != null) {
                threadCache.clear();
            }
            cache.remove();
        }
        logger.info("JsonDataReader resources cleaned up");
    }

    public static JsonDataBuilder builder() {
        return new JsonDataBuilder();
    }


    public static class JsonDataReaderException extends RuntimeException {
        public JsonDataReaderException(String message) {
            super(message);
        }

        public JsonDataReaderException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}