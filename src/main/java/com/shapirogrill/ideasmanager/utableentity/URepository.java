package com.shapirogrill.ideasmanager.utableentity;

import org.hibernate.Session;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class URepository {
    private final EntityManager entityManager;

    @Transactional
    public void executeQueryUpdate(String sqlQuery) {
        entityManager.unwrap(Session.class)
                .createNativeQuery(sqlQuery, Object.class).executeUpdate();
    }
}