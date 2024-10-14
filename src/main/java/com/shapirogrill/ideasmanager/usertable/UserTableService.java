package com.shapirogrill.ideasmanager.usertable;

import java.util.HashMap;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.shapirogrill.ideasmanager.tablefield.TableField;
import com.shapirogrill.ideasmanager.tablefield.TableFieldRepository;
import com.shapirogrill.ideasmanager.user.User;
import com.shapirogrill.ideasmanager.user.UserNotFoundException;
import com.shapirogrill.ideasmanager.user.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserTableService {
    private final UserRepository userRepository;
    private final UserTableRepository userTableRepository;
    private final TableFieldRepository tableFieldRepository;

    private User getUser(String username) throws UserNotFoundException {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    private void verifyFields(Iterable<TableField> fields, UserTable userTable)
            throws DataIntegrityViolationException {
        HashMap<Integer, String> namesAndPositions = new HashMap<Integer, String>();
        for (TableField field : fields) {
            // Verify column name is not yet used
            if (namesAndPositions.containsValue(field.getName())) {
                throw new DataIntegrityViolationException("A same name is used for different columns");
            }

            field.setUserTable(userTable);
            namesAndPositions.put(field.getPosition(), field.getName());
        }

        if (!namesAndPositions.containsKey(0) || !namesAndPositions.get(0).equals("ID")) {
            throw new DataIntegrityViolationException("ID is not present in first position");
        }

        for (int i = 0; i < namesAndPositions.size(); i++) {
            if (!namesAndPositions.containsKey(i)) {
                throw new DataIntegrityViolationException("Positions are not incremented by one from 0");
            }
        }
    }

    @Transactional
    UserTable buildUserTableFromDTO(DTOUserTable dto) throws UserNotFoundException, DataIntegrityViolationException {
        User user = this.getUser(dto.getUsername());

        UserTable userTable = new UserTable();
        userTable.setUser(user);
        userTable.setName(dto.getName()); // Must verify name is not yet used

        this.verifyFields(dto.getTableFields(), userTable);
        userTable.setTableFields(dto.getTableFields());

        UserTable saveUserTable = this.userTableRepository.save(userTable);
        for (TableField field : dto.getTableFields()) {
            tableFieldRepository.save(field);
        }

        return saveUserTable;
    }
}
