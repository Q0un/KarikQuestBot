package com.qqun.user;

import java.util.ArrayList;

public class User {
    public enum State {NEW_USER, NOT_READY, READY, ADMIN}

    private final String username;
    private final String chatId;
    private final long userId;
    private State state;

    public User(String username, String chatId, long userId) {
        this.username = username;
        this.chatId = chatId;
        this.userId = userId;
        state = State.NEW_USER;
    }

    public final void makeReady() {
        state = State.READY;
    }

    public final void verify() {
        state = State.NOT_READY;
    }

    public final String getUsername() {
        return username;
    }

    public final State getState() {
        return state;
    }

    public final String getChatId() {
        return chatId;
    }

    public final long getUserId() {
        return userId;
    }
}