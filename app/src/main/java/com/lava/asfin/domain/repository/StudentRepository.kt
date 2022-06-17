package com.lava.asfin.domain.repository

import androidx.lifecycle.LiveData
import com.lava.asfin.data.local.Student
import com.lava.asfin.data.remote.dto.StudentDetailDto
import com.lava.asfin.util.Resource

interface StudentRepository {

    suspend fun getStudentsDetails(pageNumber: String): Resource<StudentDetailDto>

    suspend fun insertStudent(student: Student)

    suspend fun insertMultipleStudents(students: List<Student>)

    suspend fun updateStudent(student: Student)

    suspend fun getStudent(key: Int): Student?

    fun getAllStudents(): LiveData<List<Student>>
}