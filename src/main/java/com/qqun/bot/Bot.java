package com.qqun.bot;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.qqun.room.Room;
import com.qqun.user.User;
import com.qqun.user.UserList;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.groupadministration.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.ChatInviteLink;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class Bot extends TelegramLongPollingBot {
    private enum State {NOT_STARTED, STARTED}

    private final Properties properties = new Properties();
    private final UserList users;
    private final ArrayList<Room> rooms;
    private State state;

    public Bot() {
        super();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        users = loadUsers();
        rooms = loadRooms();
        state = State.NOT_STARTED;
    }

    private ArrayList<Room> loadRooms() {
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader("rooms.json"));
            return gson.fromJson(reader, new TypeToken<ArrayList<Room>>(){}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
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

    private void sendMsg(User user, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(user.getChatId());
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void forwardMsg(User user, Message message) {
        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setChatId(user.getChatId());
        forwardMessage.setMessageId(message.getMessageId());
        forwardMessage.setFromChatId(message.getChatId().toString());
        try {
            execute(forwardMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void moveUserToGroup(User user, Room room) {
        for (Room roomId : rooms) {
            BanChatMember banChatMember = new BanChatMember();
            banChatMember.setChatId(roomId.getChatId());
            banChatMember.setUserId(user.getUserId());
            try {
                execute(banChatMember);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        UnbanChatMember unbanChatMember = new UnbanChatMember();
        unbanChatMember.setChatId(room.getChatId());
        unbanChatMember.setUserId(user.getUserId());
        try {
            execute(unbanChatMember);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        sendMsg(user, getChatInviteLink(room));
    }

    public void moveUserToGroup(String username, int roomId) {
        User user = users.findByName(username);
        if (user == null) {
            return;
        }
        Room room = rooms.get(roomId);
        if (room == null) {
            return;
        }
        moveUserToGroup(user, room);
    }

    public String getChatInviteLink(Room room) {
        CreateChatInviteLink createChatInviteLink = new CreateChatInviteLink();
        createChatInviteLink.setChatId(room.getChatId());
        createChatInviteLink.setMemberLimit(1);
        try {
            ChatInviteLink chatInviteLink = execute(createChatInviteLink);
            return chatInviteLink.getInviteLink();
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return "";
        }
    }

    public final void startQuest() {
        state = State.STARTED;
        for (User user : users) {
            sendMsg(user, "Ролевка началась! Все появляются в нашем мире:");
            if (user.getState() != User.State.ADMIN) {
                moveUserToGroup(user, rooms.get(0));
            }
        }
    }

    private void handleIncomingMessage(Message message) {
        if (message.isUserMessage()) {
            String username = message.getFrom().getUserName();
            User user = users.findByName(username);
            long userId = message.getFrom().getId();
            String text = message.getText();
            String chatId = message.getChatId().toString();
            if (state == State.NOT_STARTED) {
                if (user != null && user.getState() == User.State.NEW_USER) {
                    if (text.equals(properties.getProperty("passwd"))) {
                        sendMsg(user, "О, привет! Раз ты тут, то возможно ролевка скоро начнется :)");
                        user.makeReady();
                    } else {
                        sendMsg(user, "Извини, но пароль неверный :(");
                    }
                }
                if (text.equals("/start")) {
                    if (user == null) {
                        user = users.addUser(username, chatId, userId);
                        sendMsg(user, "Введи пароль чтобы зарегаться на ролевку!");
                    } else if (user.getState() == User.State.NOT_READY) {
                        sendMsg(user, "О, привет! Раз ты тут, то возможно ролевка скоро начнется :)");
                        user.makeReady();
                    }
                }
            } else {
                if (user == null) {
                    user = new User(username, chatId, userId);
                    sendMsg(user, "Извини, но ролевка уже началась :( Возможно поучаствуешь в следующий раз!");
                } else if (user.getState() == User.State.NOT_READY) {
                    sendMsg(user, "Извини, но ролевка уже началась :( Возможно поучаствуешь в следующий раз!");
                } else {
                    if (user.getState() == User.State.ADMIN) {
                        if (message.isReply()) {
                            Message source = message.getReplyToMessage();
                            if (source.getForwardFrom() == null) {
                                sendMsg(users.get(0), "Надо ответить на одно из сообщений пользователей.");
                            } else {
                                User userFrom = users.findByName(source.getForwardFrom().getUserName());
                                assert userFrom != null;
                                sendMsg(userFrom, message.getText());
                            }
                        } else {
                            sendMsg(users.get(0), "Надо ответить на одно из сообщений пользователей.");
                        }
                    } else {
                        forwardMsg(users.get(0), message);
                    }
                }
            }
        } else if (message.isSuperGroupMessage()) {
            if (!message.hasText() && message.getFrom().getIsBot()) {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setMessageId(message.getMessageId());
                deleteMessage.setChatId(message.getChatId().toString());
                try {
                    execute(deleteMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
