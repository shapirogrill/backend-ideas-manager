package com.shapirogrill.ideasmanager.usertable;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shapirogrill.ideasmanager.user.UserNotFoundException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/tables")
public class UserTableController {
    private final UserTableRepository userTableRepository;

    private final UserTableService userTableService;

    @GetMapping
    public ResponseEntity<Iterable<UserTable>> getAll() {
        log.debug("Get all UserTables");
        return ResponseEntity.ok(this.userTableRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<UserTable> createUserTable(@RequestBody @Valid DTOUserTable dto) {
        try {
            log.info("Create new UserTable");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(this.userTableRepository.save(this.userTableService.buildUserTableFromDTO(dto)));
        } catch (UserNotFoundException e) {
            log.warn("Try to find a non existing user " + e);
            return ResponseEntity.notFound().build();
        } catch (DataIntegrityViolationException e) {
            log.warn("Try to create a table with invalid fields " + e);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        } catch (Exception e) {
            log.error("Internal server error " + e + "\n" + e.getStackTrace().toString());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserTable> modifyNameById(@PathVariable Long id, @RequestBody @Valid UserTable newUserTable) {
        try {
            log.info("Modify UserTable" + id);
            return this.userTableRepository.findById(id)
                    .map(table -> {
                        table.setName(newUserTable.getName());
                        return ResponseEntity.status(HttpStatus.CREATED).body(this.userTableRepository.save(table));
                    })
                    .orElseThrow(() -> new UserTableNotFoundException(id));
        } catch (UserTableNotFoundException e) {
            log.warn("Try to modify a non existing UserTable " + e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Internal server error " + e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        try {
            log.info("Delete UserTable" + id);
            if (this.userTableRepository.existsById(id)) {
                this.userTableRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            }
            throw new UserTableNotFoundException(id);
        } catch (UserTableNotFoundException e) {
            log.warn("Try to delete a non existing UserTable " + e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Internal server error " + e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
