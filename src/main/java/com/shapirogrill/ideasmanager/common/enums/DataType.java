package com.shapirogrill.ideasmanager.common.enums;

public enum DataType {
    NUMBER, BOOL, FLOAT, STRING, DATE;

    public static String convertToSQLType(DataType type) {
        switch (type) {
            case NUMBER:
                return "BIGINT";
            case BOOL:
                return "BOOLEAN";
            case FLOAT:
                return "REAL";
            case STRING:
                return "VARCHAR(255)";
            case DATE:
                return "TIMESTAMPTZ";
            default:
                return "";
        }
    }
}
