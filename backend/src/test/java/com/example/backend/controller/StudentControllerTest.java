package com.example.backend.controller;

import com.example.backend.dto.StudentDTO;
import com.example.backend.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @DisplayName("Get all students - success")
    @Test
    void testGetAllStudents() throws Exception {
        List<StudentDTO> studentList = Arrays.asList(
                new StudentDTO(1L, "John Doe", "Male", "ABC High School"),
                new StudentDTO(2L, "Jane Doe", "Female", "XYZ High School")
        );

        when(studentService.getAllStudent()).thenReturn(studentList);

        mockMvc.perform(get("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(studentList.size()))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Jane Doe"));

        verify(studentService, times(1)).getAllStudent();
    }

    @DisplayName("Get student by ID - success")
    @Test
    void    testGetStudentById() throws Exception {
        StudentDTO studentDTO = new StudentDTO(1L, "John Doe", "Male", "ABC High School");

        when(studentService.getStudentById(anyLong())).thenReturn(studentDTO);

        mockMvc.perform(get("/api/v1/students/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentDTO.getId()))
                .andExpect(jsonPath("$.name").value(studentDTO.getName()));

        verify(studentService, times(1)).getStudentById(anyLong());
    }

    @DisplayName("Create student - success")
    @Test
    void testCreateStudent() throws Exception {
        StudentDTO studentDTO = new StudentDTO(1L, "John Doe", "Male", "ABC High School");

        when(studentService.addStudent(any(StudentDTO.class))).thenReturn(studentDTO);

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(studentDTO.getId()))
                .andExpect(jsonPath("$.name").value(studentDTO.getName()));

        verify(studentService, times(1)).addStudent(any(StudentDTO.class));
    }

    @DisplayName("Update student - success")
    @Test
    void testUpdateStudent() throws Exception {
        StudentDTO studentDTO = new StudentDTO(1L, "John Doe", "Male", "ABC High School");

        when(studentService.updateStudent(anyLong(), any(StudentDTO.class))).thenReturn(studentDTO);

        mockMvc.perform(put("/api/v1/students/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentDTO.getId()))
                .andExpect(jsonPath("$.name").value(studentDTO.getName()));

        verify(studentService, times(1)).updateStudent(anyLong(), any(StudentDTO.class));
    }

    @DisplayName("Delete student - success")
    @Test
    void testDeleteStudent() throws Exception {
        doNothing().when(studentService).deleteStudent(anyLong());

        mockMvc.perform(delete("/api/v1/students/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("A student with ID: 1 has been deleted"));

        verify(studentService, times(1)).deleteStudent(anyLong());
    }
}
