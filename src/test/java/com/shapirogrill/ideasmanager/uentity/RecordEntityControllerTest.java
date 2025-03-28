package com.shapirogrill.ideasmanager.uentity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shapirogrill.ideasmanager.usertable.UserTable;
import com.shapirogrill.ideasmanager.usertable.UserTableRepository;
import com.shapirogrill.ideasmanager.utils.TestClassFactory;

@SpringBootTest
@AutoConfigureMockMvc
public class RecordEntityControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserTableRepository userTableRepository;

    @MockBean
    private TFieldEntityService tFieldEntityService;

    private final String idToReplace = "{tableId}";
    private final String endpoint = "/v1/tables/" + idToReplace + "/records";

    private final Integer defaultNField = 2;
    private final String tableName = "users";

    /********
     * GET
     *******/
    @Test
    @WithMockUser
    public void givenNonExistingUserTable_whenGetRecordsFromTable_thenNotFound() throws Exception {
        // Given
        String nonExistingUserTableId = "1";
        Mockito.when(this.userTableRepository.findById(Long.valueOf(nonExistingUserTableId)))
                .thenReturn(Optional.empty());

        // When
        mockMvc.perform(MockMvcRequestBuilders.get(endpoint.replace(idToReplace, nonExistingUserTableId)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser
    public void givenEmptyExistingUserTable_whenGetRecordsFromTable_thenEmptyList() throws Exception {
        // Given
        UserTable userTable = TestClassFactory.createUserTable(defaultNField, tableName);
        Mockito.when(this.userTableRepository.findById(userTable.getId()))
                .thenReturn(Optional.of(userTable));
        Mockito.when(this.tFieldEntityService.selectTable(userTable)).thenReturn(Collections.emptyList());

        // When
        mockMvc.perform(MockMvcRequestBuilders.get(endpoint.replace(idToReplace, "" + userTable.getId())))
                // Then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    /********
     * POST
     *******/
    @Test
    @WithMockUser
    public void givenNonExistingUserTable_whenCreateNewRecord_thenNotFound() throws Exception {
        // Given
        String nonExistingUserTableId = "1";
        Map<String, String> newRecord = new HashMap<>();
        Mockito.when(this.userTableRepository.findById(Long.valueOf(nonExistingUserTableId)))
                .thenReturn(Optional.empty());

        // When
        mockMvc.perform(MockMvcRequestBuilders.post(endpoint.replace(idToReplace, nonExistingUserTableId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRecord)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser
    public void givenUnvalidRecord_whenCreateNewRecord_thenNotAcceptable() throws Exception {
        // Given
        Map<String, String> unvalidRecord = new HashMap<>();
        UserTable userTable = TestClassFactory.createUserTable(defaultNField, tableName);
        Mockito.when(this.userTableRepository.findById(userTable.getId()))
                .thenReturn(Optional.of(userTable));

        Mockito.when(this.tFieldEntityService.insertEntityIntoTable(Mockito.eq(userTable),
                Mockito.eq(unvalidRecord)))
                .thenThrow(DataIntegrityViolationException.class);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post(endpoint.replace(idToReplace, "" + userTable.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(unvalidRecord)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
    }

    @Test
    @WithMockUser
    public void givenValidRecord_whenCreateNewRecord_thenCreated() throws Exception {
        // Given
        Map<String, String> validRecord = new HashMap<>();
        UserTable userTable = TestClassFactory.createUserTable(defaultNField, tableName);
        String firstFieldValue = "1";
        String secondFieldValue = "Hello";
        validRecord.put(userTable.getTableFields().get(0).getName(), firstFieldValue);
        validRecord.put(userTable.getTableFields().get(1).getName(), secondFieldValue);
        Mockito.when(this.userTableRepository.findById(userTable.getId()))
                .thenReturn(Optional.of(userTable));

        Mockito.when(this.tFieldEntityService.insertEntityIntoTable(Mockito.eq(userTable),
                Mockito.eq(validRecord)))
                .thenReturn(validRecord);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post(endpoint.replace(idToReplace, "" + userTable.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRecord)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$." + userTable.getTableFields().get(0).getName())
                        .value(firstFieldValue));
    }

    /********
     * PUT
     *******/
    @Test
    @WithMockUser
    public void givenNonExistingUserTable_whenUpdateRecord_thenNotFound() throws Exception {
        // Given
        String nonExistingUserTableId = "1";
        Long previousRecordId = 1L;
        Map<String, String> newRecord = new HashMap<>();
        Mockito.when(this.userTableRepository.findById(Long.valueOf(nonExistingUserTableId)))
                .thenReturn(Optional.empty());

        // When
        mockMvc.perform(MockMvcRequestBuilders
                .put(endpoint.replace(idToReplace, nonExistingUserTableId)
                        + "/%s".formatted(previousRecordId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRecord)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser
    public void givenUnvalidRecord_whenUpdateRecord_thenNotAcceptable() throws Exception {
        // Given
        Long previousRecordId = 1L;
        Map<String, String> unvalidRecord = new HashMap<>();
        UserTable userTable = TestClassFactory.createUserTable(defaultNField, tableName);
        Mockito.when(this.userTableRepository.findById(userTable.getId()))
                .thenReturn(Optional.of(userTable));

        Mockito.when(this.tFieldEntityService.updateEntityInTable(Mockito.eq(userTable),
                Mockito.eq(unvalidRecord), Mockito.eq(previousRecordId)))
                .thenThrow(DataIntegrityViolationException.class);

        // When
        mockMvc.perform(MockMvcRequestBuilders
                .put(endpoint.replace(idToReplace, "" + userTable.getId())
                        + "/%s".formatted(previousRecordId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(unvalidRecord)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
    }

    @Test
    @WithMockUser
    public void givenNonExistingRecord_whenUpdateRecord_thenNotFound() throws Exception {
        // Given
        Long nonExistingRecordIdRecordId = 1L;
        Map<String, String> newRecord = new HashMap<>();
        UserTable userTable = TestClassFactory.createUserTable(defaultNField, tableName);
        Mockito.when(this.userTableRepository.findById(userTable.getId()))
                .thenReturn(Optional.of(userTable));

        Mockito.when(this.tFieldEntityService.updateEntityInTable(Mockito.eq(userTable),
                Mockito.eq(newRecord), Mockito.eq(nonExistingRecordIdRecordId)))
                .thenThrow(RecordEntityNotFoundException.class);

        // When
        mockMvc.perform(MockMvcRequestBuilders
                .put(endpoint.replace(idToReplace, "" + userTable.getId())
                        + "/%s".formatted(nonExistingRecordIdRecordId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRecord)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser
    public void givenValidRecord_whenUpdateRecord_thenCreated() throws Exception {
        // Given
        UserTable userTable = TestClassFactory.createUserTable(defaultNField, tableName);
        Map<String, String> validRecord = new HashMap<>();
        String firstFieldValue = "1";
        String secondFieldValue = "Hello";
        validRecord.put(userTable.getTableFields().get(0).getName(), firstFieldValue);
        validRecord.put(userTable.getTableFields().get(1).getName(), secondFieldValue);
        Mockito.when(this.userTableRepository.findById(userTable.getId()))
                .thenReturn(Optional.of(userTable));

        Mockito.when(this.tFieldEntityService.updateEntityInTable(Mockito.eq(userTable),
                Mockito.eq(validRecord), Mockito.eq(Long.valueOf(firstFieldValue))))
                .thenReturn(validRecord);

        // When
        mockMvc.perform(MockMvcRequestBuilders
                .put(endpoint.replace(idToReplace, "" + userTable.getId())
                        + "/%s".formatted(firstFieldValue))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRecord)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$." + userTable.getTableFields().get(0).getName())
                        .value(firstFieldValue));
    }

    /********
     * DELETE
     *******/
    @Test
    @WithMockUser
    public void givenNonExistingUserTable_whenDeleteRecord_thenNotFound() throws Exception {
        // Given
        String nonExistingUserTableId = "1";
        Long previousRecordId = 1L;
        Mockito.when(this.userTableRepository.findById(Long.valueOf(nonExistingUserTableId)))
                .thenReturn(Optional.empty());

        // When
        mockMvc.perform(MockMvcRequestBuilders
                .delete(endpoint.replace(idToReplace, nonExistingUserTableId)
                        + "/%s".formatted(previousRecordId)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser
    public void givenNonExistingRecord_whenDeleteRecord_thenNotFound() throws Exception {
        // Given
        Long nonExistingRecordIdRecordId = 1L;
        UserTable userTable = TestClassFactory.createUserTable(defaultNField, tableName);
        Mockito.when(this.userTableRepository.findById(userTable.getId()))
                .thenReturn(Optional.of(userTable));

        Mockito.doThrow(RecordEntityNotFoundException.class).when(this.tFieldEntityService).deleteEntityInTable(
                Mockito.eq(userTable),
                Mockito.eq(nonExistingRecordIdRecordId));

        // When
        mockMvc.perform(MockMvcRequestBuilders
                .delete(endpoint.replace(idToReplace, "" + userTable.getId())
                        + "/%s".formatted(nonExistingRecordIdRecordId)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser
    public void givenRecord_whenDeleteRecord_thenNoContent() throws Exception {
        // Given
        UserTable userTable = TestClassFactory.createUserTable(defaultNField, tableName);
        Long existingRecordId = 1L;
        Mockito.when(this.userTableRepository.findById(userTable.getId()))
                .thenReturn(Optional.of(userTable));

        Mockito.doNothing().when(this.tFieldEntityService).deleteEntityInTable(
                Mockito.eq(userTable), Mockito.eq(existingRecordId));

        // When
        mockMvc.perform(MockMvcRequestBuilders
                .delete(endpoint.replace(idToReplace, "" + userTable.getId())
                        + "/%s".formatted(existingRecordId)))
                // Then
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
