package com.data.driven.tests;

import com.data.driven.base.TestBase;
import com.data.driven.dataProvider.ExcelDataProvider;
import com.data.driven.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

public class ExcelIntegerReaderTest extends TestBase {

    private static final Logger logger = LoggerUtils.getLogger(ExcelStringReaderTest.class);

    @Test(dataProvider = "ExpiryYear",
            dataProviderClass = ExcelDataProvider.class,
            groups = {"excel-loader", "data-loader"})
    public void shouldRetrieveCardExpiryYear(int expiryYear) {
        logger.info("Retrieved Expiry Year: {}", expiryYear);
    }

    @Test(dataProvider = "CvvNumbers",
            dataProviderClass = ExcelDataProvider.class,
            groups = {"excel-loader", "data-loader"})
    public void shouldRetrieveCardCvvNumber(int cvv) {
        logger.info("Retrieved Cvv: {}", cvv);
    }
}
