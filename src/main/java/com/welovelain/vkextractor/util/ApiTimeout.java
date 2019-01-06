package com.welovelain.vkextractor.util;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class ApiTimeout {

    private final static Duration API_TIMEOUT = Duration.ofMillis(350); // Duration between API calls should be ~ 0.3 seconds

    public static void waitTimeout() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(API_TIMEOUT.toMillis());
    }

}
