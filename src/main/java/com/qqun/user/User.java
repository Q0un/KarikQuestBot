package com.qqun.user;

import java.util.ArrayList;

public class User {
    public enum State {NOT_READY, READY}

    private String username;
    private State state;

    public final String getUsername() {
        return username;
    }

    public final void makeReady() {
        state = State.READY;
    }

    public final State getState() {
        return state;
    }
}