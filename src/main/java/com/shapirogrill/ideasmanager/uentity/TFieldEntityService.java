package com.shapirogrill.ideasmanager.uentity;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.shapirogrill.ideasmanager.common.SQLCommandBuilder;
import com.shapirogrill.ideasmanager.tablefield.TableField;
import com.shapirogrill.ideasmanager.usertable.UserTable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TFieldEntityService {
    private final EntityRepository entityRepository;

    public List<Object> selectTable(UserTable table) {
        String name = table.getName() + "_" + table.getUser().getUsername();
        log.debug("Try to select elements from " + name + " table");
        return entityRepository.executeQuerySelect(
                new SQLCommandBuilder().select().from(name).buildSelect());
    }

    private Boolean doesRecordContainValidTableField(Map<String, String> record, TableField tableField) {
        if (!record.containsKey(tableField.getName())) {
            return false;
        }
        try {
            switch (tableField.getType()) {
                case NUMBER:
                    Integer.parseInt(record.get(tableField.getName()));
                    return true;
                case BOOL:
                    Boolean.parseBoolean(record.get(tableField.getName()));
                    return true;
                case FLOAT:
                    Float.parseFloat(record.get(tableField.getName()));
                    return true;
                case STRING:
                    return true;
                case DATE:
                    OffsetDateTime.parse(record.get(tableField.getName()), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                    return true;
                default:
                    return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 
     * @param table
     * @param record
     * @return
     */
    public Map<String, String> insertEntityIntoTable(UserTable table, Map<String, String> record) {
        String name = table.getName() + "_" + table.getUser().getUsername();
        entityRepository.executeQueryUpdate(
                new SQLCommandBuilder().insert(name)
                        // Build columns statement in the CREATE TABLE instruction
                        .values(table.getTableFields().stream()
                                .sorted(Comparator
                                        .comparingInt(TableField::getPosition))
                                // Always first field is a primary key (ID)
                                .map(tableField -> {
                                    if (!this.doesRecordContainValidTableField(record, tableField)) {
                                        throw new DataIntegrityViolationException("The record should contains a "
                                                + tableField.getName() + ":" + tableField.getType() + " value.");
                                    }
                                    return record.get(tableField.getName());
                                })
                                .toArray(String[]::new))
                        .buildInsert()
        );

        return record;
    }

    // public Map<String, String> updateEntityIntoTable(UserTable table, Map<String, String> newRecord, Long id) {
        // TODO
    // }
}
