package com.qqun.user;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class UserList extends ArrayList<User> {

    public final User addUser(String username, String chatId, long userId) {
        User user = new User(username, chatId, userId);
        add(user);
        return user;
    }

    public final User findByName(String username) {
        for (User user : this) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public final void save() {
        try {
            Writer writer = new FileWriter("data/users.json");
            Gson gson = new Gson();
            UserList saving = new UserList();
            for (User user : this) {
                if (user.getState() != User.State.NEW_USER) {
                    saving.add(user);
                }
            }
            gson.toJson(saving, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
