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

import java.util.Collections;
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
    // @Test
    // public void givenUserTable_whenSelectTable_thenRecords() {
    //     // Given
    //     UserTable userTable = TestClassFactory.createUserTableWithUser(defaultNField, defaultTableName);
    //     Map<String, Object> record = new HashMap<>();
    //     String firstFieldValue = "1";
    //     String secondFieldValue = "Hello";
    //     record.put(userTable.getTableFields().get(0).getName(), firstFieldValue);
    //     record.put(userTable.getTableFields().get(1).getName(), secondFieldValue);

    //     // When
    //     List<Map<String, Object>> queryResult = List.of(record);
    //     Mockito.when(this.entityRepository.executeQuerySelect(Mockito.anyString()))
    //             .thenReturn(List.of(Object record));

    //     // Then
    //     Assertions.assertEquals(this.tFieldEntityService.selectTable(userTable).size(), queryResult.size());
    //     // Assertions.assertEquals(records.get(0), queryResult.get(0));
    // }

    /************************
     * insertEntityIntoTable
     ************************/
    @Test
    public void givenUnvalidRecord_whenInsertEntityIntoTable_thenDataIntegrityViolationException() {
        // Given
        UserTable userTable = TestClassFactory.createUserTableWithUser(defaultNField, defaultTableName);
        // Convert each type as Integer for easier control on test
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
        // Convert each type as Integer for easier control on test
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

    /************************
     * updateEntityInTable
     ************************/
    @Test
    public void givenNotSameIdAndRecordId_whenUpdateEntityInTable_thenDataIntegrityViolationException() {
        // Given
        UserTable userTable = TestClassFactory.createUserTableWithUser(defaultNField, defaultTableName);

        Map<String, String> record = new HashMap<>();
        String firstFieldValue = "1";
        String secondFieldValue = "Hello";
        record.put(userTable.getTableFields().get(0).getName(), firstFieldValue);
        record.put(userTable.getTableFields().get(1).getName(), secondFieldValue);

        Long notSameId = 2l; // != firstFieldValue

        // When && Then
        Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> this.tFieldEntityService.updateEntityInTable(userTable, record, notSameId));
    }

    @Test
    public void givenUnexistingRecord_whenUpdateEntityInTable_thenEntityNotFound() {
        // Given
        UserTable userTable = TestClassFactory.createUserTableWithUser(defaultNField, defaultTableName);

        Map<String, String> record = new HashMap<>();
        String firstFieldValue = "1";
        String secondFieldValue = "2";
        record.put(userTable.getTableFields().get(0).getName(), firstFieldValue);
        record.put(userTable.getTableFields().get(1).getName(), secondFieldValue);

        Mockito.when(this.entityRepository.executeQuerySelect(Mockito.anyString())).thenReturn(Collections.emptyList());

        // When && Then
        Assertions.assertThrows(RecordEntityNotFoundException.class,
                () -> this.tFieldEntityService.updateEntityInTable(userTable, record, Long.valueOf(firstFieldValue)));
    }

    @Test
    public void givenUnvalidRecord_whenUpdateEntityInTable_thenDataIntegrityViolationException() {
        // Given
        UserTable userTable = TestClassFactory.createUserTableWithUser(defaultNField, defaultTableName);
        // Convert each type as Integer for easier control on test
        userTable.getTableFields().stream().forEach(e -> e.setType(DataType.NUMBER));

        Map<String, String> record = new HashMap<>();
        String firstFieldValue = "1"; // Is an Integer
        String secondFieldValue = "Hello"; // Is not an Integer
        record.put(userTable.getTableFields().get(0).getName(), firstFieldValue);
        record.put(userTable.getTableFields().get(1).getName(), secondFieldValue);

        Mockito.when(this.entityRepository.executeQuerySelect(Mockito.anyString())).thenReturn(List.of(record));

        // When && Then
        Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> this.tFieldEntityService.updateEntityInTable(userTable, record, Long.valueOf(firstFieldValue)));
    }

    @Test
    public void givenValidRecord_whenUpdateEntityInTable_thenUpdatedRecord() {
        // Given
        UserTable userTable = TestClassFactory.createUserTableWithUser(defaultNField, defaultTableName);
        // Convert each type as Integer for easier control on test
        userTable.getTableFields().stream().forEach(e -> e.setType(DataType.NUMBER));

        Map<String, String> record = new HashMap<>();
        String firstFieldValue = "1"; // Is an Integer
        String secondFieldValue = "2"; // Is an Integer
        record.put(userTable.getTableFields().get(0).getName(), firstFieldValue);
        record.put(userTable.getTableFields().get(1).getName(), secondFieldValue);

        Map<String, String> newRecord = new HashMap<>();
        newRecord.putAll(record);
        newRecord.put(userTable.getTableFields().get(1).getName(), firstFieldValue); // Update only the second key

        Mockito.doNothing().when(this.entityRepository).executeQueryUpdate(Mockito.any(String.class));
        Mockito.when(this.entityRepository.executeQuerySelect(Mockito.anyString())).thenReturn(List.of(record));

        // When
        Map<String, String> returnedMap = this.tFieldEntityService.updateEntityInTable(userTable, newRecord,
                Long.valueOf(firstFieldValue));

        // Then
        Assertions.assertTrue(returnedMap.containsValue(firstFieldValue));
    }

    /************************
     * deleteENtityInTable
     ************************/
    @Test
    public void givenUnexistingRecord_whenDeleteEntityInTable_thenEntityNotFound() {
        // Given
        UserTable userTable = TestClassFactory.createUserTableWithUser(defaultNField, defaultTableName);
        Long nonExistingID = 1L;

        Mockito.when(this.entityRepository.executeQuerySelect(Mockito.anyString())).thenReturn(Collections.emptyList());

        // When && Then
        Assertions.assertThrows(RecordEntityNotFoundException.class,
                () -> this.tFieldEntityService.deleteEntityInTable(userTable, nonExistingID));
    }

    @Test
    public void givenExistingRecord_whenDeleteEntityInTable_thenExecuteQUeryUpdate() {
        // Given
        UserTable userTable = TestClassFactory.createUserTableWithUser(defaultNField, defaultTableName);

        Map<String, String> record = new HashMap<>();
        String firstFieldValue = "1";
        String secondFieldValue = "2";
        record.put(userTable.getTableFields().get(0).getName(), firstFieldValue);
        record.put(userTable.getTableFields().get(1).getName(), secondFieldValue);

        Mockito.when(this.entityRepository.executeQuerySelect(Mockito.anyString())).thenReturn(List.of(record));
        Mockito.doNothing().when(this.entityRepository).executeQueryUpdate(Mockito.any(String.class));

        // When && Then
        this.tFieldEntityService.deleteEntityInTable(userTable, Long.valueOf(firstFieldValue));
    }
}
