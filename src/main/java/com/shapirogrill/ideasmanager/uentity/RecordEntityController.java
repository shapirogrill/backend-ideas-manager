package com.shapirogrill.ideasmanager.uentity;

import java.util.Map;

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

import com.shapirogrill.ideasmanager.usertable.UserTableNotFoundException;
import com.shapirogrill.ideasmanager.usertable.UserTableRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/tables/{tableId}/records")
public class RecordEntityController {
    private final UserTableRepository userTableRepository;

    private final TFieldEntityService tFieldEntityService;

    @GetMapping
    public ResponseEntity<Iterable<Map<String, Object>>> getAllRecordsFromTable(@PathVariable Long tableId) {
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
    public ResponseEntity<Map<String, String>> createNewRecord(@PathVariable Long tableId,
            @RequestBody Map<String, String> newRecord) {
        try {
            log.info("Create record in table %s".formatted(tableId));
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

    @PutMapping("/{recordId}")
    public ResponseEntity<Map<String, String>> updateRecord(@PathVariable Long tableId, @PathVariable Long recordId,
            @RequestBody Map<String, String> updatedRecord) {
        try {
            log.info("Modify record %s from table %s".formatted(recordId, tableId));
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(this.tFieldEntityService.updateEntityInTable(this.userTableRepository.findById(tableId)
                            .orElseThrow(() -> new UserTableNotFoundException(tableId)), updatedRecord, recordId));
        } catch (UserTableNotFoundException | RecordEntityNotFoundException e) {
            log.warn("Try to modify a non existing UserTable nor TableField" + e);
            return ResponseEntity.notFound().build();
        } catch (DataIntegrityViolationException e) {
            log.warn("Value not found or with unvalid type " + e);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        } catch (Exception e) {
            log.error("Internal server error " + e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<?> deleteRecord(@PathVariable Long tableId, @PathVariable Long recordId) {
        try {
            log.info("Delete record %s from table %s".formatted(recordId, tableId));
            this.tFieldEntityService.deleteEntityInTable(this.userTableRepository.findById(tableId)
                    .orElseThrow(() -> new UserTableNotFoundException(tableId)), recordId);
            return ResponseEntity.noContent().build();
        } catch (UserTableNotFoundException | RecordEntityNotFoundException e) {
            log.warn("Try to modify a non existing UserTable nor TableField" + e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Internal server error " + e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
