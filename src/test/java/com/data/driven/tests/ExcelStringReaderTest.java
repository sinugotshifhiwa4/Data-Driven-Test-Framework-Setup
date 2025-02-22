package com.data.driven.tests;

import com.data.driven.base.TestBase;
import com.data.driven.dataProvider.ExcelDataProvider;
import com.data.driven.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

public class ExcelStringReaderTest extends TestBase {

    private static final Logger logger = LoggerUtils.getLogger(ExcelStringReaderTest.class);

    @Test(dataProvider = "Location",
            dataProviderClass = ExcelDataProvider.class,
            groups = {"excel-loader", "data-loader"})
    public void shouldRetrieveLocationData(String location) {
        logger.info("Retrieved Location: {}", location);
    }

    @Test(dataProvider = "NumberOfRooms",
            dataProviderClass = ExcelDataProvider.class,
            groups = {"excel-loader", "data-loader"})
    public void shouldRetrieveRoomQuantityData(String numberOfRooms) {
        logger.info("Retrieved Number of Rooms: {}", numberOfRooms);
    }

    @Test(dataProvider = "BookingData",
            dataProviderClass = ExcelDataProvider.class,
            groups = {"excel-loader", "data-loader"})
    public void shouldRetrieveCompleteBookingData(
            String location,
            String hotel,
            String roomType,
            String numberOfRooms,
            String adultsPerRoom,
            String childrenPerRoom
    ) {
        logger.info("Retrieved Booking Data - " +
                        "Location: {}, " +
                        "Hotel: {}, " +
                        "Room Type: {}, " +
                        "Number of Rooms: {}, " +
                        "Adults Per Room: {}, " +
                        "Children Per Room: {}",
                location, hotel, roomType, numberOfRooms, adultsPerRoom, childrenPerRoom);
    }
}
