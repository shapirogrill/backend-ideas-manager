package com.shapirogrill.ideasmanager.tablefield;

import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shapirogrill.ideasmanager.common.enums.DataType;
import com.shapirogrill.ideasmanager.usertable.UserTable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "table_fields")
@Getter
@Setter
public class TableField {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fields_seq")
    @SequenceGenerator(name = "fields_seq", sequenceName = "fields_seq", allocationSize = 1)
    private Long id;

    @NotBlank
    @Column(nullable = false, name = "field_name")
    private String name;

    @NotNull
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "data_type", columnDefinition = "data_type")
    private DataType type;

    @NotNull
    @Column(nullable = false)
    private Integer position;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "table_id", nullable = false)
    private UserTable userTable;
}
