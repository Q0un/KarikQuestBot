package com.qqun.user;

import com.google.gson.annotations.SerializedName;
import com.qqun.cooldown.Cooldown;
import com.qqun.item.Item;
import com.qqun.room.Room;

import java.util.ArrayList;
import java.util.List;

public class User {
    public enum State {NEW_USER, NOT_READY, READY}
    public enum Role {User, Radiant, Dead, Inscriber, Admin}

    @SerializedName("type")
    private final String typeName;

    private final String username;
    private final String chatId;
    private final long userId;
    private final Role role;
    private Room room;
    private State state;
    private List<Item> inventory;

    public User(User user) {
        typeName = user.typeName;
        role = user.role;
        username = user.username;
        chatId = user.chatId;
        userId = user.userId;
        state = State.valueOf(user.state.name());
        room = user.room;
        inventory = new ArrayList<>(user.inventory);
    }

    public User(String username, String chatId, long userId) {
        typeName = getClass().getName();

        this.role = Role.valueOf(getClass().getSimpleName());
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

    public final void setRoom(Room room) {
        this.room = room;
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

    public final List<Item> getInventory() {
        return inventory;
    }

    public final String getTypeName() {
        return typeName;
    }

    public final Role getRole() {
        return role;
    }

    public final Room getRoom() {
        return room;
    }
}