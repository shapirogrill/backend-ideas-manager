package com.shapirogrill.ideasmanager.common;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class SQLCommandBuilder {
    private final StringBuilder selectClause = new StringBuilder();
    private final StringBuilder createClause = new StringBuilder();
    private final StringBuilder dropClause = new StringBuilder();
    private final StringBuilder alterClause = new StringBuilder();
    private final StringBuilder renameClause = new StringBuilder();
    private final StringBuilder insertClause = new StringBuilder();
    private final StringBuilder fieldsClause = new StringBuilder();
    private final StringBuilder valuesClause = new StringBuilder();
    private final StringBuilder fromClause = new StringBuilder();
    private final StringBuilder whereClause = new StringBuilder();

    public SQLCommandBuilder select(String... columns) {
        this.selectClause.append("SELECT ");
        if (columns.length == 0) {
            this.selectClause.append("*");
        } else {
            for (int i = 0; i < columns.length; i++) {
                this.selectClause.append(columns[i]);
                if (i < columns.length - 1) {
                    this.selectClause.append(", ");
                }
            }
        }
        return this;
    }

    public SQLCommandBuilder create(String table) {
        this.createClause.append("CREATE TABLE ").append(table);
        return this;
    }

    public SQLCommandBuilder drop(String table) {
        this.dropClause.append("DROP TABLE IF EXISTS ").append(table);
        return this;
    }

    public SQLCommandBuilder alter(String table) {
        this.alterClause.append("ALTER TABLE ").append(table);
        return this;
    }

    public SQLCommandBuilder rename(String table) {
        this.renameClause.append(" RENAME TO ").append(table);
        return this;
    }

    public SQLCommandBuilder insert(String table) {
        this.insertClause.append("INSERT INTO ").append(table);
        return this;
    }

    public SQLCommandBuilder fields(SQLField... fields) {
        for (int i = 0; i < fields.length; i++) {
            this.fieldsClause.append(fields[i].name)
                    .append(" ")
                    .append(fields[i].type);
            if (i < fields.length - 1) {
                this.fieldsClause.append(", ");
            }
        }
        return this;
    }

    public SQLCommandBuilder values(String... values) {
        this.valuesClause.append("VALUES ( ");
        for (int i = 0; i < values.length; i++) {
            this.valuesClause.append(values[i]);
            if (i < values.length - 1) {
                this.valuesClause.append(", ");
            }
        }
        this.valuesClause.append(")");
        return this;
    }

    public SQLCommandBuilder from(String table) {
        this.fromClause.append(" FROM ").append(table);
        return this;
    }

    public SQLCommandBuilder where(String condition) {
        if (this.whereClause.length() == 0) {
            this.whereClause.append(" WHERE ");
        } else {
            this.whereClause.append(" AND ");
        }
        this.whereClause.append(condition);
        return this;
    }

    // Build SELECT query
    public String buildSelect() {
        return selectClause.toString() + fromClause.toString() + whereClause.toString() + ";";
    }

    // Build CREATE TABLE query
    public String buildCreate() {
        return createClause.toString() + " ( " + fieldsClause.toString() + " );";
    }

    // Build DROP query
    public String buildDrop() {
        return dropClause.toString() + ";";
    }

    // Build RENAME query
    public String buildRename() {
        return alterClause.toString() + renameClause.toString() + ";";
    }

    // Build Insert query
    public String buildInsert() {
        return insertClause.toString() + " " + valuesClause.toString() + ";";
    }
}
