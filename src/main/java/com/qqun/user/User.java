package com.qqun.user;

import java.util.ArrayList;

public class User {
    public enum State {NEW_USER, NOT_READY, READY}

    private String username;
    private State state;
    private String chatId;

    public User(String username, String chatId) {
        this.username = username;
        this.chatId = chatId;
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
}