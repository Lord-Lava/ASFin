package com.lava.asfin.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.lava.asfin.domain.model.StudentDetail

data class StudentDetailDto(
    val data: Data,
    @SerializedName("execution_time")
    val executionTime: String,
    val message: String,
    @SerializedName("next_page")
    val nextPage: String,
    @SerializedName("status_code")
    val statusCode: Int,
    val success: Boolean
)

fun StudentDetailDto.toStudentDetail(): StudentDetail {
    return StudentDetail(
        data = data.names
    )
}