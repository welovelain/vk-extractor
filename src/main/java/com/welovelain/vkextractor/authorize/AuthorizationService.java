package com.welovelain.vkextractor.authorize;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.welovelain.vkextractor.config.ExtractorProperties;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.URISyntaxException;

@RequiredArgsConstructor
public class AuthorizationService {

    private final ExtractorProperties extractorProperties;
    private final VkApiClient vk;

    private static final String REDIRECT_URI = "https://oauth.vk.com/blank.html";

    public UserActor getUserActor() throws ApiException, ClientException, URISyntaxException, IOException {
        String code = new CodeReceiver(extractorProperties)
                .getCode();

        UserAuthResponse authResponse = vk.oauth()
                .userAuthorizationCodeFlow(extractorProperties.getClientId(), extractorProperties.getClientSecret(), REDIRECT_URI, code)
                .execute();
        return new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
    }
}
