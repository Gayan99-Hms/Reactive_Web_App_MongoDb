package com.example.EmployeeManagement.service;

import com.example.EmployeeManagement.model.Employee;
import com.example.EmployeeManagement.repository.EmployeeRepository;
import com.mongodb.client.result.UpdateResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import org.springframework.data.mongodb.core.query.Update;

public class EmployeeServiceImplTest {

    private EmployeeServiceImpl employeeService;

    @Mock
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        employeeService = new EmployeeServiceImpl(reactiveMongoTemplate);
    }

    @Test
    public void testCreateEmployee() {
        Employee employee = new Employee("1", "John", "Doe", "IT", 30000);
        when(reactiveMongoTemplate.insert(any(Employee.class))).thenReturn(Mono.just(employee));

        Mono<Employee> result = employeeService.createEmployee(employee);

        StepVerifier.create(result)
                .expectNext(employee)
                .verifyComplete();

        verify(reactiveMongoTemplate, times(1)).insert(employee);
    }

    @Test
    public void testGetAllEmployees() {
        Employee employee1 = new Employee("1", "John", "Doe", "IT", 30000);
        Employee employee2 = new Employee("2", "Jane", "Smith", "HR", 28000);
        when(reactiveMongoTemplate.findAll(Employee.class)).thenReturn(Flux.just(employee1, employee2));

        Flux<Employee> result = employeeService.getAllEmployees();

        StepVerifier.create(result)
                .expectNext(employee1)
                .expectNext(employee2)
                .verifyComplete();

        verify(reactiveMongoTemplate, times(1)).findAll(Employee.class);
    }

    @Test
    public void testGetEmployeeById() {
        Employee employee = new Employee("1", "John", "Doe", "IT", 30000);
        when(reactiveMongoTemplate.findById(eq("1"), eq(Employee.class))).thenReturn(Mono.just(employee));

        Mono<Employee> result = employeeService.getEmployeeById("1");

        StepVerifier.create(result)
                .expectNext(employee)
                .verifyComplete();

        verify(reactiveMongoTemplate, times(1)).findById("1", Employee.class);
    }

    @Test
    public void testGetEmployeesByDepartment() {
        Employee employee1 = new Employee("1", "John", "Doe", "IT", 30000);
        Employee employee2 = new Employee("2", "Jane", "Smith", "IT", 28000);
        when(reactiveMongoTemplate.find(any(Query.class), eq(Employee.class))).thenReturn(Flux.just(employee1, employee2));

        Flux<Employee> result = employeeService.getEmployeesByDepartment("IT");

        StepVerifier.create(result)
                .expectNext(employee1)
                .expectNext(employee2)
                .verifyComplete();

        verify(reactiveMongoTemplate, times(1)).find(any(Query.class), eq(Employee.class));
    }

    @Test
    public void testUpdateEmployeeName() {
        Employee existingEmployee = new Employee("1", "John", "Doe", "IT", 30000);
        Employee updatedEmployee = new Employee("1", "Jane Smith", "Doe", "IT", 30000);

        when(reactiveMongoTemplate.findById(eq("1"), eq(Employee.class))).thenReturn(Mono.just(existingEmployee));
        when(reactiveMongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(Employee.class))).thenReturn(Mono.just(mock(UpdateResult.class))); // Mock the UpdateResult

        Mono<Employee> result = employeeService.updateEmployee("1", updatedEmployee);

        StepVerifier.create(result)
                .assertNext(employee -> {
                    // Verify that only the name has been updated
                    assertEquals(updatedEmployee.getName(), employee.getName());
                    assertEquals(existingEmployee.getDepartment(), employee.getDepartment()); // Department remains the same
                })
                .verifyComplete();

        verify(reactiveMongoTemplate, times(1)).findById("1", Employee.class);
        verify(reactiveMongoTemplate, times(1)).updateFirst(any(Query.class), any(Update.class), eq(Employee.class));
    }



    @Test
    public void testDeleteEmployee() {
        when(reactiveMongoTemplate.remove(any(Query.class), eq(Employee.class))).thenReturn(Mono.empty());

        Mono<Void> result = employeeService.deleteEmployee("1");

        StepVerifier.create(result)
                .verifyComplete();

        verify(reactiveMongoTemplate, times(1)).remove(any(Query.class), eq(Employee.class));
    }
}




