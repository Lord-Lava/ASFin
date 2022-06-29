package com.lava.asfin.data.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStudent(student: Student)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMultipleStudents(students: List<Student>)

    @Update
    suspend fun updateStudent(student: Student)

    @Query("SELECT * FROM students_table WHERE serialnumber = :key")
    suspend fun getStudent(key: Int): Student?

    @Query("SELECT * FROM students_table ORDER BY serialnumber ASC")
    fun getAllStudents(): LiveData<List<Student>>

    @Query("DELETE FROM students_table")
    suspend fun clear()

}