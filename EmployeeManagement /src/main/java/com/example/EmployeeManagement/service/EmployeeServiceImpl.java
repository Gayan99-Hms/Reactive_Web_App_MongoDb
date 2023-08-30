package com.example.EmployeeManagement.service;//package com.example.EmployeeManagement.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.example.EmployeeManagement.model.Employee;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@Service
public class    EmployeeServiceImpl implements EmployeeService {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    public EmployeeServiceImpl(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public Mono<Employee> createEmployee(Employee employee) {
        return reactiveMongoTemplate.insert(employee);
    }

    @Override
    public Flux<Employee> getAllEmployees() {
        return reactiveMongoTemplate.findAll(Employee.class);
    }

    @Override
    public Mono<Employee> getEmployeeById(String id) {
        return reactiveMongoTemplate.findById(id, Employee.class);
    }

    @Override
    public Flux<Employee> getEmployeesByDepartment(String department) {
        Query query = new Query(Criteria.where("department").is(department));
        return reactiveMongoTemplate.find(query, Employee.class);
    }

    @Override
    public Mono<Employee> updateEmployee(String id, Employee employee) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update()
                .set("name", employee.getName())
                .set("department", employee.getDepartment())
                .set("salary", employee.getSalary());

        return reactiveMongoTemplate.updateFirst(query, update, Employee.class)
                .then(getEmployeeById(id));
    }

    @Override
    public Mono<Void> deleteEmployee(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return reactiveMongoTemplate.remove(query, Employee.class).then();
    }
}




