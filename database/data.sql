create database employeeTest;

CREATE TABLE employees (
                           emp_no INT PRIMARY KEY AUTO_INCREMENT,
                           first_name VARCHAR(50),
                           last_name VARCHAR(50),
                           hire_date DATE
);

CREATE TABLE salaries (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          emp_no INT,
                          salary INT,
                          from_date DATE,
                          to_date DATE,
                          FOREIGN KEY (emp_no) REFERENCES employees(emp_no)
);

INSERT INTO employees (first_name, last_name, hire_date) VALUES
         ('John', 'Doe', '2020-01-15'),
         ('Jane', 'Smith', '2019-03-22'),
         ('Alice', 'Johnson', '2018-07-01'),
         ('Bob', 'Brown', '2021-11-05'),
         ('Charlie', 'Davis', '2020-08-19');

INSERT INTO salaries (emp_no, salary, from_date, to_date) VALUES
          (1, 50000, '2020-01-15', '2021-01-14'),
          (1, 55000, '2021-01-15', '2022-01-14'),
          (1, 60000, '2022-01-15', '2023-01-14'),

          (2, 52000, '2019-03-22', '2020-03-21'),
          (2, 57000, '2020-03-22', '2021-03-21'),
          (2, 62000, '2021-03-22', '2022-03-21'),

          (3, 48000, '2018-07-01', '2019-06-30'),
          (3, 53000, '2019-07-01', '2020-06-30'),
          (3, 58000, '2020-07-01', '2021-06-30'),

          (4, 45000, '2021-11-05', '2022-11-04'),
          (4, 50000, '2022-11-05', '2023-11-04'),
          (4, 55000, '2023-11-05', '2024-11-04'),

          (5, 48000, '2020-08-19', '2021-08-18'),
          (5, 53000, '2021-08-19', '2022-08-18'),
          (5, 58000, '2022-08-19', '2023-08-18');
