package com.qqun;

import com.qqun.bot.Bot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
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
                String inp = scanner.nextLine();
                switch (inp) {
                    case "save":
                        bot.saveData();
                        break;
                    case "stop":
                        bot.saveData();
                        System.exit(0);
                    case "startQuest":
                        bot.startQuest();
                        break;
                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
