package com.welovelain.vkextractor.menu;

import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class VkMenu {

    public Actions run() {

        System.out.println("1: extract messages");
        System.out.println("2: extract photos");

        Scanner scanner = new Scanner(System.in);
        int val = scanner.nextInt();

        switch (val) {
            case 1: return Actions.DOWNLOAD_ALL_MESSAGES;
            case 2: return Actions.DOWNLOAD_ALL_PHOTOS;
            default: {
                throw new RuntimeException("Unknown action");
            }
        }
    }
}
