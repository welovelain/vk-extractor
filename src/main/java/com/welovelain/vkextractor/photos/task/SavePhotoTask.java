package com.welovelain.vkextractor.photos.task;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoAlbumFull;
import com.welovelain.vkextractor.util.ApiTimeout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class SavePhotoTask implements Runnable {

    private final VkApiClient vk;
    private final UserActor actor;

    private final PhotoAlbumFull album;
    private final String albumDir;

    private final int offset;
    private final int count;

    private final static int TRY_ATTEMPT_N = 10;

    @Override
    public void run() {
        log.info(String.format("Album %s, Offset: %d ...", album.getTitle(), offset));

        try {
            List<Photo> photos = vk.photos().get(actor)
                    .ownerId(actor.getId())
                    .albumId(String.valueOf(album.getId()))
                    .offset(offset)
                    .count(count)
                    .execute()
                    .getItems();


            for (int i = 0; i < photos.size(); ++i) {
                String name = (offset + i) + ".jpg";
                savePhoto(photos.get(i), name, albumDir);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void savePhoto(Photo photo, String name, String albumDir) {
        try {
            String nameToSave = albumDir + File.separator + name;
            File savedPhoto = new File(nameToSave);
            if (savedPhoto.exists()) {
                log.info("Photo with name " + name + "already exists");
                return;
            }

            int tryAttempt = TRY_ATTEMPT_N;
            while (tryAttempt > 0) {
                try {
                    FileUtils.copyURLToFile(getPhotoMaxSizeURL(photo), savedPhoto, Integer.MAX_VALUE, Integer.MAX_VALUE);
                    return;
                } catch (Exception e) {
                    tryAttempt--;
                    ApiTimeout.waitTimeout();
                }
            }

            log.warn("Failed to save photo with id {}, all tries failed", photo.getId());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    private URL getPhotoMaxSizeURL(Photo photo) {
        try {
            if (photo.getPhoto2560() != null) {
                return new URL(photo.getPhoto2560());
            }
            if (photo.getPhoto1280() != null) {
                return new URL(photo.getPhoto1280());
            }
            if (photo.getPhoto807() != null) {
                return new URL(photo.getPhoto807());
            }
            if (photo.getPhoto604() != null) {
                return new URL(photo.getPhoto604());
            }
            if (photo.getPhoto130() != null) {
                return new URL(photo.getPhoto130());
            }
            if (photo.getPhoto75() != null) {
                return new URL(photo.getPhoto75());
            }

            throw new RuntimeException("No Photo size found: Available sizes: " + photo.getSizes());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

}
