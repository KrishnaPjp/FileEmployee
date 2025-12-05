package com.example.EmployeeSystem.controller;

import com.example.EmployeeSystem.entity.Employee;
import com.example.EmployeeSystem.service.EmpService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

@Controller
public class EmpController {

    @Autowired
    private EmpService empService;

//    @GetMapping("/show")
//    public String show(Model model) {
//
//        List<Employee> show = empService.show();
//        model.addAttribute("show", show);
//
//        return "hello";
//    }


    @GetMapping("/show")
    public String show(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                sortDir.equals("asc")
                        ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending()
        );

        Page<Employee> employeePage = empService.show(pageable);

        model.addAttribute("empList", employeePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeePage.getTotalPages());
        model.addAttribute("pageSize", size);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "hello";
    }


    @GetMapping("/chooseFile")
    public String chooseFilePage() {
        return "chooseFile";
    }

    @GetMapping("/new/{id}")
    public String delete(@PathVariable int id){
        empService.delete(id);
        return "redirect:/show";
    }

    @GetMapping("/update/{id}")
    public String updatePage(@PathVariable int id, Model model) {
        Employee emp = empService.getEmpId(id);
        model.addAttribute("emp", emp);
        return "edit";
    }

    @PostMapping("/update")
    public String updateEmployee(@ModelAttribute Employee emp) {
        empService.update(emp);
        return "redirect:/show";

    }

    @PostMapping("/create")
    public String saveEmployee(@ModelAttribute Employee emp) {
        empService.addEmp(emp);
        return "redirect:/show";
    }


    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file) {


        String fileName = file.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        System.out.println("Extension "+ext);
        if (ext.equalsIgnoreCase("csv")) {

            String dispaly = dispaly(file);
            System.out.println(dispaly);
        } else if (ext.equalsIgnoreCase("txt")) {
            String dispaly = dispaly(file);
            System.out.println(dispaly);
        } else if (ext.equalsIgnoreCase("pdf")) {
            String txt = Arrays.toString(dPdf(file));
            System.out.println(txt);
            System.out.println("Hello world");
        }

        return "redirect:/show";
    }

    @PostMapping("/jsonfile")
    public String csvfile(@RequestParam ("file") MultipartFile file){
        String filename = file.getOriginalFilename();
        String extenshion = filename.substring(filename.lastIndexOf(".")+1);
        System.out.println(extenshion);

        if (extenshion.equalsIgnoreCase("csv")) {
            String show=showjson(file);
            System.out.println(show);

        } else if (extenshion.equalsIgnoreCase("txt")) {
            String show=showjson(file);
            System.out.println(show);

        } else if (extenshion.equalsIgnoreCase("pdf")) {
                String pd= onlypdf(file);
            System.out.println(pd);

        }

        return "redirect:/show";

    }

    @PostMapping("/json")
    public String showjson(@RequestParam ("file") MultipartFile file){
        //json csv file
        // json txt file
        Employee employee= new Employee();
        try {

            String json = new String(file.getBytes());

            String msg = empService.saveJson(json);
            System.out.println(msg);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "success";

    }
    @PostMapping("/jsonpdf")
    public String onlypdf(@RequestParam ("file") MultipartFile file){
            //json pdf
        try {


            PDDocument document = PDDocument.load(file.getInputStream());
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            System.out.println(text);
            empService.saveJson(text);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return "save";
    }

    @PostMapping("/upload")
    public String dispaly(@RequestParam("file") MultipartFile file) {
        //csv file
        // txt file

        Employee employee = new Employee();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;
            br.readLine();


            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                employee.setId(Integer.parseInt(data[0]));
                employee.setName(data[1]);
                employee.setAge(Integer.parseInt(data[2]));
                employee.setSalary(Integer.parseInt((data[3])));
                empService.addEmp(employee);

            }
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/show";
    }

    @PostMapping("/savepdf")
    public String[] dPdf(@RequestParam("file") MultipartFile file) {
        //pdf file

        Employee employee = new Employee();
        try {
            PDDocument document = PDDocument.load(file.getInputStream());
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            String[] s = text.split("\n");
            for (String line :s){
                String replace = line.replace("\r", "");
                String [] data=replace.split(",");
                if(data[0].equals("id")){
                    continue;
                }

                employee.setId(Integer.parseInt(data[0]));
                employee.setName(data[1]);
                employee.setAge(Integer.parseInt(data[2]));
                employee.setSalary(Integer.parseInt((data[3])));
                //  myService.show(employee);
                empService.addEmp(employee);

            }
            document.close();
            return s;


        } catch (Exception e) {
            String s= "Error: " + e.getMessage();
            String[] split = s.split("");
            return split;
        }
    }


}
