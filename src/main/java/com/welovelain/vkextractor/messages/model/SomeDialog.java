package com.welovelain.vkextractor.messages.model;

import com.vk.api.sdk.objects.users.UserXtrCounters;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class SomeDialog {

    private final UserXtrCounters user;
    private List<SomeMessage> messages = new ArrayList<>();

    public void addMessages(List<SomeMessage> messages) {
        this.messages.addAll(messages);
    }
}
