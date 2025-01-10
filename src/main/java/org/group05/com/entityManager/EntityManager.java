package org.group05.com.entityManager;

import org.group05.com.utils.Utils;
import org.group05.com.annotations.Id;
import org.group05.com.annotations.ManyToOne;
import org.group05.com.annotations.OneToMany;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EntityManager implements EntityManagerContract   {
    protected final Connection connection;

    public EntityManager(Connection connection) {
        this.connection = connection;
    }

    Map<String, Object> map = new HashMap<>();


    public <T> T insert(T entity) {
        try {
            String tableName = Utils.getTableName(entity.getClass());
            StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
            StringBuilder values = new StringBuilder(" VALUES (");
            List<Object> params = new ArrayList<>();

            // Insert after the parent entity
            Map<Field, List<Object>> childEntities = new HashMap<>();

            for (Field field : entity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object val = field.get(entity);
                if (val == null) {
                    continue;
                }

                if (field.isAnnotationPresent(OneToMany.class)){
                    // If the cascade type is ALL, insert the entities in the list after inserting the parent entity
                    if (field.getAnnotation(OneToMany.class).cascade().equals("ALL")) {
                        List<?> entities = (List<?>) val;
                        for (Object obj : entities) {
                            if (Utils.getPrimaryKeyValue(obj) == null ||
                                    find(obj.getClass(), Utils.getPrimaryKeyValue(obj)) == null) {
                                Field foreignField = Utils.getForeignKeyField(obj.getClass(), entity.getClass());
                                List<Object> list = childEntities.getOrDefault(foreignField, new ArrayList<>());
                                list.add(obj);
                                childEntities.put(foreignField, list);
                            }
                        }
                    }
                } else if (field.isAnnotationPresent(ManyToOne.class)) {
                    // If the cascade type is ALL, insert the parent entity (not exists in the database)
                    if (field.getAnnotation(ManyToOne.class).cascade().equals("ALL")) {
                        Object foreignKey = Utils.getPrimaryKeyValue(val);
                        if (find(val.getClass(), foreignKey) == null)
                            insert(val);
                    }

                    String foreignKeyName = Utils.getForeignKeyName(entity.getClass(), val.getClass());
                    query.append(foreignKeyName).append(", ");
                    values.append("?, ");
                    params.add(Utils.getPrimaryKeyValue(val));
                } else {
                    query.append(Utils.getColumnName(entity.getClass(), field.getName())).append(", ");
                    values.append("?, ");
                    params.add(val);
                }
            }

            query.delete(query.length() - 2, query.length());
            values.delete(values.length() - 2, values.length());
            query.append(")").append(values).append(")");

            PreparedStatement preparedStatement = connection.prepareStatement
                    (query.toString(), PreparedStatement.RETURN_GENERATED_KEYS);

            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setObject(i + 1, params.get(i));
            }
            System.out.println(preparedStatement.toString());
            int newRow = preparedStatement.executeUpdate();

            if (newRow > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    Object key = generatedKeys.getObject(1);
                    setKeyValue(entity, key);
                }

                for (Field field : childEntities.keySet()) {
                    List<Object> entities = childEntities.get(field);
                    for (Object obj : entities) {
                        field.setAccessible(true);
                        field.set(obj, entity);
                        insert(obj);
                    }
                }
            }
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error while inserting entity", e);
        }
    }

    public <T> T update(T entity) {
        try {
            String tableName = Utils.getTableName(entity.getClass());
            StringBuilder query = new StringBuilder("UPDATE " + tableName + " SET ");
            List<Object> params = new ArrayList<>();
            Object key = Utils.getPrimaryKeyValue(entity);

            for (Field field : entity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object val = field.get(entity);
                if (val == null) {
                    continue;
                }

                if (field.isAnnotationPresent(OneToMany.class)){
                } else if (field.isAnnotationPresent(ManyToOne.class)) {
                    String foreignKeyName = Utils.getForeignKeyName(entity.getClass(), val.getClass());
                    query.append(foreignKeyName).append(" = ?, ");
                    params.add(Utils.getPrimaryKeyValue(val));
                } else {
                    query.append(Utils.getColumnName(entity.getClass(), field.getName())).append(" = ?, ");
                    params.add(val);
                }
            }

            query.delete(query.length() - 2, query.length());
            query.append(" WHERE ").append(Utils.getPrimaryKeyName(entity.getClass())).append(" = ?");
            params.add(key);

            PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setObject(i + 1, params.get(i));
            }
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error while updating entity", e);
        }
    }

    public <T> int delete(T entity) {
        try {
            if (entity == null) {
                return 0;
            }
            int nRows = 0;
            for (Field field : entity.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(OneToMany.class)
                        && field.getAnnotation(OneToMany.class).cascade().equals("ALL")){
                    field.setAccessible(true);
                    List<?> entities = (List<?>) field.get(entity);
                    for (Object obj : entities) {
                        nRows += delete(obj);
                    }
                }
            }
            String tableName = Utils.getTableName(entity.getClass());
            String primaryKeyName = Utils.getPrimaryKeyName(entity.getClass());

            String query = "DELETE FROM " + tableName + " WHERE " + primaryKeyName + " = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            Object key = Utils.getPrimaryKeyValue(entity);
            preparedStatement.setObject(1, key);
            nRows += preparedStatement.executeUpdate();

            return nRows;
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting entity", e);
        }
    }

    public <T> T find(Class<T> entityClass, Object value) {
        if (value == null)
            return null;
        String tableName = Utils.getTableName(entityClass);
        String primaryKeyName = Utils.getPrimaryKeyName(entityClass);
        String key = tableName + primaryKeyName + value;

        if (map.containsKey(key)) {
            return (T) map.get(key);
        }

        String query = "SELECT * FROM " + tableName + " WHERE " + primaryKeyName + " = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, value);
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            T entity = null;
            if (resultSet.next()) {
                entity = entityClass.getDeclaredConstructor().newInstance();
                map.put(key, entity);
                mapResultSetToEntity(resultSet, entity, value);
            }
            map.put(key, entity);
            return entity;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while fetching data from the database.");
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> find(Class<T> entityClass, String column, Object value) {
        String tableName = Utils.getTableName(entityClass);
        String query = "SELECT * FROM " + tableName + " WHERE " + column + " = ?";
        String key = tableName + column + value.toString();

        if (map.containsKey(key)) {
            return (List<T>) map.get(key);
        }

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, value);
            System.out.println(preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<T> entities = new ArrayList<>();
            map.put(key, entities);
            while (resultSet.next()) {
                T entity = entityClass.getDeclaredConstructor().newInstance();
                mapResultSetToEntity(resultSet, entity, value);
                entities.add(entity);
            }
            return entities;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while fetching data from the database.");
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error while closing the connection", e);
        }
    }

    public abstract Object executeQuery(String query);


    // Helper methods
    private <T> void mapResultSetToEntity(ResultSet resultSet, T entity, Object value) {
        try {
            Class<?> entityClass = entity.getClass();
            Object val;

            for (Field field : entityClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(ManyToOne.class)) {
                    Class<?> parentClass = Utils.getFieldType(field);
                    String foreignKeyName = Utils.getForeignKeyName(entityClass, parentClass);
                    val = find(parentClass, resultSet.getObject(foreignKeyName));
                } else if (field.isAnnotationPresent(OneToMany.class)) {
                    Class<?> parentClass = Utils.getFieldType(field);
                    String foreignKeyName = Utils.getForeignKeyName(entityClass, parentClass);
                    val = find(parentClass, foreignKeyName, value);
                } else {
                    String columnName = Utils.getColumnName(entityClass, field.getName());
                    val = resultSet.getObject(columnName);
                }

                field.setAccessible(true);
                field.set(entity, val);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while mapping result set to entity", e);
        }
    }

    private void setKeyValue(Object entity, Object value) {
        try {
            Class<?> entityClass = entity.getClass();
            for (Field field : entityClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    if (field.getType().equals(Integer.class)) {
                        field.set(entity, ((Number) value).intValue());
                    } else {
                        field.set(entity, value);
                    }
                    return;
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error setting field value", e);
        }
    }
}
