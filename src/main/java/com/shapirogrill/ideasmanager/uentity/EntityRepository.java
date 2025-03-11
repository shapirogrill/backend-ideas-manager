package com.shapirogrill.ideasmanager.uentity;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EntityRepository {
    private final EntityManager entityManager;

    @Transactional
    public void executeQueryUpdate(String sqlQuery) {
        entityManager.unwrap(Session.class)
                .createNativeQuery(sqlQuery, Object.class).executeUpdate();
    }

    @Transactional
    public List<Object> executeQuerySelect(String sqlQuery) {
        return entityManager.unwrap(Session.class)
                .createNativeQuery(sqlQuery, Object.class).getResultList();
    }
}