package com.shapirogrill.ideasmanager.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;;

@SpringBootTest
public class SQLCommandBuilderTest {
    private SQLCommandBuilder builder;

    @BeforeEach
    private void setUp() {
        this.builder = new SQLCommandBuilder();
    }

    @Test
    public void testBuildSelect_allColumns() {
        String query = builder.select().from("users").buildSelect();
        assertThat(query).isEqualTo("SELECT * FROM users;");
    }

    @Test
    public void testBuildSelect_specificColumns() {
        String query = builder.select("id", "name").from("users").buildSelect();
        assertThat(query).isEqualTo("SELECT id, name FROM users;");
    }

    @Test
    public void testBuildSelect_withWhereClause() {
        String query = builder.select("id", "name").from("users").where("age > 30").buildSelect();
        assertThat(query).isEqualTo("SELECT id, name FROM users WHERE age > 30;");
    }

    @Test
    public void testBuildCreate() {
        SQLField idField = new SQLField("id", "BIGINT");
        SQLField nameField = new SQLField("name", "VARCHAR(255)");

        String query = builder.create("users")
                .fields(idField, nameField)
                .buildCreate();

        assertThat(query).isEqualTo("CREATE TABLE users ( id BIGINT, name VARCHAR(255) );");
    }

    @Test
    public void testBuildDrop() {
        String query = builder.drop("users").buildDrop();
        assertThat(query).isEqualTo("DROP TABLE IF EXISTS users;");
    }

    @Test
    public void testBuildRename() {
        String query = builder.alter("users").rename("customers").buildRename();
        assertThat(query).isEqualTo("ALTER TABLE users RENAME TO customers;");
    }
}
