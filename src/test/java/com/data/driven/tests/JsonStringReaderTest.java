package com.data.driven.tests;

import com.data.driven.base.TestBase;
import com.data.driven.dataProvider.JsonDataProvider;
import com.data.driven.utils.ErrorHandler;
import com.data.driven.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import java.util.List;

public class JsonStringReaderTest extends TestBase {

    private static final Logger logger = LoggerUtils.getLogger(JsonBooleanReaderTest.class);

    private static final String LOCATION_FIELD = "Locations";
    private static final String ROOM_TYPE_FIELD = "RoomTypes";
    private static final String ROOM_COUNT_FIELD = "NumberOfRooms";

    @Test(groups = {"json-loader", "data-loader"})
    public void verifyHotelDataByIndex() {
        try {
            String location = reader.getStringByIndex(LOCATION_FIELD, 5);
            String roomType = reader.getStringByIndex(ROOM_TYPE_FIELD, 2);
            String roomCount = reader.getStringByIndex(ROOM_COUNT_FIELD, 8);

            logger.info("Location: {}", location);
            logger.info("Room Type: {}", roomType);
            logger.info("Room Count: {}", roomCount);

        } catch (Exception error) {
            ErrorHandler.logError(error, "verifyHotelDataByIndex", "Failed to read hotel data by index");
            throw new RuntimeException("Failed to read hotel data by index", error);
        }
    }

    @Test(groups = {"json-loader", "data-loader"}, dataProvider = "AdultsPerRoomDataList", dataProviderClass = JsonDataProvider.class)
    public void verifyAdultsPerRoomData(String adultsPerRoom) {
        try {
            logger.info("Adults Per Room: {}", adultsPerRoom);
        } catch (Exception error) {
            ErrorHandler.logError(error, "verifyAdultsPerRoomData", "Failed to read adults per room data");
            throw new RuntimeException("Failed to read adults per room data", error);
        }
    }
}