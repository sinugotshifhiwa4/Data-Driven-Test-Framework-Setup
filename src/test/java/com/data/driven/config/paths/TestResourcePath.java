package com.data.driven.config.paths;

public enum TestResourcePath {

    ADACTIN_HOTEL_JSON("json/AdactinHotelTestData.json"),
    ADACTIN_HOTEL_SCHEMA("json/AdactinHotelDataSchema.json"),
    ADACTIN_HOTEL_EXCEL("excel/AdactinHotelTestData.xlsx");

    private static final String ROOT_PATH = "src/test/resources/testData/";
    private final String filePath;

    TestResourcePath(String path) {
        this.filePath = ROOT_PATH + path;
    }

    public String getPath() {
        return filePath;
    }
}