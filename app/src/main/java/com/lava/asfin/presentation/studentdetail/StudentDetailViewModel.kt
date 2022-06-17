package com.lava.asfin.presentation.studentdetail

import androidx.lifecycle.*
import com.lava.asfin.data.local.Student
import com.lava.asfin.domain.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentDetailViewModel @Inject constructor(
    private val repository: StudentRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _navigateToStudentList = MutableLiveData<Boolean?>()
    val navigateToStudentList = _navigateToStudentList as LiveData<Boolean?>

    val serialnumber = savedStateHandle.get<Int>("serialnumber")

    var student = MutableLiveData<Student?>()

    init {
        initializeStudent()
    }

    private fun initializeStudent() {
        viewModelScope.launch {
            student.value = serialnumber?.let {
                getCurrentStudent(it)
            }
        }
    }

    private suspend fun getCurrentStudent(serialnumber: Int) = repository.getStudent(serialnumber)

    fun doneNavigating() {
        _navigateToStudentList.value = null
    }

    fun onClose() {
        _navigateToStudentList.value = true
    }
}