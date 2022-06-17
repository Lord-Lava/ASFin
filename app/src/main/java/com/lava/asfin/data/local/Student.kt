package com.lava.asfin.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students_table")
data class Student(
    @PrimaryKey
    var serialnumber: Int = 0,

    var name: String = "",

    var score: Int = 0,
)