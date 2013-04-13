package com.thoughtworks.kunwu;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

public class CollectionUtils {
    public static <F, T> T[] arrayTransform(F[] inputArray, Function<F, T> func) {
        T[] targetArray = (T[]) new Object[inputArray.length];
        for (int i = 0; i < inputArray.length; i++) {
            T target = func.apply(inputArray[i]);
            targetArray[i] = target;
        }

        return targetArray;
    }


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
