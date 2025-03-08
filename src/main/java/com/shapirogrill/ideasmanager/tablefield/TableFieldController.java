package com.shapirogrill.ideasmanager.tablefield;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shapirogrill.ideasmanager.uentity.TFieldEntityService;
import com.shapirogrill.ideasmanager.usertable.UserTableNotFoundException;
import com.shapirogrill.ideasmanager.usertable.UserTableRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/tables/{tableId}/records")
public class TableFieldController {
    private final UserTableRepository userTableRepository;

    private final TFieldEntityService tFieldEntityService;

    @GetMapping
    public ResponseEntity<Iterable<Object>> getAllRecordsFromTable(@PathVariable Long tableId) {
        try {
            log.debug("Get all records from " + tableId);
            return ResponseEntity.ok(this.tFieldEntityService.selectTable(this.userTableRepository.findById(tableId)
                    .orElseThrow(() -> new UserTableNotFoundException(tableId))));
        } catch (UserTableNotFoundException e) {
            log.warn("Try to modify a non existing UserTable " + e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Internal server error " + e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Object> createNewRecord(@PathVariable Long tableId,
            @RequestBody Map<String, String> newRecord) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(this.tFieldEntityService.insertEntityIntoTable(this.userTableRepository.findById(tableId)
                            .orElseThrow(() -> new UserTableNotFoundException(tableId)), newRecord));
        } catch (UserTableNotFoundException e) {
            log.warn("Try to modify a non existing UserTable " + e);
            return ResponseEntity.notFound().build();
        } catch (DataIntegrityViolationException e) {
            log.warn("Value not found or with unvalid type " + e);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        } catch (Exception e) {
            log.error("Internal server error " + e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
