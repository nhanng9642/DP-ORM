package org.group05.com.entityManager;
import java.sql.Connection;

public class MySQLEntityManager extends EntityManager {
    public MySQLEntityManager(Connection connection) {
        super(connection);
    }

    @Override
    public Object executeQuery(String query) {
        //TODO: Implement MySQL query execution, return the result
        return null;
    }
}


