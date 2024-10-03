package com.shapirogrill.ideasmanager.user;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    public Boolean existsByUsername(String username);
    
    public User findByUsername(String username);
}
