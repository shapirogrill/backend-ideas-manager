package com.shapirogrill.ideasmanager.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;;

@SpringBootTest
public class SQLCommandBuilderTest {
    private String tableName = "users";

    private SQLCommandBuilder builder;

    @BeforeEach
    private void setUp() {
        this.builder = new SQLCommandBuilder();
    }

    @Test
    public void testBuildSelect_allColumns() {
        String query = builder.select().from(tableName).buildSelect();
        assertThat(query).isEqualTo("SELECT * FROM " + tableName + ";");
    }

    @Test
    public void testBuildSelect_specificColumns() {
        String query = builder.select("id", "name").from(tableName).buildSelect();
        assertThat(query).isEqualTo("SELECT id, name FROM " + tableName + ";");
    }

    @Test
    public void testBuildSelect_withWhereClause() {
        String condition = "age > 30";
        String query = builder.select("id", "name")
                .from(tableName).where(condition).buildSelect();
        assertThat(query)
                .isEqualTo("SELECT id, name FROM " + tableName + " WHERE " + condition + ";");
    }

    @Test
    public void testBuildCreate() {
        SQLField idField = new SQLField("id", "BIGINT");
        SQLField nameField = new SQLField("name", "VARCHAR(255)");

        String query = builder.create(tableName)
                .fields(idField, nameField)
                .buildCreate();

        assertThat(query).isEqualTo("CREATE TABLE " + tableName + " ( id BIGINT, name VARCHAR(255) );");
    }

    @Test
    public void testBuildDrop() {
        String query = builder.drop(tableName).buildDrop();
        assertThat(query).isEqualTo("DROP TABLE IF EXISTS " + tableName + ";");
    }

    @Test
    public void testBuildRename() {
        String newName = "customers";
        String query = builder.alter(tableName).rename(newName).buildRename();
        assertThat(query)
                .isEqualTo("ALTER TABLE " + tableName + " RENAME TO " + newName + ";");
    }

    @Test
    public void testBuildInsert() {
        String idField = "1";
        String nameField = "my_table";
        String query = builder.insert(tableName)
                .values(idField, nameField)
                .buildInsert();

        assertThat(query)
                .isEqualTo("INSERT INTO " + tableName + " VALUES ( " + idField + ", " + nameField + ");");
    }

    @Test
    public void testBuildUpdate() {
        String assignment = "year = 2002";
        String whereClause = "ID = 1";
        String query = builder.update(tableName)
                .set(assignment)
                .where(whereClause)
                .buildUpdate();

        assertThat(query)
                .isEqualTo("UPDATE " + tableName + " SET " + assignment + "  WHERE " + whereClause + ";");
    }

    @Test
    public void testBuildDelete() {
        String whereClause = "ID = 1";
        String query = builder.from(tableName)
                .where(whereClause)
                .buildDelete();

        assertThat(query)
                .isEqualTo("DELETE  FROM " + tableName + "  WHERE " + whereClause + ";");
    }
}
