package com.qqun.room;

public class Room {
    private final String chatId;

    public Room(String chatId) {
        this.chatId = chatId;
    }

    public final String getChatId() {
        return chatId;
    }
}
