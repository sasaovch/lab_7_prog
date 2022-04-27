package com.lab.common.util;

import java.util.Objects;

public class ParsFromVR {
    public static <T> T getFromVR(String name, T defaultParam, ConvertVR<T> converter) {
        String variable = System.getenv(name);
        if (Objects.isNull(variable)) {
            return defaultParam;
        }
        return converter.convert(variable, defaultParam);
    }
}
