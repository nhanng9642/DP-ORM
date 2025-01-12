package org.group05.com.entityManager;

import java.util.List;

public interface EntityManagerContract {
    <T> T insert(T entity);
    <T> T update(T entity);
    <T> int delete(T entity);
    <T> T find(Class<T> entityClass, Object value);
    <T> List<T> find(Class<T> entityClass, String column, Object value);
    void close();
}
