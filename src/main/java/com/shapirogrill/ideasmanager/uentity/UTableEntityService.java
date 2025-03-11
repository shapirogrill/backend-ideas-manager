package com.shapirogrill.ideasmanager.uentity;

import java.util.Comparator;

import org.springframework.stereotype.Service;

import com.shapirogrill.ideasmanager.common.SQLCommandBuilder;
import com.shapirogrill.ideasmanager.common.SQLField;
import com.shapirogrill.ideasmanager.common.enums.DataType;
import com.shapirogrill.ideasmanager.tablefield.TableField;
import com.shapirogrill.ideasmanager.usertable.UserTable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UTableEntityService {
	private final EntityRepository entityRepository;

	public void createTable(UserTable table) {
		String name = table.getName() + "_" + table.getUser().getUsername();
		// Execute SQL Query
		entityRepository.executeQueryUpdate(
				// Build SQL String
				new SQLCommandBuilder().create(name)
						// Build columns statement in the CREATE TABLE instruction
						.fields(table.getTableFields().stream()
								.sorted(Comparator
										.comparingInt(TableField::getPosition))
								// Always first field is a primary key (ID)
								.map(tableField -> (tableField.getPosition() == 0)
										? new SQLField(tableField.getName(),
												DataType.convertToSQLType(
														tableField.getType())
														+ " PRIMARY KEY")
										: new SQLField(tableField.getName(),
												DataType.convertToSQLType(
														tableField.getType())))
								.toArray(SQLField[]::new))
						.buildCreate());
		log.info("Table " + name + " correctly created in database");
	}

	public void renameTable(String newTableName, UserTable previousTable) {
		String previousName = previousTable.getName() + "_" + previousTable.getUser().getUsername();
		String newName = newTableName + "_" + previousTable.getUser().getUsername();
		// Execute SQL Query
		entityRepository.executeQueryUpdate(
				// Build SQL String
				new SQLCommandBuilder().alter(previousName)
						.rename(newName)
						.buildRename());
		log.info("Table " + previousName + " correctly renamed to " + newName);
	}

	private void deleteTable(UserTable tableToDrop) {
		entityRepository.executeQueryUpdate(
				// Build SQL String
				new SQLCommandBuilder()
						.drop(tableToDrop.getName() + "_" + tableToDrop.getUser().getUsername())
						.buildDrop());
	}

	public void dropTable(UserTable tableToDrop) {
		this.deleteTable(tableToDrop);
		log.info("Table " +
				tableToDrop.getName() + "_" + tableToDrop.getUser().getUsername()
				+ " correctly deleted in database");
	}
}
