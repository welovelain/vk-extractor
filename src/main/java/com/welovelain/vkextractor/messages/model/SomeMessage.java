package com.welovelain.vkextractor.messages.model;

import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import lombok.Value;

import java.time.Instant;

@Value
public class SomeMessage {

    private Instant date;
    private UserXtrCounters user;
    private Message message;

    @Override
    public String toString() {
        return message.getBody();
    }

}
