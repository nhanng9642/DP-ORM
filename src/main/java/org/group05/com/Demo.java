package org.group05.com;

import org.group05.com.entity.Employee;
import org.group05.com.entity.Salary;
import org.group05.com.entityManager.EntityManager;
import org.group05.com.entityManager.EntityManagerFactory;
import org.group05.com.logging.proxy.EntityManagerLoggingProxy;
import org.group05.com.logging.strategy.impl.ConsoleLogging;

import java.util.Date;
import java.util.List;

public class Demo {
    private EntityManagerLoggingProxy entityManagerWithLogging;

    Demo() {
        EntityManagerFactory entityManagerFactory = new EntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        this.entityManagerWithLogging = new EntityManagerLoggingProxy(entityManager, new ConsoleLogging());
    }

    public void testFindById() {
        Employee employee = entityManagerWithLogging.find(Employee.class, 7);
        System.out.println(employee);
    }

    public void testWhere() {
        List<Employee> employees = entityManagerWithLogging.find(Employee.class, "first_name",  "John");
        System.out.println(employees);
    }

    public void testDelete() {
        Employee employee = entityManagerWithLogging.find(Employee.class, 12);
        if (employee == null) {
            System.out.println("Employee not found");
            return;
        }
        int nRows = entityManagerWithLogging.delete(employee);
        System.out.println("Deleted " + nRows + " rows");
    }

    public void testInsert() {
        Salary s1 = new Salary(1000, new Date(), new Date());
        Salary s2 = new Salary(2000, new Date(), new Date());
        List<Salary> salaries = List.of(s1, s2);

        Employee employee = new Employee( "John", "Doe", new Date(), salaries);
        Employee employeeDB = entityManagerWithLogging.insert(employee);

        System.out.println(employeeDB);
    }

    public void testInsertWithManyToOne() {
        Salary s = new Salary(3000, new Date(), new Date());
        s.setEmployee(new Employee("Nhan", "Nguyen", new Date(), null));

        Salary salary = entityManagerWithLogging.insert(s);
        System.out.println(salary);
    }

    public void testUpdate() {
        Salary salary = entityManagerWithLogging.find(Salary.class, 10);
        if (salary == null) {
            System.out.println("Salary not found");
            return;
        }
        salary.setSalary(5000);
        salary.setFromDate(new Date());
        salary.setToDate(new Date());
        salary.setEmployee(new Employee(4, "Nhan", "Nguyen", new Date(), null));

        Salary s = entityManagerWithLogging.update(salary);
        System.out.println(s);
    }
    void testGroupByHaving() {
        List<Object[]> map = entityManagerWithLogging
                .executeQuery("SELECT emp_no, count(emp_no) FROM salaries group by emp_no");
        for (Object[] obj : map) {
            for (Object o : obj) {
                System.out.print(o + " ");
            }
            System.out.println();
        }
    }
    void testCloseConnection() {
        entityManagerWithLogging.close();
    }
}
