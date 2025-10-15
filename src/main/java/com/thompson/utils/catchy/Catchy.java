package com.thompson.utils.catchy;

public final class Catchy {

    public static <R extends AutoCloseable, T> T autoClose(R resource, TryWrapper.ThrowingFunction<R, T> function) throws Exception {
        try (R res = resource) {
            return function.apply(res);
        }
    }

}
