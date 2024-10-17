package com.shapirogrill.ideasmanager.usertable;

import java.util.ArrayList;
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
import com.shapirogrill.ideasmanager.user.UserNotFoundException;
import com.shapirogrill.ideasmanager.utils.TestClassFactory;

@SpringBootTest
@AutoConfigureMockMvc
public class UserTableControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private UserTableRepository userTableRepository;

        @MockBean
        private UserTableService userTableService;

        private final String endpoint = "/v1/tables";

        private final Integer defaultNField = 2;

        /********
         * GET
         *******/

        @Test
        @WithMockUser
        public void givenUserTables_whenGetArray_thenEmptyArray() throws Exception {
                ArrayList<UserTable> userTables = new ArrayList<>();
                userTables.add(TestClassFactory.createUserTable(defaultNField, "datatable1"));
                userTables.add(TestClassFactory.createUserTable(defaultNField, "datatable2"));

                // Given
                Mockito.when(this.userTableRepository.findAll())
                                .thenReturn(userTables);

                // When
                mockMvc.perform(MockMvcRequestBuilders.get(endpoint))
                                // Then
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(userTables.size()));
        }

        /********
         * POST
         *******/

        @Test
        @WithMockUser
        public void givenNonExistingUsername_whenCreateUserTable_thenNotFound() throws Exception {
                // Given
                String invalidUsername = "nonExistingUsername";
                DTOUserTable dto = TestClassFactory.createDTOUserTable("userTable_name", invalidUsername,
                                defaultNField);

                Mockito.when(this.userTableService.buildUserTableFromDTO(Mockito.any(DTOUserTable.class)))
                                .thenThrow(UserNotFoundException.class);

                // When
                mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                // Then
                                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

        @Test
        @WithMockUser
        public void givenInvalidField_whenCreateUserTable_thenNotAcceptable() throws Exception {
                // Given
                DTOUserTable dto = TestClassFactory.createDTOUserTable("userTable_name", "username",
                                defaultNField);
                dto.getTableFields().add(TestClassFactory.createTableField("UnvalidField", null, 0));

                Mockito.when(this.userTableService.buildUserTableFromDTO(Mockito.any(DTOUserTable.class)))
                                .thenThrow(DataIntegrityViolationException.class);

                // When
                mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                // Then
                                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
        }

        @Test
        @WithMockUser
        public void givenValidDTO_whenCreateUserTable_thenCreated() throws Exception {
                // Given
                String userTableName = "table_name";
                String username = "username";
                DTOUserTable dto = TestClassFactory.createDTOUserTable(userTableName, username,
                                defaultNField);

                UserTable createdUserTable = TestClassFactory.createUserTableWithUser(defaultNField, userTableName);
                createdUserTable.getUser().setUsername(username);
                Mockito.when(this.userTableService.buildUserTableFromDTO(Mockito.any(DTOUserTable.class)))
                                .thenReturn(createdUserTable);
                Mockito.when(this.userTableRepository.save(createdUserTable)).thenReturn(createdUserTable);

                // When
                mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                // Then
                                .andExpect(MockMvcResultMatchers.status().isCreated())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(createdUserTable.getId()));
        }

        /********
         * PUT
         *******/

        @Test
        @WithMockUser
        public void givenNotFoundableID_whenModifyNameById_thenNotFound() throws Exception {
                // Given
                String userTableName = "table_name";
                String newName = "username";

                UserTable createdUserTable = TestClassFactory.createUserTableWithUser(defaultNField, userTableName);
                UserTable newUserTable = TestClassFactory.createUserTable(0, newName);
                newUserTable.setId(createdUserTable.getId());
                newUserTable.setTableFields(createdUserTable.getTableFields());
                newUserTable.setUser(createdUserTable.getUser());

                Mockito.when(userTableRepository.findById(createdUserTable.getId())).thenReturn(Optional.empty());
                // When
                mockMvc.perform(MockMvcRequestBuilders.put(endpoint + "/" + newUserTable.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newUserTable)))
                                // Then
                                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

        @Test
        @WithMockUser
        public void givenValidName_whenModifyNameById_thenCreated() throws Exception {
                // Given
                String userTableName = "table_name";
                String newName = "username";

                UserTable createdUserTable = TestClassFactory.createUserTableWithUser(defaultNField, userTableName);
                UserTable newUserTable = TestClassFactory.createUserTable(0, newName);
                newUserTable.setId(createdUserTable.getId());
                newUserTable.setTableFields(createdUserTable.getTableFields());
                newUserTable.setUser(createdUserTable.getUser());

                Mockito.when(userTableRepository.findById(createdUserTable.getId()))
                                .thenReturn(Optional.of(createdUserTable));
                Mockito.when(userTableRepository.save(Mockito.eq(createdUserTable))).thenReturn(createdUserTable);
                // When
                mockMvc.perform(MockMvcRequestBuilders.put(endpoint + "/" + newUserTable.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newUserTable)))
                                .andExpect(MockMvcResultMatchers.status().isCreated())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(newName));
        }

        /********
         * DELETE
         *******/

        @Test
        @WithMockUser
        public void givenNonExistingId_whenDeleteById_thenNotFound() throws Exception {
                // Given
                Long nonExistingUserTableId = 0L;

                Mockito.when(userTableRepository.existsById(nonExistingUserTableId)).thenReturn(false);
                // When
                mockMvc.perform(MockMvcRequestBuilders.delete(endpoint + "/" + nonExistingUserTableId))
                                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

        @Test
        @WithMockUser
        public void givenExistingUserTable_whenDeleteById_thenNoContent() throws Exception {
                // Given
                UserTable userTable = TestClassFactory.createUserTable(defaultNField);

                Mockito.when(userTableRepository.existsById(userTable.getId())).thenReturn(true);
                // When
                mockMvc.perform(MockMvcRequestBuilders.delete(endpoint + "/" + userTable.getId()))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());
        }
}
