package com.shapirogrill.ideasmanager.uentity;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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

    private Map<String, Object> buildMapFromObjectAndSortedTableField(Object o, List<TableField> sortedTableFields) {
        Map<String, Object> map = new HashMap<>();

        sortedTableFields.forEach(t -> {
            map.put(t.getName(), ((Object[]) o)[t.getPosition()]);
        });
        return map;
    }

    public List<Map<String, Object>> selectTable(UserTable table) {
        String name = table.getName() + "_" + table.getUser().getUsername();
        log.debug("Try to select elements from " + name + " table");

        List<TableField> sortedTableFields = table.getTableFields();
        sortedTableFields.sort(Comparator.comparingInt(TableField::getPosition));

        List<Map<String, Object>> records = new ArrayList<>();
        entityRepository.executeQuerySelect(
                new SQLCommandBuilder().select().from(name).buildSelect()).stream()
                .forEach(o -> records.add(this.buildMapFromObjectAndSortedTableField(o, sortedTableFields)));
        return records;
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
                        .buildInsert());
        return record;
    }

    public Map<String, String> updateEntityInTable(UserTable table, Map<String, String> newRecord, Long givenId) {
        List<TableField> sortedTableFields = table.getTableFields();
        sortedTableFields.sort(Comparator.comparingInt(TableField::getPosition));

        // Verify the given ID and the record one are the same
        String pkFieldName = sortedTableFields.get(0).getName();
        if (Long.valueOf(newRecord.get(pkFieldName)) != givenId) {
            throw new DataIntegrityViolationException("Given ID %s does not match with the one in the record %s"
                    .formatted(givenId, newRecord.get(pkFieldName)));
        }

        // Verify the record with given ID exist
        String name = table.getName() + "_" + table.getUser().getUsername();
        String whereClause = "%s = %s".formatted(pkFieldName, givenId);
        if (this.entityRepository.executeQuerySelect(new SQLCommandBuilder().select().from(name)
                .where(whereClause).buildSelect()).isEmpty()) {
            throw new RecordEntityNotFoundException(givenId);
        }

        // Build the update record statement
        entityRepository.executeQueryUpdate(
                new SQLCommandBuilder().update(name).set(sortedTableFields.stream().skip(1).map(tableField -> {
                    if (!this.doesRecordContainValidTableField(newRecord, tableField)) {
                        throw new DataIntegrityViolationException("The record should contains a "
                                + tableField.getName() + ":" + tableField.getType() + " value.");
                    }
                    return "%s = %s".formatted(tableField.getName(), newRecord.get(tableField.getName()));
                }).toArray(String[]::new)).where(whereClause).buildUpdate());

        return newRecord;
    }

    public void deleteEntityInTable(UserTable table, Long givenId) {
        List<TableField> sortedTableFields = table.getTableFields();
        sortedTableFields.sort(Comparator.comparingInt(TableField::getPosition));

        // Verify the record with given ID exist
        String name = table.getName() + "_" + table.getUser().getUsername();
        String whereClause = "%s = %s".formatted(sortedTableFields.get(0).getName(), givenId);
        if (this.entityRepository.executeQuerySelect(new SQLCommandBuilder().select().from(name)
                .where(whereClause).buildSelect()).isEmpty()) {
            throw new RecordEntityNotFoundException(givenId);
        }

        // Build the update record statement
        entityRepository.executeQueryUpdate(new SQLCommandBuilder().from(name).where(whereClause).buildDelete());
    }
}
