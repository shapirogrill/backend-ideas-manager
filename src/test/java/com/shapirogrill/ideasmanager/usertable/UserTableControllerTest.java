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
import com.shapirogrill.ideasmanager.utableentity.UTableEntityService;
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

        @MockBean
        private UTableEntityService uTableEntityService;

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

                UserTable userTable = TestClassFactory.createUserTableWithUser(defaultNField, userTableName);
                DTOPatchUserTable dtoPatch = new DTOPatchUserTable();
                dtoPatch.setName(newName);

                Mockito.when(userTableRepository.findById(userTable.getId())).thenReturn(Optional.empty());
                // When
                mockMvc.perform(MockMvcRequestBuilders.patch(endpoint + "/" + userTable.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dtoPatch)))
                                // Then
                                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

        @Test
        @WithMockUser
        public void givenValidName_whenModifyNameById_thenOk() throws Exception {
                // Given
                String userTableName = "table_name";
                String newName = "username";

                UserTable userTable = TestClassFactory.createUserTableWithUser(defaultNField, userTableName);
                DTOPatchUserTable dtoPatch = new DTOPatchUserTable();
                dtoPatch.setName(newName);

                Mockito.when(userTableRepository.findById(userTable.getId()))
                                .thenReturn(Optional.of(userTable));
                Mockito.when(userTableRepository.save(Mockito.eq(userTable))).thenReturn(userTable);
                System.out.println(objectMapper.writeValueAsString(dtoPatch));
                // When
                mockMvc.perform(MockMvcRequestBuilders.patch(endpoint + "/" + userTable.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dtoPatch)))
                                // Then
                                .andExpect(MockMvcResultMatchers.status().isOk())
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

                Mockito.when(userTableRepository.findById(nonExistingUserTableId)).thenReturn(Optional.empty());
                // When
                mockMvc.perform(MockMvcRequestBuilders.delete(endpoint + "/" + nonExistingUserTableId))
                                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

        @Test
        @WithMockUser
        public void givenExistingUserTable_whenDeleteById_thenNoContent() throws Exception {
                // Given
                UserTable userTable = TestClassFactory.createUserTable(defaultNField);

                Mockito.when(userTableRepository.findById(userTable.getId())).thenReturn(Optional.of(userTable));
                // When
                mockMvc.perform(MockMvcRequestBuilders.delete(endpoint + "/" + userTable.getId()))
                                .andExpect(MockMvcResultMatchers.status().isNoContent());
        }
}
