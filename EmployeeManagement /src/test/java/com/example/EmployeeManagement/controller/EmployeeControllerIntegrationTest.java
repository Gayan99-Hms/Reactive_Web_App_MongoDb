package com.example.EmployeeManagement.controller;

import com.example.EmployeeManagement.model.Employee;
import com.example.EmployeeManagement.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureWebTestClient
public class EmployeeControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private EmployeeService employeeService;

    @Test
    public void testCreateEmployee() {
        Employee newEmployee = new Employee("John Doe", "IT");
        Employee savedEmployee = new Employee("1", "John Doe", 7000.0);

        when(employeeService.createEmployee(any(Employee.class))).thenReturn(Mono.just(savedEmployee));

        webTestClient.post()
                .uri("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(newEmployee), Employee.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Employee.class)
                .value(createdEmployee -> {
                    assertThat(createdEmployee).usingRecursiveComparison().isEqualTo(savedEmployee);
                });
    }

    @Test
    public void testGetAllEmployees() {
        List<Employee> expectedEmployees = Arrays.asList(
                new Employee("1", "John Doe", "Engineering", 60000.0),
                new Employee("2", "Jane Smith", "Marketing", 55000.0),
                new Employee("3", "Michael Johnson", "Sales Rep", 58000.0)
        );

        when(employeeService.getAllEmployees()).thenReturn(Flux.fromIterable(expectedEmployees));

        webTestClient.get()
                .uri("/employees")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Employee.class)
                .value(employees -> {
                    assertThat(employees).usingRecursiveComparison().isEqualTo(expectedEmployees);
                });
    }

    @Test
    public void testGetEmployeeById() {
        Employee expectedEmployee = new Employee("1", "John Doe", "Engineering", 60000.0);
        String id = "1";

        when(employeeService.getEmployeeById(id)).thenReturn(Mono.just(expectedEmployee));

        webTestClient.get()
                .uri("/employees/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Employee.class)
                .value(employee -> {
                    assertThat(employee).usingRecursiveComparison().isEqualTo(expectedEmployee);
                });
    }

    @Test
    public void testGetEmployeesByDepartment() {
        List<Employee> expectedEmployees = Arrays.asList(
                new Employee("1", "John Doe", "Engineering", 60000.0),
                new Employee("2", "Jane Smith", "Marketing", 55000.0)
        );

        when(employeeService.getEmployeesByDepartment("Engineering"))
                .thenReturn(Flux.fromIterable(expectedEmployees));

        webTestClient.get()
                .uri("/employees/department/Engineering")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Employee.class)
                .value(employees -> {
                    assertThat(employees).usingRecursiveComparison().isEqualTo(expectedEmployees);
                });
    }


    @Test
    public void testUpdateEmployee() {
        Employee employeeToUpdate = new Employee("1", "Updated John", "Updated Doe", "Updated Department", 70000);

        when(employeeService.updateEmployee(eq("1"), any(Employee.class))).thenReturn(Mono.just(employeeToUpdate));

        webTestClient.put()
                .uri("/employees/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(employeeToUpdate), Employee.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Employee.class)
                .value(updatedEmployee -> {
                    assertThat(updatedEmployee).usingRecursiveComparison().isEqualTo(employeeToUpdate);
                });
    }

    @Test
    public void testDeleteEmployee() {
        String id = "1";
        when(employeeService.deleteEmployee(id)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/employees/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }
}
