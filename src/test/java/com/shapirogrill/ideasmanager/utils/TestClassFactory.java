package com.shapirogrill.ideasmanager.utils;

import com.shapirogrill.ideasmanager.user.User;

public class TestClassFactory {
    private static Long serialUserID = 0L;

    public static User createUser(String username, String password) {
        User user = new User();
        user.setId(serialUserID++);
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    public static User createUser() {
        return createUser("username" + serialUserID, "password" + serialUserID);
    }
}
