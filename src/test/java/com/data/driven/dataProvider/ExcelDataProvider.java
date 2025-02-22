package com.data.driven.dataProvider;

import com.data.driven.config.dataProvider.ExcelDataProviderConfig;
import com.data.driven.config.paths.TestResourcePath;
import org.testng.annotations.DataProvider;

import java.util.Iterator;

public class ExcelDataProvider {

    private static final String EXCEL_FILE_PATH = TestResourcePath.ADACTIN_HOTEL_EXCEL.getPath();
    private static final String BOOKING_SHEET = "BookingData";
    private static final String PAYMENTS_SHEET = "PaymentData";
    private static final String[] BOOKING_COLUMNS = {
            "Location", "Hotels", "RoomType", "NumberOfRooms",
            "AdultsPerRoom", "ChildrenPerRoom"
    };


    @DataProvider(name = "Location")
    public Object[][] getLocationDataByIndex() {
        return ExcelDataProviderConfig.getValueByIndex(
                TestResourcePath.ADACTIN_HOTEL_EXCEL.getPath(),
                BOOKING_SHEET,
                "Location",
                5
        );
    }

    @DataProvider(name = "NumberOfRooms")
    public Iterator<Object[]> getAllNumberOfRooms() {
        return ExcelDataProviderConfig.getColumnData(
                TestResourcePath.ADACTIN_HOTEL_EXCEL.getPath(),
                BOOKING_SHEET,
                "NumberOfRooms"
        );
    }

    @DataProvider(name = "BookingData")
    public Iterator<Object[]> getBookingData() {
        return ExcelDataProviderConfig.getMultiColumnData(
                EXCEL_FILE_PATH,
                BOOKING_SHEET,
                BOOKING_COLUMNS
        );
    }

    @DataProvider(name = "ExpiryYear")
    public Object[][] ExpiryYearDataByIndex() {
        return ExcelDataProviderConfig.getValueByIndex(
                TestResourcePath.ADACTIN_HOTEL_EXCEL.getPath(),
                PAYMENTS_SHEET,
                "ExpiryYear",
                3
        );
    }

    @DataProvider(name = "CvvNumbers")
    public Iterator<Object[]> getAllCvvNumbers() {
        return ExcelDataProviderConfig.getColumnData(
                TestResourcePath.ADACTIN_HOTEL_EXCEL.getPath(),
                PAYMENTS_SHEET,
                "Cvv"
        );
    }

}
