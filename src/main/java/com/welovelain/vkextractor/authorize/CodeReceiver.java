package com.welovelain.vkextractor.authorize;

import com.welovelain.vkextractor.config.ExtractorProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

@RequiredArgsConstructor
@Slf4j
class CodeReceiver {

    private final ExtractorProperties extractorProperties;

    private static final Scanner scanner = new Scanner(System.in);

    private static final String REDIRECT_URI = "https://oauth.vk.com/blank.html";
    private static final String DISPLAY = "page";
    private static final String RESPONSE_TYPE = "code";
    private static final String v = "5.75";

    private static final String SCOPE;

    private static final int PHOTOS_SCOPE = 4;
    private static final int MESSAGES_SCOPE = 4096;


    static {
        int totalScope = PHOTOS_SCOPE + MESSAGES_SCOPE;
        SCOPE = String.valueOf(totalScope);
    }

    String getCode() throws URISyntaxException, IOException {
        String url = new StringBuilder("https://oauth.vk.com/authorize?")
                .append("client_id=").append(extractorProperties.getClientId())
                .append("&display=").append(DISPLAY)
                .append("&redirect_uri=").append(REDIRECT_URI)
                .append("&scope=").append(SCOPE)
                .append("&response_type=").append(RESPONSE_TYPE)
                .append("&v=").append(v)
                .toString();

        Desktop.getDesktop().browse(new URI(url));

        System.out.println("Enter code from browser URL bar: ");

        return scanner.next();
    }
}
