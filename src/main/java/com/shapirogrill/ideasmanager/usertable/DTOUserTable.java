package com.shapirogrill.ideasmanager.usertable;

import java.util.List;

import com.shapirogrill.ideasmanager.tablefield.TableField;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DTOUserTable {
    @NotBlank
    private String name;

    @NotBlank
    private String username;

    @NotNull
    private List<TableField> tableFields;
}