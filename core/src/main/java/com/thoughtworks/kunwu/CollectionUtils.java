package com.thoughtworks.kunwu;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

public class CollectionUtils {
    public static <T> T[] arrayFilter(T[] inputArray, Predicate<T> predicate) {
        List<T> outputList = Lists.newArrayList();
        for (T input : inputArray) {
            if (predicate.apply(input)) {
                outputList.add(input);
            }
        }

        return outputList.toArray(Arrays.copyOf(inputArray, 0));
    }

}
