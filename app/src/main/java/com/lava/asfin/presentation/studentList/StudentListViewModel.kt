package com.lava.asfin.presentation.studentList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lava.asfin.data.local.Student
import com.lava.asfin.data.remote.dto.StudentDetailDto
import com.lava.asfin.domain.model.StudentDetail
import com.lava.asfin.domain.repository.StudentRepository
import com.lava.asfin.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentListViewModel @Inject constructor(
    private val repository: StudentRepository,
) : ViewModel() {

    private var _pageNumber = "0"
    val pageNumber = _pageNumber

    private val _studentsResponse = MutableLiveData<Resource<StudentDetailDto>>()
    val studentResponse = _studentsResponse as LiveData<Resource<StudentDetailDto>>

    private val _navigateToStudentDetailFragment = MutableLiveData<Int>()
    val navigateToStudentDetailFragment = _navigateToStudentDetailFragment as LiveData<Int>

    private val _isValidScore = MutableLiveData<Boolean?>(null)
    val isValidScore = _isValidScore as LiveData<Boolean?>

    init {
        if (getStudentsList().value.isNullOrEmpty()) getStudentsResponse(_pageNumber)
    }

    fun getStudentsList() = repository.getAllStudents()

    fun getStudentsResponse(pageNumber: String) {
        viewModelScope.launch {
            _studentsResponse.value = Resource.Loading
            _studentsResponse.value = repository.getStudentsDetails(pageNumber)
        }
    }

    fun insertStudents(studentDetails: StudentDetail) {
        val students: ArrayList<Student> = ArrayList()
        studentDetails.data.let { list ->
            list.forEach {
                students.add(Student(it.serialnumber,it.name))
            }
        }
        viewModelScope.launch {
            repository.insertMultipleStudents(students)
        }
    }

    fun deleteAllStudents() = viewModelScope.launch {
        repository.clear()
    }

    fun onStudentClicked(serialnumber: Int) {
        _navigateToStudentDetailFragment.value = serialnumber
    }

    fun setStudentScore(student: Student, score: String) {
        if (score.isNotEmpty()) {
            student.score = score.toInt()
            viewModelScope.launch {
                repository.updateStudent(student)
            }
            _isValidScore.value = true
        }
        else {
            _isValidScore.value = false
        }
        _isValidScore.value = null
    }

    fun setPageNumber(pageNumber: String) {
        _pageNumber = pageNumber
    }

    fun onStudentDetailNavigated() {
        _navigateToStudentDetailFragment.value = null
    }
}