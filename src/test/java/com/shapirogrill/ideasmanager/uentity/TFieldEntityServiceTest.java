package com.shapirogrill.ideasmanager.uentity;

import com.shapirogrill.ideasmanager.common.enums.DataType;
import com.shapirogrill.ideasmanager.usertable.UserTable;
import com.shapirogrill.ideasmanager.utils.TestClassFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class TFieldEntityServiceTest {

    @MockBean
    private EntityRepository entityRepository;

    @Autowired
    private TFieldEntityService tFieldEntityService;

    private Integer defaultNField = 2;

    private String defaultTableName = "userTableName";

    /**************
     * selectTable
     *************/
    @Test
    public void givenUserTable_whenSelectTable_thenRecords() {
        // Given
        UserTable userTable = TestClassFactory.createUserTableWithUser(defaultNField, defaultTableName);
        Map<String, String> record = new HashMap<>();
        String firstFieldValue = "1";
        String secondFieldValue = "Hello";
        record.put(userTable.getTableFields().get(0).getName(), firstFieldValue);
        record.put(userTable.getTableFields().get(1).getName(), secondFieldValue);

        // When
        List<Object> queryResult = List.of(record);
        Mockito.when(this.entityRepository.executeQuerySelect(Mockito.anyString()))
                .thenReturn(queryResult);

        // Then
        Assertions.assertIterableEquals(this.tFieldEntityService.selectTable(userTable), queryResult);
    }

    @Test
    public void givenUserTable_whenSelectTable_thenQueryExecuted() {
        // Given
        UserTable userTable = TestClassFactory.createUserTableWithUser(defaultNField, defaultTableName);
        Map<String, String> record = new HashMap<>();
        String firstFieldValue = "1";
        String secondFieldValue = "Hello";
        record.put(userTable.getTableFields().get(0).getName(), firstFieldValue);
        record.put(userTable.getTableFields().get(1).getName(), secondFieldValue);

        // When
        List<Object> queryResult = List.of(record);
        Mockito.when(this.entityRepository.executeQuerySelect(Mockito.any(String.class)))
                .thenReturn(queryResult);

        // Then
        Assertions.assertIterableEquals(this.tFieldEntityService.selectTable(userTable), queryResult);
    }

    /************************
     * insertEntityIntoTable
     ************************/
    @Test
    public void givenUnvalidRecord_whenInsertEntityIntoTable_thenDataIntegrityViolationException() {
        // Given
        UserTable userTable = TestClassFactory.createUserTableWithUser(defaultNField, defaultTableName);
        // Convert each type as String for easier control on test
        userTable.getTableFields().stream().forEach(e -> e.setType(DataType.NUMBER));

        Map<String, String> record = new HashMap<>();
        String firstFieldValue = "1"; // Is an Integer
        String secondFieldValue = "Hello"; // Is not an Integer
        record.put(userTable.getTableFields().get(0).getName(), firstFieldValue);
        record.put(userTable.getTableFields().get(1).getName(), secondFieldValue);

        // When && Then
        Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> this.tFieldEntityService.insertEntityIntoTable(userTable, record));
    }

    @Test
    public void givenValidRecord_whenInsertEntityIntoTable_thenRecordRegistered() {
        // Given
        UserTable userTable = TestClassFactory.createUserTableWithUser(defaultNField, defaultTableName);
        // Convert each type as String for easier control on test
        userTable.getTableFields().stream().forEach(e -> e.setType(DataType.NUMBER));

        Map<String, String> record = new HashMap<>();
        String firstFieldValue = "1"; // Is an Integer
        String secondFieldValue = "2"; // Is an Integer
        record.put(userTable.getTableFields().get(0).getName(), firstFieldValue);
        record.put(userTable.getTableFields().get(1).getName(), secondFieldValue);

        Mockito.doNothing().when(this.entityRepository).executeQueryUpdate(Mockito.any(String.class));

        // When
        Map<String, String> returnedRecord = this.tFieldEntityService.insertEntityIntoTable(userTable, record);

        // Then
        Assertions.assertTrue(returnedRecord.containsValue(firstFieldValue));
        Assertions.assertTrue(returnedRecord.containsValue(secondFieldValue));
    }

}
