package com.example.backend.mapper;

import com.example.backend.dto.StudentDTO;
import com.example.backend.entity.Student;

public class StudentMapper {
    public static StudentDTO mapToStudentDTO(Student student) {
        return new StudentDTO(
                student.getId(),
                student.getName(),
                student.getSex(),
                student.getSchool()
        );
    }
    public static Student mapToStudent(StudentDTO studentDTO) {
        return new Student(
                studentDTO.getId(),
                studentDTO.getName(),
                studentDTO.getSex(),
                studentDTO.getSchool()
        );
    }
}
