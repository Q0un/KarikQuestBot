package com.qqun.user.roles;

import com.qqun.user.User;

public class Dead extends User {
    public enum Person {Imposter, Brother1, Brother2, Remaining}

    private final Person person;

    public Dead(String username, String chatId, long userId, String person) {
        super(username, chatId, userId);
        this.person = Person.valueOf(person);
    }

    public Dead(User user, String person) {
        super(user);
        this.person = Person.valueOf(person);
    }
}
