package com.welovelain.vkextractor.messages.printer;

import com.welovelain.vkextractor.messages.model.SomeDialog;
import com.welovelain.vkextractor.messages.model.SomeMessage;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConsoleDialogPrinter implements DialogPrinter {

    @Override
    public void print(SomeDialog dialog) {
        System.out.println("Printing dialog, friend: " + dialog.getUser().getLastName() +", message size: " + dialog.getMessages().size());
        List<SomeMessage> messages = dialog.getMessages();

        Date dayDate = Date.from(messages.get(0).getDate().truncatedTo(ChronoUnit.DAYS));
        System.out.println(convertDayDate(dayDate.toInstant()) + "\r\n");

        for (SomeMessage message: messages) {
            if (!message.getMessage().getBody().isEmpty()) {
                String userName = message.getUser().getFirstName() + " " + message.getUser().getLastName();
                Instant newDate = message.getDate();

                Date newDayDate = Date.from(newDate.truncatedTo(ChronoUnit.DAYS));
                if (newDayDate.after(dayDate)) {
                    dayDate = newDayDate;
                    System.out.println(convertDayDate(dayDate.toInstant()) + "\r\n");
                }


                String date = convertTimeDate(message.getDate());
                String messageBody = message.getMessage().getBody();

                System.out.println(userName + " " + date + "\r\n" + messageBody + "\r\n");
            }
        }
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
