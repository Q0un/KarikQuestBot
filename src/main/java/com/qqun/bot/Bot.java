package com.qqun.bot;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.qqun.bot.keyboard.Keyboard;
import com.qqun.room.Room;
import com.qqun.user.User;
import com.qqun.user.UserList;
import com.qqun.user.roles.Radiant;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.groupadministration.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.ChatInviteLink;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Bot extends TelegramLongPollingBot {
    private enum State {NOT_STARTED, PRESTARTED, STARTED}

    private final Properties properties = new Properties();
    private final UserList users;
    private final List<Room> rooms;
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
            JsonReader reader = new JsonReader(new FileReader("data/rooms.json"));
            return gson.fromJson(reader, new TypeToken<ArrayList<Room>>(){}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private UserList loadUsers() {
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader("data/users.json"));
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
        } else if (update.hasCallbackQuery()) {
            handleIncomingCallbackQuery(update.getCallbackQuery());
        }
    }

    public void moveUserToGroup(String username, int roomId) {
        User user = users.findByName(username);
        Room room = rooms.get(roomId);
        try {
            moveUserToGroup(user, room);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public final void prestartQuest() {
        for (User user : users) {
            sendMsg(user, "Ролевка скоро начнется, пока прочитай правила:");
            sendMsg(user, "https://...");
        }
    }

    public final void startQuest() {
        state = State.STARTED;
        for (User user : users) {
            if (user.getTypeName().equals("com.qqun.user.roles.Admin")) {
                sendMsg(user, "Ролевка началась! Удачи тебе, надеюсь игроки ниче не сломают...)");
            } else {
                sendMsg(user, "Ролевка началась! Все появляются в нашем мире:");
                user.init();
                moveUserToGroup(user, rooms.get(0));
                sendKeyboard(user);
            }
        }
    }

    public final void setRole(String username, String group, int person) throws NullPointerException {
        User user = users.findByName(username);
        if (user == null) {
            throw new NullPointerException();
        }
        users.remove(user);
        switch (group) {
            case "Radiant":
                users.add(new Radiant(user));
                break;
            case "Dead":

                break;
            case "Inscriber":

                break;
            case "Admin":

                break;
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
        try {
            String message = "Проходи:\n" + getChatInviteLink(room);
            sendMsg(user, message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String getChatInviteLink(Room room) throws TelegramApiException {
        CreateChatInviteLink createChatInviteLink = new CreateChatInviteLink();
        createChatInviteLink.setChatId(room.getChatId());
        createChatInviteLink.setMemberLimit(1);
        ChatInviteLink chatInviteLink = execute(createChatInviteLink);
        return chatInviteLink.getInviteLink();
    }

    private void sendKeyboard(User user) {
        Keyboard keyboard = new Keyboard(user);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        sendMessage.setText("Управление:");
        sendMessage.setReplyMarkup(keyboard);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void openInventory(User user) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        StringBuilder msg = new StringBuilder("Инвентарь \uD83C\uDF92:\n\n");
        for (int i = 0; i < user.getInventory().size(); i++) {
            msg.append(i + 1).append(". ").append(user.getInventory().get(i).getName());
        }
        sendMessage.setText(msg.toString());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void openInscribe(User user) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(user.getChatId());
        sendPhoto.setPhoto(new InputFile(new File("data/img/people.jpg")));
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inscribe = new InlineKeyboardButton();
        inscribe.setText("\uD83D\uDCDD");
        inscribe.setCallbackData("inscribePeople");
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inscribe);
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);
        sendPhoto.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        sendPhoto.setPhoto(new InputFile(new File("data/img/radiant.jpg")));
        inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inscribe.setCallbackData("inscribeRadiant");
        rowList = new ArrayList<>();
        keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inscribe);
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);
        sendPhoto.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        sendPhoto.setPhoto(new InputFile(new File("data/img/dead.jpg")));
        inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inscribe.setCallbackData("inscribeDead");
        rowList = new ArrayList<>();
        keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inscribe);
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);
        sendPhoto.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
            } else if (state == State.PRESTARTED) {
                if (user == null) {
                    user = new User(username, chatId, userId);
                    sendMsg(user, "Извини, но ролевка уже началась :( Возможно поучаствуешь в следующий раз!");
                } else if (user.getState() == User.State.NOT_READY) {
                    sendMsg(user, "Извини, но ролевка уже началась :( Возможно поучаствуешь в следующий раз!");
                }
            } else {
                if (user == null) {
                    user = new User(username, chatId, userId);
                    sendMsg(user, "Извини, но ролевка уже началась :( Возможно поучаствуешь в следующий раз!");
                } else if (user.getState() == User.State.NOT_READY) {
                    sendMsg(user, "Извини, но ролевка уже началась :( Возможно поучаствуешь в следующий раз!");
                } else {
                    if (user.getRole() == User.Role.Admin) {
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

    private void handleIncomingCallbackQuery(CallbackQuery callbackQuery) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(callbackQuery.getMessage().getChatId().toString());
        deleteMessage.setMessageId(callbackQuery.getMessage().getMessageId());
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        String data = callbackQuery.getData();
        User user = users.findByName(callbackQuery.getFrom().getUserName());
        assert user != null;
        if (data.equals("openInventory")) {
            openInventory(user);
        } else if (data.equals("openInscribe")) {
            openInscribe(user);
        }

        sendKeyboard(user);
    }
}
