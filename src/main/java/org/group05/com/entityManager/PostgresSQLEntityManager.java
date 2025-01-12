package org.group05.com.entityManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PostgresSQLEntityManager extends EntityManager {
    public PostgresSQLEntityManager(Connection connection) {
        super(connection);
    }

}
