package com.shapirogrill.ideasmanager.uentity;

import com.shapirogrill.ideasmanager.common.enums.DataType;
import com.shapirogrill.ideasmanager.usertable.UserTable;
import com.shapirogrill.ideasmanager.utils.TestClassFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class UTableEntityServiceTest {

    @Mock
    private EntityRepository uRepository;

    @InjectMocks
    private UTableEntityService uTableEntityService;

    private Integer defaultNField = 2;

    private String defaultTableName = "userTableName";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void givenUserTable_whenCreateTable_thenQueryExecuted() {
        // Given
        UserTable userTable = TestClassFactory.createUserTableWithUser(defaultNField, defaultTableName);
        userTable.getTableFields().get(0).setName("ID");
        userTable.getTableFields().get(0).setType(DataType.NUMBER);

        // When
        uTableEntityService.createTable(userTable);

        // Then
        String expectedSQL = "CREATE TABLE " +
                userTable.getName() + "_" + userTable.getUser().getUsername() +
                " ( " + userTable.getTableFields().get(0).getName() + " BIGINT PRIMARY KEY, " +
                userTable.getTableFields().get(1).getName() + " "
                + DataType.convertToSQLType(userTable.getTableFields().get(1).getType()) + " );";
        verify(uRepository, times(1)).executeQueryUpdate(expectedSQL);
    }

    @Test
    public void givenNewName_whenRenameTable_thenQueryExecuted() {
        // Given
        UserTable userTable = TestClassFactory.createUserTableWithUser(defaultNField, defaultTableName);
        String newTableName = "new_table";

        // When
        uTableEntityService.renameTable(newTableName, userTable);

        // Then
        String expectedSQL = "ALTER TABLE " +
                userTable.getName() + "_" + userTable.getUser().getUsername() +
                " RENAME TO " + newTableName + "_" +
                userTable.getUser().getUsername() + ";";
        verify(uRepository, times(1)).executeQueryUpdate(expectedSQL);
    }

    @Test
    public void givenUserTable_whenDropTable_thenQueryExecuted() {
        // Given
        UserTable userTable = TestClassFactory.createUserTableWithUser(defaultNField, defaultTableName);

        // When
        uTableEntityService.dropTable(userTable);

        // Then
        String expectedSQL = "DROP TABLE IF EXISTS " + userTable.getName() +
                "_" + userTable.getUser().getUsername() + ";";
        verify(uRepository, times(1)).executeQueryUpdate(expectedSQL);
    }
}
