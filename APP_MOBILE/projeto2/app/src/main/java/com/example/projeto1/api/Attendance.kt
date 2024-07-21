package com.example.projeto1.api

import java.sql.Timestamp

data class Attendance(
    val userid: Int,
    val username: String,
    val data: String,
    val hora: String,
    // Removidos os outros campos que não estão mais especificados
)
