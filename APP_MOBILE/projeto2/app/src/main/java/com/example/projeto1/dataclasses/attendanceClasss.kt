package com.example.projeto1.dataclasses

import java.sql.Timestamp


 // o pk desta classe

data class  attendanceClasss(
    val userid: Int,
    val username: String,
    val time: String,
    // Removidos os outros campos que não estão mais especificados
)
