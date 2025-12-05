package com.example.EmployeeSystem.service;

import com.example.EmployeeSystem.dao.MyRepo;
import com.example.EmployeeSystem.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

@Service
public class EmpService {

    @Autowired
    private MyRepo myRepo;

//public List<Employee> show(){
//    return myRepo.findAll();
//}

    public Page<Employee> show(Pageable pageable) {
        return myRepo.findAll(pageable);
    }

public void addEmp(Employee employee){
myRepo.save(employee);
}


public void delete(int id){
    myRepo.deleteById(id);

}

    public Employee getEmpId(int id) {
        return myRepo.findById(id).orElse(null);
    }


public void update(Employee employee){
    Optional<Employee> byId = myRepo.findById(employee.getId());
byId.get().setName(employee.getName());
byId.get().setAge(employee.getAge());
byId.get().setSalary(employee.getSalary());
myRepo.save(byId.get());
}

    public String saveJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            List<Employee> employees = mapper.readValue(json, new TypeReference<List<Employee>>() {
            });
            for (Employee e : employees){
                myRepo.save(e);
            }


            return "Saved Successfully!";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }

    }

}
