package com.welovelain.vkextractor.photos.service;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.photos.PhotoAlbumFull;
import com.welovelain.vkextractor.config.ExtractorProperties;
import com.welovelain.vkextractor.photos.task.SavePhotoTask;
import com.welovelain.vkextractor.util.ApiTimeout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
public class PhotosExtractorService {

    private final ExtractorProperties extractorProperties;
    private final VkApiClient vk;
    private final UserActor actor;

    private static final String DOWNLOAD_PATH_DIR = "photos";
    private static final int PHOTOS_COUNT = 100;

    public void extract() throws Exception {

        final ExecutorService executor = Executors.newFixedThreadPool(8); // todo make executor as field and use countDownLatch

        createDir(getDownloadDir());

        vk.photos().getAlbums(actor).execute().getItems()
                .forEach(album -> downloadAlbum(album, executor));

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }


    private void downloadAlbum(PhotoAlbumFull album, ExecutorService executor) {
        log.info(String.format("Start download album %s :: %d photos", album.getTitle(), album.getSize()));

        String albumDir = getDownloadAlbumDir(album);
        createDir(albumDir);

        try {
            for (int offset = 0; offset < album.getSize(); offset += PHOTOS_COUNT) {
                SavePhotoTask savePhotoTask = new SavePhotoTask(vk, actor, album, albumDir, offset, PHOTOS_COUNT);
                executor.execute(savePhotoTask);
                ApiTimeout.waitTimeout();

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private String getDownloadAlbumDir(PhotoAlbumFull album) {
        return getDownloadDir() + File.separator + album.getTitle();
    }

    private String getDownloadDir() {
        return extractorProperties.getDownloadPath() + File.separator + DOWNLOAD_PATH_DIR;
    }


    private void createDir(String path) {
        File file = new File(path);
        if (!file.exists() && !file.mkdirs()) {
            throw new RuntimeException("Directory couldn't be created");
        }
    }


}
