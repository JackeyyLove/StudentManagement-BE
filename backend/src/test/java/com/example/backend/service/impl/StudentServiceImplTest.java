package com.example.backend.service.impl;

import com.example.backend.dto.StudentDTO;
import com.example.backend.entity.Student;
import com.example.backend.mapper.StudentMapper;
import com.example.backend.repository.StudentRepository;
import com.example.backend.service.StudentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class StudentServiceImplTest {
    @Mock
    private StudentRepository studentRepository;
    @InjectMocks
    StudentService studentService = new StudentServiceImpl(studentRepository);

    @DisplayName("Get student - success")
    @Test
    void test_get_student_by_id_success() {
        Student student = getMockStudent();
        // Mocking
        Mockito.when(studentRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(student));
        // Actual
        StudentDTO studentDTO = studentService.getStudentById(1L);
        // Verification
        verify(studentRepository, times(1)).findById(ArgumentMatchers.anyLong());
        // Assert
        Assertions.assertNotNull(studentDTO);
        Assertions.assertEquals(student.getId(), studentDTO.getId());
    }
    @DisplayName("Add student - success")
    @Test
    void test_add_student_success() {
        Student student = getMockStudent();
        StudentDTO studentDTO = getMockStudentDTO();

        try (MockedStatic<StudentMapper> mockedStatic = mockStatic(StudentMapper.class)) {
            // Mocking
            when(studentRepository.save(any(Student.class))).thenReturn(student);
            mockedStatic.when(() -> StudentMapper.mapToStudent(any(StudentDTO.class))).thenReturn(student);
            mockedStatic.when(() -> StudentMapper.mapToStudentDTO(any(Student.class))).thenReturn(studentDTO);

            // Actual
            StudentDTO result = studentService.addStudent(studentDTO);

            // Verification
            verify(studentRepository, times(1)).save(any(Student.class));

            // Assert
            assertNotNull(result);
            assertEquals(student.getId(), result.getId());
        }
    }

    @DisplayName("Update student - success")
    @Test
    void test_update_student_success() {
        Student student = getMockStudent();
        StudentDTO studentDTO = getMockStudentDTO();

        try (MockedStatic<StudentMapper> mockedStatic = mockStatic(StudentMapper.class)) {
            // Mocking
            when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
            when(studentRepository.save(any(Student.class))).thenReturn(student);
            mockedStatic.when(() -> StudentMapper.mapToStudentDTO(any(Student.class))).thenReturn(studentDTO);

            // Actual
            StudentDTO result = studentService.updateStudent(1L, studentDTO);

            // Verification
            verify(studentRepository, times(1)).findById(anyLong());
            verify(studentRepository, times(1)).save(any(Student.class));

            // Assert
            assertNotNull(result);
            assertEquals(student.getId(), result.getId());
        }
    }
    @DisplayName("Delete student - success")
    @Test
    void test_delete_student_success() {
        Student student = getMockStudent();

        // Mocking
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        doNothing().when(studentRepository).deleteById(anyLong());

        // Actual
        studentService.deleteStudent(1L);

        // Verification
        verify(studentRepository, times(1)).findById(anyLong());
        verify(studentRepository, times(1)).deleteById(anyLong());
    }
    @DisplayName("Get all students - success")
    @Test
    void test_get_all_students_success() {
        List<Student> studentList = Stream.of(getMockStudent()).collect(Collectors.toList());
        List<StudentDTO> studentDTOList = studentList.stream()
                .map(StudentMapper::mapToStudentDTO)
                .collect(Collectors.toList());

        try (MockedStatic<StudentMapper> mockedStatic = mockStatic(StudentMapper.class)) {
            // Mocking
            when(studentRepository.findAll()).thenReturn(studentList);
            mockedStatic.when(() -> StudentMapper.mapToStudentDTO(any(Student.class)))
                    .thenAnswer(invocation -> {
                        Student student = invocation.getArgument(0);
                        return new StudentDTO(student.getId(), student.getName(), student.getSex(), student.getSchool());
                    });

            // Actual
            List<StudentDTO> result = studentService.getAllStudent();

            // Verification
            verify(studentRepository, times(1)).findAll();

            // Assert
            assertNotNull(result);
            assertEquals(studentDTOList.size(), result.size());
            assertEquals(studentDTOList.get(0).getId(), result.get(0).getId());
        }
    }
    private Student getMockStudent() {
        return new Student(1L, "John Doe", "Male", "ABC High School");
    }

    private StudentDTO getMockStudentDTO() {
        return new StudentDTO(1L, "John Doe", "Male", "ABC High School");
    }
}