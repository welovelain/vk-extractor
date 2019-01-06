package com.welovelain.vkextractor.messages.printer;

import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.welovelain.vkextractor.config.ExtractorProperties;
import com.welovelain.vkextractor.messages.model.SomeDialog;
import com.welovelain.vkextractor.messages.model.SomeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
public class FileDialogPrinter implements DialogPrinter {

    private final ExtractorProperties extractorProperties;

    private static final String DOWNLOAD_PATH_DIR = "photos";

    @Override
    public void print(SomeDialog dialog) {

        UserXtrCounters user = dialog.getUser();
        String fileName = getDialogFileName(user);

        try (PrintWriter printWriter = new PrintWriter(fileName, "UTF-8"); BufferedWriter writer = new BufferedWriter(printWriter)) {

            log.info(String.format("Printing dialog with %s, amount of messages: %d", dialog.getUser().getLastName(), dialog.getMessages().size()));

            List<SomeMessage> messages = dialog.getMessages();

            Date dayDate = Date.from(messages.get(0).getDate().truncatedTo(ChronoUnit.DAYS));
            writer.append(convertDayDate(dayDate.toInstant())).append("\r\n");

            for (SomeMessage message : messages) {

                if (!message.getMessage().getBody().isEmpty()) {

                    String userName = getUserFullname(message.getUser());
                    Instant newDate = message.getDate();

                    Date newDayDate = Date.from(newDate.truncatedTo(ChronoUnit.DAYS));
                    if (newDayDate.after(dayDate)) {
                        dayDate = newDayDate;
                        writer.append(convertDayDate(dayDate.toInstant())).append("\r\n");
                    }

                    writer
                            .append(userName).append(" ").append(convertTimeDate(message.getDate()))
                            .append("\r\n")
                            .append(message.getMessage().getBody())
                            .append("\r\n")
                            .append("\r\n");
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    private String getUserFullname(UserXtrCounters user) {
        return user.getFirstName() + " " + user.getLastName();
    }

    private String getDialogFileName(UserXtrCounters user) {
        return getDownloadDir() + File.separator + getUserFullname(user) + ".txt";
    }

    private String getDownloadDir() {
        return extractorProperties.getDownloadPath() + File.separator + DOWNLOAD_PATH_DIR;
    }

    private String convertDayDate(Instant instant) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yy").withZone(ZoneOffset.UTC).withLocale(Locale.getDefault());
        return dateTimeFormatter.format(instant);
    }

    private String convertTimeDate(Instant instant) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneOffset.UTC).withLocale(Locale.getDefault());
        return dateTimeFormatter.format(instant);
    }
}
