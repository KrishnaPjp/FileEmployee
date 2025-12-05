package com.example.EmployeeSystem.dao;

import com.example.EmployeeSystem.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyRepo extends JpaRepository<Employee ,Integer> {

}
