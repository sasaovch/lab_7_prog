package com.lab.common.util;

import java.util.Objects;

public final class ParsFromEV {

    private ParsFromEV() {
    }

    public static <T> T getFromEV(String name, T defaultParam, ConvertVR<T> converter) {
        String variable = System.getenv(name);
        if (Objects.isNull(variable)) {
            return defaultParam;
        }
        return converter.convert(variable, defaultParam);
    }
}
