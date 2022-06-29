package com.lava.asfin.data.repository

import androidx.lifecycle.LiveData
import com.lava.asfin.data.local.Student
import com.lava.asfin.data.local.StudentDatabase
import com.lava.asfin.data.remote.services.AspireApi
import com.lava.asfin.domain.repository.StudentRepository
import com.lava.asfin.util.safeApiCall
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val api: AspireApi,
    private val database: StudentDatabase,
) : StudentRepository {

    override suspend fun getStudentsDetails(pageNumber: String) =
        safeApiCall { api.getStudentDetails(pageNumber) }

    override suspend fun insertStudent(student: Student) =
        database.studentDao().insertStudent(student)

    override suspend fun insertMultipleStudents(students: List<Student>) =
        database.studentDao().insertMultipleStudents(students)

    override suspend fun updateStudent(student: Student) =
        database.studentDao().updateStudent(student)

    override suspend fun getStudent(key: Int): Student? = database.studentDao().getStudent(key)

    override fun getAllStudents(): LiveData<List<Student>> = database.studentDao().getAllStudents()

    override suspend fun clear() = database.studentDao().clear()
}