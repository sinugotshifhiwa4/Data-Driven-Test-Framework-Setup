package com.data.driven.base;

import com.data.driven.config.jackson.JsonDataLoader;
import com.data.driven.config.paths.TestResourcePath;
import com.data.driven.utils.ErrorHandler;

public class TestSetupManager {

    public static void loadJsonData() {
        try{
            JsonDataLoader.initializeJsonData(
                    TestResourcePath.ADACTIN_HOTEL_JSON.getPath(),
                    TestResourcePath.ADACTIN_HOTEL_SCHEMA.getPath()
            );
        } catch (Exception error){
            ErrorHandler.logError(error, "loadJsonData", "Failed to load JSON data");
            throw new JsonDataLoader.JsonDataLoaderException("Failed to load JSON data", error);
        }
    }

}
