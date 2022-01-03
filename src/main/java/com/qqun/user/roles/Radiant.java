package com.qqun.user.roles;

import com.qqun.user.User;

public class Radiant extends User {

    public Radiant(String username, String chatId, long userId) {
        super(username, chatId, userId);
    }

    public Radiant(User user) {
        super(user);
    }
}
