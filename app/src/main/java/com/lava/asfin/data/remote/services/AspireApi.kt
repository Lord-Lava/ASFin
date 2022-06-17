package com.lava.asfin.data.remote.services

import com.lava.asfin.data.remote.dto.StudentDetailDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AspireApi {

    @GET("/api/v1/get_student_details/?format=json")
    suspend fun getStudentDetails(
        @Query("next_page")
        pageNumber: String,
    ): StudentDetailDto
}