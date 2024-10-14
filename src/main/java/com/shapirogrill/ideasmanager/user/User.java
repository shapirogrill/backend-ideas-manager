package com.shapirogrill.ideasmanager.user;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shapirogrill.ideasmanager.usertable.UserTable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_user")
    @SequenceGenerator(name = "generator_user", sequenceName = "user_seq", allocationSize = 1)
    private Long id;

    // User information
    @NotBlank
    @Column(nullable = false)
    private String username;

    @JsonIgnore
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UserTable> userTables;
}
