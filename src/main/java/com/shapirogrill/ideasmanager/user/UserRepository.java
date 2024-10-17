package com.shapirogrill.ideasmanager.user;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    public Boolean existsByUsername(String username);

    public Optional<User> findByUsername(String username);
}
