package org.group05.com.entity;


import org.group05.com.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity(tableName = "employees")
public class Employee {
    @Id
    @Column(name = "emp_no")
    private Integer id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "hire_date")
    private Date hireDate;

    @OneToMany(cascade = "ALL")
    @JoinColumn(name = "emp_no")
    private List<Salary> salaries;

    @Override
    public String toString() {
        String str =  "Employee {" +
                " id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", hireDate=" + hireDate +
                ", salaries=";
        if (salaries != null) {
            str += salaries.stream()
                    .map(salary -> {
                        if (salary.id != null)
                            return salary.id.toString();
                        return "null";
                    })
                    .collect(Collectors.joining(", ")) +
                    "}\n";
        } else if (salaries.isEmpty())
            str += "[]";
        else str += "null}\n";
        return str;
    }

    public Employee(Integer id, String firstName, String lastName, Date hireDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hireDate = hireDate;
    }

    public Employee(Integer id, String firstName, String lastName, Date hireDate, List<Salary> salaries) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hireDate = hireDate;
        this.salaries = salaries;
    }

    public Employee(String firstName, String lastName, Date hireDate, List<Salary> salaries) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.hireDate = hireDate;
        this.salaries = salaries;
    }

    public Employee() {

    }

    public String getFirstName() {
        return firstName;
    }

    public Integer getId() {
        return id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
