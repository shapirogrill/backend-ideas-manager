package com.shapirogrill.ideasmanager.usertable;

public class UserTableNotFoundException extends RuntimeException {
    public UserTableNotFoundException(Long id) {
        super("Could not find UserTable" + id);
    }
}
