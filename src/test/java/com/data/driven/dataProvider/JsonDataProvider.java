package com.data.driven.dataProvider;

import com.data.driven.config.dataProvider.JsonDataProviderConfig;
import com.data.driven.utils.ErrorHandler;
import org.testng.annotations.DataProvider;

import java.util.Iterator;

public class JsonDataProvider {

    @DataProvider(name = "AdultsPerRoomDataList")
    public Iterator<Object[]> AdultsPerRoomDataList() {
        try {
         return JsonDataProviderConfig.getStringDataList("AdultsPerRoom");
        } catch (Exception error) {
            ErrorHandler.logError(error, "getAdultsPerDataList", "Failed to retrieve adults per room data list");
            throw new RuntimeException("Failed to retrieve adults per room data list", error);
        }
    }

    @DataProvider(name = "ExpiryYearDataList")
    public Iterator<Object[]> ExpiryYearDataList() {
        try {
            return JsonDataProviderConfig.getIntegerDataList("ExpiryYear");
        } catch (Exception error) {
            ErrorHandler.logError(error, "ExpiryYearDataList", "Failed to retrieve expiry year data list");
            throw new RuntimeException("Failed to retrieve expiry year data list", error);
        }
    }

    @DataProvider(name = "DepositDataList")
    public Iterator<Object[]> DepositDataList() {
        try {
            return JsonDataProviderConfig.getBooleanDataList("Deposit");
        } catch (Exception error) {
            ErrorHandler.logError(error, "DepositDataList", "Failed to retrieve deposit data list");
            throw new RuntimeException("Failed to retrieve deposit data list", error);
        }
    }

}
