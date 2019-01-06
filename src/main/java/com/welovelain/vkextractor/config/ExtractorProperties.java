package com.welovelain.vkextractor.config;

import com.welovelain.vkextractor.Main;
import lombok.Builder;
import lombok.Value;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Value
@Builder
public class ExtractorProperties {

    private int clientId;
    private String clientSecret;
    private String downloadPath;

    private static final String PROPERTIES_FILENAME = "application.properties";

    public static ExtractorProperties load() throws IOException {

        try (InputStream is = Main.class.getClassLoader().getResourceAsStream(PROPERTIES_FILENAME)) {
            Properties properties = new Properties();
            properties.load(is);
            PropertiesEnvWrapper propertiesWrapper = new PropertiesEnvWrapper(properties);

            return ExtractorProperties.builder()
                    .clientId(Integer.valueOf(propertiesWrapper.getProperty("clientId")))
                    .clientSecret(propertiesWrapper.getProperty("protectedKey"))
                    .downloadPath(propertiesWrapper.getProperty("downloadPath"))
                    .build();
        }
    }


}
