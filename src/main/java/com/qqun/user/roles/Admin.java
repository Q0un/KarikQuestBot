package com.qqun.user.roles;

import com.qqun.user.User;

public class Admin extends User {

    public Admin(String username, String chatId, long userId) {
        super(username, chatId, userId);
    }

    public Admin(User user) {
        super(user);
    }
}
