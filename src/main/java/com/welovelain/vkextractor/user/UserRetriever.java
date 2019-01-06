package com.welovelain.vkextractor.user;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.welovelain.vkextractor.util.ApiTimeout;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class UserRetriever {

    private final VkApiClient vk;
    private final UserActor actor;

    private final static Map<Integer, UserXtrCounters> idToUserCacheMap = new HashMap<>();

    public UserXtrCounters getUserById(Integer userId) throws ApiException, ClientException, InterruptedException {

        if (idToUserCacheMap.containsKey(userId)) {
            return idToUserCacheMap.get(userId);
        } else {
            UserXtrCounters users = vk.users()
                    .get(actor)
                    .userIds(userId.toString())
                    .execute()
                    .get(0);
            idToUserCacheMap.put(userId, users);
            ApiTimeout.waitTimeout();

            return users;
        }
    }

}
