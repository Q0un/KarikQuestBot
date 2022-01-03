package com.qqun;

import com.qqun.bot.Bot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            Bot bot = new Bot();
            telegramBotsApi.registerBot(bot);
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String inp = scanner.next();
                String username;
                switch (inp) {
                    case "save":
                        bot.saveData();
                        break;
                    case "stop":
                        bot.saveData();
                        System.exit(0);
                        break;
                    case "startQuest":
                        bot.startQuest();
                        break;
                    case "prestart":
                        bot.prestartQuest();
                        break;
                    case "move":
                        username = scanner.next();
                        int roomId = scanner.nextInt();
                        bot.moveUserToGroup(username, roomId);
                        break;
                    case "setRole":
                        username = scanner.next();
                        String group = scanner.next();
                        String person = scanner.next();
                        try {
                            bot.setRole(username, group, person);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
