package org.group05.com.entityManager;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MySQLEntityManager extends EntityManager {
    public MySQLEntityManager(Connection connection) {
        super(connection);
    }

    public List<Object> executeQuery(String query, Object... params) {
        List<Object> results = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
            }
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                while (resultSet.next()) {
                    Object result = null;
                    if (resultSet.getMetaData().getColumnCount() == 1) {
                        result = resultSet.getObject(1);
                    } else {
                        result = new Object();
                        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                            ((Object) result).getClass().getDeclaredField(resultSet.getMetaData().getColumnName(i)).set(result, resultSet.getObject(i));
                        }
                    }
                    results.add(result);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error executing query: " + query, e);
        }
        return results;
    }
}


