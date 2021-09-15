package com.qqun.bot;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.qqun.user.User;
import com.qqun.user.UserList;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Bot extends TelegramLongPollingBot {
    private enum State {NOT_STARTED, STARTED}

    private final Properties properties = new Properties();
    private final UserList users;
    private State state;

    public Bot() {
        super();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        users = loadUsers();
        state = State.NOT_STARTED;
    }

    private UserList loadUsers() {
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader("users.json"));
            return gson.fromJson(reader, new TypeToken<UserList>(){}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public final void saveData() {
        users.save();
    }

    @Override
    public final String getBotUsername() {
        return properties.getProperty("username");
    }

    @Override
    public final String getBotToken() {
        return properties.getProperty("token");
    }

    @Override
    public final void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            handleIncomingMessage(update.getMessage());
        }
    }

    public final void sendMsg(String chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public final void startQuest() {
        state = State.STARTED;
    }

    private void handleIncomingMessage(Message message) {
        String username = message.getFrom().getUserName();
        User user = users.findByName(username);
        String text = message.getText();
        String chatId = message.getChatId().toString();
        if (state == State.NOT_STARTED) {
            if (text.equals("/start")) {
                if (user == null) {
                    sendMsg(chatId, "Тебя нет в списках! Ты точно регался на квест?");
                } else if (user.getState() == User.State.NOT_READY) {
                    sendMsg(chatId, "О, привет! Раз ты тут, то возможно квест скоро начнется :)");
                    user.makeReady();
                }
            }
        } else {
            if (user == null || user.getState() == User.State.NOT_READY) {
                sendMsg(chatId, "Извини, но квест уже начался( Возможно поучаствуешь в следующий раз!");
            } else {

            }
        }
    }
}
