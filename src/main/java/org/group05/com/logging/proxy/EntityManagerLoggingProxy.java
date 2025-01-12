package org.group05.com.logging.proxy;

import org.group05.com.entityManager.EntityManager;
import org.group05.com.entityManager.EntityManagerContract;
import org.group05.com.logging.strategy.LoggingStrategy;

import java.util.List;
import java.util.Map;

public class EntityManagerLoggingProxy implements EntityManagerContract {
    private final LoggingStrategy loggingStrategy;
    private final EntityManager entityManager;

    public EntityManagerLoggingProxy(EntityManager entityManager, LoggingStrategy loggingStrategy) {
        this.loggingStrategy = loggingStrategy;
        this.entityManager = entityManager;
    }

    public <T> T insert(T entity){
        loggingStrategy.log("Saving entity: " + entity.toString());
        return entityManager.insert(entity);
    }
    public <T> T update(T entity){
        loggingStrategy.log("Updating entity: " + entity.toString());
        return entityManager.update(entity);
    }
    public <T> int delete(T entity){
        loggingStrategy.log("Deleting entity: " + entity.toString());
        return entityManager.delete(entity);
    }
    public <T> T find(Class<T> entityClass, Object value){
        loggingStrategy.log("Finding entity: " + entityClass.getSimpleName() + " with id: " + value);
        return entityManager.find(entityClass, value);
    }
    public <T> List<T> find(Class<T> entityClass, String column, Object value){
        loggingStrategy.log("Finding entity: " + entityClass.getSimpleName() + " with " + column + " = " + value);
        return entityManager.find(entityClass, column, value);
    }

    public void close(){
        loggingStrategy.log("Closing entity manager");
        entityManager.close();
    }

    public List<Object[]> executeQuery(String query, Object... params) {
        loggingStrategy.log("Executing query: " + query);
        return entityManager.executeQuery(query);
    }

}
