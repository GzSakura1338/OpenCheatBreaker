package com.cheatbreaker.client.util;

public final class SneakyThrow {
    private SneakyThrow() {
    }

    public static RuntimeException sneakyThrow(Throwable throwable) {
        if (throwable == null) {
            throw new NullPointerException("t");
        }
        SneakyThrow.<RuntimeException>sneakyThrow0(throwable);
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void sneakyThrow0(Throwable throwable) throws T {
        throw (T) throwable;
    }
}
