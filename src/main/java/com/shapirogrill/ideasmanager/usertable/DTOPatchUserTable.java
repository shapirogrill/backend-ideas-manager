package com.shapirogrill.ideasmanager.usertable;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DTOPatchUserTable {
    @NotBlank
    private String name;
}
