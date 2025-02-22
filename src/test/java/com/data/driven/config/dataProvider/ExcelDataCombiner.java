package com.data.driven.config.dataProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelDataCombiner {

    /**
     * Combines multiple arrays of data into a single array
     * @param arrays Variable number of Object arrays to combine
     * @return Combined array containing all elements
     */
    public static Object[] combineArrays(Object[]... arrays) {
        int totalLength = calculateTotalLength(arrays);
        Object[] result = new Object[totalLength];

        int currentIndex = 0;
        for (Object[] array : arrays) {
            System.arraycopy(array, 0, result, currentIndex, array.length);
            currentIndex += array.length;
        }
        return result;
    }

    /**
     * Calculates total length needed for combined array
     */
    private static int calculateTotalLength(Object[]... arrays) {
        int totalLength = 0;
        for (Object[] array : arrays) {
            totalLength += array.length;
        }
        return totalLength;
    }

    /**
     * Combines data from multiple iterators into a single list
     * @param iterators List of iterators to combine
     * @return Combined list of data
     */
    public static List<Object[]> combineIteratorData(List<Iterator<Object[]>> iterators) {
        List<Object[]> combinedData = new ArrayList<>();

        while (allIteratorsHaveNext(iterators)) {
            List<Object[]> currentRows = new ArrayList<>();
            for (Iterator<Object[]> iterator : iterators) {
                currentRows.add(iterator.next());
            }
            combinedData.add(combineArrays(currentRows.toArray(new Object[0][])));
        }

        return combinedData;
    }

    /**
     * Checks if all iterators have next element
     */
    private static boolean allIteratorsHaveNext(List<Iterator<Object[]>> iterators) {
        return iterators.stream().allMatch(Iterator::hasNext);
    }
}
