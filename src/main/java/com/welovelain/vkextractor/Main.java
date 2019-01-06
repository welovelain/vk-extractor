package com.welovelain.vkextractor;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.welovelain.vkextractor.authorize.AuthorizationService;
import com.welovelain.vkextractor.config.ExtractorProperties;
import com.welovelain.vkextractor.menu.Actions;
import com.welovelain.vkextractor.menu.VkMenu;
import com.welovelain.vkextractor.messages.printer.FileDialogPrinter;
import com.welovelain.vkextractor.messages.service.MessagesExtractorService;
import com.welovelain.vkextractor.photos.service.PhotosExtractorService;
import com.welovelain.vkextractor.user.UserRetriever;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) throws Exception {

        ExtractorProperties extractorProperties = ExtractorProperties.load();
        log.info("Loaded vk properties: " + extractorProperties);

        VkApiClient vk = new VkApiClient(HttpTransportClient.getInstance());
        UserActor userActor = new AuthorizationService(extractorProperties, vk).getUserActor();

        UserRetriever userRetriever = new UserRetriever(vk, userActor);
        MessagesExtractorService messagesExtractorService = new MessagesExtractorService(vk, userActor, userRetriever, new FileDialogPrinter(extractorProperties));
        PhotosExtractorService photosExtractorService = new PhotosExtractorService(extractorProperties, vk, userActor);


        while (true) {
            Actions actions = new VkMenu().run();
            switch (actions) {
                case DOWNLOAD_ALL_MESSAGES:
                    messagesExtractorService.extract();
                    break;

                case DOWNLOAD_ALL_PHOTOS:
                    photosExtractorService.extract();
                    break;

                default:
                    System.exit(0);
            }
            log.info("Operation complete");
        }

    }

}
