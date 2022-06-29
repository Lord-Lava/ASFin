package com.lava.asfin.domain.model

import com.lava.asfin.data.remote.dto.Name

data class StudentDetail (
    val data: List<Name>,
    val nextPage: String
)