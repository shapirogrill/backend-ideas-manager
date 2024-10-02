package com.shapirogrill.ideasmanager.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "generator_user")
    @SequenceGenerator(name = "generator_user", sequenceName = "user_seq", allocationSize = 1)
    private Long id;
}
