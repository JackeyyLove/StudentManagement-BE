package com.example.backend.service.impl;

import com.example.backend.dto.StudentDTO;
import com.example.backend.entity.Student;
import com.example.backend.mapper.StudentMapper;
import com.example.backend.repository.StudentRepository;
import com.example.backend.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {
    private StudentRepository studentRepository;
    @Override
    public List<StudentDTO> getAllStudent() {
        List<Student> studentList = studentRepository.findAll();
        return studentList.stream()
                .map(student -> StudentMapper.mapToStudentDTO(student))
                .collect(Collectors.toList());
    }

    @Override
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find student with id: " + id));
        return StudentMapper.mapToStudentDTO(student);
    }

    @Override
    public StudentDTO addStudent(StudentDTO studentDTO) {
        Student student = StudentMapper.mapToStudent(studentDTO);
        studentRepository.save(student);
        return StudentMapper.mapToStudentDTO(student);
    }

    @Override
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find student with id: " + id));
        student.setName(studentDTO.getName());
        student.setSex(studentDTO.getSex());
        student.setSchool(studentDTO.getSchool());
        Student updatedStudent = studentRepository.save(student);
        return StudentMapper.mapToStudentDTO(updatedStudent);
    }

    @Override
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find student with id: " + id));
        studentRepository.deleteById(id);
    }
}
