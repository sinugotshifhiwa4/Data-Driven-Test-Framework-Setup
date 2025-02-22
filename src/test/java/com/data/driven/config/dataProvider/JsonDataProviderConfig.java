package com.data.driven.config.dataProvider;

import com.data.driven.config.jackson.JsonDataLoader;
import com.data.driven.config.jackson.JsonDataReader;
import com.data.driven.utils.ErrorHandler;
import com.data.driven.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class JsonDataProviderConfig {

    private static final Logger logger = LoggerUtils.getLogger(JsonDataProviderConfig.class);
    private static final JsonDataReader reader = JsonDataLoader.getJsonReaderInstance();



    public static Iterator<Object[]> getStringDataList(String section) {
        return getDataList(section, reader::getAllStrings, "string");
    }

    public static Iterator<Object[]> getIntegerDataList(String section) {
        return getDataList(section, reader::getAllIntegers, "integer");
    }

    public static Iterator<Object[]> getBooleanDataList(String section) {
        return getDataList(section, reader::getAllBooleans, "boolean");
    }

    /**
     * Generic method to get data from the reader based on the provided function.
     */
    private static  <T> Iterator<Object[]> getDataList(String section, Function<String, List<T>> dataRetriever, String dataType) {
        try {
            List<T> dataList = dataRetriever.apply(section);
            return dataList.stream().map(data -> new Object[]{data}).iterator();
        } catch (Exception error) {
            String errorMessage = String.format("Failed to retrieve %s data list for section: %s", dataType, section);
            ErrorHandler.logError(error, "getDataList", errorMessage);
            throw new RuntimeException(errorMessage, error);
        }
    }
}
