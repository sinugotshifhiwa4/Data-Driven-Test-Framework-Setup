package com.data.driven.tests;

import com.data.driven.base.TestBase;
import com.data.driven.dataProvider.JsonDataProvider;
import com.data.driven.utils.ErrorHandler;
import com.data.driven.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

public class JsonBooleanReaderTest extends TestBase {

    private static final Logger logger = LoggerUtils.getLogger(JsonBooleanReaderTest.class);
    private static final String DEPOSIT_FIELD = "Deposit";

    @Test(groups = {"json-loader", "data-loader"})  // run json group mvn clean test -Pjson-data-loader -Denv=json
    public void verifyDepositStatusByIndex() {
        try {
            Boolean isPaidDeposit = reader.getBooleanByIndex(DEPOSIT_FIELD, 0);
            Boolean isUnpaidDeposit = reader.getBooleanByIndex(DEPOSIT_FIELD, 1);

            logger.info("Deposit Paid Status: {}", isPaidDeposit);
            logger.info("Deposit Unpaid Status: {}", isUnpaidDeposit);

        } catch (Exception error) {
            ErrorHandler.logError(error, "verifyDepositStatusByIndex", "Failed to read deposit status by index");
            throw new RuntimeException("Failed to read deposit status by index", error);
        }
    }

    @Test(groups = {"json-loader", "data-loader"}, dataProvider = "DepositDataList", dataProviderClass = JsonDataProvider.class)
    public void verifyAllDepositStatuses(Boolean depositStatus) {
        try {
            logger.info("Deposit Status: {}", depositStatus);
        } catch (Exception error) {
            ErrorHandler.logError(error, "verifyAllDepositStatuses", "Failed to read all deposit statuses");
            throw new RuntimeException("Failed to read all deposit statuses", error);
        }
    }
}