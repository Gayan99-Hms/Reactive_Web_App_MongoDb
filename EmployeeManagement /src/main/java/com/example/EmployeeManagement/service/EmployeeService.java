package com.example.EmployeeManagement.service;

import com.example.EmployeeManagement.model.Employee;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface EmployeeService {
    Mono<Employee> createEmployee(Employee employee);
    Flux<Employee> getAllEmployees();
    Mono<Employee> getEmployeeById(String id);
    Flux<Employee> getEmployeesByDepartment(String department);
    Mono<Employee> updateEmployee(String id, Employee employee);
    Mono<Void> deleteEmployee(String id);
}



