package org.group05.com.entityManager;

import java.sql.Connection;

public class PostgresSQLEntityManager extends EntityManager {
    public PostgresSQLEntityManager(Connection connection) {
        super(connection);
    }

    @Override
    public Object executeQuery(String query) {
        //TODO: Implement PostgresSql query execution, return the result
        return null;
    }
}
