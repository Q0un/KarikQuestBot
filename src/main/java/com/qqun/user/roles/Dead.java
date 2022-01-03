package com.qqun.user.roles;

import com.qqun.user.User;

public class Dead extends User {

    public Dead(String username, String chatId, long userId) {
        super(username, chatId, userId);
    }

    public Dead(User user) {
        super(user);
    }
}
