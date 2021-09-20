package com.qqun.user;

import com.qqun.item.Item;

import java.util.ArrayList;
import java.util.List;

public class User {
    public enum State {NEW_USER, NOT_READY, READY}

    private final String username;
    private final String chatId;
    private final long userId;
    private Role role;
    private State state;
    private List<Item> inventory;

    public User(String username, String chatId, long userId) {
        this.username = username;
        this.chatId = chatId;
        this.userId = userId;
        state = State.NEW_USER;
        inventory = new ArrayList<>();
    }

    public final void init() {

    }

    public final void makeReady() {
        state = State.READY;
    }

    public final void setRole(Role role) {
        this.role = role;
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

    public final Role getRole() {
        return role;
    }

    public final List<Item> getInventory() {
        return inventory;
    }
}