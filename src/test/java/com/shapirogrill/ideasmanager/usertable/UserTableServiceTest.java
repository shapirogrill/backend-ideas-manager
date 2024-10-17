package com.shapirogrill.ideasmanager.usertable;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

import com.shapirogrill.ideasmanager.common.enums.DataType;
import com.shapirogrill.ideasmanager.tablefield.TableFieldRepository;
import com.shapirogrill.ideasmanager.user.UserNotFoundException;
import com.shapirogrill.ideasmanager.user.UserRepository;
import com.shapirogrill.ideasmanager.utils.TestClassFactory;

@SpringBootTest
public class UserTableServiceTest {
        @Autowired
        private UserTableService userTableService;

        @MockBean
        private UserRepository userRepository;

        @MockBean
        private UserTableRepository userTableRepository;

        @MockBean
        private TableFieldRepository tableFieldRepository;

        private final Integer defaultNField = 2;

        private final String username = "username";

        private final String tableName = "table_name";

        /*************************
         * buildUserTableFromDTO
         ***********************/

        @Test
        void givenNonExistingUsername_whenBuildUserTableFromDTO_thenUserNotFoundException() {
                // Given
                String notExistingUsername = "Invalid_username";
                DTOUserTable dto = TestClassFactory.createDTOUserTable(tableName, notExistingUsername, defaultNField);

                Mockito.when(userRepository.findByUsername(notExistingUsername)).thenReturn(Optional.empty());

                // When & Then
                Assertions.assertThrows(UserNotFoundException.class,
                                () -> userTableService.buildUserTableFromDTO(dto));
        }

        @Test
        void givenTwoSameTableFieldNames_whenBuildUserTableFromDTO_thenDataIntegrityViolationException() {
                // Given
                DTOUserTable dto = TestClassFactory.createDTOUserTable(tableName, username, defaultNField);
                dto.getTableFields().add(TestClassFactory.createTableField(
                                dto.getTableFields().get(0).getName(), // Same name
                                DataType.BOOL,
                                dto.getTableFields().size()));

                Mockito.when(userRepository.findByUsername(username))
                                .thenReturn(Optional.of(TestClassFactory.createUser(username)));

                // When & Then
                Assertions.assertThrows(DataIntegrityViolationException.class,
                                () -> userTableService.buildUserTableFromDTO(dto));
        }

        @Test
        void givenIDNotInFirstPosition_whenBuildUserTableFromDTO_thenDataIntegrityViolationException() {
                // Given
                DTOUserTable dto = TestClassFactory.createDTOUserTable(tableName, username, defaultNField);

                Assertions.assertFalse(dto.getTableFields().size() == 0, "To valid this test, array must not be empty");
                dto.getTableFields().add(TestClassFactory.createTableField(
                                "ID", // ID
                                DataType.NUMBER,
                                dto.getTableFields().size())); // Not in first position (size == nDefaultField)

                Mockito.when(userRepository.findByUsername(username))
                                .thenReturn(Optional.of(TestClassFactory.createUser(username)));

                // When & Then
                Assertions.assertThrows(DataIntegrityViolationException.class,
                                () -> userTableService.buildUserTableFromDTO(dto));
        }

        @Test
        void givenUnvalidIncrementPosition_whenBuildUserTableFromDTO_thenDataIntegrityViolationException() {
                // Given
                DTOUserTable dto = TestClassFactory.createDTOUserTable(tableName, username, defaultNField);

                dto.getTableFields().get(0).setName("ID"); // ID must be in first position
                dto.getTableFields()
                                .add(
                                                TestClassFactory.createTableField(
                                                                dto.getTableFields()
                                                                                .get(dto.getTableFields().size() - 1)
                                                                                .getPosition()
                                                                                + 2)); // Unvalid incremential

                Mockito.when(userRepository.findByUsername(username))
                                .thenReturn(Optional.of(TestClassFactory.createUser(username)));

                // When & Then
                Assertions.assertThrows(DataIntegrityViolationException.class,
                                () -> userTableService.buildUserTableFromDTO(dto));
        }

        @Test
        void givenDTO_whenBuildUserTableFromDTO_thenUserTable() {
                // Given
                DTOUserTable dto = TestClassFactory.createDTOUserTable(tableName, username, defaultNField);
                dto.getTableFields().get(0).setName("ID"); // ID must be in first position

                Mockito.when(userRepository.findByUsername(username))
                                .thenReturn(Optional.of(TestClassFactory.createUser(username)));

                // When
                UserTable userTable = userTableService.buildUserTableFromDTO(dto);

                Assertions.assertEquals(dto.getName(), userTable.getName());
                Assertions.assertArrayEquals(dto.getTableFields().toArray(), userTable.getTableFields().toArray());
        }
}
