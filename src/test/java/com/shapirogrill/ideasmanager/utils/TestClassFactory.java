package com.shapirogrill.ideasmanager.utils;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

import com.shapirogrill.ideasmanager.common.enums.DataType;
import com.shapirogrill.ideasmanager.tablefield.TableField;
import com.shapirogrill.ideasmanager.user.User;
import com.shapirogrill.ideasmanager.usertable.DTOUserTable;
import com.shapirogrill.ideasmanager.usertable.UserTable;

public class TestClassFactory {
    private final static Random random = new Random();

    private static Long serialUserId = 0L;
    private static Long serialUserTableId = 0L;
    private static Long serialTableFieldId = 0L;

    /********
     * User
     ********/
    public static User createUser(String username, String password) {
        User user = new User();
        user.setId(serialUserId++);
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    public static User createUser() {
        return createUser("username" + serialUserId, "password" + serialUserId);
    }

    public static User createUser(String username) {
        return createUser(username, "password" + serialUserId);
    }

    /***********
     * UserTable
     ***********/

    public static UserTable createUserTable(Integer nField, String name) {
        UserTable userTable = new UserTable();
        userTable.setId(serialUserTableId++);
        userTable.setName(name);
        userTable.setTableFields(createTableFields(nField, userTable));
        return userTable;
    }

    public static UserTable createUserTable(Integer nField) {
        return createUserTable(nField, "userTable_" + serialUserTableId);
    }

    public static UserTable createUserTableWithUser(Integer nField, String name) {
        UserTable userTable = createUserTable(nField, name);
        userTable.setUser(createUser());
        return userTable;
    }

    public static UserTable createUserTableWithUser(Integer nField, String name, User user) {
        UserTable userTable = createUserTable(nField, name);
        userTable.setUser(user);
        return userTable;
    }

    /***************
     * DTOUserTable
     **************/
    public static DTOUserTable createDTOUserTable(String name, String username, Integer nFields) {
        DTOUserTable dto = new DTOUserTable();
        dto.setName(name);
        dto.setUsername(username);
        dto.setTableFields(createTableFields(nFields, null));
        return dto;
    }

    /*************
     * TableField
     ************/
    private static List<TableField> createTableFields(Integer nField, UserTable userTable) {
        List<TableField> tableFields = new ArrayList<>();
        for (int i = 0; i < nField; i++) {
            TableField tableField = createTableField(i);
            tableField.setUserTable(userTable);
            tableFields.add(tableField);
        }
        return tableFields;
    }

    public static TableField createTableField(String name, DataType type, Integer position) {
        TableField tableField = new TableField();
        tableField.setId(serialTableFieldId++);
        tableField.setName(name);
        tableField.setType(type);
        tableField.setPosition(position);
        return tableField;
    }

    public static TableField createTableField(Integer position) {
        return createTableField("field" + serialTableFieldId, getRandomDataType(), position);
    }

    /***********
     * DataType
     ***********/
    private static DataType getRandomDataType() {
        DataType[] dataTypes = DataType.values();
        return dataTypes[random.nextInt(dataTypes.length)];
    }
}
