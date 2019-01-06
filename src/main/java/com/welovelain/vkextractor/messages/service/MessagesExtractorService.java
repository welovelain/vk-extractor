package com.welovelain.vkextractor.messages.service;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiCaptchaException;
import com.vk.api.sdk.objects.messages.Dialog;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.responses.GetDialogsResponse;
import com.vk.api.sdk.objects.messages.responses.GetHistoryResponse;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.welovelain.vkextractor.messages.model.SomeDialog;
import com.welovelain.vkextractor.messages.model.SomeMessage;
import com.welovelain.vkextractor.messages.printer.DialogPrinter;
import com.welovelain.vkextractor.user.UserRetriever;
import com.welovelain.vkextractor.util.ApiTimeout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
@Slf4j
public class MessagesExtractorService {

    private final VkApiClient vk;
    private final UserActor actor;
    private final UserRetriever userRetriever;

    private final DialogPrinter dialogPrinter;

    private final static int EXTRACT_STEP = 200;
    private final static Scanner scanner = new Scanner(System.in);

    public void extract() throws Exception {
        GetDialogsResponse response = vk.messages().getDialogs(actor).count(200).execute();

        List<SomeDialog> dialogs = new ArrayList<>();
        List<Dialog> vkDialogs = response.getItems();

        for (int i = 0; i < vkDialogs.size(); ++i) {
            log.info("Extracting dialog {} of {}", i, vkDialogs.size());

            Dialog dialog = vkDialogs.get(i);
            Integer friendId = dialog.getMessage().getUserId();
            SomeDialog someDialog = extractAllMessages(friendId);
            dialogs.add(someDialog);
        }

        dialogs.forEach(dialogPrinter::print);
    }

    private SomeDialog extractAllMessages(int friendId) throws Exception {

        UserXtrCounters friend = userRetriever.getUserById(friendId);
        SomeDialog someDialog = new SomeDialog(friend);

        ApiTimeout.waitTimeout();
        int totalCount = vk.messages()
                .getHistory(actor)
                .userId(friendId)
                .count(0)
                .execute()
                .getCount();

        ApiTimeout.waitTimeout();

        GetHistoryResponse historyResponse;
        String sid = null;
        String captchaKey = null;

        int i = 0;
        while (i < totalCount) {

            try {
                historyResponse = vk.messages().getHistory(actor).userId(friendId).count(EXTRACT_STEP).offset(i)
                        .captchaKey(captchaKey).captchaSid(sid)
                        .execute();

                addMessagesToDialog(someDialog, historyResponse);

                sid = null;
                captchaKey = null;
                i += EXTRACT_STEP;

                ApiTimeout.waitTimeout();

            } catch (ApiCaptchaException e) {
                Desktop.getDesktop().browse(new URI(e.getImage()));
                log.info("Enter captcha: ");

                captchaKey = scanner.next();
                sid = e.getSid();
            }
        }

        Collections.reverse(someDialog.getMessages());

        return someDialog;
    }

    private void addMessagesToDialog(SomeDialog someDialog, GetHistoryResponse historyResponse) throws Exception {

        List<SomeMessage> messages = new ArrayList<>(historyResponse.getItems().size());

        for (Message message : historyResponse.getItems()) {
            UserXtrCounters user = userRetriever.getUserById(message.getFromId());

            Instant date = Instant.ofEpochSecond(message.getDate());
            SomeMessage someMessage = new SomeMessage(date, user, message);
            messages.add(someMessage);
        }

        someDialog.addMessages(messages);
    }



}
