package com.qqun.user;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class UserList extends ArrayList<User> {

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
            Writer writer = new FileWriter("users.json");
            Gson gson = new Gson();
            gson.toJson(this, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
