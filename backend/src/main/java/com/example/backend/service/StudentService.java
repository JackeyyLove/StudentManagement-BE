package com.example.backend.service;

import com.example.backend.dto.StudentDTO;

import java.util.List;

public interface StudentService {
    List<StudentDTO> getAllStudent();
    StudentDTO getStudentById(Long id);
    StudentDTO addStudent(StudentDTO studentDTO);
    StudentDTO updateStudent(Long id, StudentDTO studentDTO);
    void deleteStudent(Long id);
}
