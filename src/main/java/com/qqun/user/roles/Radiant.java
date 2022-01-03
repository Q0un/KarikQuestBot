package com.qqun.user.roles;

import com.qqun.user.User;

public class Radiant extends User {
    public enum Person {God, Sister, Remaining, Moron}

    private final Person person;

    public Radiant(String username, String chatId, long userId, String person) {
        super(username, chatId, userId);
        this.person = Person.valueOf(person);
    }

    public Radiant(User user, String person) {
        super(user);
        this.person = Person.valueOf(person);
    }
}
