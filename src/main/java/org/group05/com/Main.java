package org.group05.com;

import org.group05.com.entity.Employee;
import org.group05.com.entityManager.EntityManager;
import org.group05.com.entityManager.EntityManagerFactory;
import org.group05.com.logging.proxy.EntityManagerLoggingProxy;
import org.group05.com.logging.strategy.impl.ConsoleLogging;
import org.group05.com.logging.strategy.impl.FileLogging;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = new EntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityManagerLoggingProxy entityManagerLoggingProxy = new EntityManagerLoggingProxy(entityManager, new ConsoleLogging());

        Employee employeeDB = entityManagerLoggingProxy.find(Employee.class, 1);
        employeeDB.setFirstName("NewName");

        entityManagerLoggingProxy.update(employeeDB);


    }
}