package com.data.driven.base;

import com.data.driven.config.excel.ExcelDataCacheConfig;
import com.data.driven.config.jackson.JsonDataLoader;
import com.data.driven.config.jackson.JsonDataReader;
import com.data.driven.utils.ErrorHandler;
import com.data.driven.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

public class TestBase {

    private static final Logger logger = LoggerUtils.getLogger(TestBase.class);
    protected JsonDataReader reader;

    @BeforeSuite(alwaysRun = true)
    public void globalSetup() {
        try {
            // Load JSON data
            TestSetupManager.loadJsonData();

            logger.info("Global test environment setup is complete and ready for execution.");
        } catch (Exception error) {
            ErrorHandler.logError(error, "globalSetup", "Failed to setup global test environment");
            throw new RuntimeException("Failed to setup test environment", error);
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void testSetup() {
        try {
            // Initialize JSON reader before each test
            reader = JsonDataLoader.getJsonReaderInstance();
            logger.info("Test environment setup completed successfully.");
        } catch (Exception error) {
            ErrorHandler.logError(error, "testSetup", "Failed to setup test environment");
            throw new RuntimeException("Failed to setup test environment", error);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        try {
            logger.info("Test tear down completed successfully.");
        } catch (Exception error) {
            ErrorHandler.logError(error, "tearDown", "Failed to clean up test environment");
            throw new RuntimeException("Failed to clean up test environment", error);
        }
    }

    @AfterSuite(alwaysRun = true)
    public void cleanUp() {
        try {
            // Reset JSON data
            JsonDataLoader.resetJsonData();

            // Clear Excel Cache
            ExcelDataCacheConfig.clearCache();

            logger.info("Test environment cleanup completed successfully.");
        } catch (Exception error) {
            ErrorHandler.logError(error, "cleanUp", "Failed to clean up test environment");
            throw new RuntimeException("Failed to clean up test environment", error);
        }
    }
}
