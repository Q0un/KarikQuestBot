package com.qqun.user.roles;

import com.qqun.user.User;

public class Inscriber extends User {
    public enum Person {Moron, Remaining}

    private final Person person;

    public Inscriber(String username, String chatId, long userId, String person) {
        super(username, chatId, userId);
        this.person = Person.valueOf(person);
    }

    public Inscriber(User user, String person) {
        super(user);
        this.person = Person.valueOf(person);
    }
}
