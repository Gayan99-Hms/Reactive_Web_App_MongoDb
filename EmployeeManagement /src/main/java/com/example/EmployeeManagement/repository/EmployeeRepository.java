
package com.example.EmployeeManagement.repository;

import com.example.EmployeeManagement.model.Employee;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class EmployeeRepository {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public EmployeeRepository(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    public Mono<Employee> createEmployee(Employee employee) {
        return reactiveMongoTemplate.insert(employee);
    }

    public Flux<Employee> getAllEmployees() {
        return reactiveMongoTemplate.findAll(Employee.class);
    }

    public Mono<Employee> getEmployeeById(String id) {
        return reactiveMongoTemplate.findById(id, Employee.class);
    }

    public Flux<Employee> getEmployeesByDepartment(String department) {
        Query query = new Query(Criteria.where("department").is(department));
        return reactiveMongoTemplate.find(query, Employee.class);
    }

    public Mono<Employee> updateEmployee(String id, Employee employee) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update()
                .set("name", employee.getName())
                .set("department", employee.getDepartment())
                .set("salary", employee.getSalary());

        return reactiveMongoTemplate.updateFirst(query, update, Employee.class)
                .then(getEmployeeById(id));
    }

    public Mono<Void> deleteEmployee(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return reactiveMongoTemplate.remove(query, Employee.class).then();
    }
}



