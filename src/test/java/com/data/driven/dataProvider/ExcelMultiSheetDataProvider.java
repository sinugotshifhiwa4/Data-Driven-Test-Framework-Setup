package com.data.driven.dataProvider;

import com.data.driven.config.dataProvider.ExcelDataCombiner;
import com.data.driven.config.dataProvider.ExcelDataProviderConfig;
import com.data.driven.config.paths.TestResourcePath;
import org.testng.annotations.DataProvider;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ExcelMultiSheetDataProvider {

    private static final String EXCEL_FILE_PATH = TestResourcePath.ADACTIN_HOTEL_EXCEL.getPath();
    private static final String BOOKING_SHEET = "BookingData";
    private static final String PAYMENTS_SHEET = "PaymentData";
    private static final String[] BOOKING_COLUMNS = {
            "Location", "Hotels", "RoomType", "NumberOfRooms",
            "AdultsPerRoom", "ChildrenPerRoom"
    };
    private static final String[] PAYMENTS_COLUMNS = {
            "FirstName", "LastName", "BillingAddress", "CreditCardType", "ExpiryYear", "Cvv"
    };

    @DataProvider(name = "BookingData")
    public Iterator<Object[]> getCombinedData() {
        List<Iterator<Object[]>> dataIterators = Arrays.asList(
                getBookingData(),
                getPaymentData()
        );

        return ExcelDataCombiner.combineIteratorData(dataIterators).iterator();
    }

    private Iterator<Object[]> getBookingData() {
        return ExcelDataProviderConfig.getMultiColumnData(
                EXCEL_FILE_PATH, BOOKING_SHEET, BOOKING_COLUMNS);
    }

    private Iterator<Object[]> getPaymentData() {
        return ExcelDataProviderConfig.getMultiColumnData(
                EXCEL_FILE_PATH, PAYMENTS_SHEET, PAYMENTS_COLUMNS);
    }
}