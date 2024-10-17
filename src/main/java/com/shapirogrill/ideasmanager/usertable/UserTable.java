package com.shapirogrill.ideasmanager.usertable;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shapirogrill.ideasmanager.tablefield.TableField;
import com.shapirogrill.ideasmanager.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_tables")
@Getter
@Setter
public class UserTable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tables_seq")
    @SequenceGenerator(name = "tables_seq", sequenceName = "tables_seq", allocationSize = 1)
    private Long id;

    @NotBlank
    @Column(nullable = false, name = "table_name")
    private String name;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "userTable", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties({ "userTable" })
    private List<TableField> tableFields;
}
