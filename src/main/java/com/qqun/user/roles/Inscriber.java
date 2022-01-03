package com.qqun.user.roles;

import com.qqun.user.User;

public class Inscriber extends User {

    public Inscriber(String username, String chatId, long userId) {
        super(username, chatId, userId);
    }

    public Inscriber(User user) {
        super(user);
    }
}
