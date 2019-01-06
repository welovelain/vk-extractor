package com.welovelain.vkextractor.authorize;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.welovelain.vkextractor.config.ExtractorProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserActorBuilder {

    private final ExtractorProperties extractorProperties;
    private final VkApiClient vk;
    private final String code;

    private static final String REDIRECT_URI = "https://oauth.vk.com/blank.html";

    public UserActor getUserActor() throws ApiException, ClientException {
        UserAuthResponse authResponse = vk.oauth()
                .userAuthorizationCodeFlow(extractorProperties.getClientId(), extractorProperties.getClientSecret(), REDIRECT_URI, code)
                .execute();
        return new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
    }
}
