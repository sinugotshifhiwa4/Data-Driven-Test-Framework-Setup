package com.data.driven.tests;

import com.data.driven.dataProvider.ExcelMultiSheetDataProvider;
import com.data.driven.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

public class ExcelBookingDataTest {

    private static final Logger logger = LoggerUtils.getLogger(ExcelBookingDataTest.class);

    @Test(dataProvider = "BookingData",
            dataProviderClass = ExcelMultiSheetDataProvider.class,
            groups = {"excel-loader", "data-loader"})
    public void shouldRetrieveCompleteBookingWithPaymentData(
            String location,
            String hotels,
            String roomType,
            String numberOfRooms,
            String adultsPerRoom,
            String childrenPerRoom,
            String firstName,
            String lastName,
            String billingAddress,
            String creditCardType,
            int expiryYear,
            int cvv
    ) {
        logger.info("Retrieved Booking Data - " +
                        "Location: {}, " +
                        "Hotels: {}, " +
                        "Room Type: {}, " +
                        "Number of Rooms: {}, " +
                        "Adults Per Room: {}, " +
                        "Children Per Room: {}, " +
                        "First Name: {}, " +
                        "Last Name: {}, " +
                        "Billing Address: {}, " +
                        "Credit Card Type: {}, " +
                        "Expiry Year: {}, " +
                        "Cvv: {}",
                location, hotels, roomType, numberOfRooms,
                adultsPerRoom, childrenPerRoom, firstName, lastName,
                billingAddress, creditCardType, expiryYear, cvv);
    }
}
