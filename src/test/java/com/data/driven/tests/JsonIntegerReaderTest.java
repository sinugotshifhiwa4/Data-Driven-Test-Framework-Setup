package com.data.driven.tests;

import com.data.driven.base.TestBase;
import com.data.driven.dataProvider.JsonDataProvider;
import com.data.driven.utils.ErrorHandler;
import com.data.driven.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

public class JsonIntegerReaderTest extends TestBase {

    private static final Logger logger = LoggerUtils.getLogger(JsonIntegerReaderTest.class);

    private static final String EXPIRY_YEAR_SECTION = "ExpiryYear";
    private static final String CVV_NUMBER_SECTION = "CvvNumber";

    @Test(groups = {"json-loader", "data-loader"})
    public void validatePaymentCardDetailsRetrieval() {
        try {
            int expiryYear = reader.getIntByIndex(EXPIRY_YEAR_SECTION, 3);
            int cvvNumber = reader.getIntByIndex(CVV_NUMBER_SECTION, 5);

            logger.info("Retrieved Expiry Year: {}", expiryYear);
            logger.info("Retrieved CVV Number: {}", cvvNumber);

        } catch (Exception error) {
            ErrorHandler.logError(error,
                    "validatePaymentCardDetailsRetrieval",
                    "Failed to retrieve payment card details from JSON by index"
            );
            throw new RuntimeException("Failed to retrieve payment card details from JSON by index", error);
        }
    }

    @Test(groups = {"json-loader", "data-loader"}, dataProvider = "ExpiryYearDataList", dataProviderClass = JsonDataProvider.class)
    public void validateMultipleExpiryYears(int expiryYear) {
        try {
            logger.info("Validating Expiry Year: {}", expiryYear);

        } catch (Exception error) {
            ErrorHandler.logError(error,
                    "validateMultipleExpiryYears",
                    "Failed to validate multiple expiry years from JSON"
            );
            throw new RuntimeException("Failed to validate multiple expiry years from JSON", error);
        }
    }
}
